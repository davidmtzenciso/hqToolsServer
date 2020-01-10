package com.healthsparq.app.util.sqltranslatorImpl;

import static com.healthsparq.app.util.sqltranslatorImpl.SQLStmts.*;
import static com.healthsparq.app.util.sqltranslatorImpl.TranslationErrors.*;

import java.util.List;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.healthsparq.app.util.SQLTranslator;
import com.healthsparq.app.util.sqltranslator.exceptions.IncompleteRequestException;
import com.healthsparq.app.util.sqltranslator.exceptions.MetadataNotPresentException;
import com.healthsparq.app.util.sqltranslator.exceptions.NoValuePresentException;
import com.healthsparq.app.util.sqltranslator.exceptions.PrimitiveTypeNotSupportedException;
import com.healthsparq.app.util.sqltranslator.exceptions.RelationNotSupportedException;
import com.healthsparq.app.util.sqltranslator.exceptions.SQLStmtNotSupportedException;


@Component
public class SQLTranslatorImpl extends Thread implements SQLTranslator {
	
	@Autowired
	private InsertSQLTranslator insertSqlTranslator;
	
	@Autowired
	private ParamQuery paramQuery;
	
	private Consumer<String> onSuccess;
	private Consumer<String> onError;
	private List<Object> data;
	private static final String INCOMPLETE_ERROR = "data or onSuccess or onError callback missing ";
		
	@Override
	public SQLTranslator setOnSuccess(Consumer<String> onSuccess) {
		this.onSuccess = onSuccess;
		return this;
	}
	
	@Override
	public SQLTranslator onError(Consumer<String> onError) {
		this.onError = onError;
		return this;
	}
	
	@Override
	public SQLTranslator setData(List<Object> data) {
		this.data = data;
		return this;
	}
	
	@Override
	public void toSql(int stmt) throws IncompleteRequestException {
		if(isComplete()) {
			for(int i = 0; i < data.size(); i++) {
				try {
					this.onSuccess.accept(execute(stmt, data.get(i), i+1));
				} catch (NoValuePresentException | SQLStmtNotSupportedException | MetadataNotPresentException
						| PrimitiveTypeNotSupportedException | RelationNotSupportedException
						| ReflectiveOperationException e) {
					this.onError.accept(getError(i+1, e.getMessage()));
					e.printStackTrace();
				}
			}
			cleanUp();
		} else {
			cleanUp();
			throw new IncompleteRequestException(INCOMPLETE_ERROR);
		}
	}
	
	private String execute(int sqlStmt, Object obj, int position) throws NoValuePresentException, SQLStmtNotSupportedException,
														MetadataNotPresentException, PrimitiveTypeNotSupportedException, 
														RelationNotSupportedException, ReflectiveOperationException {
		switch(sqlStmt) {	
		case INSERT: return this.insertSqlTranslator.translate(obj);
		case DELETE: return this.paramQuery.translate(ParamQuery.DELETE, obj);
		case SELECT: return  this.paramQuery.translate(ParamQuery.SELECT, obj);
		case DELETE_INSERT: return new StringBuilder().append(paramQuery.translate(ParamQuery.DELETE, obj)).toString();
		default:
			throw new SQLStmtNotSupportedException(STMT_NOT_SUPPORTED);
		}
	}
	
	private String getError(int position, String reason) {
		return new StringBuilder().append("Element: ").append(position)
								  .append(" failed, reason: ").append(reason)
								  .toString();
	}
	
	private boolean isComplete() {
		return onSuccess != null && onError != null && data != null;
	}
	
	private void cleanUp() {
		this.onSuccess = null;
		this.onError = null;
		this.data = null;
	}
}
