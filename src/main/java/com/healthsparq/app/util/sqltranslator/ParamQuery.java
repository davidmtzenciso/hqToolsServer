package com.healthsparq.app.util.sqltranslator;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.Column;
import javax.persistence.Table;

import com.healthsparq.app.exceptions.MetadataNotPresentException;
import com.healthsparq.app.exceptions.NoValuePresentException;
import com.healthsparq.app.exceptions.PrimitiveTypeNotSupportedException;
import com.healthsparq.app.exceptions.RelationNotSupportedException;

public class ParamQuery extends AbstractSQLTranslator {

	public static final String SELECT = "SELECT ";
	public static final String DELETE = "DELETE ";
	
	public String translate(String sufix, Object obj) throws NoValuePresentException, MetadataNotPresentException, 
														PrimitiveTypeNotSupportedException, RelationNotSupportedException, 
														ReflectiveOperationException {
		Class<?> cls = obj.getClass();
		Table table = getAnnotation(cls);
		Field id = findIdField(cls);
		Annotation annotation = getAnnotation(id);
		return new StringBuilder().append("SELECT ").append(getColumnName(id, annotation))
		.append(getParameteredQuery(cls, obj, table)).toString();
		
	}
		
		private String getParameteredQuery(Class<?> cls, Object target, Table table) throws 
													NoValuePresentException, MetadataNotPresentException, 
													PrimitiveTypeNotSupportedException, RelationNotSupportedException,
													ReflectiveOperationException {
			var fields  = findFieldsWithValue(cls, target);
			var builder = new StringBuilder();
			
			return builder.append(" FROM ").append(table).append(getQueryConditionals(fields))
			.append(" )").toString();
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
}
