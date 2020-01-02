package com.healthsparq.app.util.unit.sqltranslator.model;

import javax.persistence.Column;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.healthsparq.app.annotations.Ignore;

@Table(name="TABLE_NAME")
public class HasUnsupportedRelation {

	@Column(name="COLUMN_1")
	private String field1;
	
	@Ignore
	private Integer field2;
	
	@OneToMany
	private String[] fields;
	
}
