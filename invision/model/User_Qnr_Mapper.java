package com.miniproj.invision.model;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "user_qnr_mapper")
public class User_Qnr_Mapper {

	public User_Qnr_Mapper(String emp_num, Integer q_id, boolean status,
			LocalDate date_accepted) {
		this.emp_num = emp_num;
		this.q_id = q_id;
		this.status = status;
		this.date_accepted = date_accepted;
	}
	
	public User_Qnr_Mapper(String emp_num, Integer q_id, boolean status) {
		this.emp_num = emp_num;
		this.q_id = q_id;
		this.status = status;
	}
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer map_id;
	
	private String emp_num;
	private Integer q_id;
	private boolean status;
	private LocalDate date_mail_sent;
	private LocalDate date_accepted;
	
	
	public Integer getMap_id() {
		return map_id;
	}
	public void setMap_id(Integer map_id) {
		this.map_id = map_id;
	}
	public String getEmp_num() {
		return emp_num;
	}
	public void setEmp_num(String emp_num) {
		this.emp_num = emp_num;
	}
	public Integer getQ_id() {
		return q_id;
	}
	public void setQ_id(Integer q_id) {
		this.q_id = q_id;
	}
	public boolean isStatus() {
		return status;
	}
	public void setStatus(boolean status) {
		this.status = status;
	}
	public LocalDate getDate_mail_sent() {
		return date_mail_sent;
	}
	public void setDate_mail_sent(LocalDate date_mail_sent) {
		this.date_mail_sent = date_mail_sent;
	}
	public LocalDate getDate_accepted() {
		return date_accepted;
	}
	public void setDate_accepted(LocalDate date_accepted) {
		this.date_accepted = date_accepted;
	}
	
	public User_Qnr_Mapper() {}
	
	/*
	 * [{
    "emp_num":"RT010101",
    "username":"riya",
    "email":"adigaplnr64@gmail.com"
},
{
     "emp_num":"RT005",
    "username":"sreelakshmi",
    "email":"sreelakshmi.ap@robosoftincom"
}
]
	 */
}
