package com.healthsparq.app.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
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
	public static final String NO_VALUE_IN_FIELD_ANNOTATION_ERROR = "No value present in annotation in field: ";
	public static final String NO_VALUE_IN_CLASS_ANNOTATION_ERROR = "No value present in annotation in class: ";
	public static final String NO_VALUE_ERROR = "No value present in class: "; 
	public static final String NOT_SUPPORTED_TYPE_ERROR = "Type not supported: ";
	
	public String toInsert(Object obj) throws ReflectiveOperationException,MetadataNotPresentException, 
												PrimitiveTypeNotSupportedException, NoValuePresentException, 
												RelationNotSupportedException {
		List<Field> fields;
		Class<?> cls = obj.getClass();
		Table table = getAnnotation(cls);
		
		fields = Arrays.asList(cls.getDeclaredFields())
					   .stream().sorted(Comparator.comparing(Field::getName))
					   .collect(Collectors.toList());
		
		return new StringBuilder()
					.append("INSERT INTO ").append(table.name())
					.append(" (").append(getColumns(fields)).append(")\nVALUES ( ")
					.append(getValues(cls, fields, obj)).append(" );").toString();
		
	}
	
	private Table getAnnotation(final Class<?> cls) throws NoValuePresentException, MetadataNotPresentException {
		Table table;
		
		if(cls.isAnnotationPresent(Table.class)) {
			table = cls.getAnnotation(Table.class);
			if(table.name().isEmpty()) {
				throw new NoValuePresentException(NO_VALUE_IN_CLASS_ANNOTATION_ERROR + cls.getSimpleName());
			}
		} else {
			throw new MetadataNotPresentException(MISSING_TABLE_ANNOTATION_ERROR + cls.getSimpleName());
		}

		return table;
	}
	
	private Annotation getAnnotation(final Field field) throws RelationNotSupportedException, 
														MetadataNotPresentException, NoValuePresentException {
		Annotation annotation;
		
		if(field.isAnnotationPresent(OneToMany.class) || field.isAnnotationPresent(OneToOne.class)) {
			throw new RelationNotSupportedException(NOT_SUPPORTED_RELATION_ERROR);
		} 
		else if(field.isAnnotationPresent(ManyToOne.class)) {
			if(field.isAnnotationPresent(ForeignKey.class)) {
				if(field.getAnnotation(ForeignKey.class).field().isEmpty()) {
					throw new NoValuePresentException(NO_VALUE_IN_FIELD_ANNOTATION_ERROR + field.getName());
				} else {
					annotation = field.getAnnotation(ManyToOne.class);
				}
			} else {
				throw new MetadataNotPresentException(MISSING_FOREIGN_KEY_ANNOTATION_ERROR + field.getName());
			}
		} else if(!field.isAnnotationPresent(Ignore.class) && !field.isAnnotationPresent(Column.class)) {
			throw new MetadataNotPresentException(MISSING_FIELD_ANNOTATION_ERROR + field.getName());
		} else if(field.isAnnotationPresent(Column.class)) {
			if(field.getAnnotation(Column.class).name().isEmpty()) {
				throw new NoValuePresentException(NO_VALUE_IN_FIELD_ANNOTATION_ERROR + field.getName());
			} else {
				annotation = field.getAnnotation(Column.class);
			}
		} else {
			annotation = null;
		}
		return annotation;
	}
	
	private String getColumns(List<Field> fields) throws ReflectiveOperationException,MetadataNotPresentException, 
														RelationNotSupportedException, NoValuePresentException {
		var builder = new StringBuilder();
		Annotation annotation;
		String columns;

		for(Field field: fields) {
			annotation = getAnnotation(field);
			if(annotation instanceof Ignore) {
				builder.append(getColumnName(field, annotation));
				builder.append(", ");
			}
		}
		
		columns = builder.toString();
		return columns.isEmpty() ? columns : columns.substring(0, columns.length() - 2);
	}
	
	private String getColumnName(final Field field, final Annotation annotation) throws ReflectiveOperationException,
																					MetadataNotPresentException, 
																					RelationNotSupportedException, 
																					NoValuePresentException {
		Annotation foreignAnnotation;
		Field foreignField;
		
		if(annotation instanceof Column) {
			return ((Column) annotation).name();
		} else {
			foreignField = field.getType().getDeclaredField(field.getAnnotation(ForeignKey.class).field());
			foreignAnnotation = getAnnotation(foreignField);
			return getColumnName(foreignField, foreignAnnotation);
		}
	}
	
	private String getMethodName(String fieldName) {
		return "get" + String.valueOf(fieldName.charAt(0)).toUpperCase() + fieldName.substring(1, fieldName.length());
	}
	
	private String getValues(Class<?> cls, List<Field>  fields, Object target) throws ReflectiveOperationException,
																					PrimitiveTypeNotSupportedException, 
																					NoValuePresentException, 
																					RelationNotSupportedException, 
																					MetadataNotPresentException {
		var builder = new StringBuilder();
		Annotation annotation;
		String values;
		
		for(Field field : fields) {
			annotation = getAnnotation(field);
			if(!(annotation instanceof Ignore)) {
				builder.append(getValue(annotation, cls, field, target)).append(", ");
			}
		}
		values = builder.toString();
		return values.isEmpty() ? values : values.substring(0, values.length() - 2);
	}
	
	private Object getValue(Annotation annotation, Class<?> cls, Field field, Object target) throws 
																						NoValuePresentException, MetadataNotPresentException, 
																						PrimitiveTypeNotSupportedException, RelationNotSupportedException,
																						ReflectiveOperationException {
		Table table;
		Object value;
		
		if(annotation instanceof Column) {
			return getValueFromColumn(field, target, cls);
		} else if(annotation instanceof ManyToOne) {
		 	table = getAnnotation(field.getType());
			value = cls.getMethod(getMethodName(field.getName())).invoke(target);
			if(value != null) {
				return getValueFromRelation(field.getType(), value, table, 
								field.getAnnotation(ForeignKey.class).field());
			}
		}
		
		return null;
	}
	
	private Object getValueFromRelation(Class<?> cls, Object target, Table table, String fieldName) throws 
																				NoValuePresentException, MetadataNotPresentException, 
																				PrimitiveTypeNotSupportedException, RelationNotSupportedException,
																				ReflectiveOperationException {
		var fields  = findFieldsWithValue(cls, target);
		var builder = new StringBuilder();
		Annotation annotation;
		Field field;
		
		field = cls.getDeclaredField(fieldName);
		annotation = getAnnotation(field);
		if(annotation instanceof Column) {
			return builder.append("( SELECT ").append(((Column)annotation).name())
						  .append(" FROM ").append(table).append(getQueryConditionals(fields))
						  .append(" )").toString();
		} else {
			return getValueFromRelation(field.getType(), null, getAnnotation(field.getType()), null);
		}
	}
		

	private String getQueryConditionals(Map<Field, Object> args) {
		var builder = new StringBuilder();
		int i = 0;
		
		for (Entry<Field, Object> entry : args.entrySet()) {
			builder.append(i == 0 ? " WHERE ": " AND ");
			builder.append(entry.getKey().getAnnotation(Column.class).name())
				   .append(" = ").append(entry.getValue());
			i++;
		}
		return builder.toString();
	}
	
	private Map<Field, Object> findFieldsWithValue(Class<?> cls, final Object target) throws 
																					ReflectiveOperationException, 
																					NoValuePresentException, 
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
	
	private String getValueFromColumn(Field field, Object target, Class<?> cls) throws 
																				PrimitiveTypeNotSupportedException, 
																				ReflectiveOperationException {
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
