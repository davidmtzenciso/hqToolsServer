package com.healthsparq.app.util.unit.sqltranslator.model;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


@Table(name="TABLE_NAME")
public class FieldClassHasFieldWithoutAnnotation {
	
	public FieldClassHasFieldWithoutAnnotation() {
		this.field1 = new HasFieldWithoutAnnotation();
	}

	@Id
	@Column(name="ID")
	private Integer id;
	
	@ManyToOne(optional=false)
	private HasFieldWithoutAnnotation field1;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public HasFieldWithoutAnnotation getField1() {
		return field1;
	}

	public void setField1(HasFieldWithoutAnnotation field1) {
		this.field1 = field1;
	}
}
