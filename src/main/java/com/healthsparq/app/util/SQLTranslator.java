package com.healthsparq.app.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.persistence.*;

import org.springframework.stereotype.Component;

import com.healthsparq.app.annotations.ForeignKey;
import com.healthsparq.app.annotations.Ignore;
import com.healthsparq.app.exceptions.*;

@Component
public class SQLTranslator {
	
	public static final String MISSING_TABLE_ANNOTATION_ERROR = "Missing Table annotations in class: ";
	public static final String MISSING_FOREIGN_KEY_ANNOTATION_ERROR = "Missing Foreingkey annotation in field: ";
	public static final String MISSING_FIELD_ANNOTATION_ERROR = "Missing one of the following annotations: Column, Ignore, ManyToOne. In field: ";
	public static final String MANY_TO_ONE_VALUE_ERROR = "No value present for ManyToOne relation in field: ";
	public static final String NOT_SUPPORTED_RELATION_ERROR = "Relation OneToMany and OneToOne aren't supported yet!";	
	public static final String NO_VALUE_ERROR = "No value present in class: "; 
	public static final String NOT_SUPPORTED_TYPE_ERROR = "Type not supported: ";
	
	public String toInsert(Object obj) throws NoSuchFieldException, SecurityException, 
													MetadataNotPresentException, IllegalAccessException, 
													IllegalArgumentException, InvocationTargetException, 
													NoSuchMethodException, PrimitiveTypeNotSupportedException, 
													NoValuePresentException, RelationNotSupportedException {
		List<Field> fields;
		var cls = obj.getClass();
		
		if(cls.isAnnotationPresent(Table.class)) {
			fields = Arrays.asList(cls.getDeclaredFields())
						   .stream().sorted(Comparator.comparing(Field::getName))
						   .collect(Collectors.toList());
			return new StringBuilder()
						.append("INSERT INTO ").append(cls.getAnnotation(Table.class).name())
						.append(" (").append(getColumns(fields)).append(")\nVALUES ( ")
						.append(getValues(cls, fields, obj)).append(" );").toString();
		} else {
			throw new MetadataNotPresentException(MISSING_TABLE_ANNOTATION_ERROR + cls.getSimpleName());
		}
	}
	
	private boolean isValidField(final Field field) throws RelationNotSupportedException, MetadataNotPresentException {
		if(field.isAnnotationPresent(OneToMany.class) || field.isAnnotationPresent(OneToOne.class)) {
			throw new RelationNotSupportedException(NOT_SUPPORTED_RELATION_ERROR);
		} 
		else if(field.isAnnotationPresent(ManyToOne.class)) {
			if(!field.isAnnotationPresent(ForeignKey.class)) {
				throw new MetadataNotPresentException(MISSING_FOREIGN_KEY_ANNOTATION_ERROR + field.getName());
			}
		} else if(!field.isAnnotationPresent(Ignore.class) && !field.isAnnotationPresent(Column.class)) {
			throw new MetadataNotPresentException(MISSING_FIELD_ANNOTATION_ERROR + field.getName());
		}
		return true;
	}
	
	private String getColumns(List<Field> fields) throws NoSuchFieldException, SecurityException, 
													MetadataNotPresentException, RelationNotSupportedException {
		var builder = new StringBuilder();
		String columns;

		for(Field field: fields) {
			if(isValidField(field)) {
				if(!field.isAnnotationPresent(Ignore.class)) {
						builder.append(getColumnName(field));
						builder.append(", ");
				}
			}
		}
		
		columns = builder.toString();
		return columns.substring(0, columns.length()-2);
	}
	
	private String getColumnName(final Field field) throws NoSuchFieldException, SecurityException, 
														MetadataNotPresentException, RelationNotSupportedException {
		if(field.isAnnotationPresent(Column.class)) {
			return field.getAnnotation(Column.class).name();
		}
		else {
			return getColumnName(field.getType().getDeclaredField(field.getAnnotation(ForeignKey.class).field()));
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
			if(isValidField(field)) {
				if(!field.isAnnotationPresent(Ignore.class)) {
					 if(field.isAnnotationPresent(Column.class)) {
						builder.append(getValueFromColumn(field, target, cls));
					 } 
					 else if(field.isAnnotationPresent(ManyToOne.class)) {
							relationValue = cls.getMethod(getMethodName(field.getName())).invoke(target);
							if(relationValue != null) {
								builder.append(getValueFromRelation(field.getType(), 
											relationValue, field.getAnnotation(ForeignKey.class).field()));
							} else {
								throw new NoValuePresentException(MANY_TO_ONE_VALUE_ERROR + field.getName());
							}
					}
					builder.append(", ");
				}
			}
		}
		values = builder.toString();
		return values.substring(0, values.length() - 2);
	}
	
	private Object getValueFromRelation(Class<?> cls, Object target, String foreingFieldName) 
																				throws NoSuchFieldException, SecurityException, 
																				IllegalAccessException, IllegalArgumentException, 
																				InvocationTargetException, NoSuchMethodException, 
																				NoValuePresentException, MetadataNotPresentException, 
																				PrimitiveTypeNotSupportedException {
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
				throw new MetadataNotPresentException(MISSING_FIELD_ANNOTATION_ERROR + cls.getSimpleName());
			}
		} else {
			throw new MetadataNotPresentException(MISSING_TABLE_ANNOTATION_ERROR + cls.getSimpleName());
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
																					SecurityException, NoValuePresentException, 
																					PrimitiveTypeNotSupportedException {
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
			throw new NoValuePresentException(NO_VALUE_ERROR + cls.getSimpleName());
		} else {
			return fields;
		}
	}
	
	private String getValueFromColumn(Field field, Object target, Class<?> cls) 
																		throws IllegalAccessException, IllegalArgumentException,
																		InvocationTargetException, NoSuchMethodException, 
																		SecurityException, PrimitiveTypeNotSupportedException {
		var builder = new StringBuilder();
		var value = cls.getMethod(getMethodName(field.getName())).invoke(target);
		
		if(field.getType().getSimpleName().equals(String.class.getSimpleName())) {
			builder.append("'").append(value).append("'");
		} else if(field.getType().getSimpleName().equals(Integer.class.getSimpleName())) {
			builder.append(value);
		} else {
			throw new PrimitiveTypeNotSupportedException(NOT_SUPPORTED_TYPE_ERROR+ field.getType().getSimpleName());
		}
		return builder.toString();
	}
}
