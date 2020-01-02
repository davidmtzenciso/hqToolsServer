package com.healthsparq.app.model.jira;

import java.io.Serializable;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;

import org.springframework.stereotype.Component;

@Component
public class Ticket implements Serializable {

	@NotEmpty
	private String title;
	
	@NotEmpty
	private String type;
	
	@Positive
	private int number;
	@NotNull
	
	@NotEmpty
	@Pattern(regexp = "[0-9]{4}-[0-9]{1,2}-[0-9]{1,2}")
	private String label;
	
	@NotEmpty
	private String customer;
	
	@NotEmpty
	private String author;
	
	@Positive
	private int version;
	
	private static final long serialVersionUID = 1L;
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getCustomer() {
		return customer;
	}
	public void setCustomer(String customer) {
		this.customer = customer;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	
}
