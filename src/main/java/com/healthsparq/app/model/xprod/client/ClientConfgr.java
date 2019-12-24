package com.healthsparq.app.model.xprod.client;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.healthsparq.app.model.xprod.Product;

@Entity
@Table(name="CLIENT_CONFGR", catalog="xproduct")
public class ClientConfgr implements Serializable {

	@Id
	private Integer id;
	
	@ManyToOne(optional=false)
	private Client client;
	
	@ManyToOne(optional=false)
	private Product product;
	
	@Column(name="CONFGR_KEY", nullable=false)
	private String confgrKey;
	
	@Column(name="CONFGR_VAL", nullable=true)
	private String confgrVal;
	
	@Column(name="DEFAULT_IND", nullable=false)
	private String defaultInd;
	
	@Column(name="CUSTOM_EXPRESSION", nullable=true)
	private String customExpression;
	
	@Column(name="PRECEDENCE", nullable=true)
	private Integer precedence;
	
	private static final long serialVersionUID = 1L;
	
}
