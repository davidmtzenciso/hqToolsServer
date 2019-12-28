package com.healthsparq.app.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.stereotype.Component;

import com.healthsparq.app.annotations.ForeignKey;
import com.healthsparq.app.annotations.Ignore;
import com.healthsparq.app.exceptions.MetadataNotPresentException;
import com.healthsparq.app.exceptions.NoValuePresentException;
import com.healthsparq.app.exceptions.PrimitiveTypeNotSupportedException;
import com.healthsparq.app.exceptions.RelationNotSupportedException;

@Component
public class SQLTranslator {
	
	public String toInsert(Object obj) throws NoSuchFieldException, SecurityException, 
													MetadataNotPresentException, IllegalAccessException, 
													IllegalArgumentException, InvocationTargetException, 
													NoSuchMethodException, PrimitiveTypeNotSupportedException, 
													NoValuePresentException, RelationNotSupportedException {
		var cls = obj.getClass();
		var fields = Arrays.asList(cls.getDeclaredFields()).stream().sorted(Comparator.comparing(Field::getName)).collect(Collectors.toList());
		
		return 
			new StringBuilder()
				.append("INSERT INTO ").append(cls.getAnnotation(Table.class).name()).append(" (")
				.append(getColumns(fields)).append(")\nVALUES( ").append(getValues(cls, fields, obj)).append(" );").toString();
	}
	
	private String getColumns(List<Field> fields) throws NoSuchFieldException, SecurityException, MetadataNotPresentException {
		var builder = new StringBuilder();
		String columns;
		
		if(fields.size() == 1) {
			return builder.append(getColumnName(fields.get(0))).toString();
		} else {
			for(int i=0; i < fields.size(); i++) {
				if(!fields.get(i).isAnnotationPresent(Ignore.class)) {
					builder.append(getColumnName(fields.get(i)));
					builder.append(", ");
				}
			}
			columns = builder.toString();
			return columns.substring(0, columns.length()-2);
		}
	}
	
	private String getColumnName(Field field) throws NoSuchFieldException, SecurityException, MetadataNotPresentException {
		Class<?> cls;

		if(field.isAnnotationPresent(Column.class)) {
			return field.getAnnotation(Column.class).name();
		}
		else if(field.isAnnotationPresent(ManyToOne.class)) {
			cls = field.getType();
			if(field.isAnnotationPresent(ForeignKey.class)) {
				return getColumnName(cls.getDeclaredField(field.getAnnotation(ForeignKey.class).field()));
			} else {
				throw new MetadataNotPresentException("Missing Foreingkey annotation in field: " + field.getName());
			}
		} else {
			throw new MetadataNotPresentException("Missing one of the following annotations: Column, OneToMany, ManyToOne. In field: " + field.getName()) ;
		}
	}
	
	private String getMethodName(String fieldName) {
		return "get" + String.valueOf(fieldName.charAt(0)).toUpperCase() + fieldName.substring(1, fieldName.length());
	}
	
	private String getValues(Class<?> cls, List<Field>  fields, Object target) throws IllegalAccessException, IllegalArgumentException, 
																					InvocationTargetException, NoSuchMethodException, 
																					SecurityException, PrimitiveTypeNotSupportedException, 
																					NoSuchFieldException, NoValuePresentException, 
																					RelationNotSupportedException, MetadataNotPresentException {
		var builder = new StringBuilder();
		Object relationValue;
		String values;
		
		for(Field field : fields) {
			if(!field.isAnnotationPresent(Ignore.class)) {
				 if(field.isAnnotationPresent(Column.class)) {
					builder.append(getValueFromColumn(field, target, cls));
				} else if(field.isAnnotationPresent(ManyToOne.class)) {
					if(field.isAnnotationPresent(ForeignKey.class)) {
						relationValue = cls.getMethod(getMethodName(field.getName())).invoke(target);
						if(relationValue != null) {
							builder.append(getValueFromRelation(field.getType(), 
										relationValue, field.getAnnotation(ForeignKey.class).field()));
						} else {
							throw new NoValuePresentException("No value present for ManyToOne relation in field: " + field.getName());
						}
					} else {
						throw new MetadataNotPresentException("ForeignKey annotation in not present in field: " + field.getName());
					}
				} else {
					throw new RelationNotSupportedException("Relation OneToMany and OneToOne aren't supported yet!");
				}
				builder.append(", ");
			}
		}
		values = builder.toString();
		return values.substring(0, values.length() - 2);
	}
	
	private Object getValueFromRelation(Class<?> cls, Object target, String foreingFieldName) throws NoSuchFieldException, SecurityException, 
																		IllegalAccessException, IllegalArgumentException, 
																		InvocationTargetException, NoSuchMethodException, 
																		NoValuePresentException, MetadataNotPresentException, PrimitiveTypeNotSupportedException {
		var foreignField = cls.getDeclaredField(foreingFieldName);
		var fields  = findFieldsWithValue(cls, target);
		var builder = new StringBuilder();
		String table;
		Object value;
		
		if(fields.size() == 1) {
			value = fields.get(foreignField);
			if(value != null) {
				return value;
			}
		} 
		if(cls.isAnnotationPresent(Table.class)) {
			table = cls.getAnnotation(Table.class).name();
			if(foreignField.isAnnotationPresent(Column.class)) {
				return builder.append("( SELECT ").append(foreignField.getAnnotation(Column.class).name())
							  .append(" FROM ").append(table).append(getQueryConditionals(fields)).append(" )").toString();
			} else {
				throw new MetadataNotPresentException("Field reference by ForeingKey annotation missing Column or relationship annotation");
			}
		} else {
			throw new MetadataNotPresentException("Missing Table annotations in cls: " + cls.getSimpleName());
		}
	}
		

	private String getQueryConditionals(Map<Field, Object> args) {
		var builder = new StringBuilder();
		int i = 0;
		
		for (Entry<Field, Object> entry : args.entrySet()) {
			builder.append(i == 0 ? " WHERE ": " AND ");
			builder.append(entry.getKey().getAnnotation(Column.class).name()).append(" = ").append(entry.getValue());
			i++;
		}
		return builder.toString();
	}
	
	private Map<Field, Object> findFieldsWithValue(Class<?> cls, final Object target) throws IllegalAccessException, IllegalArgumentException, 
																					InvocationTargetException, NoSuchMethodException, 
																					SecurityException, NoValuePresentException, PrimitiveTypeNotSupportedException {
		Object value;
		Map<Field, Object> fields = new HashMap<>();
		
		for (Field field :  cls.getDeclaredFields()) {
			if (!field.isAnnotationPresent(Ignore.class)) {
				value = this.getValueFromColumn(field, target, cls);
				if (value != null) {
					fields.put(field, value);
				}
			}
		}
		
		if (fields.isEmpty()) {
			throw new NoValuePresentException("No value present in class: " + cls.getSimpleName());
		} else {
			return fields;
		}
	}
	
	private String getValueFromColumn(Field field, Object target, Class<?> cls) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, PrimitiveTypeNotSupportedException {
		var builder = new StringBuilder();
		var value = cls.getMethod(getMethodName(field.getName())).invoke(target);
		
		if(value == null) {
			return null;
		} else if(field.getType().getSimpleName().equals(String.class.getSimpleName())) {
			builder.append("'").append(value).append("'");
		} else if(field.getType().getSimpleName().equals(Integer.class.getSimpleName())) {
			builder.append(value);
		} else {
			throw new PrimitiveTypeNotSupportedException("Type not supported: " + field.getType().getSimpleName());
		}
		return builder.toString();
	}
}
