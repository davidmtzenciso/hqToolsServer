package com.healthsparq.app.util.sqltranslator;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.healthsparq.app.exceptions.MetadataNotPresentException;
import com.healthsparq.app.exceptions.NoValuePresentException;
import com.healthsparq.app.exceptions.PrimitiveTypeNotSupportedException;
import com.healthsparq.app.exceptions.RelationNotSupportedException;

public abstract class AbstractSQLTranslator {

	protected Table getAnnotation(final Class<?> cls) throws NoValuePresentException, MetadataNotPresentException {
		Table table;
		
		if(cls.isAnnotationPresent(Table.class)) {
			table = cls.getAnnotation(Table.class);
			if(table.name().isEmpty()) {
				throw new NoValuePresentException(TranslationErrors.NO_VALUE_IN_CLASS_ANNOTATION_ERROR + cls.getSimpleName());
			}
		} else {
			throw new MetadataNotPresentException(TranslationErrors.MISSING_TABLE_ANNOTATION_ERROR + cls.getSimpleName());
		}
		return table;
	}
	
	protected Annotation getAnnotation(final Field field) throws RelationNotSupportedException, 
														MetadataNotPresentException, NoValuePresentException {
		
		if(field.isAnnotationPresent(OneToMany.class) || field.isAnnotationPresent(OneToOne.class)) {
			throw new RelationNotSupportedException(TranslationErrors.NOT_SUPPORTED_RELATION_ERROR);
		} else if(field.isAnnotationPresent(Column.class)) {
			if(field.getAnnotation(Column.class).name().isEmpty()) {
				throw new NoValuePresentException(TranslationErrors.NO_VALUE_IN_FIELD_ANNOTATION_ERROR + field.getName());
			} else {
				return field.getAnnotation(Column.class);
			}
		} else if(field.isAnnotationPresent(ManyToOne.class)) {
			return  field.getAnnotation(ManyToOne.class);
		} else {
			throw new MetadataNotPresentException(TranslationErrors.MISSING_FIELD_ANNOTATION_ERROR + field.getName());
		}
	}
	
	protected Map<Field, Object> findFieldsWithValue(Class<?> cls, final Object target) throws 
																						ReflectiveOperationException, 
																						NoValuePresentException, 
																						PrimitiveTypeNotSupportedException {
		Object value;
		Map<Field, Object> fields = new HashMap<>();
		
		for (Field field :  cls.getDeclaredFields()) {
			if (!Modifier.isStatic(field.getModifiers())) {
				value = this.getValueFromColumn(field, target, cls);
				if (value != null) {
				fields.put(field, value);
				}
			}
		}
				
		if (fields.isEmpty()) {
			throw new NoValuePresentException(TranslationErrors.NO_VALUE_ERROR + cls.getSimpleName());
		} else {
			return fields;
		}
	}
	
	protected Field findIdField(Class<?> cls) {
		for(Field field : cls.getDeclaredFields()) {
			if (field.isAnnotationPresent(Id.class)) {
				return field;
			}
		}
		return null;
	}
	
	protected String getValueFromColumn(Field field, Object target, Class<?> cls) throws 
																					PrimitiveTypeNotSupportedException, 
																					ReflectiveOperationException {
		var builder = new StringBuilder();
		var value = cls.getMethod(getMethodName(field.getName())).invoke(target);
		
		if(field.getType().getSimpleName().equals(String.class.getSimpleName())) {
			builder.append("'").append(value).append("'");
		} else if(field.getType().getSimpleName().equals(Integer.class.getSimpleName())) {
			builder.append(value);
		} else {
			throw new PrimitiveTypeNotSupportedException(TranslationErrors.NOT_SUPPORTED_TYPE_ERROR+ field.getType().getSimpleName());
		}
		return builder.toString();
	}
	
	protected String getMethodName(String fieldName) {
		return "get" + String.valueOf(fieldName.charAt(0)).toUpperCase() + fieldName.substring(1, fieldName.length());
	}
	
	protected String getColumnName(final Field field, final Annotation annotation) throws ReflectiveOperationException,
																						MetadataNotPresentException, 
																						RelationNotSupportedException, 
																						NoValuePresentException {
		Field innerField;
		
		if(annotation instanceof Column) {
			return ((Column) annotation).name();
		} else if(annotation instanceof ManyToOne) {
			innerField = findIdField(field.getType());
			return getColumnName(innerField, getAnnotation(innerField));
		} else {
			return null;
		}
	}
}
