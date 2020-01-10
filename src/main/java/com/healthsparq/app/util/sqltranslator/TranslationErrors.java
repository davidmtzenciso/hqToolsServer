package com.healthsparq.app.util.sqltranslator;

public class TranslationErrors {

	public static final String MISSING_TABLE_ANNOTATION_ERROR = "Missing Table annotations in class: ";
	public static final String MISSING_FIELD_ANNOTATION_ERROR = "Missing one of the following annotations: Column, ManyToOne. In field: ";
	public static final String MANY_TO_ONE_VALUE_ERROR = "No value present for ManyToOne relation in field: ";
	public static final String NOT_SUPPORTED_RELATION_ERROR = "Relation OneToMany and OneToOne aren't supported yet!";	
	public static final String NO_VALUE_IN_FIELD_ANNOTATION_ERROR = "No value present in annotation in field: ";
	public static final String NO_VALUE_IN_CLASS_ANNOTATION_ERROR = "No value present in annotation in class: ";
	public static final String NO_VALUE_ERROR = "No value present in class: "; 
	public static final String NOT_SUPPORTED_TYPE_ERROR = "Type not supported: ";
}
