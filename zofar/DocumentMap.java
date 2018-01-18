package model.converter.zofar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.xmlbeans.XmlObject;

import de.his.zofar.xml.questionnaire.MultipleChoiceItemType;
import de.his.zofar.xml.questionnaire.PageType;
import de.his.zofar.xml.questionnaire.PreloadType;
import de.his.zofar.xml.questionnaire.PreloadsType;
import de.his.zofar.xml.questionnaire.QuestionOpenType;
import de.his.zofar.xml.questionnaire.QuestionSingleChoiceResponseDomainType;
import de.his.zofar.xml.questionnaire.QuestionnaireDocument;
import de.his.zofar.xml.questionnaire.QuestionnaireType;
import de.his.zofar.xml.questionnaire.TransitionType;
import de.his.zofar.xml.questionnaire.TransitionsType;
import de.his.zofar.xml.questionnaire.VariableType;
import de.his.zofar.xml.questionnaire.VariablesType;
import model.writerparagraphs.TextParagraph;

public class DocumentMap extends ContainerMap {

	private static final PageType[] DUMMY_PAGE = new PageType[0];
	private Map<String, VariableType.Type.Enum> variables = new HashMap<String, VariableType.Type.Enum>();

	@Override
	public XmlObject convert() {
		if (type == Type.DOCUMENT) {
			final QuestionnaireDocument doc = QuestionnaireDocument.Factory.newInstance();
			final QuestionnaireType qml = doc.addNewQuestionnaire();

			qml.setDescription("");
			qml.setName("Name");
			qml.setLanguage(QuestionnaireType.Language.DE);

			final PreloadsType preloads = qml.addNewPreloads();
			final PreloadType preload = preloads.addNewPreload();
			preload.setName("token");
			preload.setPassword("token");

			for (Object header : this.headerElement) {
				System.out.println("Header of (" + this.getClass().getName() + ") : " + header.getClass().getName());
				if ((TextParagraph.class).isAssignableFrom(header.getClass())) {
					TextParagraph tmp = (TextParagraph) header;
					System.out.println("Unhandeled Header Type in (" + this.getClass().getName() + ")" + tmp);

				}
			}

			for (Object child : this.childElement) {
				if ((PageType.class).isAssignableFrom(child.getClass())) {
					final List<PageType> pages = new ArrayList<PageType>(Arrays.asList(qml.getPageArray()));
					pages.add((PageType) child);
					qml.setPageArray(pages.toArray(DUMMY_PAGE));
				}
			}

			final XmlObject[] varObjs = doc.selectPath("//*[@variable]");
			if (varObjs != null) {
				for (XmlObject varObj : varObjs) {
					if ((QuestionSingleChoiceResponseDomainType.class).isAssignableFrom(varObj.getClass())) {
						final QuestionSingleChoiceResponseDomainType tmp = (QuestionSingleChoiceResponseDomainType) varObj;
						variables.put(tmp.getVariable(), VariableType.Type.SINGLE_CHOICE_ANSWER_OPTION);
					} else if ((MultipleChoiceItemType.class).isAssignableFrom(varObj.getClass())) {
						final MultipleChoiceItemType tmp = (MultipleChoiceItemType) varObj;
						variables.put(tmp.getVariable(), VariableType.Type.BOOLEAN);
					} 
					else if ((QuestionOpenType.class).isAssignableFrom(varObj.getClass())) {
						final QuestionOpenType tmp = (QuestionOpenType) varObj;
						variables.put(tmp.getVariable(), VariableType.Type.STRING);
					} 
					else {
						System.out.println("unhandled varObj : " + varObj.getClass());
					}
				}
			}

			if (!variables.isEmpty()) {
				/*
				 * Variablen anlegen
				 */
				final VariablesType variablesArea = qml.addNewVariables();
				for (final Map.Entry<String, VariableType.Type.Enum> variable : variables.entrySet()) {
					final VariableType variableItem = variablesArea.addNewVariable();
					variableItem.setName(variable.getKey());
					variableItem.setType(variable.getValue());
				}
			}

			// Serialize Navigation
			PageType[] pages = qml.getPageArray();
			int pageCount = pages.length;

			pages[0].setUid("index");
			pages[pageCount - 1].setUid("end");

			for (int i = 0; i < pageCount; i++) {
				final PageType page = pages[i];
				if (i < (pageCount - 1)) {
					TransitionsType transArea = page.getTransitions();
					if (transArea == null)
						transArea = page.addNewTransitions();
					if (transArea.sizeOfTransitionArray() == 0) {
						final TransitionType trans = transArea.addNewTransition();
						trans.setTarget(pages[i + 1].getUid());
						trans.setCondition("true");
					}
				} else
					page.unsetTransitions();
			}

			return doc;
		}
		return null;

	}

	// SetMethode
	@Override
	public void setType(Type type) throws ZofarMapException {
		if (type == null)
			throw new ZofarMapException("type is null");
		boolean valid = false;
		if (type == Type.DOCUMENT)
			valid = true;
		if (!valid)
			throw new ZofarMapException("no valid Type");
		this.type = type;
	}

}
