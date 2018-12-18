package testcode;

import com.hazelcast.com.eclipsesource.json.Json;
import com.hazelcast.com.eclipsesource.json.JsonObject;

import kr.re.keti.osif.Client;
import kr.re.keti.osif.event.IOpendataListener;

public class DatabusClient {

	
	
	final static String JSON_OPTION = "{\"application\":{\"instanceId\":\"uuid111\",\"appName\":\"appName-111\",\"openData\":{\"local\":{\"name\":\"local111\",\"description\":\"data description\",\"template\":{\"local-key\":\"local-value\"}},\"global\":{\"name\":\"global-111\",\"description\":\"data description\",\"template\":{\"global-key1\":\"globa-value-l111\",\"global-key2\":\"global-value-222\"}}}},\"databus\":{\"global\":{\"host\":\"osif.synctechno.com\",\"port\":\"5701\"},\"local\":{\"host\":\"127.0.0.1\",\"port\":\"5701\"}}}";
	
	
	
	final static String JSON_DATA = "{\"data-name\":{\"data-name-2\":\"value of data-name-2\"}}";

	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		Client c = Client.newClient("./test-src/testcode/osif-service.json");
		Client c2 = Client.newClient("./test-src/testcode/osif-service2.json");
		
		try {
			c.startService();
			c2.startService();
			
			String serviceId = "3fb8800e-edd5-473c-97fa-ce461196aaf3";
		
			
			
			IOpendataListener listener = new IOpendataListener() {

				public void entryAdded(String key, String value) {
					System.out.println("ADDED: " + key + " : " + value);;					
				}
 
				public void entryUpdated(String key, String value) {
					System.out.println("UPDATED: " + key + " : " + value);;					
					
				}
 
				
			};
			
			
			c2.subscribeToGlobalOpendata(serviceId,  "global_data_1", listener);
			
			c.setGlobalAppData("global_data_1", "new value");
			
			
			c.stopApplication();

			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
