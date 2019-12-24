package com.healthsparq.app.model.xprod;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="PROMO_TRIGGER", catalog="xproduct")
public class PromoTrigger implements Serializable {

	@Id
	private Integer id;
	
	@ManyToOne(optional=false)
	private Promo promo;
	
	@Column(name="TRIGGER_TYPE", nullable=false, length=30)
	private String type;
	
	@Column(name="TRIGGER_VALUE", nullable=false, length=100)
	private String value;
	
	
	private static final long serialVersionUID = 1L;

}
