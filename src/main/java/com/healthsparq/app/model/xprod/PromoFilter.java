package com.healthsparq.app.model.xprod;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="PROMO_FILTER")
public class PromoFilter implements Serializable {

	@Id
	private Integer id;
	
	@ManyToOne
	private Promo promo;
	
	@Column(name="FILTER_VALUE", nullable=false)
	private String value;
	
	private static final long serialVersionUID = 1L;
}
