package kr.re.keti.ciot.databus.model;

import com.hazelcast.com.eclipsesource.json.JsonObject;

public class DatabusConfig {

	private static String DEFAULT_PORT = "5701";
	
	
	protected String _host;
	protected String _port;
	
	
	public DatabusConfig(String _host, String _port) {
		super();
		
		this._host = _host;
		this._port = _port;
		
		if(_port == null)
			this._port = DEFAULT_PORT;
	}
	
	public DatabusConfig(String _host) {
		super();
		
		this._host = _host;
		this._port = DEFAULT_PORT;
	}

	/**
	 * @return the _host
	 */
	public String get_host() {
		return _host;
	}

	/**
	 * @return the _port
	 */
	public String get_port() {
		return _port;
	}

	public static DatabusConfig fromJson(JsonObject jsonObject) {
		if(jsonObject.get("host") == null)
			throw new IllegalArgumentException("Failed on creating new CiotDatabusClient object: missing required options");	
		if(jsonObject.get("port") == null)
			throw new IllegalArgumentException("Failed on creating new CiotDatabusClient object: missing required options");	
				
		String host = jsonObject.get("host").asString();
		String port = jsonObject.get("port").asString();
		
		return new DatabusConfig(host, port);
	}
	
	
	
}
