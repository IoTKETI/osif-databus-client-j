package testcode;

import com.hazelcast.com.eclipsesource.json.Json;
import com.hazelcast.com.eclipsesource.json.JsonObject;

import kr.re.keti.ciot.databus.Client;

public class DatabusClient {

	
	
	final static String JSON_OPTION = "{\"application\":{\"instanceId\":\"uuid111\",\"appName\":\"appName-111\",\"openData\":{\"local\":{\"name\":\"local111\",\"description\":\"data description\",\"template\":{\"local-key\":\"local-value\"}},\"global\":{\"name\":\"global-111\",\"description\":\"data description\",\"template\":{\"global-key1\":\"globa-value-l111\",\"global-key2\":\"global-value-222\"}}}},\"databus\":{\"global\":{\"host\":\"dev.synctechno.com\",\"port\":\"5701\"},\"local\":{\"host\":\"127.0.0.1\",\"port\":\"5701\"}}}";
	
	
	
	final static String JSON_DATA = "{\"data-name\":{\"data-name-2\":\"value of data-name-2\"}}";

	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		JsonObject options = Json.parse(DatabusClient.JSON_OPTION).asObject();
		
		Client c = Client.newClient(options);
		
		try {
			c.startApplication();
			
			c.stopApplication();

			JsonObject jsonData = Json.parse(DatabusClient.JSON_DATA).asObject();

			c.setGlobalAppData(jsonData);
			
			JsonObject obj = c.getGlobalAppData();
			
			System.out.println(obj.toString());
			
			
			
			
			
			
			
			
			
			
			
			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
