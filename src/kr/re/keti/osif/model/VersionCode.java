package kr.re.keti.osif.model;

import java.io.IOException;

import com.hazelcast.com.eclipsesource.json.JsonObject;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;

public class VersionCode implements DataSerializable {

	protected int _major;
	protected int _minor;
	protected int _revision;
	
	
	public VersionCode(int _major, int _minor, int _revision) {
		super();
		this._major = _major;
		this._minor = _minor;
		this._revision = _revision;
	}
	
	
	public VersionCode(int _major, int _minor) {
		super();
		this._major = _major;
		this._minor = _minor;
		this._revision = 0;
	}
	
	public VersionCode(int _major) {
		super();
		this._major = _major;
		this._minor = 0;
		this._revision = 0;
	}


	/**
	 * @return the _major
	 */
	public int get_major() {
		return _major;
	}


	/**
	 * @return the _minor
	 */
	public int get_minor() {
		return _minor;
	}


	/**
	 * @return the _revision
	 */
	public int get_revision() {
		return _revision;
	}


	/**
	 * @param _major the _major to set
	 */
	public void set_major(int _major) {
		this._major = _major;
	}


	/**
	 * @param _minor the _minor to set
	 */
	public void set_minor(int _minor) {
		this._minor = _minor;
	}


	/**
	 * @param _revision the _revision to set
	 */
	public void set_revision(int _revision) {
		this._revision = _revision;
	}


	public static VersionCode fromJson(JsonObject jsonObject) {

		if(jsonObject.get("major") == null)
			throw new IllegalArgumentException("Failed on creating new VersionCode object: missing required options");	
				
		int major = jsonObject.get("major").asInt();
		int minor = jsonObject.get("minor").asInt();
		int revision = jsonObject.get("revision").asInt();
		
		return new VersionCode(major, minor, revision);
	}


	public void readData(ObjectDataInput arg0) throws IOException {
		this._major = arg0.readInt();
		this._minor = arg0.readInt();
		this._revision = arg0.readInt();
	}


	public void writeData(ObjectDataOutput arg0) throws IOException {
		arg0.writeInt(this._major);
		arg0.writeInt(this._minor);
		arg0.writeInt(this._revision);
	}
	
	
}
