package com.healthsparq.app.util;

import java.io.File;
import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import org.springframework.stereotype.Component;

import com.healthsparq.app.model.jira.Ticket;

@Component
public class FileManager {
	
	private DocumentBuilder builder;
	
	@PostConstruct
	public void init() throws ParserConfigurationException {
		DocumentBuilderFactory factory =
		DocumentBuilderFactory.newInstance();
	    builder = factory.newDocumentBuilder();
	}

	public String createClientDir(String name) {
		return null;
	}
	
	public String createLqbLog(Ticket ticket, String schema) throws SAXException, IOException {
		Document template = builder.parse(new File("db-changelog-template.xml"));
		StringBuilder strBuilder = new StringBuilder();
		Node changeSet;
		Node comment;
		Node sql;
		
		changeSet = createChangeSet(template.getElementsByTagName("changeSet").item(0), ticket);
		return null;
	}
	
	private Node createChangeSet(Node changeSet, Ticket ticket) {
		final String prefix = "db.changelog";
		StringBuilder idBuilder = new StringBuilder();

		idBuilder = new StringBuilder()
				 .append(prefix).append("-")
				 .append(ticket.getLabel()).append(".")
				 .append(ticket.getType()).append("-")
				 .append(ticket.getNumber()).append(".")
				 .append(ticket.getVersion());		
		
		((Element) changeSet).setAttribute("id", idBuilder.toString());
		((Element) changeSet).setAttribute("author", ticket.getAuthor());
		return changeSet;
	}
	
	private Node createComment(Node node) {
		return null;
	}
	
	public String createJson(String type, Ticket ticket ) {
		return null;
	}
}
