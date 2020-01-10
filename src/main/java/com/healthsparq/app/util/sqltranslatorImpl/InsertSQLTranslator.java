package com.healthsparq.app.util.sqltranslatorImpl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.healthsparq.app.util.sqltranslator.exceptions.*;

@Component
final class InsertSQLTranslator {
	
	@Autowired
	private ParamQuery query;
	
	@Autowired
	private Finder finder;

	public String translate(Object obj) throws ReflectiveOperationException,MetadataNotPresentException, 
												PrimitiveTypeNotSupportedException, NoValuePresentException, 
												RelationNotSupportedException {
		List<Field> fields;
		Class<?> cls = obj.getClass();
		Table table = finder.findAnnotation(cls);
		
		fields = Arrays.asList(cls.getDeclaredFields())
		.stream().sorted(Comparator.comparing(Field::getName))
		.collect(Collectors.toList());
		
		return new StringBuilder()
		.append("INSERT INTO ").append(table.name())
		.append(" (").append(getColumns(fields)).append(")\nVALUES ( ")
		.append(getValues(cls, fields, obj)).append(" );").toString();

	}

	private String getColumns(List<Field> fields) throws ReflectiveOperationException,MetadataNotPresentException, 
			RelationNotSupportedException, NoValuePresentException {
		var builder = new StringBuilder();
		Annotation annotation;
		String columns;
		
		for(Field field: fields) {
			if(!Modifier.isStatic(field.getModifiers())) {
				annotation = finder.findAnnotation(field);
				builder.append(finder.findColumnName(field, annotation));
				builder.append(", ");
			}
		}

		columns = builder.toString();
		return columns.isEmpty() ? columns : columns.substring(0, columns.length() - 2);
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
			if(!Modifier.isStatic(field.getModifiers())) {
				annotation = finder.findAnnotation(field);
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
		if(annotation instanceof Column) {
			return finder.findValueInField(field, target, cls);
		} else if(annotation instanceof ManyToOne) {
			return query.translate(ParamQuery.SELECT, cls.getMethod(getMethodName(field.getName())).invoke(target) );
		} 
		return null;
	}
	
	private String getMethodName(String fieldName) {
		return "get" + String.valueOf(fieldName.charAt(0)).toUpperCase() + fieldName.substring(1, fieldName.length());
	}
}
