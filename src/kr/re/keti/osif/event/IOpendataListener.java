package kr.re.keti.osif.event;

public interface IOpendataListener  {

    public void entryAdded(String key, String value);
    public void entryUpdated(String key, String value); 
}
