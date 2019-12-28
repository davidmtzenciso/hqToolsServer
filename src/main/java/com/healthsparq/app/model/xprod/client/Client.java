package com.healthsparq.app.model.xprod.client;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.healthsparq.app.annotations.Ignore;

@Entity
@Table(name="CLIENT", catalog="xproduct")
public class Client implements Serializable {

	@Id
	@Column(name="CLIENT_ID")
	private Integer id;
	
	@Column(name="CLIENT_CODE", length=20, nullable=true)
	private String code;
	
	@Column(name="CLIENT_NAME",length=60, nullable=true)
	private String name;
	
	@Ignore
	private static final long serialVersionUID = 1L;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
