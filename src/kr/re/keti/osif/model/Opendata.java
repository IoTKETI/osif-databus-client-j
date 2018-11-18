package kr.re.keti.osif.model;

import java.io.IOException;
import java.util.Map;

import com.hazelcast.com.eclipsesource.json.JsonObject;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;

public class Opendata implements DataSerializable {

	protected String _name;
	protected String _description;
	protected String _template;
	
	
	public Opendata(String _name, String _description, String _template) {
		super();
		this._name = _name;
		this._description = _description;
		this._template = _template;
	}
	
	
	public Opendata(String _name, String _description) {
		super();
		this._name = _name;
		this._description = _description;
		this._template = null;
	}
	
	public Opendata(String _name) {
		super();
		this._name = _name;
		this._description = "";
		this._template = null;
	}


	/**
	 * @return the _name
	 */
	public String get_name() {
		return _name;
	}


	/**
	 * @return the _description
	 */
	public String get_description() {
		return _description;
	}


	/**
	 * @return the _template
	 */
	public String get_template() {
		return _template;
	}


	/**
	 * @param _name the _name to set
	 */
	public void set_name(String _name) {
		this._name = _name;
	}


	/**
	 * @param _description the _description to set
	 */
	public void set_description(String _description) {
		this._description = _description;
	}


	/**
	 * @param _template the _template to set
	 */
	public void set_template(String _template) {
		this._template = _template;
	}


	public static Opendata fromJson(JsonObject jsonObject) {

		if(jsonObject.get("name") == null)
			throw new IllegalArgumentException("Failed on creating new CiotDatabusClient object: missing required options");	
				
		String name = jsonObject.get("name").asString();
		String description = jsonObject.get("description").asString();
		String template = jsonObject.get("template").asString();	
		
		return new Opendata(name, description, template);
	}


	public void readData(ObjectDataInput arg0) throws IOException {
		this._name = arg0.readUTF();
		this._description = arg0.readUTF();
		this._template = arg0.readUTF();
	}


	public void writeData(ObjectDataOutput arg0) throws IOException {
		arg0.writeUTF(this._name);
		arg0.writeUTF(this._description);
		arg0.writeUTF(this._template);
	}
	
	
}
