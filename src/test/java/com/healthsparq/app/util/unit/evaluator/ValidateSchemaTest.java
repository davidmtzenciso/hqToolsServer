package com.healthsparq.app.util.unit.evaluator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import com.healthsparq.app.util.Evaluator;


@SpringBootTest
public class ValidateSchemaTest {

	@Autowired
	private Evaluator evaluator;
	
	@Test
	public void given_schema_name_has_invalid_characters_when_the_name_is_validated_then_it_should_failed() {

		//given the name has invalid characters
		final String name = "Xproduct!";
		
		//when the name is validated
		final Boolean itExists = evaluator.isExistingSchema(name);
		
		//then it should fail
		Assertions.assertFalse(itExists);
	}
	
	@Test
	public void given_schema_name_is_not_lower_case_when_the_name_is_validated_then_it_should_pass() {

		//given the name is not lower case
		final String name = "Xproduct";
		
		//when the name is validated
		final Boolean itExists = evaluator.isExistingSchema(name);
		
		//then it should pass
		Assertions.assertTrue(itExists);
	}
	
	@Test
	public void given_schema_name_is_misspelled_when_the_name_is_validated_then_it_should_failed() {

		//given the name is misspelled
		final String name = "xprodut";
		
		//when the name is validated
		final Boolean itExists = evaluator.isExistingSchema(name);
		
		//then it should failed
		Assertions.assertFalse(itExists);
	}
	
	@Test
	public void given_schema_name_contains_blank_spaces_when_the_name_is_validated_then_it_should_failed() {

		//given the name contains blank spaces
		final String name = "xproduct ";
		
		//when the name is validated
		final Boolean itExists = evaluator.isExistingSchema(name);
		
		//then it should failed
		Assertions.assertFalse(itExists);
	}
	
	@Test
	public void given_schema_name_follows_all_the_rules_when_the_name_is_validated_then_it_should_pass() {

		//given the name follows all the rules
		final String name = "xproduct";
		
		//when the name is validated
		final Boolean itExists = evaluator.isExistingSchema(name);
		
		//then it should pass
		Assertions.assertTrue(itExists);
	}
	
	
}
