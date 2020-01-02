package com.healthsparq.app.model.xprod.client;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="CLIENT_TIER_CONFGR")
public class ClientTierConfgr implements Serializable {

	@Id
	private Integer id;
	
	@ManyToOne(optional=false)
	private Client client;
	
	@Column(name="TIER_NUM", nullable=false)
	private String tierNum;
	
	@Column(name="TIER_NAME", nullable=false)
	private String tierName;
	
	@Column(name="TIER_DESCRIPTION", nullable=true)
	private String tierDescription;
	
	@Column(name="TIER_ICON", nullable=true)
	private String tierIcon;
	
	@Column(name="PRODUCT_CODE", nullable=true)
	private String productCode;
	
	private static final long serialVersionUID = 1L;
}
