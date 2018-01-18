package model.converter.zofar;

import java.util.ArrayList;
import java.util.List;

import org.apache.xmlbeans.XmlObject;

import de.his.zofar.xml.questionnaire.MatrixHeaderType;
import de.his.zofar.xml.questionnaire.MatrixQuestionSingleChoiceItemType;
import de.his.zofar.xml.questionnaire.MatrixQuestionSingleChoiceResponseDomainType;
import de.his.zofar.xml.questionnaire.MatrixQuestionSingleChoiceType;
import de.his.zofar.xml.questionnaire.MultipleChoiceItemType;
import de.his.zofar.xml.questionnaire.MultipleChoiceResponseDomainType;
import de.his.zofar.xml.questionnaire.MultipleChoiceType;
import de.his.zofar.xml.questionnaire.QuestionHeaderType;
import de.his.zofar.xml.questionnaire.QuestionOpenType;
import de.his.zofar.xml.questionnaire.QuestionSingleChoiceAnswerOptionType;
import de.his.zofar.xml.questionnaire.QuestionSingleChoiceResponseDomainType;
import de.his.zofar.xml.questionnaire.QuestionSingleChoiceType;
import de.his.zofar.xml.questionnaire.TextInstructionType;
import de.his.zofar.xml.questionnaire.TextQuestionType;
import eu.dzhw.zofar.management.utils.string.StringUtils;
import model.writerparagraphs.TextParagraph;

public class QuestionMap extends AbstractMap {

	// Constructor
	public QuestionMap() {
		this.type = Type.UNKNOWN;
	}

	// SetMethode
	@Override
	public void setType(Type type) throws ZofarMapException {

		if (type == null)
			throw new ZofarMapException("type is null");
		boolean valid = false;
		if (type == Type.SC)
			valid = true;
		if (type == Type.MC)
			valid = true;
		if (type == Type.OPEN)
			valid = true;
		if (type == Type.MSC)
			valid = true;
		if (type == Type.LIKS)
			valid = true;
		if (type == Type.UNKNOWN)
			valid = true;
		if (!valid)
			throw new ZofarMapException("no valid Type");
		this.type = type;
	}

	@Override
	public XmlObject convert() {
		for (Object child : this.childElement) {
			System.out.println("Child ("+this.type+") of (" + this.getClass().getName() + ") : " + child.toString());
		}
		if (type == Type.SC) {
			
			// einfache SC Frage
			// singleChoiceQuestion = scq
			final QuestionSingleChoiceType scq = QuestionSingleChoiceType.Factory.newInstance();
			scq.setUid(StringUtils.getInstance().randomString(10,false));

			/*
			 * Fragen Header
			 */
			// singleChoiceQuestionHeader = scqh
			final QuestionHeaderType scqh = scq.addNewHeader();
			// Fragetext
			for(TextParagraph headerelement : headerElement){
				if(headerelement.getStyle() == 7) {
					final TextQuestionType SCquestiontext = scqh.addNewQuestion();
					SCquestiontext.setUid(StringUtils.getInstance().randomString(10,false));
					SCquestiontext.newCursor().setTextValue(headerelement.getContent());
				}
				// singleChoiceQuestionHeaderInstruction = scqhIns
				else if(headerelement.getStyle() == 8){
					final TextInstructionType scqhInst = scqh.addNewInstruction();
					scqhInst.setUid(StringUtils.getInstance().randomString(10,false));
					scqhInst.newCursor().setTextValue(headerelement.getContent());
				}
			}

			/*
			 * Fragen RDC
			 */
			// singleChoiceQuestionRdc = scqRdc
			final QuestionSingleChoiceResponseDomainType scqRdc = scq.addNewResponseDomain();
			scqRdc.setUid(StringUtils.getInstance().randomString(10,false));

			// Variable verknüpfen
			scqRdc.setVariable(StringUtils.getInstance().randomString(10,false));

			// SC Fragenparameter
			scqRdc.setDirection(QuestionSingleChoiceResponseDomainType.Direction.VERTICAL);
			scqRdc.setMissingSeparated(true);
			scqRdc.setShowValues(true);

			// SC Antwortoptionen
			
			int index = 1;
			for(Object child : this.childElement) {
				if((TextParagraph.class).isAssignableFrom(child.getClass())){
					final TextParagraph tmp = (TextParagraph)child;
					final QuestionSingleChoiceAnswerOptionType scao = scqRdc.addNewAnswerOption();
					scao.setUid("scao" + index);
					scao.setValue(index + "");
					scao.setMissing(false);
					scao.setLabel2(tmp.getContent());
					index++;
				}
			}
			
			return scq;
		}
		
		// einfache MC Frage
		if(type == Type.MC) {
			// multipleChoiceQuestion = mcq
			MultipleChoiceType mcq = MultipleChoiceType.Factory.newInstance();
			mcq.setUid(StringUtils.getInstance().randomString(10,false));
			
			/*
			 * Fragen Header
			 */
			// multipleChoiceQuestionHeader = mcqh
			final QuestionHeaderType mcqh = mcq.addNewHeader();

			// Fragetext
			for(TextParagraph headerelement : headerElement) {
				if(headerelement.getStyle() == 7) {
					final TextQuestionType MCquestiontext = mcqh.addNewQuestion();
					MCquestiontext.setUid(StringUtils.getInstance().randomString(10,false));
					MCquestiontext.newCursor().setTextValue(headerelement.getContent());
				}
				// multipleChoiceQuestionHeaderInstruction = mcqhIns 
				else if(headerelement.getStyle() == 8) {
					final TextInstructionType mcqhIns = mcqh.addNewInstruction();
					mcqhIns.setUid(StringUtils.getInstance().randomString(10,false));
					mcqhIns.newCursor().setTextValue(headerelement.getContent());
				}
			}			
			/*
			 * Fragen RDC
			 */
			// multipleChoiceQuestionRdc = mcqRdc
			MultipleChoiceResponseDomainType mcqRdc = mcq.addNewResponseDomain();
			mcqRdc.setUid(StringUtils.getInstance().randomString(10,false));
			mcqRdc.setItemClasses(true);
			int index = 1;
			for(Object child : this.childElement) {
				if((TextParagraph.class).isAssignableFrom(child.getClass())) {
					final TextParagraph tmp = (TextParagraph)child;
					final MultipleChoiceItemType mcao = mcqRdc.addNewAnswerOption();
					mcao.setUid("mcao" + index);
					mcao.setVariable(StringUtils.getInstance().randomString(10,false));
					mcao.setMissing(false);
					mcao.setLabel2(tmp.getContent());
					index++;
				}
			}
			
			return mcq;
		}		
		// Open Question
		if(type == Type.OPEN) {
			QuestionOpenType openq = QuestionOpenType.Factory.newInstance();
			openq.setUid(StringUtils.getInstance().randomString(10,false));
			openq.setVariable(StringUtils.getInstance().randomString(10,false));
			/*
			 * Fragen Header
			 */
			final QuestionHeaderType openqh = openq.addNewHeader();
			
			// Fragetext
			for(TextParagraph headerelement : headerElement) {
				if(headerelement.getStyle() == 7) {
					final TextQuestionType openqtext = openqh.addNewQuestion();
					openqtext.setUid(StringUtils.getInstance().randomString(10,false));
					openqtext.newCursor().setTextValue(headerelement.getContent());
				}
				else if(headerelement.getStyle() == 8) {
					final TextInstructionType openqhIns = openqh.addNewInstruction();
					openqhIns.setUid(StringUtils.getInstance().randomString(10,false));
					openqhIns.newCursor().setTextValue(headerelement.getContent());
				}
			}			
			return openq;				
		}
		// MATRIXSC Frage
		if(type == Type.MSC) {
			final List<TextParagraph> answeroptions = new ArrayList<TextParagraph>();
			final List<TextParagraph> matrixitems = new ArrayList<TextParagraph>();
			MatrixQuestionSingleChoiceType msc = MatrixQuestionSingleChoiceType.Factory.newInstance();
			msc.setUid(StringUtils.getInstance().randomString(10,false));
			/*
			 * Fragen Header
			 */
			final MatrixHeaderType mscqh = msc.addNewHeader();
			
			// Fragetext
			for(TextParagraph headerelement : headerElement) {
				if(headerelement.getStyle() == 7) {
					final TextQuestionType msctext = mscqh.addNewQuestion();
					msctext.setUid(StringUtils.getInstance().randomString(10,false));
					msctext.newCursor().setTextValue(headerelement.getContent());
				}
				else if(headerelement.getStyle() == 8) {
					final TextInstructionType mscqhIns = mscqh.addNewInstruction();
					mscqhIns.setUid(StringUtils.getInstance().randomString(10,false));
					mscqhIns.newCursor().setTextValue(headerelement.getContent());
				}
			}
			/*
			 * Fragen RDC
			 */
			MatrixQuestionSingleChoiceResponseDomainType mscqRdc = msc.addNewResponseDomain();
			mscqRdc.setItemClasses(true);
			mscqRdc.setUid(StringUtils.getInstance().randomString(10,false));
			
			for(Object tmp : childElement){
				final TextParagraph tmpc = (TextParagraph)tmp;
				if(tmpc.getStyle() == 16) answeroptions.add(tmpc);
				if(tmpc.getStyle() == 21) answeroptions.add(tmpc);
				if(tmpc.getStyle() == 22) matrixitems.add(tmpc);
			}				
			for(TextParagraph matrixitem : matrixitems) {
				final MatrixQuestionSingleChoiceItemType mscitem = mscqRdc.addNewItem();
				mscitem.setUid(StringUtils.getInstance().randomString(10,false));
				final QuestionHeaderType itemheader = mscitem.addNewHeader();
				final TextQuestionType itemhq = itemheader.addNewQuestion();
				itemhq.setUid(StringUtils.getInstance().randomString(10,false));
				itemhq.newCursor().setTextValue(matrixitem.getContent());
				QuestionSingleChoiceResponseDomainType itemrdc = mscitem.addNewResponseDomain();
				itemrdc.setUid(StringUtils.getInstance().randomString(10,false));
				itemrdc.setVariable(StringUtils.getInstance().randomString(10,false));
				int index = 1;
				for(TextParagraph mitemao : answeroptions){						
					final TextParagraph tmp = (TextParagraph)mitemao;
					final QuestionSingleChoiceAnswerOptionType scao = itemrdc.addNewAnswerOption();
					scao.setUid("scao" + index);
					scao.setValue(mscitem.getUid()+"_"+index + "");
					if(tmp.getStyle() == 21)scao.setMissing(true);
					scao.setLabel2(tmp.getContent());						
					index++;
				}					
			}						
			return msc;	
		}
		
		// Likertskala Frage
		if(type == Type.LIKS) {
			final List<TextParagraph> answeroptions = new ArrayList<TextParagraph>();
			MatrixQuestionSingleChoiceType liks = MatrixQuestionSingleChoiceType.Factory.newInstance();
			liks.setUid(StringUtils.getInstance().randomString(10,false));
			/*
			 * Fragen Header
			 */
			final MatrixHeaderType liksqh = liks.addNewHeader();
			
			// Fragetext
			for(TextParagraph headerelement : headerElement) {
				if(headerelement.getStyle() == 7) {
					final TextQuestionType likstext = liksqh.addNewQuestion();
					likstext.setUid(StringUtils.getInstance().randomString(10,false));
					likstext.setBlock(true);
					likstext.newCursor().setTextValue(headerelement.getContent());
				}
				else if(headerelement.getStyle() == 8) {
					final TextInstructionType liksqhIns = liksqh.addNewInstruction();
					liksqhIns.setUid(StringUtils.getInstance().randomString(10,false));
					liksqhIns.newCursor().setTextValue(headerelement.getContent());
				}
			}
			/*
			 * Fragen RDC
			 */
			MatrixQuestionSingleChoiceResponseDomainType liksRdc = liks.addNewResponseDomain();
			liksRdc.setUid(StringUtils.getInstance().randomString(10,false));
			liksRdc.setIsDifferential(true);
			
			for(Object tmp : childElement) {
				final TextParagraph tmpc = (TextParagraph)tmp;
				if(tmpc.getStyle() == 23) answeroptions.add(tmpc);
				if(tmpc.getStyle() == 24) answeroptions.add(tmpc);
				if(tmpc.getStyle() == 25) answeroptions.add(tmpc);
			}
				final MatrixQuestionSingleChoiceItemType likitem = liksRdc.addNewItem();
				likitem.setUid(StringUtils.getInstance().randomString(10,false));
				final QuestionHeaderType itemheader = likitem.addNewHeader();
				final TextQuestionType itemhq = itemheader.addNewQuestion();
				itemhq.setUid(StringUtils.getInstance().randomString(10,false));
				itemhq.setBlock(true);
				QuestionSingleChoiceResponseDomainType itemrdc = likitem.addNewResponseDomain();
				itemrdc.setUid(StringUtils.getInstance().randomString(10,false));
				itemrdc.setVariable(StringUtils.getInstance().randomString(10,false));
				int index = 1;
				for(TextParagraph liksitemao : answeroptions) {
					final TextParagraph tmp = (TextParagraph)liksitemao;
					final QuestionSingleChoiceAnswerOptionType scao = itemrdc.addNewAnswerOption();
					if(tmp.getStyle() == 23 || tmp.getStyle() == 25) scao.setLabel2(tmp.getContent());
					scao.setUid("answer" + index);
					scao.setValue(likitem.getUid()+"_"+index + "");
					index++;
				}			
			return liks;
		}	
		else {
			System.out.println("Unhandled Question Type : " + this.type);
		}

		return null;
	}

	@Override
	public void addchildElement(final Object element) throws ZofarMapException {
		if (element == null) {
			throw new ZofarMapException("childElement is null");
		}
		if (!(TextParagraph.class).isAssignableFrom(element.getClass()))
			throw new ZofarMapException("childElement is not a TextParagraph");
		super.addchildElement(element);
	}

	@Override
	public void addHeaderElement(TextParagraph element) throws ZofarMapException {
		if (element == null)
			throw new ZofarMapException("headerElement is null");
		boolean valid = false;
		if (element.getStyle() == 7)valid = true;
		if (element.getStyle() == 8)valid = true;
		if (!valid)
			throw new ZofarMapException("no valid Type");
		super.addHeaderElement(element);
	}

}
