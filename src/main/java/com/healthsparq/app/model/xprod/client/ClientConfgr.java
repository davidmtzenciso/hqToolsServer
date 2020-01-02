package com.healthsparq.app.model.xprod.client;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.healthsparq.app.model.xprod.Product;

@Entity
@Table(name="CLIENT_CONFGR")
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
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public String getConfgrKey() {
		return confgrKey;
	}

	public void setConfgrKey(String confgrKey) {
		this.confgrKey = confgrKey;
	}

	public String getConfgrVal() {
		return confgrVal;
	}

	public void setConfgrVal(String confgrVal) {
		this.confgrVal = confgrVal;
	}

	public String getDefaultInd() {
		return defaultInd;
	}

	public void setDefaultInd(String defaultInd) {
		this.defaultInd = defaultInd;
	}

	public String getCustomExpression() {
		return customExpression;
	}

	public void setCustomExpression(String customExpression) {
		this.customExpression = customExpression;
	}

	public Integer getPrecedence() {
		return precedence;
	}

	public void setPrecedence(Integer precedence) {
		this.precedence = precedence;
	}
	
}
