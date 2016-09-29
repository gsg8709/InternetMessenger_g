package com.msg.bean;

import java.io.Serializable;
import java.util.ArrayList;

public class GroupParent implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String name;

	public ArrayList<Group> groups;

	public GroupParent(String name, ArrayList<Group> gs) {
		super();
		this.name = name;
		this.groups = gs;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<Group> getGroups() {
		return groups;
	}

	public void setGroups(ArrayList<Group> groups) {
		this.groups = groups;
	}

	@Override
	public String toString() {
		return "GroupParent [name=" + name + ", groups=" + groups + "]";
	}
}
