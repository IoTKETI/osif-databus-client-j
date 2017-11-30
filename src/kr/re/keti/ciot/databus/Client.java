package kr.re.keti.ciot.databus;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.com.eclipsesource.json.JsonObject;
import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.IQueue;
import com.hazelcast.map.listener.EntryAddedListener;
import com.hazelcast.map.listener.EntryEvictedListener;
import com.hazelcast.map.listener.EntryRemovedListener;
import com.hazelcast.map.listener.EntryUpdatedListener;

import kr.re.keti.ciot.databus.event.IOpendataListener;
import kr.re.keti.ciot.databus.model.Application;
import kr.re.keti.ciot.databus.model.ApplicationManagerRequestMessage;
import kr.re.keti.ciot.databus.model.DatabusConfig;
import kr.re.keti.ciot.databus.model.Opendata;

public class Client {

	public static final String CIOT_APP_MANAGER_REQ = "CIOT_APP_MANAGER_REQ";
	public static final String CIOT_OPENDATA_LIST = "CIOT_OPENDATA_LIST";

	static class MapEventListener
			implements EntryAddedListener<String, JsonObject>, EntryRemovedListener<String, JsonObject>,
			EntryUpdatedListener<String, JsonObject>, EntryEvictedListener<String, JsonObject> {

		IOpendataListener _opendataListener;

		public MapEventListener(IOpendataListener l) {
			this._opendataListener = l;
		}

		public void entryEvicted(EntryEvent<String, JsonObject> arg0) {
			_opendataListener.entryEvicted(arg0.getValue());
		}

		public void entryUpdated(EntryEvent<String, JsonObject> arg0) {
			_opendataListener.entryUpdated(arg0.getValue());
		}

		public void entryRemoved(EntryEvent<String, JsonObject> arg0) {
			_opendataListener.entryRemoved(arg0.getValue());
		}

		public void entryAdded(EntryEvent<String, JsonObject> arg0) {
			_opendataListener.entryAdded(arg0.getValue());
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
	 *     "application": {
	 *       "instanceId": 'uuid',
	 *       "appName": 'application name from package.json',
	 *       "openData": {
	 *         "local": {
	 *           "name": "open data name",
	 *           "description": "data description",
	 *           "template": {object template with default value}
	 *         },
	 *         "global: {
	 *           "name": "open data name",
	 *           "description": "data description",
	 *           "template": {object template with default value}
	 *         }
	 *       }
	 *     }
	 *
	 *     "databus": {
	 *       "global": {
	 *         "host": "ip.address.of.cloud",
	 *         "port": "port",
	 *       },
	 *       "local": {
	 *         "host": "ip.address.of.appmanager",
	 *         "port": "port"
	 *       }
	 *     }
	 *   }
	 * </code>
	 *            </pre>
	 * 
	 * @return @see kr.re.keti.ciot.databusClient
	 */
	public static Client newClient(JsonObject options) {

		// check required properties
		JsonObject applicationOptions = options.get("application").asObject();
		JsonObject databusOptions = options.get("databus").asObject();
		JsonObject localDatabusOptions = databusOptions.get("local").asObject();
		JsonObject globalDatabusOptions = databusOptions.get("global").asObject();

		Application application = Application.fromJson(applicationOptions);

		DatabusConfig localDatabus = DatabusConfig.fromJson(localDatabusOptions);
		DatabusConfig globalDatabus = DatabusConfig.fromJson(globalDatabusOptions);

		return new Client(application, localDatabus, globalDatabus);
	}

	/* **
	 * Member variables
	 */

	private Application _application = null;
	private DatabusConfig _localDatabusConfig = null;
	private DatabusConfig _globalDatabusConfig = null;

	private HazelcastInstance _globalDatabusClient = null;
	private HazelcastInstance _localDatabusClient = null;

	private boolean _clientInitialized = false;

	protected Client(Application _application, DatabusConfig _localDatabusConfig, DatabusConfig _globalDatabusConfig) {
		super();

		this._application = _application;
		this._localDatabusConfig = _localDatabusConfig;
		this._globalDatabusConfig = _globalDatabusConfig;
	}

	private HazelcastInstance _createHazelcastClient(DatabusConfig config) throws Exception {
		try {
			ClientConfig cfg = new ClientConfig();
			cfg.getNetworkConfig().addAddress(config.get_host() + ":" + config.get_port());

			return HazelcastClient.newHazelcastClient(cfg);
		} catch (Exception ex) {
			throw ex;
		}
	}

	
	/**
	 * Initialize CIoT Databus client.
	 *  - make connection
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

				this._globalDatabusClient = this._createHazelcastClient(this._globalDatabusConfig);
				this._localDatabusClient = this._createHazelcastClient(this._localDatabusConfig);

				this._clientInitialized = true;

				return this;
			}
		} catch (Exception ex) {
			throw ex;
		}
	}

	
	/**
	 * this method called when a application startup
	 * 
	 * @throws Exception
	 */
	public void startApplication() throws Exception {
		this.init();

		IQueue<Object> appManagerRequestQueue = this._localDatabusClient.getQueue(Client.CIOT_APP_MANAGER_REQ);
		appManagerRequestQueue
				.put(new ApplicationManagerRequestMessage(ApplicationManagerRequestMessage.STOP, this._application));
	}

	/**
	 * this method called when a application shutdown
	 * 
	 * @throws Exception
	 */
	public void stopApplication() throws Exception {
		this.init();

		IQueue<Object> appManagerRequestQueue = this._localDatabusClient.getQueue(Client.CIOT_APP_MANAGER_REQ);
		appManagerRequestQueue
				.put(new ApplicationManagerRequestMessage(ApplicationManagerRequestMessage.START, this._application));
	}

	/**
	 * write global application data
	 * 
	 * @param data
	 * @throws Exception
	 */
	public void setGlobalAppData(JsonObject data) throws Exception {

		Opendata globalOpendata = this._application.get_globalOpendata();
		if (globalOpendata == null || globalOpendata.get_name() == null)
			throw new Exception("This application does not have global open data");

		this.init();
		IMap<String, JsonObject> opendataList = this._globalDatabusClient.getMap(Client.CIOT_OPENDATA_LIST);

		opendataList.set(globalOpendata.get_name(), data);

	}

	/**
	 * read global application data
	 * 
	 * @return
	 * @throws Exception
	 */
	public JsonObject getGlobalAppData() throws Exception {
		Opendata globalOpendata = this._application.get_globalOpendata();
		if (globalOpendata == null || globalOpendata.get_name() == null)
			throw new Exception("This application does not have global open data");

		this.init();
		IMap<String, JsonObject> opendataList = this._globalDatabusClient.getMap(Client.CIOT_OPENDATA_LIST);

		return opendataList.get(globalOpendata.get_name());
	}

	public void setLocalAppData(JsonObject data) throws Exception {

		Opendata localOpendata = this._application.get_localOpendata();
		if (localOpendata == null || localOpendata.get_name() == null)
			throw new Exception("This application does not have local open data");

		this.init();
		IMap<String, JsonObject> opendataList = this._localDatabusClient.getMap(Client.CIOT_OPENDATA_LIST);

		opendataList.set(localOpendata.get_name(), data);

	}

	public JsonObject getLocalAppData() throws Exception {

		Opendata localOpendata = this._application.get_localOpendata();
		if (localOpendata == null || localOpendata.get_name() == null)
			throw new Exception("This application does not have local open data");

		this.init();
		IMap<String, JsonObject> opendataList = this._localDatabusClient.getMap(Client.CIOT_OPENDATA_LIST);

		return opendataList.get(localOpendata.get_name());
	}

	// read open data which is owned by other application
	public JsonObject getGlobalOpendata(String name) throws Exception {

		if (name == null || "".equals(name))
			throw new Exception("Opendata name must not null string");

		this.init();
		IMap<String, JsonObject> opendataList = this._globalDatabusClient.getMap(Client.CIOT_OPENDATA_LIST);

		return opendataList.get(name);
	}

	public JsonObject getLocalOpendata(String name) throws Exception {

		if (name == null || "".equals(name))
			throw new Exception("Opendata name must not null string");

		this.init();
		IMap<String, JsonObject> opendataList = this._localDatabusClient.getMap(Client.CIOT_OPENDATA_LIST);

		return opendataList.get(name);
	}

	// subscribe/unsubscribe to open data which is owned by other application
	public void subscribeToLocalOpendata(String name, final IOpendataListener listener) throws Exception {

		if (name == null || "".equals(name))
			throw new Exception("Opendata name must not null string");
		if (listener == null)
			throw new Exception("Event listener must not null");

		this.init();
		IMap<String, JsonObject> opendataList = this._localDatabusClient.getMap(Client.CIOT_OPENDATA_LIST);

		if (!opendataList.containsKey(name)) {
			opendataList.putIfAbsent(name, null);
		}

		MapEventListener l = new MapEventListener(listener);
		opendataList.addEntryListener(l, name, true);
	}

	public void unsubscribeToLocalOpendata() {
		try {

		} catch (Exception e) {

		}
	}

	public void subscribeToGlobalOpendata(String name, final IOpendataListener listener) throws Exception {
		if (name == null || "".equals(name))
			throw new Exception("Opendata name must not null string");
		if (listener == null)
			throw new Exception("Event listener must not null");

		this.init();
		IMap<String, JsonObject> opendataList = this._globalDatabusClient.getMap(Client.CIOT_OPENDATA_LIST);

		if (!opendataList.containsKey(name)) {
			opendataList.putIfAbsent(name, null);
		}

		MapEventListener l = new MapEventListener(listener);
		opendataList.addEntryListener(l, name, true);
	}

	public void unsubscribeToGlobalOpendata() {
		try {

		} catch (Exception e) {

		}
	}

}
