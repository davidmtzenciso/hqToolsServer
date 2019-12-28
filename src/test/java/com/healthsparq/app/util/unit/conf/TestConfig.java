package com.healthsparq.app.util.unit.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import com.healthsparq.app.model.xprod.client.Client;
import com.healthsparq.app.model.xprod.client.ClientConfgr;
import com.healthsparq.app.util.SQLTranslator;

@Configuration
public class TestConfig {

	@Bean
	@Scope("prototype")
	public Client client() {
		return new Client();
	}
	
	@Bean
	@Scope("prototype")
	public ClientConfgr clientConfgr() {
		ClientConfgr cc =  new ClientConfgr();
		cc.setClient(client());
		return cc;
	}
	
	@Bean
	public SQLTranslator sqlTranslator() {
		return new SQLTranslator();
	}
}
