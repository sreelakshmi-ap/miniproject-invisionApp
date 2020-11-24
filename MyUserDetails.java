package com.miniproj.invision.security;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.miniproj.invision.model.*;
 
import java.util.Collection;
import java.util.List;
import java.util.Objects;


public class MyUserDetails implements UserDetails {
	private static final long serialVersionUID = 1L;

	private String emp_num;

	private String username;
	@JsonIgnore
	private String email;
	private String image_path;
	@JsonIgnore
	private String password;

	private Collection<? extends GrantedAuthority> authorities;

	public MyUserDetails(String id, String username, String email,String image_path, String password,
			Collection<? extends GrantedAuthority> authorities) {
		this.emp_num = id;
		this.username = username;
		this.email = email;
		this.password = password;
		this.authorities = authorities;
		this.image_path = image_path;
	}

	public static MyUserDetails build(Employees user) {
		List<GrantedAuthority> authorities = user.getRoles().stream()
				.map(role -> new SimpleGrantedAuthority(role.getName().name()))
				.collect(Collectors.toList());

		return new MyUserDetails(
				user.getEmp_num(), 
				user.getUsername(), 
				user.getEmail(),
				user.getImage_path(),
				user.getPassword(), 
				authorities);
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	public String getEmp_num() {
		return emp_num;
	}

	public String getEmail() {
		return email;
	}

	public String getImage_path() {
		return image_path;
	}


	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		MyUserDetails user = (MyUserDetails) o;
		return Objects.equals(emp_num, user.emp_num);
	}
}

 
