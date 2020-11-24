package com.miniproj.invision.payload.response;

public class AdminsResponse {
	public AdminsResponse(String emp_num, String username, String email) {
		this.emp_num = emp_num;
		this.username = username;
		this.email = email;
	}

	String emp_num;
	String username;
	String email;
	
	public String getEmp_num() {
		return emp_num;
	}
	public void setEmp_num(String emp_num) {
		this.emp_num = emp_num;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	public AdminsResponse() {}

}
