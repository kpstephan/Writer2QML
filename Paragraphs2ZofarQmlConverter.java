package model.converter;

import java.util.HashMap;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;

import de.his.zofar.xml.questionnaire.QuestionnaireDocument;
import model.converter.zofar.AbstractMap.Type;
import model.converter.zofar.ContainerMap;
import model.converter.zofar.DocumentMap;
import model.converter.zofar.PageMap;
import model.converter.zofar.QuestionMap;
import model.converter.zofar.ZofarMapException;
import model.writerparagraphs.TextParagraph;
import model.writerparagraphs.TextParagraphList;

public class Paragraphs2ZofarQmlConverter {

	// the qml generation will be implemented here
	public static String convertToZofarQml(TextParagraphList textParagraphList) throws ZofarMapException {

		final DocumentMap doc = new DocumentMap();
		doc.setType(Type.DOCUMENT);

		// access paragraphs using paragraphs index
		final int size = textParagraphList.size();

		PageMap page = null;
		ContainerMap section = null;
		QuestionMap question = null;

		for (int i = 0; i < size; i++) {
			final TextParagraph element = textParagraphList.get(i);
			
			System.out.println("tmp : "+element);
			
			final int style = element.getStyle();
			switch (style) {
			case 1:
				if (page == null) {
					page = new PageMap();
					page.setType(Type.PAGE);
				} else {
					if (question != null) {
						final XmlObject converted = question.convert();
						if (converted != null) {
							if (section == null) {
								section = new ContainerMap();
								section.setType(Type.SECTION);
							}
							section.addchildElement(converted);
						}
						question = null;
					}

					if (section != null) {
						final XmlObject converted = section.convert();
						if (converted != null)
							page.addchildElement(converted);
						section = null;
					}

					final XmlObject converted = page.convert();
					if (converted != null)
						doc.addchildElement(converted);
				}
				page = new PageMap();
				page.setType(Type.PAGE);
				page.addHeaderElement(element);
				break;
			case 5:
				if (section == null) {
					section = new ContainerMap();
					section.setType(Type.SECTION);
				} else {
					if (question != null) {
						final XmlObject converted = question.convert();
						if (converted != null)
							section.addchildElement(converted);
						question = null;
					}

					final XmlObject converted = section.convert();
					if (converted != null)
						page.addchildElement(converted);
					section = new ContainerMap();
					section.setType(Type.SECTION);
				}
				section.addHeaderElement(element);
				break;
			case 7: // Question Title
				if (question == null) {
					question = new QuestionMap();
				} else {
					final XmlObject converted = question.convert();
					if (converted != null) {
						if (section == null) {
							section = new ContainerMap();
							section.setType(Type.SECTION);
						}
						section.addchildElement(converted);
					}
					question = new QuestionMap();
				}
				question.addHeaderElement(element);
				break;
			case 8:// Instruction
				if (question == null) {
					question = new QuestionMap();
				}
				question.addHeaderElement(element);
				break;
			case 9: // SC AO
				if (question == null) {
					question = new QuestionMap();
				}
				question.setType(Type.SC);
				question.addchildElement(element);
				break;
			case 12: // MC AO
				if (question == null) {
					question = new QuestionMap();
				}
				question.setType(Type.MC);
				question.addchildElement(element);
				break;
			case 15: // OPEN AO
				if (question == null) {
					question = new QuestionMap();
				}
				question.setType(Type.OPEN);
				question.addchildElement(element);
				break;
			case 16: // MATRIXSCK Kategorie
				if (question == null) {
					question = new QuestionMap();
				}
				question.addchildElement(element);
				break;
			case 21: // MATRIXSC Kein Antwortmöglich
				if (question == null) {
					question = new QuestionMap();
				}
				question.addchildElement(element);
				break;
			case 22: // MATRIXSC Aussage
				if (question == null) {
					question = new QuestionMap();
				}
				question.setType(Type.MSC);
				question.addchildElement(element);
				break;
			case 23: // Likertskala stimme zu
				if (question == null) {
					question = new QuestionMap();
				}
				question.setType(Type.LIKS);
				question.addchildElement(element);
				break;
			case 24: // Likertskala 5 - 1
				if (question == null) {
					question = new QuestionMap();
				}
				question.addchildElement(element);
				break;
			case 25: // Likertskala lehne ab
				if (question == null) {
					question = new QuestionMap();
				}
				question.addchildElement(element);
				break;
			case 6: // PageBreak ?
				if (question != null) {
					final XmlObject converted = question.convert();
					if (converted != null) {
						if (section == null) {
							section = new ContainerMap();
							section.setType(Type.SECTION);
						}
						section.addchildElement(converted);
					}
					question = null;
				}

				if (section != null) {
					final XmlObject converted = section.convert();
					if (converted != null)
						page.addchildElement(converted);
					section = null;
				}
								
				if (page != null) {
					final XmlObject converted = page.convert();
					if (converted != null)
						doc.addchildElement(converted);
				}

				page = new PageMap();
				page.setType(Type.PAGE);
				// page.addHeaderElement(element);
				break;
			default:
				System.out.println("paragraph index: " + i + ", style: " + element.getStyle() + ", content: "
						+ element.getContent());
				break;
			}
		}
		if (question != null) {
			final XmlObject converted = question.convert();
			if (converted != null) {
				if (section == null) {
					section = new ContainerMap();
					section.setType(Type.SECTION);
				}
				section.addchildElement(converted);
			}
			question = null;
		}
		
		if (section != null) {
			final XmlObject converted = section.convert();
			if (converted != null)
				page.addchildElement(converted);
			section = null;
		}

		if (page != null) {
			final XmlObject converted = page.convert();
			if (converted != null)
				doc.addchildElement(converted);
		}
		QuestionnaireDocument docConverted = (QuestionnaireDocument) doc.convert();

		final XmlOptions opts = new XmlOptions();
		opts.setCharacterEncoding("utf8");
		opts.setSavePrettyPrint();
		opts.setSavePrettyPrintIndent(4);
		opts.setSaveOuter();
		opts.setSaveAggressiveNamespaces();

		final HashMap<String, String> nsMap = new HashMap<String, String>();
		nsMap.put("http://www.his.de/zofar/xml/questionnaire", "zofar");
		opts.setSaveSuggestedPrefixes(nsMap);


		return convert2String(docConverted, opts);
	}

	public static String convert2String(XmlObject xml, final XmlOptions opts) {
		if (xml == null)
			return null;
		if (opts != null)
			return xml.xmlText(opts);
		else
			return xml.xmlText();
	}

}
