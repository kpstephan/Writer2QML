package model.converter.zofar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.xmlbeans.XmlObject;

import de.his.zofar.xml.questionnaire.MatrixQuestionSingleChoiceType;
import de.his.zofar.xml.questionnaire.MultipleChoiceType;
import de.his.zofar.xml.questionnaire.QuestionOpenType;
import de.his.zofar.xml.questionnaire.QuestionSingleChoiceType;
import de.his.zofar.xml.questionnaire.SectionBodyType;
import de.his.zofar.xml.questionnaire.SectionHeaderType;
import de.his.zofar.xml.questionnaire.SectionType;
import de.his.zofar.xml.questionnaire.TextTitleType;
import eu.dzhw.zofar.management.utils.string.StringUtils;
import model.writerparagraphs.TextParagraph;

public class ContainerMap extends AbstractMap {
	private static final QuestionSingleChoiceType[] DUMMY_SC = new QuestionSingleChoiceType[0];
	private static final MultipleChoiceType[] DUMMY_MC = new MultipleChoiceType[0];	
	private static final QuestionOpenType[] DUMMY_OP = new QuestionOpenType[0];
	private static final MatrixQuestionSingleChoiceType[] DUMMY_MSC = new MatrixQuestionSingleChoiceType[0];
	
	
	// Constructor
	public ContainerMap() {
		this.type = Type.UNKNOWN;
	}
	
	// SetMethode
	@Override
	public void setType(Type type) throws ZofarMapException {
		if(type == null)throw new ZofarMapException("type is null");
		boolean valid = false;
		if(type == Type.SECTION)valid = true;
		if(type == Type.UNKNOWN)valid = true;
		if(!valid)throw new ZofarMapException("no valid Type");
		this.type = type;		
	}

	@Override
	public XmlObject convert() {
		for (Object child : this.childElement) {
			System.out.println("Child ("+this.type+") of (" + this.getClass().getName() + ") : " + child.toString());
		}
		if(type == Type.SECTION) {
			// Section erstellen
			final SectionType section = SectionType.Factory.newInstance();
			section.setUid(StringUtils.getInstance().randomString(10,false));
			
			// Sichtbarkeit definieren (Inhalt eines EL-Ausdrucks ohne umgebenen #{} )
			section.setVisible("true");

			// Sektionsüberschrift
			final SectionHeaderType sectionHeader = section.addNewHeader();
			for(Object header : this.headerElement) {
				System.out.println("Header of ("+this.getClass().getName()+") : "+header.getClass().getName());
				if ((TextParagraph.class).isAssignableFrom(header.getClass())) {
					TextParagraph tmp = (TextParagraph)header;
					if (tmp.getStyle() == 5) {
						final TextTitleType title = sectionHeader.addNewTitle();
						title.setUid(StringUtils.getInstance().randomString(10,false));
						title.newCursor().setTextValue(tmp.getContent());
					}
					else {
						System.out.println("Unhandeled Header Type in (" + this.getClass().getName() + ")"+tmp);
					}
				}
			}
			
			final SectionBodyType sectionBody = section.addNewBody();
			sectionBody.setUid("body");
			
			for(Object child : this.childElement) {
//				System.out.println("Child of ("+this.getClass().getName()+") : "+child.getClass().getName());
				if((QuestionSingleChoiceType.class).isAssignableFrom(child.getClass())) {
					final List<QuestionSingleChoiceType> questions = new ArrayList<QuestionSingleChoiceType>(Arrays.asList(sectionBody.getQuestionSingleChoiceArray()));
					questions.add((QuestionSingleChoiceType)child);
					sectionBody.setQuestionSingleChoiceArray(questions.toArray(DUMMY_SC));	
				}
				else if((MultipleChoiceType.class).isAssignableFrom(child.getClass())) {
					final List<MultipleChoiceType> questions = new ArrayList<MultipleChoiceType>(Arrays.asList(sectionBody.getMultipleChoiceArray()));
					questions.add((MultipleChoiceType)child);
					sectionBody.setMultipleChoiceArray(questions.toArray(DUMMY_MC));	
				}
				else if((QuestionOpenType.class).isAssignableFrom(child.getClass())) {
					final List<QuestionOpenType> questions = new ArrayList<QuestionOpenType>(Arrays.asList(sectionBody.getQuestionOpenArray()));
					questions.add((QuestionOpenType)child);
					sectionBody.setQuestionOpenArray(questions.toArray(DUMMY_OP));
				}
				else if((MatrixQuestionSingleChoiceType.class).isAssignableFrom(child.getClass())) {
					final List<MatrixQuestionSingleChoiceType> questions = new ArrayList<MatrixQuestionSingleChoiceType>
					(Arrays.asList(sectionBody.getMatrixQuestionSingleChoiceArray()));
					questions.add((MatrixQuestionSingleChoiceType)child);
					sectionBody.setMatrixQuestionSingleChoiceArray(questions.toArray(DUMMY_MSC));
				}
				
				else {
					System.out.println("Unhandled Type in ContainerMap Convert : "+child.getClass());
				}
			}
			return section;
		}
		return null;
	}
	
	
	@Override
	public void addchildElement(final Object element)throws ZofarMapException{
		if(element == null) {
			throw new ZofarMapException("childElement is null");
		}
		if(!(XmlObject.class).isAssignableFrom(element.getClass()))throw new ZofarMapException("childElement is not a XmlObject");
		super.addchildElement(element);
	}

	@Override
	public void addHeaderElement(TextParagraph element) throws ZofarMapException {
		if(element == null)throw new ZofarMapException("headerElement is null");
		boolean valid = false;
		if(element.getStyle() == 1)valid = true;
		if(element.getStyle() == 5)valid = true;
		if(!valid)throw new ZofarMapException("no valid Type : "+element.getStyle());
		super.addHeaderElement(element);
	}

	
}
