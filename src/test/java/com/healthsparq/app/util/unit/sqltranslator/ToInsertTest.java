package com.healthsparq.app.util.unit.sqltranslator;

import java.lang.reflect.InvocationTargetException;

import org.jboss.logging.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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
	
	private static final String PARENTESIS_REGEX = "[\\W\\D\\s]+";
	private static final String COLUMN_REGEX = "[A-Z]{4,10}(_[A-Z]{4,10})?";
	private static final String SELECT_REGEX = "";
	private static final String INSERT_REGEX = "INSERT INTO " + COLUMN_REGEX + PARENTESIS_REGEX + "(" + COLUMN_REGEX + ")+ "
			+ "VALUES" + PARENTESIS_REGEX + "(" + COLUMN_REGEX + ")+ " + PARENTESIS_REGEX + ";";
	
	
	private static final Logger LOG = Logger.getLogger(ToInsertTest.class);
	
	@BeforeEach
	public void init() {
		clientConfgr.setConfgrKey("property.key");
		clientConfgr.setConfgrVal("property.value");
		clientConfgr.setDefaultInd("N");
		clientConfgr.setCustomExpression("");
		clientConfgr.setPrecedence(2);
		clientConfgr.getClient().setCode("EXC");
		clientConfgr.getProduct().setId(52);
	}
	
	@Test
	public void given_object_class_is_valid_when_translate_to_sql_then_it_should_pass() throws NoSuchFieldException, SecurityException, 
																							IllegalAccessException, IllegalArgumentException, 
																							InvocationTargetException, NoSuchMethodException, 
																							MetadataNotPresentException, PrimitiveTypeNotSupportedException, 
																							NoValuePresentException, RelationNotSupportedException {
		String sql = sqlTranslator.toInsert(clientConfgr);
		LOG.info(sql);
		Assertions.assertTrue(sql.matches(INSERT_REGEX));
	}
	
}
