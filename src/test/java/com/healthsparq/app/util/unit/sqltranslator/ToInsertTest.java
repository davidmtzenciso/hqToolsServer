package com.healthsparq.app.util.unit.sqltranslator;

import java.lang.reflect.InvocationTargetException;

import org.jboss.logging.Logger;
import org.jboss.logging.Logger.Level;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.healthsparq.app.exceptions.MetadataNotPresentException;
import com.healthsparq.app.exceptions.NoValuePresentException;
import com.healthsparq.app.exceptions.PrimitiveTypeNotSupportedException;
import com.healthsparq.app.exceptions.RelationNotSupportedException;
import com.healthsparq.app.model.xprod.client.ClientConfgr;
import com.healthsparq.app.util.SQLTranslator;
import com.healthsparq.app.util.unit.conf.TestConfig;


@SpringBootTest(classes= {TestConfig.class})
public class ToInsertTest {

	@Autowired
	private SQLTranslator sqlTranslator;
	
	@Autowired
	private ClientConfgr clientConfgr;
	
	private static final Logger LOG = Logger.getLogger(ToInsertTest.class);
	
	public void init() {
		clientConfgr.setConfgrKey("property.key");
		clientConfgr.setConfgrVal("property.value");
		clientConfgr.setDefaultInd("N");
		clientConfgr.setPrecedence(2);
		clientConfgr.getClient().setCode("EXC");
	}
	
	@Test
	public void given_object_class_is_valid_when_translate_to_sql_then_it_should_pass() throws NoSuchFieldException, SecurityException, 
																							IllegalAccessException, IllegalArgumentException, 
																							InvocationTargetException, NoSuchMethodException, 
																							MetadataNotPresentException, PrimitiveTypeNotSupportedException, 
																							NoValuePresentException, RelationNotSupportedException {
		
		String sql = sqlTranslator.toInsert(clientConfgr);
		LOG.log(Level.DEBUG, sql);
	}
}
