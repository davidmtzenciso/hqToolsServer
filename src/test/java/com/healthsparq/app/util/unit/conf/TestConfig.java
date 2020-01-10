package com.healthsparq.app.util.unit.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import com.healthsparq.app.model.xprod.Product;
import com.healthsparq.app.model.xprod.client.Client;
import com.healthsparq.app.model.xprod.client.ClientConfgr;
import com.healthsparq.app.util.sqltranslatorImpl.SQLTranslatorImpl;

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
		cc.setProduct(product());
		return cc;
	}
	
	@Bean
	@Scope("prototype")
	public Product product() {
		return new Product();
	}
	
	@Bean
	public SQLTranslatorImpl sqlTranslator() {
		return new SQLTranslatorImpl();
	}
}
