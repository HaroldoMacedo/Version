package com.versioning.test;

import com.versioning.entity.Entity;
import com.versioning.entity.VersionEntity;

@VersionEntity(name="Employee", version=1)
public class EmployeeV1 implements Entity {

	private int id;
	private String fullName;
	private String email;
	private String phone;
	private int	status;
	
	public EmployeeV1(int id) {
	  this.id = id;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}

}
