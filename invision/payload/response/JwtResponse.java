package com.miniproj.invision.payload.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class JwtResponse {
	private String token;
	private String type = "Bearer";
	private String emp_num;
	private String username;
	@JsonIgnore
	private String email;
	@JsonIgnore
	private List<String> roles;
	private String image_path;

	public JwtResponse(String accessToken, String emp_num, String username,String email,List<String> roles, String image_path) {
		this.token = accessToken;
		this.emp_num = emp_num;
		this.username = username;
		this.email = email;
		this.roles = roles;
		this.image_path = image_path;
	}

	public String getAccessToken() {
		return token;
	}

	public void setAccessToken(String accessToken) {
		this.token = accessToken;
	}

	public String getTokenType() {
		return type;
	}

	public void setTokenType(String tokenType) {
		this.type = tokenType;
	}

	public String getId() {
		return emp_num;
	}

	public void setId(String id) {
		this.emp_num = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public List<String> getRoles() {
		return roles;
	}
	
	public String getImage_path() {
		return image_path;
	}

	public void setImage_path(String image_path) {
		this.image_path = image_path;
	}
}
