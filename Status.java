package com.miniproj.invision.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


@Entity
@Table(name = "status")
public class Status {
	
	public Status(Integer s_id, Employees employee, Questionnaire qnr, boolean status) {
		this.s_id = s_id;
		this.employee = employee;
		this.qnr = qnr;
		this.status = status;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer s_id;
	
	   @ManyToOne(fetch = FetchType.LAZY, optional = false)
	    @JoinColumn(name = "emp_num", nullable = false)
	   private Employees employee;
	
	   @ManyToOne(fetch = FetchType.LAZY, optional = false)
	    @JoinColumn(name = "q_id", nullable = false)
	   private Questionnaire qnr;
	   
	   private boolean status;

	public Integer getS_id() {
		return s_id;
	}

	public void setS_id(Integer s_id) {
		this.s_id = s_id;
	}

	public Employees getEmployee() {
		return employee;
	}

	public void setEmployee(Employees employee) {
		this.employee = employee;
	}

	public Questionnaire getQnr() {
		return qnr;
	}

	public void setQnr(Questionnaire qnr) {
		this.qnr = qnr;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}
	
	public Status() {}

}
