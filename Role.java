package com.miniproj.invision.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import javax.persistence.*;

@Entity
@Table(name = "roles")
public class Role {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer role_id;

	@Enumerated(EnumType.STRING)
	@Column(length = 20)
	private ERoles name;

	public Role() {

	}

	public Role(ERoles name) {
		this.name = name;
	}

	public Integer getId() {
		return role_id;
	}

	public void setId(Integer id) {
		this.role_id = id;
	}

	public ERoles getName() {
		return name;
	}

	public void setName(ERoles name) {
		this.name = name;
	}
}
	

