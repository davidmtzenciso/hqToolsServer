package com.healthsparq.app.util;

import com.healthsparq.app.exceptions.MetadataNotPresentException;
import com.healthsparq.app.exceptions.NoValuePresentException;
import com.healthsparq.app.exceptions.PrimitiveTypeNotSupportedException;
import com.healthsparq.app.exceptions.RelationNotSupportedException;

public interface SQLTranslator {

	public String toInsert(Object obj) throws ReflectiveOperationException, MetadataNotPresentException, 
												PrimitiveTypeNotSupportedException, NoValuePresentException, 
												RelationNotSupportedException;
	
	public String toSelect(Object obj) throws NoValuePresentException, MetadataNotPresentException, 
												PrimitiveTypeNotSupportedException, RelationNotSupportedException, 
												ReflectiveOperationException;
	
	public String toDelete(Object obj) throws NoValuePresentException, MetadataNotPresentException, 
												PrimitiveTypeNotSupportedException, RelationNotSupportedException, 
												ReflectiveOperationException;
}
