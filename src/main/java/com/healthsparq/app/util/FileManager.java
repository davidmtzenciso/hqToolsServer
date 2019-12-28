package com.healthsparq.app.util;

import java.io.File;
import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import org.springframework.stereotype.Component;

import com.healthsparq.app.model.jira.Ticket;

@Component
public class FileManager {
	
	private DocumentBuilder builder;
	private String lqbPath;
	
	@PostConstruct
	public void init() throws ParserConfigurationException {
		DocumentBuilderFactory factory =
		DocumentBuilderFactory.newInstance();
	    builder = factory.newDocumentBuilder();
	}
	
	public void setLqbPath(String path) {
		this.lqbPath = path;
	}

	public String createClientDir(String name) {
		return null;
	}
	
	public String createLqbLog(Ticket ticket, String schema, String query) throws SAXException, IOException, TransformerException {
		Document template = builder.parse(new File("db-changelog-template.xml"));
		Node changeSet;
		Node comment;
		Node sql;
		
		changeSet = template.getElementsByTagName("changeSet").item(0);
		((Element) changeSet).setAttribute("id", getFileName(ticket));
		((Element) changeSet).setAttribute("author", ticket.getAuthor());
		
		comment = template.getElementsByTagName("comment").item(0);
		((Element) comment).setTextContent(ticket.getTitle());
		
		sql = template.getElementsByTagName("sql").item(0);
		((Element) sql).setTextContent(new StringBuilder().append("\n\t\t<![CDATA[").append(query)
														  .append("COMMIT;\r\n\t\t]]>").toString());
		createXmlFile(template, this.lqbPath + getFileName(ticket) + ".xml");
		return sql.getTextContent();
	}
	
	private String getFileName(Ticket ticket) {
		return
			new StringBuilder()
				.append("db-changelog-").append(ticket.getLabel()).append(".")
				.append(ticket.getType()).append("-").append(ticket.getNumber())
				.append(".").append(ticket.getVersion()).toString();
	}
	
	private void createXmlFile(Document document, String path) throws TransformerException {
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(document);
		StreamResult result = new StreamResult(new File(path));
		transformer.transform(source, result);
	}
	
	
	public String createJson(String type, Ticket ticket ) {
		return null;
	}
}
