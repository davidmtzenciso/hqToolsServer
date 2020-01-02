package com.healthsparq.app.model.xprod.client;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="CLIENT_SEARCH_CATEGORY")
public class ClientSearchCategorie implements Serializable {

	@Id
	private Integer id;
	
	@Column(name="CLIENT_CODE", nullable=false)
	private String clientCode;
	
	@Column(name="CATEGORY_TYPE", nullable=false)
	private String categoryType;
	
	@Column(name="LABEL_KEY_LANG", nullable=false)
	private String labelKeyLang;
	
	@Column(name="DESCRIPTION_KEY_LANG", nullable=true)
	private String descKeyLang;
	
	@Column(name="CATEGORY_ORDER", nullable=true)
	private Integer categoryOrder;
	
	@Column(name="PARENT_ID", nullable=true)
	private Integer parentId;
	
	private static final long serialVersionUID = 1L;
}
