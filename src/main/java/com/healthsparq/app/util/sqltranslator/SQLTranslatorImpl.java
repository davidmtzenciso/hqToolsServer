package com.healthsparq.app.util.sqltranslator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.healthsparq.app.exceptions.MetadataNotPresentException;
import com.healthsparq.app.exceptions.NoValuePresentException;
import com.healthsparq.app.exceptions.PrimitiveTypeNotSupportedException;
import com.healthsparq.app.exceptions.RelationNotSupportedException;
import com.healthsparq.app.util.SQLTranslator;


@Component
public class SQLTranslatorImpl extends Thread implements SQLTranslator {
	
	@Autowired
	private InsertSQLTranslator insert;
	
	@Autowired
	private ParamQuery paramQuery;
	
	
	public String toInsert(Object obj) throws ReflectiveOperationException, MetadataNotPresentException, 
											PrimitiveTypeNotSupportedException, NoValuePresentException, 
											RelationNotSupportedException {
		return insert.translate(obj);
	}
	
	public String toSelect(Object obj) throws NoValuePresentException, MetadataNotPresentException, 
											PrimitiveTypeNotSupportedException, RelationNotSupportedException, 
											ReflectiveOperationException {
		return paramQuery.translate(ParamQuery.SELECT, obj);
	}
	
	public String toDelete(Object obj) throws NoValuePresentException, MetadataNotPresentException, 
											PrimitiveTypeNotSupportedException, RelationNotSupportedException, 
											ReflectiveOperationException {
		return paramQuery.translate(ParamQuery.DELETE, obj);
	}
	
}
