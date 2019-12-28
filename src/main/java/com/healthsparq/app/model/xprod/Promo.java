package com.healthsparq.app.model.xprod;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.healthsparq.app.model.xprod.client.Client;

@Entity
@Table(name="PROMO", catalog="xproduct")
public class Promo implements Serializable {

	@Id
	@Column(name="PROMO_ID")
	private Integer id;
	
	@Column(name="PROMO_KEY", nullable=false, length=55)
	private String key;
	
	@ManyToOne(optional=true)
	private Client client;
	
	@Column(name="PRECEDENCE", nullable=false)
	private Integer precedence;
	
	@Column(name="SORT_TYPE", nullable=true, length=30)
	private String sortType;
	
	@Column(name="SHOW_PROMO_IN_PDF", nullable=false)
	private Integer showInPdf;
	
	private static final long serialVersionUID = 1L;

}
