package com.miniproj.invision.model;

import java.time.LocalDate;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "questionnaire")
public class Questionnaire {

	public Questionnaire(Integer q_id, String title, String description,
			String button_text, String button_title, String checkbox_text, LocalDate start_date, LocalDate end_date,
			String mail_body) {
		super();
		
		this.title = title;
		this.description = description;
		this.button_text = button_text;
		this.button_title = button_title;
		this.checkbox_text = checkbox_text;
		this.start_date = start_date;
		this.end_date = end_date;
		this.mail_body = mail_body;
	}
	
	public Questionnaire(Integer q_id, String title, String description,
			String button_text, String button_title, String checkbox_text,
			String mail_body) {
		super();
		
		this.title = title;
		this.description = description;
		this.button_text = button_text;
		this.button_title = button_title;
		this.checkbox_text = checkbox_text;
		this.mail_body = mail_body;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer q_id;
	
	private String title;
	private String description;
	private String ppt_path;
	private String button_text;
	private String button_title;
	private String checkbox_text;
	private LocalDate start_date;
	private LocalDate end_date;
	private String mail_body;
	
	public Integer getQ_id() {
		return q_id;
	}
	public void setQ_id(Integer q_id) {
		this.q_id = q_id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getPpt_path() {
		return ppt_path;
	}
	public void setPpt_path(String ppt_path) {
		this.ppt_path = ppt_path;
	}
	public String getButton_text() {
		return button_text;
	}
	public void setButton_text(String button_text) {
		this.button_text = button_text;
	}
	public String getButton_title() {
		return button_title;
	}
	public void setButton_title(String button_title) {
		this.button_title = button_title;
	}
	public String getCheckbox_text() {
		return checkbox_text;
	}
	public void setCheckbox_text(String checkbox_text) {
		this.checkbox_text = checkbox_text;
	}
	public LocalDate getStart_date() {
		return start_date;
	}
	public void setStart_date() {
		this.start_date = LocalDate.now();
	}
	public LocalDate getEnd_date() {
		return end_date;
	}
	public void setEnd_date(LocalDate end_date) {
		this.end_date = LocalDate.ofEpochDay(40);
	}
	public String getMail_body() {
		return mail_body;
	}
	public void setMail_body(String mail_body) {
		this.mail_body = mail_body;
	}

	public Questionnaire() {}
}
