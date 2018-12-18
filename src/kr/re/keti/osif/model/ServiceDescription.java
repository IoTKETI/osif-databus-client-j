package kr.re.keti.osif.model;
import java.io.IOException;
import java.util.UUID;

import com.hazelcast.com.eclipsesource.json.JsonArray;
import com.hazelcast.com.eclipsesource.json.JsonObject;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;

public class ServiceDescription implements DataSerializable {
	
	protected String _serviceName;
	protected String _serviceId;
	protected VersionCode _versionCode;
	protected Opendata []_localOpendata;
	protected Opendata []_globalOpendata;
	
	
	public ServiceDescription(String _serviceId, String _serviceName, VersionCode _versionCode, Opendata[] _localOpendata, Opendata[] _globalOpendata) {
		super();
		
		System.out.println( "SERVICE ID: " + _serviceId );
		
		
		if(_serviceId == null || "".equals(_serviceId))
			this._serviceId = UUID.randomUUID().toString();
		else
			this._serviceId = _serviceId;
		this._serviceName = _serviceName;
		this._versionCode = _versionCode;
		this._localOpendata = _localOpendata;
		this._globalOpendata = _globalOpendata;
	}
	
	public String get_serviceName() {
		return _serviceName;
	}

	public void set_serviceName(String _serviceName) {
		this._serviceName = _serviceName;
	}

	public String get_serviceId() {
		return _serviceId;
	}

	public void set_serviceId(String _serviceId) {
		this._serviceId = _serviceId;
	}

	public VersionCode get_versionCode() {
		return _versionCode;
	}

	public void set_versionCode(VersionCode _versionCode) {
		this._versionCode = _versionCode;
	}

	/**
	 * @return the _localOpendata
	 */
	public Opendata[] get_localOpendata() {
		return _localOpendata;
	}


	/**
	 * @return the _globalOpendata
	 */
	public Opendata[] get_globalOpendata() {
		return _globalOpendata;
	}

	/**
	 * @param _localOpendata the _localOpendata to set
	 */
	public void set_localOpendata(Opendata[] _localOpendata) {
		this._localOpendata = _localOpendata;
	}


	/**
	 * @param _globalOpendata the _globalOpendata to set
	 */
	public void set_globalOpendata(Opendata[] _globalOpendata) {
		this._globalOpendata = _globalOpendata;
	}
	
	public static Opendata[] opendataFromJson(JsonArray jsonArray) {
		if(jsonArray == null || jsonArray.size() == 0)
			return null;
		else {
			int len = jsonArray.size();
			Opendata[] result = new Opendata[len];
			for(int i=0; i < len; i ++ ) {
				JsonObject jo = jsonArray.get(i).asObject();
				
				result[i] = Opendata.fromJson(jo);
			}
			
			return result;
		}
	}
	
	public static ServiceDescription fromJson(JsonObject jsonObject) {
		if(jsonObject.get("serviceName") == null)
			throw new IllegalArgumentException("Failed on creating new CiotDatabusClient object: missing required options");	
				
		String serviceId = jsonObject.get("serviceId").asString();
		String serviceName = jsonObject.get("serviceName").asString();
		JsonObject versionCodeObj = jsonObject.get("versionCode").asObject();
		VersionCode versionCode = VersionCode.fromJson(versionCodeObj);
		JsonObject opendata = jsonObject.get("openData").asObject();
		Opendata[] localOpendata = null;
		Opendata[] globalOpendata = null;
		
		if(opendata!=null) {

			if(opendata.get("local") != null) {
				JsonArray aryOpendata = opendata.get("local").asArray();
				localOpendata = opendataFromJson(aryOpendata);
			}
			if(opendata.get("global") != null) {
				JsonArray aryOpendata = opendata.get("global").asArray();
				globalOpendata = opendataFromJson(aryOpendata);
			}
		}
		
		return new ServiceDescription(serviceId, serviceName, versionCode, localOpendata, globalOpendata);
	}

	public Opendata[] _readArrayData(ObjectDataInput odi) throws IOException {
		int len = odi.readInt();
		Opendata [] result = new Opendata[len];
		
		for(int i=0; i < len; i++) {
			result[i] = new Opendata("");
			result[i].readData(odi);
		}

		return result;
	}

	public void readData(ObjectDataInput arg0) throws IOException {
		this._serviceId = arg0.readUTF();
		this._serviceName = arg0.readUTF();
		this._versionCode.readData(arg0);
		
		this._localOpendata = _readArrayData(arg0);
		this._globalOpendata = _readArrayData(arg0);
	}

	public void _writeArrayData(Opendata[] data, ObjectDataOutput odo) throws IOException {
		odo.writeInt(data.length);
		for(int i=0; i < data.length; i++)
			data[i].writeData(odo);;
	}

	public void writeData(ObjectDataOutput arg0) throws IOException {
		arg0.writeUTF(this._serviceId);
		arg0.writeUTF(this._serviceName);
		this._versionCode.writeData(arg0);;
		
		_writeArrayData(this._localOpendata, arg0);
		_writeArrayData(this._globalOpendata, arg0);
	}

	public boolean hasLocalOpendata() {
		if(this._localOpendata != null && this._localOpendata.length > 0) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public boolean hasGlobalOpendata() {
		if(this._globalOpendata != null && this._globalOpendata.length > 0) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public Opendata findGlobalAppData(String key) {
		if(this._globalOpendata == null || this._globalOpendata.length == 0) {
			return null;
		}
		else {
			for(int i=0;i < this._globalOpendata.length; i++) {
				if(key.equals( this._globalOpendata[i].get_name() ))
					return this._globalOpendata[i];
			}
			return null;
		}
	}
	public Opendata findLocalAppData(String key) {
		if(this._localOpendata == null || this._localOpendata.length == 0) {
			return null;
		}
		else {
			for(int i=0;i < this._localOpendata.length; i++) {
				if(key.equals( this._localOpendata[i].get_name() ))
					return this._localOpendata[i];
			}
			return null;
		}
	}
}
