package test;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;

import org.apache.xmlbeans.XmlObject;
import org.junit.Ignore;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

import eu.dzhw.zofar.management.utils.xml.XmlClient;
import junit.framework.TestCase;
import model.converter.ODFParagraphsExtractor;
import model.converter.Paragraphs2ZofarQmlConverter;
import model.converter.zofar.ZofarMapException;
import model.writerparagraphs.TextParagraphList;

public class ZofarQMLTests extends TestCase {

	private Document doc;
	private XmlObject xmlObj;

	private class SimpleErrorHandler implements ErrorHandler {
		
		final Map<String,List<SAXParseException>> messages;

		public SimpleErrorHandler() {
			super();
			messages = new HashMap<String,List<SAXParseException>>();
		}

		public void warning(SAXParseException e) throws SAXException {
//			List<SAXParseException> msgList = messages.get("warning");
//			if(msgList == null)msgList = new ArrayList<SAXParseException>();
//			msgList.add(e);
//			messages.put("warning",msgList);
		}

		public void error(SAXParseException e) throws SAXException {
			List<SAXParseException> msgList = messages.get("error");
			if(msgList == null)msgList = new ArrayList<SAXParseException>();
			msgList.add(e);
			messages.put("error",msgList);
		}

		public void fatalError(SAXParseException e) throws SAXException {
			List<SAXParseException> msgList = messages.get("fatalError");
			if(msgList == null)msgList = new ArrayList<SAXParseException>();
			msgList.add(e);
			messages.put("fatalError",msgList);
		}

		public Map<String, List<SAXParseException>> getMessages() {
			return messages;
		}
	}

	public ZofarQMLTests() {
		super();

	}
	@Override
	protected void setUp() throws Exception {
		System.out.println("Set Up");
		super.setUp();
		final String fileName = "input-files/Fragebogen1_mit_Instruction.odt";
		TextParagraphList textParagraphList = ODFParagraphsExtractor.extractParagraphsFromODF(fileName);
		try {
			final String converted = Paragraphs2ZofarQmlConverter.convertToZofarQml(textParagraphList);
			doc = XmlClient.getInstance().getDocumentFromString(converted);
			xmlObj = XmlClient.getInstance().docToXmlObject(doc);
		} catch (ZofarMapException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@Test
	public void test() {
		System.out.println("validate document against schema...");

		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setValidating(false);
		factory.setNamespaceAware(true);
		
		final SimpleErrorHandler errorHandler = new SimpleErrorHandler();

		SAXParser parser;
		try {
			SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");

			factory.setSchema(schemaFactory
					.newSchema(new Source[] { new StreamSource("xsd/de/his/zofar/xml/zofar_questionnaire_0.2.xsd"),new StreamSource("xsd/de/his/zofar/xml/navigation_0.1.xsd"),new StreamSource("xsd/de/his/zofar/xml/display_0.1.xsd") }));

			parser = factory.newSAXParser();
			XMLReader reader = parser.getXMLReader();
			reader.setErrorHandler(errorHandler);
			reader.parse(new InputSource(new StringReader(XmlClient.getInstance().convert2String(xmlObj))));
		} catch (ParserConfigurationException | SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		TestCase.assertTrue("QML not valid : "+ errorHandler.getMessages(),errorHandler.getMessages().isEmpty());
	}
}
