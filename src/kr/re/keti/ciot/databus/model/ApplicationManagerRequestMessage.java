package kr.re.keti.ciot.databus.model;

import java.io.IOException;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;

public class ApplicationManagerRequestMessage implements DataSerializable {

	public static final String START = "start";
	public static final String STOP = "stop";
	
	
	
	protected String _state;
	protected String _instanceId;
	protected Application _instance;
	
	public ApplicationManagerRequestMessage(String _state, Application _instance) {
		super();
		
		this._state = _state;
		this._instanceId = _instance.get_instanceId();
		this._instance = _instance;
	}
	/**
	 * @return the _state
	 */
	public String get_state() {
		return _state;
	}
	/**
	 * @return the _instance
	 */
	public String get_instancIde() {
		return _instanceId;
	}
	/**
	 * @return the _instance
	 */
	public Application get_instance() {
		return _instance;
	}
	
	public void readData(ObjectDataInput arg0) throws IOException {
		this._state = arg0.readUTF();
		this._instanceId = arg0.readUTF();
		this._instance.readData(arg0);
	}
	
	public void writeData(ObjectDataOutput arg0) throws IOException {
		arg0.writeUTF(this._state);
		arg0.writeUTF(this._instanceId);
		this._instance.writeData(arg0);
	}
	
	
}
