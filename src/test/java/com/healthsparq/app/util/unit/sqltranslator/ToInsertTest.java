package com.healthsparq.app.util.unit.sqltranslator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.jboss.logging.Logger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.healthsparq.app.exceptions.MetadataNotPresentException;
import com.healthsparq.app.exceptions.PrimitiveTypeNotSupportedException;
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
		var obj = new ClassWithoutTableAnnotation();
		var exception = assertThrows(MetadataNotPresentException.class, () -> {
			sqlTranslator.toInsert(obj);
		});
		
		assertEquals(SQLTranslator.MISSING_TABLE_ANNOTATION_ERROR + obj.getClass().getSimpleName(), exception.getMessage());
	}
	
	@Test
	public void given_class_has_field_with_invalid_type_when_translate_to_sql_then_it_should_fail() {
		assertThrows(PrimitiveTypeNotSupportedException.class, () -> {
			sqlTranslator.toInsert(new ClassWithInvalidFieldType());
		});
	}
	
	@Test
	public void given_class_has_field_without_annotation_when_translate_to_sql_then_it_should_fail() throws NoSuchFieldException, SecurityException {
		var obj = new ClassWithFieldWithoutAnnotation();
		var exception = assertThrows(MetadataNotPresentException.class, () -> {
			sqlTranslator.toInsert(obj);
		});
		
		assertEquals(SQLTranslator.MISSING_FIELD_ANNOTATION_ERROR + 
					 obj.getClass().getDeclaredField("field1").getName(), 
					 exception.getMessage());
	}

	@Test
	public void given_class_has_field_with_many_to_one_but_no_foreign_key_annotation_when_translate_to_sql_then_it_should_fail() throws NoSuchFieldException, SecurityException {
		var obj = new ClassWithManyToOneButNoForeignKey();
		var exception = assertThrows(MetadataNotPresentException.class, () -> {
			sqlTranslator.toInsert(obj);
		});
		
		assertEquals(SQLTranslator.MISSING_FOREIGN_KEY_ANNOTATION_ERROR + 
					 obj.getClass().getDeclaredField("field1").getName(), 
					 exception.getMessage());
	}
}
