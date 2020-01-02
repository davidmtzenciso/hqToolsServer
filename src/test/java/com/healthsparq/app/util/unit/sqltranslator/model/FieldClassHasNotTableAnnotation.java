package com.healthsparq.app.util.unit.sqltranslator.model;

import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.healthsparq.app.annotations.ForeignKey;

@Table(name="TABLE_NAME")
public class FieldClassHasNotTableAnnotation {
	
	public FieldClassHasNotTableAnnotation() {
		this.field3 = new HasNotTableAnnotation();
	}

	@Column(name="COLUMN_1")
	private String field1;
	
	@Column(name="COLUMN_2")
	private Integer field2;
	
	@ManyToOne(optional=false) 
	@ForeignKey(field="field1")
	private HasNotTableAnnotation field3;

	public String getField1() {
		return field1;
	}

	public void setField1(String field1) {
		this.field1 = field1;
	}

	public Integer getField2() {
		return field2;
	}

	public void setField2(Integer field2) {
		this.field2 = field2;
	}

	public HasNotTableAnnotation getField3() {
		return field3;
	}

	public void setField3(HasNotTableAnnotation field3) {
		this.field3 = field3;
	}
	
	
}
