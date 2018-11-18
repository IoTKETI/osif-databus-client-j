package kr.re.keti.osif.model;

import com.hazelcast.com.eclipsesource.json.JsonObject;

public class DatabusConfig {

	private static String DEFAULT_PORT = "5701";
	
	
	protected String _localHost = "127.0.0.1";;
	protected String _localPort = "5701";;
	protected String _globalHost = "osif.synctechno.com";;
	protected String _globalPort = "5701";;
	
	
	public DatabusConfig(String _globalHost, String _globalPort, String _localPort) {
		super();
		
		this._globalHost = _globalHost;
		this._globalPort = _globalPort;
		
		this._localPort = _localPort;
	}

 
	public String get_localHost() {
		return _localHost;
	}


	public void set_localHost(String _localHost) {
		this._localHost = _localHost;
	}


	public String get_localPort() {
		return _localPort;
	}


	public void set_localPort(String _localPort) {
		this._localPort = _localPort;
	}


	public String get_globalHost() {
		return _globalHost;
	}


	public void set_globalHost(String _globalHost) {
		this._globalHost = _globalHost;
	}


	public String get_globalPort() {
		return _globalPort;
	}


	public void set_globalPort(String _globalPort) {
		this._globalPort = _globalPort;
	}


	public static DatabusConfig fromJson(JsonObject jsonObject) {

		String globalHost = jsonObject.get("globalHost").asString();
		String globalPort = jsonObject.get("globalPort").asString();
		String localPort = jsonObject.get("localPort").asString();
		
		return new DatabusConfig(globalHost, globalPort, localPort);
	}
	
	
	
}
