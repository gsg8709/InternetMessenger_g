package com.msg.bean;

/**
 * 机器通讯录组
 * 
 * @author Chris
 * 
 */
public class Group {

	public Group(Long id, String name) {
		// TODO Auto-generated constructor stub
		this.id = id;
		this.name = name;
	}

	Long id;
	String name;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Group [id=" + id + ", name=" + name + "]";
	}

}
