package com.miniproj.invision.payload.response;

import java.time.LocalDate;

public class ReportResponse {
	
	String username;
	String title;
	String status;
	String mail_sent_on;
	String accepted_on;
	
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMail_sent_on() {
		return mail_sent_on;
	}

	public void setMail_sent_on(String mail_sent_on) {
		this.mail_sent_on = mail_sent_on;
	}

	public String getAccepted_on() {
		return accepted_on;
	}

	public ReportResponse(String username, String title, String status, String mail_sent_on, String accepted_on) {
		this.username = username;
		this.title = title;
		this.status = status;
		this.mail_sent_on = mail_sent_on;
		this.accepted_on = accepted_on;
	}

	public void setAccepted_on(String accepted_on) {
		this.accepted_on = accepted_on;
	}


	public ReportResponse() {}
}
