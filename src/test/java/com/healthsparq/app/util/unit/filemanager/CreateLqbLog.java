package com.healthsparq.app.util.unit.filemanager;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.healthsparq.app.util.FileManager;

@SpringBootTest
public class CreateLqbLog {

	@Autowired
	private FileManager fileManager;
	
	@Test
	public void given_all_parameters_are_valid_when_create_lqb_log_then_it_should_pass() {
	
	}
}
