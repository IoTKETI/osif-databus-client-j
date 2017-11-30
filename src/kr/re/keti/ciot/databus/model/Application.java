package kr.re.keti.ciot.databus.model;
import java.io.IOException;
import java.util.UUID;

import com.hazelcast.com.eclipsesource.json.JsonObject;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;

public class Application implements DataSerializable {
	
	protected String _instanceId;
	protected String _appName;
	protected Opendata _localOpendata;
	protected Opendata _globalOpendata;
	
	
	public Application(String _instanceId, String _appName, Opendata _localOpendata, Opendata _globalOpendata) {
		super();
		
		if(_instanceId == null || "".equals(_instanceId))
			this._instanceId = UUID.randomUUID().toString();
		else
			this._instanceId = _instanceId;
		this._instanceId = _instanceId;
		this._appName = _appName;
		this._localOpendata = _localOpendata;
		this._globalOpendata = _globalOpendata;
	}


	/**
	 * @return the _instanceId
	 */
	public String get_instanceId() {
		return _instanceId;
	}


	/**
	 * @return the _appName
	 */
	public String get_appName() {
		return _appName;
	}


	/**
	 * @return the _localOpendata
	 */
	public Opendata get_localOpendata() {
		return _localOpendata;
	}


	/**
	 * @return the _globalOpendata
	 */
	public Opendata get_globalOpendata() {
		return _globalOpendata;
	}


	/**
	 * @param _instanceId the _instanceId to set
	 */
	public void set_instanceId(String _instanceId) {
		this._instanceId = _instanceId;
	}


	/**
	 * @param _appName the _appName to set
	 */
	public void set_appName(String _appName) {
		this._appName = _appName;
	}


	/**
	 * @param _localOpendata the _localOpendata to set
	 */
	public void set_localOpendata(Opendata _localOpendata) {
		this._localOpendata = _localOpendata;
	}


	/**
	 * @param _globalOpendata the _globalOpendata to set
	 */
	public void set_globalOpendata(Opendata _globalOpendata) {
		this._globalOpendata = _globalOpendata;
	}


	
	
	
	public static Application fromJson(JsonObject jsonObject) {
		if(jsonObject.get("appName") == null)
			throw new IllegalArgumentException("Failed on creating new CiotDatabusClient object: missing required options");	
		
		Application result = null;
		
		String instanceId = jsonObject.get("instanceId").asString();
		String appName = jsonObject.get("appName").asString();
		JsonObject opendata = jsonObject.get("openData").asObject();
		if(opendata==null) {
			result = new Application(instanceId, appName, null, null);
		}
		else {
			Opendata localOpendata = null;
			Opendata globalOpendata = null;
			
			if(opendata.get("local") != null)
				localOpendata = Opendata.fromJson(opendata.get("local").asObject());
			if(opendata.get("global") != null)
				globalOpendata = Opendata.fromJson(opendata.get("global").asObject());
			
			result = new Application(instanceId, appName, localOpendata, globalOpendata);
		}
	
	
		return result;
	}


	public void readData(ObjectDataInput arg0) throws IOException {
		this._instanceId = arg0.readUTF();
		this._appName = arg0.readUTF();
		this._localOpendata.readData(arg0);
		this._globalOpendata.readData(arg0);
	}


	public void writeData(ObjectDataOutput arg0) throws IOException {
		arg0.writeUTF(this._instanceId);
		arg0.writeUTF(this._appName);
		this._localOpendata.writeData(arg0);;
		this._globalOpendata.writeData(arg0);;
		
	}
}
