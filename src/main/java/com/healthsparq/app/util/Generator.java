package com.healthsparq.app.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.stereotype.Component;

import com.healthsparq.app.annotations.ForeignKey;
import com.healthsparq.app.exceptions.MetadataNotPresentException;
import com.healthsparq.app.exceptions.NoValuePresentException;
import com.healthsparq.app.exceptions.PrimitiveTypeNotSupportedException;
import com.healthsparq.app.exceptions.RelationNotSupportedException;

@Component
public class Generator {
	
	public String sqlInsertStmt(Object obj) throws NoSuchFieldException, SecurityException, 
													MetadataNotPresentException, IllegalAccessException, 
													IllegalArgumentException, InvocationTargetException, 
													NoSuchMethodException, PrimitiveTypeNotSupportedException, 
													NoValuePresentException, RelationNotSupportedException {
		var cls = obj.getClass();
		var fields = Arrays.asList(cls.getDeclaredFields()).stream().sorted().collect(Collectors.toList());
		
		return 
			new StringBuilder()
				.append("INSERT INTO ").append(cls.getAnnotation(Table.class).name())
				.append(getColumns(fields)).append("\nVALUES( ").append(getValues(cls, fields, obj)).toString();
	}
	
	private String getColumns(List<Field> fields) throws NoSuchFieldException, SecurityException, MetadataNotPresentException {
		var builder = new StringBuilder();
		
		if(fields.size() == 1) {
			return builder.append(getColumnName(fields.get(0))).toString();
		} else {
			for(int i=0; i < fields.size(); i++) {
				builder.append(getColumnName(fields.get(i)));
				if(i < fields.size()-1) {
					builder.append(", ");
				}
			}
			return builder.toString();
		}
	}
	
	private String getColumnName(Field field) throws NoSuchFieldException, SecurityException, MetadataNotPresentException {
		Class<?> cls;
		
		if(field.isAnnotationPresent(Column.class)) {
			return field.getAnnotation(Column.class).name();
		}
		else if(field.isAnnotationPresent(ManyToOne.class)) {
			cls = field.getClass();
			if(cls.isAnnotationPresent(ForeignKey.class)) {
				return getColumnName(cls.getDeclaredField(cls.getAnnotation(ForeignKey.class).field()));
			} else {
				throw new MetadataNotPresentException("Missing MainField annotation in class: " + cls.getSimpleName());
			}
		} else {
			throw new MetadataNotPresentException("Missing one of the following annotations: Column, OneToMany, ManyToOne. In field: " + field.getName()) ;
		}
	}
	
	private String getMethodName(String fieldName) {
		return String.valueOf(fieldName.charAt(0)).toUpperCase() + fieldName.substring(1, fieldName.length());
	}
	
	private String getValues(Class<?> cls, List<Field>  fields, Object target) throws IllegalAccessException, IllegalArgumentException, 
																					InvocationTargetException, NoSuchMethodException, 
																					SecurityException, PrimitiveTypeNotSupportedException, 
																					NoSuchFieldException, NoValuePresentException, 
																					RelationNotSupportedException, MetadataNotPresentException {
		var builder = new StringBuilder();
		
		for(Field field : fields) {
			if(field.isAnnotationPresent(Column.class)) {
				builder.append(getValueFromColumn(field, target, cls));
			} else if(field.isAnnotationPresent(ManyToOne.class)) {
				if(field.isAnnotationPresent(ForeignKey.class))
				builder.append(getValueFromRelation(field.getType(), 
									cls.getMethod(getMethodName(field.getName())).invoke(target),
									field.getAnnotation(ForeignKey.class).field()));
			} else {
				throw new RelationNotSupportedException("Relation OneToMany and OneToOne aren't supported yet!");
			}
		}
		
		return builder.toString();
	}
	
	private String getValueFromRelation(Class<?> cls, Object target, String foreingFieldName) throws NoSuchFieldException, SecurityException, 
																		IllegalAccessException, IllegalArgumentException, 
																		InvocationTargetException, NoSuchMethodException, 
																		NoValuePresentException, MetadataNotPresentException {
		var builder = new StringBuilder();
		var foreignField = cls.getDeclaredField(foreingFieldName);
		var fieldWithValue  = findField(cls, target);
		
		if(cls.isAnnotationPresent(Table.class)) {	
			if(fieldWithValue.getClass().isAnnotationPresent(Column.class)) {
				if(fieldWithValue.getClass().getSimpleName().equals(foreignField.getType().getSimpleName())) {
					
				} else {
					builder.append(foreignField.getAnnotation(Column.class).name());
				}
				builder.append("SELECT ");
				builder.append(" FROM ").append(cls.getAnnotation(Table.class).name())
				   	   .append("\nWHERE ");
			}
		} else {
			throw new MetadataNotPresentException("Table annotations is missing in class: " + cls.getSimpleName());
		}
		return builder.toString();
	}
	
	private SimpleEntry<Field, Object> findField(Class<?> cls, Object target) throws IllegalAccessException, IllegalArgumentException, 
															InvocationTargetException, NoSuchMethodException, 
															SecurityException, NoValuePresentException {
		Object value;
		
		for(Field field :  cls.getDeclaredFields()) {
			value = cls.getMethod(getMethodName(field.getName())).invoke(target);
			if(value != null) {
				return new SimpleEntry<Field, Object>(field, value);
			}
		}
		throw new NoValuePresentException("No value present in class: " + cls.getSimpleName());
	}
	
	private String getValueFromColumn(Field field, Object target, Class<?> cls) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, PrimitiveTypeNotSupportedException {
		var builder = new StringBuilder();
		var value = cls.getMethod(getMethodName(field.getName())).invoke(target);
		
		if(field.getType().getSimpleName().equals(String.class.getSimpleName())) {
			builder.append("'").append(value).append("', ");
		} else if(field.getType().getSimpleName().equals(Integer.class.getSimpleName())) {
			builder.append(value).append(", ");
		} else {
			throw new PrimitiveTypeNotSupportedException("Type not supported: " + field.getType().getSimpleName());
		}
		return builder.toString();
	}
}
