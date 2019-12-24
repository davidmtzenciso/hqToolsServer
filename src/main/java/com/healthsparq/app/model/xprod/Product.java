package com.healthsparq.app.model.xprod;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="PRODUCT", catalog="xproduct")
public class Product implements Serializable {

	@Id
	@Column(name="PRODUCT_ID")
	private Integer id;
	
	@Column(name="PRODUCT_NAME")
	private String name;
	
	private static final long serialVersionUID = 1L;
}
