package com.healthsparq.app.util;

import java.util.List;
import java.util.function.Consumer;

import com.healthsparq.app.util.sqltranslator.exceptions.IncompleteRequestException;

public interface SQLTranslator {

	public SQLTranslator setOnSuccess(Consumer<String> onSuccess);
	
	public SQLTranslator onError(Consumer<String> onError);
	
	public SQLTranslator setData(List<Object> data);
	
	public void toSql(int stmt) throws IncompleteRequestException;
	
}
