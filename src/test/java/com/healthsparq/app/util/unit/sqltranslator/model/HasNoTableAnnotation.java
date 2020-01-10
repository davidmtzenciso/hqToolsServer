package com.healthsparq.app.util.unit.sqltranslator.model;

import javax.persistence.Column;
import javax.persistence.Id;

public class HasNoTableAnnotation {

	@Id
	@Column(name="COLUMN_ID")
	private Integer id;
}
