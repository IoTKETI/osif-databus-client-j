package kr.re.keti.ciot.databus.event;

import com.hazelcast.com.eclipsesource.json.JsonObject;

public interface IOpendataListener  {

    public void entryAdded(JsonObject value);
    public void entryRemoved(JsonObject value); 
    public void entryUpdated(JsonObject value); 
    public void entryEvicted(JsonObject value); 
}
