package icai.dtc.isw.domain;

import java.io.Serializable;

public class Customer implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id;
	private String name;
	private String email;

	public Customer() {
		this.setId(new String());
		this.setName(new String());
		this.setEmail(new String());
	}

	public Customer(String id, String name) {
		this.setName(name);
		this.setId(id);
	}

	public Customer(String id, String name, String email) {
		this.setId(id);
		this.setName(name);
		this.setEmail(email);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	
}
