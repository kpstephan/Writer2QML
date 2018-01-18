package model.converter.zofar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.xmlbeans.XmlObject;

import de.his.zofar.xml.questionnaire.PageType;
import de.his.zofar.xml.questionnaire.SectionBodyType;
import de.his.zofar.xml.questionnaire.SectionHeaderType;
import de.his.zofar.xml.questionnaire.SectionType;
import de.his.zofar.xml.questionnaire.TextTitleType;
import de.his.zofar.xml.questionnaire.TransitionsType;
import eu.dzhw.zofar.management.utils.string.StringUtils;
import model.writerparagraphs.TextParagraph;

public class PageMap extends ContainerMap {

	private static final SectionType[] DUMMY_SECTION = new SectionType[0];
	private List<Object> transitions = new ArrayList<Object>();

	// SetMethode
	@Override
	public void setType(Type type) throws ZofarMapException {
		if (type == null)
			throw new ZofarMapException("type is null");
		boolean valid = false;
		if (type == Type.PAGE)
			valid = true;
		if (!valid)
			throw new ZofarMapException("no valid Type");
		this.type = type;
	}

	public void addTransition(TextParagraph element) throws ZofarMapException {
		if(element == null)throw new ZofarMapException("transition is null");
		boolean valid = true;
//		boolean valid = false;
//		if(element.getStyle() == 1)valid = true;
//		if(element.getStyle() == 5)valid = true;
		if(!valid)throw new ZofarMapException("no valid Type : "+element.getStyle());
		transitions.add(element);
	}
	
	public void removeTransition(final TextParagraph element)throws ZofarMapException {
		if(element == null) {
			throw new ZofarMapException("transition is null");
		}
		transitions.remove(element);
	}

	@Override
	public XmlObject convert() {
		System.out.println("convert ("+this.type+") of (" + this.getClass().getName() + ")" );

		if (type == Type.PAGE) {
			// Seite anlegen
			final PageType page = PageType.Factory.newInstance();
			page.setUid(StringUtils.getInstance().randomString(10,false));

			/*
			 * Page Header
			 */
			final SectionHeaderType pageHeader = page.addNewHeader();

//			// Seitentitel erstellen
//			final TextTitleType pageTitle = pageHeader.addNewTitle();
//			pageTitle.setUid("title1");
//			pageTitle.newCursor().setTextValue("Dies ist der Seitentitel");
			
			for(Object header : this.headerElement) {
				System.out.println("Header of ("+this.getClass().getName()+") : "+header.getClass().getName());
				if ((TextParagraph.class).isAssignableFrom(header.getClass())) {
					TextParagraph tmp = (TextParagraph)header;
					if (tmp.getStyle() == 1) {
						final TextTitleType pageTitle = pageHeader.addNewTitle();
						pageTitle.setUid(StringUtils.getInstance().randomString(10,false));
						pageTitle.newCursor().setTextValue(tmp.getContent());
					}
					else {
						System.out.println("Unhandeled Header Type in (\" + this.getClass().getName() + \")"+tmp);
					}
				}
			}

			/*
			 * Page Body
			 */
			final SectionBodyType pageBody = page.addNewBody();
			pageBody.setUid("body");

			for (Object child : this.childElement) {
				if ((SectionType.class).isAssignableFrom(child.getClass())) {
					final List<SectionType> sections = new ArrayList<SectionType>(
							Arrays.asList(pageBody.getSectionArray()));
					sections.add((SectionType) child);
					pageBody.setSectionArray(sections.toArray(DUMMY_SECTION));
				}
				else {
					System.out.println("Unhandled Type in PageMap Convert : "+child.getClass());
				}
			}
			
			final TransitionsType transitionsArea = page.addNewTransitions();
			
			if(!transitions.isEmpty()) {
				for(final Object transItem : transitions) {
					//TODO
				}
			}
			
			return page;
		}
		return null;
	}

}