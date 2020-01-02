package com.healthsparq.app.util.unit.sqltranslator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.jboss.logging.Logger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.healthsparq.app.exceptions.MetadataNotPresentException;
import com.healthsparq.app.exceptions.PrimitiveTypeNotSupportedException;
import com.healthsparq.app.exceptions.RelationNotSupportedException;
import com.healthsparq.app.util.SQLTranslator;
import com.healthsparq.app.util.unit.conf.TestConfig;
import com.healthsparq.app.util.unit.sqltranslator.model.*;


@SpringBootTest(classes= {TestConfig.class})
public class ToInsertTest {

	@Autowired
	private SQLTranslator sqlTranslator;
	
	private static final Logger LOG = Logger.getLogger(ToInsertTest.class);
	
	@Test
	public void given_class_is_missing_table_annotation_when_translate_to_sql_then_it_should_fail() {
		var obj = new HasNoTableAnnotation();
		var exception = assertThrows(MetadataNotPresentException.class, () -> {
			sqlTranslator.toInsert(obj);
		});
		
		assertEquals(SQLTranslator.MISSING_TABLE_ANNOTATION_ERROR + obj.getClass().getSimpleName(), exception.getMessage());
	}
	
	@Test
	public void given_class_has_field_with_invalid_type_when_translate_to_sql_then_it_should_fail() {
		assertThrows(PrimitiveTypeNotSupportedException.class, () -> {
			sqlTranslator.toInsert(new HasInvalidFieldType());
		});
	}
	
	@Test
	public void given_class_has_field_without_annotation_when_translate_to_sql_then_it_should_fail() throws NoSuchFieldException, SecurityException {
		var obj = new HasFieldWithoutAnnotation();
		var exception = assertThrows(MetadataNotPresentException.class, () -> {
			sqlTranslator.toInsert(obj);
		});
		
		assertEquals(SQLTranslator.MISSING_FIELD_ANNOTATION_ERROR + 
					 obj.getClass().getDeclaredField("field1").getName(), 
					 exception.getMessage());
	}

	@Test
	public void given_class_has_field_with_unsupported_relation_when_translate_to_sql_then_it_should_fail() {
		var obj = new HasUnsupportedRelation();
		var exception = assertThrows(RelationNotSupportedException.class, () -> {
			sqlTranslator.toInsert(obj);
		});
		
		assertEquals(SQLTranslator.NOT_SUPPORTED_RELATION_ERROR, exception.getMessage());
	}
	
	@Test
	public void given_field_class_has_not_table_annotation_when_translate_to_sql_then_it_should_fail() {
		var obj = new FieldClassHasNoTableAnnotation();
		var exception = assertThrows(MetadataNotPresentException.class, () -> {
			sqlTranslator.toInsert(obj);
		});
		
		assertEquals(SQLTranslator.MISSING_TABLE_ANNOTATION_ERROR + obj.getField3().getClass().getSimpleName(), exception.getMessage());
	}
	
	@Test
	public void given_field_class_has_field_without_annotation_when_translate_to_sql_then_it_should_fail() throws NoSuchFieldException, SecurityException {
		var obj = new FieldClassHasFieldWithoutAnnotation();
		var exception = assertThrows(MetadataNotPresentException.class, () -> {
			sqlTranslator.toInsert(obj);
		});
		
		assertEquals(SQLTranslator.MISSING_FIELD_ANNOTATION_ERROR + 
						obj.getField1().getClass().getDeclaredField("field1").getName(), exception.getMessage());
	}
	
	
	
}
