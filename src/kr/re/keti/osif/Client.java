package kr.re.keti.osif;

import java.io.FileReader;
import java.io.IOException;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.com.eclipsesource.json.Json;
import com.hazelcast.com.eclipsesource.json.JsonObject;
import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.map.listener.EntryAddedListener;
import com.hazelcast.map.listener.EntryEvictedListener;
import com.hazelcast.map.listener.EntryRemovedListener;
import com.hazelcast.map.listener.EntryUpdatedListener;

import kr.re.keti.osif.event.IOpendataListener;
import kr.re.keti.osif.model.ServiceDescription;
import kr.re.keti.osif.model.Opendata;
import kr.re.keti.osif.model.DatabusConfig;

public class Client {

	
	public static final boolean GLOBAL_ONLY = true;
	
	public static final String OSIF_DEVICE_INFO = "OSIF_DEVICE_INFO";
	public static final String OSIF_LOCAL_OPENDATA = "OSIF_LOCAL_OPENDATA";

	public static final String OSIF_RUNNING_DEVICE = "OSIF_RUNNING_DEVICE";
	public static final String OSIF_RUNNING_SERVICE = "OSIF_RUNNING_SERVICE";
	public static final String OSIF_GLOBAL_OPENDATA = "OSIF_GLOBAL_OPENDATA";

	public static final String OSIF_DEVICE_ID = "OSIF_DEVICE_ID";

	public static final String CIOT_APP_MANAGER_REQ = "CIOT_APP_MANAGER_REQ";
	public static final String CIOT_OPENDATA_LIST = "CIOT_OPENDATA_LIST";

	public static final String GLOBAL_DATABUS_GROUP_NAME = "osif";
	public static final String GLOBAL_DATABUS_GROUP_PASS = "osif-pass";

	static class MapEventListener
			implements EntryAddedListener<String, String>, EntryRemovedListener<String, String>,
			EntryUpdatedListener<String, String>, EntryEvictedListener<String, String> {

		IOpendataListener _opendataListener;

		public MapEventListener(IOpendataListener l) {
			this._opendataListener = l;
		}

		public void entryEvicted(EntryEvent<String, String> arg0) {
		}

		public void entryUpdated(EntryEvent<String, String> arg0) {
			_opendataListener.entryUpdated(arg0.getKey(), arg0.getValue());
		}

		public void entryRemoved(EntryEvent<String, String> arg0) {
		}

		public void entryAdded(EntryEvent<String, String> arg0) {
			_opendataListener.entryAdded(arg0.getKey(), arg0.getValue());
		}
	};

	/**
	 * Create new CIoT Databus client object
	 * 
	 * 
	 * @param options
	 *            example for "options" parameter
	 * 
	 * @param options
	 * 
	 *            <pre>
	 * <code>
	 *   {
	 *     "service": {
	 *       "serviceId": 'uuid',
	 *       "serviceName": 'service name',
	 *       "serviceDesc": 'description text for service',
	 *       "versionCode": {
	 *         major: 1, minor: 1, revision:1
	 *       },
	 *       "openData": {
	 *         "local": [{
	 *           "name": "open data name",
	 *           "description": "data description",
	 *           "template": "descriptive text for format of open data contents"
	 *         }],
	 *         "global: [{
	 *           "name": "open data name",
	 *           "description": "data description",
	 *           "template": "descriptive text for format of open data contents"
	 *         }]
	 *       }
	 *     }
	 *
	 *     "databus": {
	 *       "globalHost":  "ip.address.of.cloud",
	 *       "globalPort": "port",
	 *       "localPort": "port"
	 *     }
	 *   }
	 * </code>
	 *            </pre>
	 * 
	 * @return @see kr.re.keti.osif.Client
	 */
	public static Client newClient(JsonObject options) {

		// check required properties
		JsonObject serviceOptions = options.get("service").asObject();
		JsonObject databusOptions = options.get("databus").asObject();

		ServiceDescription service = ServiceDescription.fromJson(serviceOptions);
		DatabusConfig databusConfig = DatabusConfig.fromJson(databusOptions);

		return new Client(service, databusConfig, serviceOptions);
	}
	
	public static Client newClient(String path) {

		
		JsonObject options;
		try {
			FileReader reader = new FileReader(path);

			options = Json.parse(reader).asObject();
			
			// check required properties
			JsonObject serviceOptions = options.get("service").asObject();
			JsonObject databusOptions = options.get("databus").asObject();

			ServiceDescription service = ServiceDescription.fromJson(serviceOptions);
			DatabusConfig databusConfig = DatabusConfig.fromJson(databusOptions);

			return new Client(service, databusConfig, serviceOptions);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;

	}


	/*
	 * ** Member variables
	 */

	private ServiceDescription _serviceDescription = null;
	private DatabusConfig _databusConfig = null;
	private JsonObject _serviceOptions = null;

	private HazelcastInstance _globalDatabusClient = null;
	private HazelcastInstance _localDatabusClient = null;

	private boolean _clientInitialized = false;

	private String _deviceId = null;
	private String _runningServiceId = null;
	private IMap<String, String> _runningServiceMap = null;
	private IMap<String, String> _globalOpenDataMap = null;
	private IMap<String, String> _localOpenDataMap = null;

	protected Client(ServiceDescription _service, DatabusConfig _databusConfig, JsonObject _serviceOptions) {
		super();

		this._serviceDescription = _service;
		this._databusConfig = _databusConfig;
		this._serviceOptions = _serviceOptions;
	}

	private HazelcastInstance _createHazelcastClient(String host, String port) throws Exception {
		return _createHazelcastClient(host, port, null, null);
	}

	private HazelcastInstance _createHazelcastClient(String host, String port, String group, String groupPassword)
			throws Exception {
		try {
			ClientConfig cfg = new ClientConfig();
			cfg.getNetworkConfig().addAddress(host + ":" + port);

			if (group != null && groupPassword != null)
				cfg.getGroupConfig().setName(group).setPassword(groupPassword);

			return HazelcastClient.newHazelcastClient(cfg);
		} catch (Exception ex) {
			throw ex;
		}
	}

	/**
	 * Initialize CIoT Databus client. - make connection
	 * 
	 * 
	 * @return
	 * @throws Exception
	 */
	public Client init() throws Exception {
		try {
			if (this._clientInitialized) {
				return this;
			} else {

				this._globalDatabusClient = this._createHazelcastClient(this._databusConfig.get_globalHost(),
						this._databusConfig.get_globalPort(), GLOBAL_DATABUS_GROUP_NAME, GLOBAL_DATABUS_GROUP_PASS);
				if(GLOBAL_ONLY == false) {
					this._localDatabusClient = this._createHazelcastClient(this._databusConfig.get_localHost(),
							this._databusConfig.get_localPort());					
				}

				this._deviceId = this.readDeviceId();
				this._runningServiceId = this._serviceDescription.get_serviceId();
				
				this._runningServiceMap = this._globalDatabusClient.getMap(OSIF_RUNNING_SERVICE);
				this._globalOpenDataMap = this._globalDatabusClient.getMap(OSIF_GLOBAL_OPENDATA);
				
				if(GLOBAL_ONLY == false ) {
					this._localOpenDataMap = this._localDatabusClient.getMap(OSIF_LOCAL_OPENDATA);
				}

				this._clientInitialized = true;

				return this;
			}
		} catch (Exception ex) {
			throw ex;
		}
	}

	protected String readDeviceId() {
		try {
			if(GLOBAL_ONLY == false) {
				IMap<String, String> deviceInfoMap = this._localDatabusClient.getMap(OSIF_DEVICE_INFO);
				
				return deviceInfoMap.get(OSIF_DEVICE_ID);
			}
			else {
				return java.util.UUID.randomUUID().toString();
			}
		}
		catch(Exception e) {
			return null;
		}
	}

	
	protected void registerServiceInstanceInfo() {
		try {
			this._runningServiceMap.set(this._runningServiceId, this._serviceOptions.toString());
			
			if(GLOBAL_ONLY == false) {
				if(this._serviceDescription.hasLocalOpendata() ) {
					Opendata[] opendata = this._serviceDescription.get_localOpendata();
					
					for(int i=0; i < opendata.length; i++) {
						String dataKey = this._runningServiceId + "#" + opendata[i].get_name();
						this._localOpenDataMap.set(dataKey, null);
					}
				}
			}

			if(this._serviceDescription.hasGlobalOpendata() ) {
				Opendata[] opendata = this._serviceDescription.get_globalOpendata();
				
				for(int i=0; i < opendata.length; i++) {
					String dataKey = this._runningServiceId + "#" + opendata[i].get_name();
					this._globalOpenDataMap.set(dataKey, null);
				}
			}
		}
		catch(Exception ex) {
		}
	}
	
	
	protected void unregisterServiceInstanceInfo() {
		try {
			this._runningServiceMap.remove(this._runningServiceId);
			
			if(GLOBAL_ONLY == false) {
				if(this._serviceDescription.hasLocalOpendata() ) {
					Opendata[] opendata = this._serviceDescription.get_localOpendata();
					
					for(int i=0; i < opendata.length; i++) {
						String dataKey = this._runningServiceId + "#" + opendata[i].get_name();
						this._localOpenDataMap.remove(dataKey);
					}
				}				
			}
			
			if(this._serviceDescription.hasGlobalOpendata() ) {
				Opendata[] opendata = this._serviceDescription.get_globalOpendata();
				
				for(int i=0; i < opendata.length; i++) {
					String dataKey = this._runningServiceId + "#" + opendata[i].get_name();
					this._globalOpenDataMap.remove(dataKey);
				}
			}
		}
		catch(Exception ex) {
		}
	}
	
	/**
	 * this method called when a service startup
	 * 
	 * @throws Exception
	 */
	public void startService() throws Exception {
		this.init();

		this.registerServiceInstanceInfo();
		
	}

	/**
	 * this method called when a application shutdown
	 * 
	 * @throws Exception
	 */
	public void stopApplication() throws Exception {
		this.init();

		this.unregisterServiceInstanceInfo();

	}

	/**
	 * write global application data
	 * 
	 * @param data
	 * @return 
	 * @throws Exception
	 */
	public void setGlobalAppData(String key, Object data) throws Exception {
		Opendata appData = this._serviceDescription.findGlobalAppData(key);
		if(appData == null) {
			throw new Exception("Illegal argument");
		}
	   
		this.init();
		
		String dataName = this._runningServiceId + "#" + appData.get_name();
	    this._globalOpenDataMap.set(dataName,  data.toString());
	    
	    
	    System.out.println( "SET: " + dataName );
	}


	/**
	 * read global application data
	 * 
	 * @return
	 * @throws Exception
	 */
	public Object getGlobalAppData(String key) throws Exception {
		Opendata appData = this._serviceDescription.findGlobalAppData(key);
		if(appData == null) {
			throw new Exception("Illegal argument");
		}
		
		this.init();
		
		String dataName = this._runningServiceId + "#" + appData.get_name();
	    return this._globalOpenDataMap.get(dataName);
	}

	public void setLocalAppData(String key, Object data) throws Exception {
		Opendata appData = this._serviceDescription.findLocalAppData(key);
		if(appData == null) {
			throw new Exception("Illegal argument");
		}
	   
		this.init();

		String dataName = this._runningServiceId + "#" + appData.get_name();
	    this._localOpenDataMap.set(dataName,  data.toString());
	}

	public Object getLocalAppData(String key) throws Exception {
		Opendata appData = this._serviceDescription.findLocalAppData(key);
		if(appData == null) {
			throw new Exception("Illegal argument");
		}
		
		this.init();
		
		String dataName = this._runningServiceId + "#" + appData.get_name();
	    return this._localOpenDataMap.get(dataName);
	}


	public Object getGlobalOpenata(String serviceId, String key) throws Exception {
		this.init();
		
		String dataName = serviceId + "#" + key;
	    return this._globalOpenDataMap.get(dataName);
	}


	public Object getLocalOpenata(String serviceId, String key) throws Exception {
		this.init();
		
		String dataName = serviceId + "#" + key;
	    return this._localOpenDataMap.get(dataName);
	}



	// subscribe/unsubscribe to open data which is owned by other application
	public void subscribeToLocalOpendata(String serviceId, String name, final IOpendataListener listener) throws Exception {

		if (serviceId == null || "".equals(serviceId))
			throw new Exception("Service ID must not null string");
		if (name == null || "".equals(name))
			throw new Exception("Opendata name must not null string");
		if (listener == null)
			throw new Exception("Event listener must not null");

		this.init();
		
		MapEventListener l = new MapEventListener(listener);

		
		String dataName = serviceId + "#" + name;
		this._localOpenDataMap.addEntryListener(l, dataName, true);
		
	}

	public void unsubscribeToLocalOpendata() {
		try {

		} catch (Exception e) {

		}
	}

	public void subscribeToGlobalOpendata(String serviceId, String name, final IOpendataListener listener) throws Exception {
		if (serviceId == null || "".equals(serviceId))
			throw new Exception("Service ID must not null string");
		if (name == null || "".equals(name))
			throw new Exception("Opendata name must not null string");
		if (listener == null)
			throw new Exception("Event listener must not null");

		this.init();
		
		MapEventListener l = new MapEventListener(listener);

		
		String dataName = serviceId + "#" + name;
		this._globalOpenDataMap.addEntryListener(l, dataName, true);
		

	}

	public void unsubscribeToGlobalOpendata() {
		try {

		} catch (Exception e) {

		}
	}

}
