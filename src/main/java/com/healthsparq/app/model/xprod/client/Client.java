package com.healthsparq.app.model.xprod.client;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

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
	
	private static final long serialVersionUID = 1L;
}
