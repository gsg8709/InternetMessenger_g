package com.msg.bean;

import java.io.Serializable;
import java.util.ArrayList;

public class ContactGroup implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Long id;

	public String name;

	public int count;

	public boolean check;

	public ArrayList<Contacts> contacts;

	public ContactGroup(Long id, String name, int count,
			ArrayList<Contacts> contacts) {
		this.id = id;
		this.name = name;
		this.count = count;
		this.contacts = contacts;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public boolean isCheck() {
		return check;
	}

	public void setCheck(boolean check) {
		this.check = check;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public ArrayList<Contacts> getContacts() {
		return contacts;
	}

	public void setContacts(ArrayList<Contacts> contacts) {
		this.contacts = contacts;
	}

	@Override
	public String toString() {
		return "ContactGroup [name=" + name + ", count=" + count
				+ ", contacts=" + contacts + "]";
	}
}
