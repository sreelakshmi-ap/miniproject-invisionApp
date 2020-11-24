package com.miniproj.invision.payload.response;

public class PasswordResponse {
	public PasswordResponse(String password) {
		this.password = password;
	}

	String password;

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
