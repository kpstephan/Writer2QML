/*

 Copyright 2004-2008 Karsten Stephan

 This file is part of Writer2QML.

 Writer2QML is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Writer2QML is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with Writer2QML. If not, see <http://www.gnu.org/licenses/>.

*/


/*
	 * Created on 10.02.2008
	 *
	 * verwendet noch das QML-Modell aus OOoDataReader
	 * Später dieses Modell überarbeiten
	 *
	 *
	 *
	 * Konvertiert OOo nach QML
	 *
	 *
	 * q&d Hack: Variablenweitergabe für output
	 * Eleganter machen wer was aufruft
	 *
	 *
	 *
	 *
	 * parsing und ergebnisüberprüfung trennen
	 *
	 *
	 */

package main;

	import java.util.ArrayList;
import java.util.HashMap;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import model.simplequestionnaire.Qstn;
import model.writerparagraphs.TextParagraph;
import model.writerparagraphs.TextParagraphList;

	public class W2qOoDataReader extends DefaultHandler{


		/* Dokumentation:
		 *
		 * Einlesen der OOo Writer Elemente über Sax
		 * Sartelement, Endelement und Char ..
		 *
		 * Fragebogen als Kompositum
		 *
		 * Alle gelesenen Objekte werden dem Objekt zugewiesen, das mit currentSection
		 * referenziert wird. Currentsection zeigt zuerst auf das qstn objekt. Sobald
		 * eine Überschrift eingelesen wird, wird eine neue Section erzeugt, die von
		 * currentSection referenziert wird.
		 *
		 * Ergebnis: welche Objekte werden von außen gelsesen
		 * eigenes Qstn Objekt (könnte auch übetrgeben werden)
		 *
		 */

		private TextParagraphList textParagraphList;

        private boolean debugMode = false;

		//private int valueCount;
		@SuppressWarnings("unused")
		private Locator locator;

		//q&d zum weiterreichen des Dateinamens
		@SuppressWarnings("unused")
		private String aFileName;

		private Qstn qstn;

	    //Listen zum Speichern der Formatvorlagen Namen

		ArrayList<String> qstnTitleTags;
		ArrayList<String> introTags;
		ArrayList<String> introItemTags;

		ArrayList<String> captionTags;
		ArrayList<String> captionNewPageTags;

		ArrayList<String> questionTags;
		ArrayList<String> instructionTags;

		ArrayList<String> choiceSingleTags;
		ArrayList<String> choiceSingleNonOpinionTags;
		ArrayList<String> choiceMultipleTags;
		ArrayList<String> openaddonTags;

		ArrayList<String> choiceOpenTags;

		ArrayList<String> matrixHeadSingleTags;
		ArrayList<String> matrixSingleNonOpinionTags;
		ArrayList<String> matrixHeadMultipleTags;
		ArrayList<String> matrixOpenTags;

		ArrayList<String> matrixItemTags;

		ArrayList<String> likertLeftTags;
		ArrayList<String> likertMidTags;
		ArrayList<String> likertRightTags;

		//Liste zum Zwischenspeichern von Matrix-Antwortkategorie
		//ArrayList matrixHeadCategiories;


		StringBuffer titleText = new StringBuffer("");
		StringBuffer introText = new StringBuffer("");
		StringBuffer introItemText = new StringBuffer("");
		StringBuffer captionText = new StringBuffer("");
		StringBuffer captionNewPageText = new StringBuffer("");
		StringBuffer questionText = new StringBuffer("");
		StringBuffer instructionText = new StringBuffer("");
		StringBuffer choiceSingleText = new StringBuffer("");
		StringBuffer choiceSingleNonOpinionText = new StringBuffer("");
		StringBuffer choiceMultipleText = new StringBuffer("");
		StringBuffer choiceOpenAddonText = new StringBuffer("");
		StringBuffer choiceOpenText = new StringBuffer("");
		StringBuffer matrixHeadSingleText = new StringBuffer("");
		StringBuffer matrixHeadMultipleText = new StringBuffer("");
		StringBuffer matrixSingleText = new StringBuffer("");
		StringBuffer matrixOpenText = new StringBuffer("");
		StringBuffer matrixSingleNonOpinionText = new StringBuffer("");
		StringBuffer matrixItemText = new StringBuffer("");
		StringBuffer likertLeftText = new StringBuffer("");
		StringBuffer likertMidText = new StringBuffer("");
		StringBuffer likertRightText = new StringBuffer("");


		//Konstanten für Absatzparsing
		private final static int ITEMSTATE_NONE = 0;
		private final static int ITEMSTATE_CLOSED = 1;
		private final static int ITEMSTATE_OPEN = 2;

		//Fragebogen
		//Qstn qstn = new Qstn();
		//erklären: insgesamt gesamte LLogik erklären
		//QstnSection currentSection = qstn;

		//Variablen für Parsing
		int parsingState;
		int formerParsingState;

		static final int psNone = 0;
		static final int psTitle = 1;
		static final int psIntro = 3;
		static final int psIntroItem = 4;
		static final int psCaption = 5;
		static final int psCaptionNewPage = 6;
		static final int psQuestion = 7;
		static final int psInstruction = 8;
		static final int psChoiceSingle = 9;
		static final int psChoiceSingleNonOpinion = 10;
		static final int psChoiceMultiple = 12;
		static final int psChoiceOpenAddon = 14;
		static final int psChoiceOpen = 15;
		static final int psMatrixHeadSingle = 16;
		static final int psMatrixHeadMultiple = 18;
		static final int psMatrixOpen = 20;
		static final int psMatrixSingleNonOpinion = 21;
		static final int psMatrixItem = 22;
		static final int psLikertLeft = 23;
		static final int psLikertMid = 24;
		static final int psLikertRight = 25;

		// Verbale Bezeichnungen für Fehlermeldungen
		//Später Privat, ist hier für writer2qml freigegeben
		public HashMap<Integer, String> styleMap = new HashMap<Integer, String>();

		//Aus Kompatibilitätserwägungen werden Absatzformatnamen
		//aus älteren Versionen akzeptiert
		private HashMap<String, String> compatibilityMap = new HashMap<String, String>();


		//Formatlisten für Parsing
		private ArrayList<Integer> styleMustNotBeEmpty;
		private ArrayList<Integer> styleMustBeEmpty;


	/*
		//Evtl Buffer und Error in eigenem Objekt kapseln
		//Ein Absatz wird zunächst in writerItem Objekt gespeichert
		public class WriterParagraph {
			public int parStyle;
			public String parContent;
			//Konstruktor
			public WriterParagraph( int style, String content ){
				parStyle = style;
				parContent = content;
			}

		}

	*/

		//Ein Formatierungsfehler wird in StyleCodingError-Objekt gekapselt
		/*
		private class StyleCodingError {
			public int parNumber;
			public int errorNumber;
			public String errorDescription;
			//Konstruktor
			public StyleCodingError( int parNumber, String errorDescription ){
				this.parNumber = parNumber;
				this.errorDescription = errorDescription;
			}
		}
		*/



		//Liste der WriterItem-Objekte
		//ist zum testen mit public geöffnet, später andere struktur: iteratormethode
		public ArrayList<WriterParagraph> writerContentBuffer = new ArrayList<WriterParagraph>();


		//Liste der Formatierungfehler
		//ist zum testen mit public geöffnet, später andere struktur: iteratormethode
        public ArrayList<StyleCodingError> codingErrors = new ArrayList<StyleCodingError>();


		//Liste zum Prüfen der Einträge
		int[][]allowedStyleOrder = {

				{ psTitle, psIntro },
				{ psTitle, psCaption },
				{ psTitle, psCaptionNewPage },
				{ psTitle, psIntroItem },
				{ psTitle, psInstruction },
				{ psTitle, psQuestion},

				{ psCaption, psQuestion },
				{ psCaption, psIntro },
				{ psCaption, psIntroItem },
				{ psCaption, psInstruction },

				{ psCaptionNewPage, psCaption },
				{ psCaptionNewPage, psIntro },
				{ psCaptionNewPage, psIntroItem },
				{ psCaptionNewPage, psInstruction },
				{ psCaptionNewPage, psQuestion },

				{ psIntro, psIntro },
				{ psIntro, psCaption},
				{ psIntro, psIntroItem },
				{ psIntro, psInstruction },
				{ psIntro, psQuestion },

				{ psIntroItem, psIntroItem },
				{ psIntroItem, psQuestion },
				{ psIntroItem, psInstruction },
				{ psIntroItem, psChoiceSingle },
				{ psIntroItem, psChoiceSingleNonOpinion },
				{ psIntroItem, psChoiceMultiple },
				{ psIntroItem, psChoiceOpenAddon },
				{ psIntroItem, psChoiceOpen },
				{ psIntroItem, psMatrixHeadSingle },
				{ psIntroItem, psMatrixHeadMultiple },
				{ psIntroItem, psMatrixOpen },
			    { psIntroItem, psLikertLeft },

				{ psQuestion, psIntroItem },
				{ psQuestion, psInstruction },
				{ psQuestion, psChoiceSingle },
				{ psQuestion, psChoiceSingleNonOpinion},
				{ psQuestion, psChoiceMultiple },
				{ psQuestion, psChoiceOpenAddon },
				{ psQuestion, psChoiceOpen },
				{ psQuestion, psMatrixHeadSingle },
				{ psQuestion, psMatrixHeadMultiple },
			    { psQuestion, psLikertLeft },

			    { psInstruction, psInstruction },
			    { psInstruction, psIntroItem },
			    { psInstruction, psQuestion },
			    { psInstruction, psChoiceSingle },
			    { psInstruction, psChoiceSingleNonOpinion },
			    { psInstruction, psChoiceMultiple },
			    { psInstruction, psChoiceOpenAddon },
			    { psInstruction, psChoiceOpen },
			    { psInstruction, psMatrixHeadSingle },
			    { psInstruction, psMatrixHeadMultiple },
			    { psInstruction, psLikertLeft },

			    { psChoiceSingle, psChoiceSingle },
			    { psChoiceSingle, psChoiceSingleNonOpinion },
			    { psChoiceSingle, psChoiceOpenAddon },
				{ psChoiceSingle, psCaption },
				{ psChoiceSingle, psCaptionNewPage },
				{ psChoiceSingle, psIntro },
				{ psChoiceSingle, psIntroItem },
				{ psChoiceSingle, psInstruction },
				{ psChoiceSingle, psQuestion },


				{ psChoiceSingleNonOpinion, psCaption },
				{ psChoiceSingleNonOpinion, psCaptionNewPage },
				{ psChoiceSingleNonOpinion, psIntro },
				{ psChoiceSingleNonOpinion, psIntroItem },
				{ psChoiceSingleNonOpinion, psInstruction },
				{ psChoiceSingleNonOpinion, psQuestion },


				{ psChoiceMultiple, psChoiceMultiple },
				{ psChoiceMultiple, psChoiceOpenAddon },
				{ psChoiceMultiple, psCaption },
				{ psChoiceMultiple, psCaptionNewPage },
				{ psChoiceMultiple, psIntro },
				{ psChoiceMultiple, psIntroItem },
				{ psChoiceMultiple, psInstruction },
				{ psChoiceMultiple, psQuestion },


				{ psChoiceOpenAddon, psChoiceOpenAddon },
				{ psChoiceOpenAddon, psChoiceSingle },
				{ psChoiceOpenAddon, psChoiceSingleNonOpinion },
				{ psChoiceOpenAddon, psChoiceMultiple },
				{ psChoiceOpenAddon, psCaption },
				{ psChoiceOpenAddon, psCaptionNewPage },
				{ psChoiceOpenAddon, psIntro },
				{ psChoiceOpenAddon, psIntroItem },
				{ psChoiceOpenAddon, psInstruction },
				{ psChoiceOpenAddon, psQuestion },


				{ psChoiceOpen, psCaption},
				{ psChoiceOpen, psCaptionNewPage},
				{ psChoiceOpen, psIntro },
				{ psChoiceOpen, psIntroItem },
				{ psChoiceOpen, psInstruction },
				{ psChoiceOpen, psQuestion},

				{ psMatrixHeadSingle, psMatrixHeadSingle },
				{ psMatrixHeadSingle, psMatrixSingleNonOpinion },
				{ psMatrixHeadSingle, psMatrixOpen },
				{ psMatrixHeadSingle, psMatrixItem },

				{ psMatrixOpen, psMatrixHeadSingle},
				{ psMatrixOpen, psMatrixSingleNonOpinion},
				{ psMatrixOpen, psMatrixHeadMultiple},
				{ psMatrixOpen, psMatrixOpen},
				{ psMatrixOpen, psMatrixItem},

				{ psMatrixHeadMultiple, psMatrixHeadMultiple },
				{ psMatrixHeadMultiple, psMatrixOpen },
				{ psMatrixHeadMultiple, psMatrixItem },

				{psMatrixSingleNonOpinion, psMatrixItem},

				{ psMatrixItem, psMatrixItem },
				{ psMatrixItem, psCaption },
				{ psMatrixItem, psCaptionNewPage },
				{ psMatrixItem, psIntro },
				{ psMatrixItem, psIntroItem },
				{ psMatrixItem, psInstruction },
				{ psMatrixItem, psQuestion },

				{ psLikertLeft, psLikertMid },

				{ psLikertMid, psLikertMid  },
				{ psLikertMid, psLikertRight  },

				{ psLikertRight, psCaption },
				{ psLikertRight, psCaptionNewPage },
				{ psLikertRight, psIntro },
				{ psLikertRight, psIntroItem },
				{ psLikertRight, psInstruction },
				{ psLikertRight, psQuestion }


				};





		//Objekte zum Speichern der Werte während Parsing
		//QstnItem currentItem;
		//Category currentCategory;
		//QstnCaption currentCaption;
		//QstnMatrix currentMatrix;

		String currentQuestionText = "";
		String currentInstructionText = "";
		String currentSectionTitle = "";

		//Category currentMatrixCategory;

		String currentMatrixItemText = "";

		//Varaiblen für Parsing von Likert Items
		//QstnItem currentLikertItem;
		//Category currentLikertCategory;
		//String currentLikertLeft = "";
		//String currentLikertRight = "";

		//Funktion zum Ausgeben von Debug Informationen
		private void debugEcho( String echo ){
	      if (debugMode ){
		    System.out.print( echo );
		  }
		}



		//Konstruktor
		public W2qOoDataReader( TextParagraphList textParagraphList, String fileName){

			this.textParagraphList = textParagraphList;

			//q&d zum Übergeben des Dateinamens
			//Wer liest das??
			this.aFileName = fileName;

			formerParsingState = psNone;

			// Verbale Bezeichnungen für Fehlermeldungen
			styleMap.put( psTitle, "qml:title");
			styleMap.put( psIntro, "qml:intro");
			styleMap.put( psIntroItem, "qml:itemIntro");
			styleMap.put( psCaption, "qml:caption");
			styleMap.put( psCaptionNewPage, "qml:newPage");
			styleMap.put( psQuestion, "qml:question");
			styleMap.put( psInstruction, "qml:instruction");
			styleMap.put( psChoiceSingle, "qml:singleChoice");
			styleMap.put( psChoiceSingleNonOpinion, "qml:nonOpinion");
			styleMap.put( psChoiceMultiple, "qml:multipleChoice");
			styleMap.put( psChoiceOpenAddon, "qml:openChoice");
			styleMap.put( psChoiceOpen, "qml:openText");
			styleMap.put( psMatrixHeadSingle, "qml:mx_singleChoice");
			styleMap.put( psMatrixHeadMultiple, "qml:mx_multipleChoice");
			styleMap.put( psMatrixOpen, "qml:mx_openChoice");
			styleMap.put( psMatrixSingleNonOpinion, "qml:mx_nonOpinion");
			styleMap.put( psMatrixItem, "qml:matrixItem");
			styleMap.put( psLikertLeft, "qml:likert_left");
			styleMap.put( psLikertMid, "qml:likert_mid");
			styleMap.put( psLikertRight, "qml:likert_right");


			//Aus Kompatibilitätserwägungen werden Absatzformatnamen
			//aus älteren Versionen akzeptiert. Die Syntax zum Eintragen der mappings lautet:
			//compatibilityMap.put("ALIASNAME", "INTERN_VERWENDETER_NAME");

			compatibilityMap.put("qml_3a_choice_5f_single_5f_open", "qml_3a_choice_5f_openaddon");
			compatibilityMap.put("qml_3a_choice_5f_multiple_5f_open", "qml_3a_choice_5f_openaddon");
			compatibilityMap.put("qml_3a_matrix_5f_head_5f_single_5f_open", "qml_3a_matrix_5f_open");
			compatibilityMap.put("qml_3a_matrix_5f_head_5f_multiple_5f_open", "qml_3a_matrix_5f_open");

			//Die aktuellen Vorlagennamen werden ebenfalls auf diese Art und Weise bereitgestellt.
			//Dies sollte evtl. geändert werden. Dazu müssten die Variablennamen im Parsingprozess etc.
			//geändert werden.

			compatibilityMap.put("qml_3a_newPage", "qml_3a_caption_5f_newpage");
			compatibilityMap.put("qml_3a_itemIntro", "qml_3a_intro_5f_item");
			compatibilityMap.put("qml_3a_singleChoice", "qml_3a_choice_5f_single");
			compatibilityMap.put("qml_3a_nonOpinion", "qml_3a_choice_5f_single_5f_nonopinion");
			compatibilityMap.put("qml_3a_multipleChoice", "qml_3a_choice_5f_multiple");
			compatibilityMap.put("qml_3a_openChoice", "qml_3a_choice_5f_openaddon");
			compatibilityMap.put("qml_3a_openText", "qml_3a_choice_5f_open");
			compatibilityMap.put("qml_3a_mx_5f_singleChoice", "qml_3a_matrix_5f_head_5f_single");
			compatibilityMap.put("qml_3a_mx_5f_nonOpinion", "qml_3a_matrix_5f_single_5f_nonopinion");
			compatibilityMap.put("qml_3a_mx_5f_multipleChoice", "qml_3a_matrix_5f_head_5f_multiple");
			compatibilityMap.put("qml_3a_mx_5f_openChoice", "qml_3a_matrix_5f_open");
			compatibilityMap.put("qml_3a_matrixItem", "qml_3a_matrix_5f_item");





			/*
			 Liste der aktuellen Writer Vorlagen, Kodierung in odt-Datei

			  qml:title				qml_3a_title
			  qml:newpage			qml_3a_newpage
			  qml:caption 			qml_3a_caption
			  qml:intro 			qml_3a_intro
			  qml:itemIntro 		qml_3a_itemintro
			  qml:instruction 		qml_3a_instruction
			  qml:question 			qml_3a_question
			  qml:singleChoice 		qml_3a_singleChoice
			  qml:nonOpinion 		qml_3a_nonOpinion
			  qml:multipleChoice 	qml_3a_multipleChoice
			  qml:openChoice 		qml_3a_openChoice
			  qml:openText 			qml_3a_openText
			  qml:mx_singleChoice 	qml_3a_mx_5f_singleChoice
			  qml:mx_nonopinion 	qml_3a_mx_5f_nonopinion
			  qml:mx_multipleChoice qml_3a_mx_5f_multipleChoice
			  qml:mx_openChoice 	qml_3a_mx_5f_openChoice
			  qml:matrixItem		qml_3a_matrixItem
			  qml:likert_left 		qml_3a_likert_5f_left
			  qml:likert_mid 		qml_3a_likert_5f_mid
			  qml:likert_right 		qml_3a_likert_5f_right


			  Liste der intern verwendete Namen

			  qml_3a_title
              qml_3a_intro

              qml_3a_caption
              qml_3a_caption_5f_newpage

              qml_3a_intro_5f_item
              qml_3a_instruction
              qml_3a_question

              qml_3a_choice_5f_single
              qml_3a_choice_5f_single_5f_nonopinion
              qml_3a_choice_5f_multiple
              qml_3a_choice_5f_openaddon
              qml_3a_choice_5f_open

              qml_3a_matrix_5f_head_5f_single
              qml_3a_matrix_5f_head_5f_multiple
              qml_3a_matrix_5f_single_5f_nonopinion
              qml_3a_matrix_5f_open
              qml_3a_matrix_5f_item

              qml_3a_likert_5f_left
              qml_3a_likert_5f_mid
              qml_3a_likert_5f_right


			 */



			//Listen für Fehlerüberprüfung
			styleMustNotBeEmpty = new ArrayList<Integer>();
			styleMustNotBeEmpty.add( psCaption );
			styleMustNotBeEmpty.add( psIntro );
			styleMustNotBeEmpty.add( psIntroItem );
			styleMustNotBeEmpty.add( psInstruction );
			styleMustNotBeEmpty.add( psQuestion );
			styleMustNotBeEmpty.add( psChoiceSingleNonOpinion );
            styleMustNotBeEmpty.add( psMatrixSingleNonOpinion );
			styleMustNotBeEmpty.add( psMatrixItem );
			styleMustNotBeEmpty.add( psLikertLeft );
			styleMustNotBeEmpty.add( psLikertRight );

			styleMustBeEmpty = new ArrayList<Integer>();
			styleMustBeEmpty.add( psCaptionNewPage );
			styleMustBeEmpty.add( psChoiceOpen );




			//matrixHeadCategiories = new ArrayList();


			qstnTitleTags = new ArrayList<String>();
			qstnTitleTags.add("qml_3a_title");


			introTags = new ArrayList<String>();
			introTags.add( "qml_3a_intro" );

			introItemTags = new ArrayList<String>();
			introItemTags.add( "qml_3a_intro_5f_item" );

			captionTags = new ArrayList<String>();
			captionTags.add("qml_3a_caption");

			captionNewPageTags = new ArrayList<String>();
			captionNewPageTags.add("qml_3a_caption_5f_newpage");


			questionTags = new ArrayList<String>();
			questionTags.add("qml_3a_question");


			instructionTags = new ArrayList<String>();
			instructionTags.add("qml_3a_instruction");


			choiceSingleTags = new ArrayList<String>();
			choiceSingleTags.add("qml_3a_choice_5f_single");


			choiceSingleNonOpinionTags = new ArrayList<String>();
	    	choiceSingleNonOpinionTags.add("qml_3a_choice_5f_single_5f_nonopinion");


	    	choiceMultipleTags = new ArrayList<String>();
	    	choiceMultipleTags.add( "qml_3a_choice_5f_multiple" );


	    	openaddonTags = new ArrayList<String>();
	    	openaddonTags.add("qml_3a_choice_5f_openaddon");

	    	choiceOpenTags = new ArrayList<String>();
	    	choiceOpenTags.add( "qml_3a_choice_5f_open");

	    	matrixHeadSingleTags = new ArrayList<String>();
			matrixHeadSingleTags.add("qml_3a_matrix_5f_head_5f_single");


			matrixHeadMultipleTags = new ArrayList<String>();
			matrixHeadMultipleTags.add("qml_3a_matrix_5f_head_5f_multiple");


			matrixOpenTags = new ArrayList<String>();
			matrixOpenTags.add("qml_3a_matrix_5f_open");

			matrixSingleNonOpinionTags = new ArrayList<String>();
			matrixSingleNonOpinionTags.add("qml_3a_matrix_5f_single_5f_nonopinion");


			matrixItemTags = new ArrayList<String>();
			matrixItemTags.add("qml_3a_matrix_5f_item");


			likertLeftTags = new ArrayList<String>();
			likertLeftTags.add( "qml_3a_likert_5f_left" );


			likertMidTags = new ArrayList<String>();
			likertMidTags.add( "qml_3a_likert_5f_mid" );


			likertRightTags = new ArrayList<String>();
			likertRightTags.add( "qml_3a_likert_5f_right" );



		}


		//QstnObjekt wird zurückgegeben
		public Qstn getQstn(){
			return qstn;
		}



	  public void setDocumentLocator (Locator locator){
		  this.locator = locator;
	  }


	  /*

	  //Konstruktor parst gleich
	  public OOoDataReader( String dataFile ){

		  try {
		    // Use an instance of ourselves as the SAX event handler
			//!!Objekt Defaulthandler wird von Konstruktor erzeugt ?? weil keine main methode??
		    //DefaultHandler handler = new ExampleSaxEcho();
		    // Parse the input with the default (non-validating) parser
		    SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
		    //saxParser.parse( new File( qmlFile ), handler );
		    saxParser.parse( new File( dataFile ), this );
		    //System.exit( 0 );
		  } catch( Throwable t ) {
		    t.printStackTrace();
	//!!Hier nicht abbrechen sondern exception in programm behandeln
		    System.exit( 2 );
		  }

	  } //constructor


	  */




	  //Überprüft eingelesene Absätze auf Gültigkeit
	  //Bei Problemen werde der ArrayList codingError Fehlerbeschreibungsobjekte hinzugefügt
	  private boolean checkParStyles(){

		  //Listen mit Absatzgruppen für Parsing
		  //Evtl. nur einm al im Konstruktor erzeugen

		  //Absatzformate, mit denen Datei beginnen darf
		  ArrayList<Integer> validStartTags = new ArrayList<Integer>();
	      validStartTags.add( psTitle );
	      validStartTags.add( psIntro );
	      validStartTags.add( psIntroItem );
	      validStartTags.add( psCaption );
	      validStartTags.add( psCaptionNewPage );
	      validStartTags.add( psQuestion );
	      validStartTags.add( psInstruction );

	      //Styles, die im ersten Itemteil auftreten
	      //Werden auch in convertParagraphs() deklariert
	      ArrayList<Integer> itemStartTags = new ArrayList<Integer>();
	      itemStartTags.add( psIntroItem  );
	      itemStartTags.add( psQuestion );
	      itemStartTags.add( psInstruction );

	      //Absatztypen, die in einer Antwortskala mit Einfachnennung auftreten können
	      ArrayList<Integer> singleChoiceTags = new ArrayList<Integer>();
	      singleChoiceTags.add( psChoiceSingle);
	      singleChoiceTags.add( psChoiceOpenAddon );
	      singleChoiceTags.add( psChoiceSingleNonOpinion );

	      //Absatztypen, die in einer Matrix Antwortskala auftreten können
	      ArrayList<Integer> mxSingleTags = new ArrayList<Integer>();
	      mxSingleTags.add( psMatrixHeadSingle );
	      mxSingleTags.add( psMatrixOpen );
	      mxSingleTags.add( psMatrixSingleNonOpinion);



	      //Variablen für Kosistenzprüfung
		  int aStyle;
		  int aFormerStyle;
		  int followingStyle;

		  WriterParagraph par;

		  int lastScaleStart= -1;
		  int lastLikertPointStart = -1;
		  int lastMatrixScaleStart = -1;

		  int lastItemStart = -1;

		  //Flag, welches anzeigt, ob Item in einem Absatz gültig abgeschlosssen ist
		  //wird evtl. nicht mehr benötigt
		  int itemState = ITEMSTATE_NONE;


		  //Testen ob Datei gültige Absatzformate enthält
		  if( writerContentBuffer.size() == 0 ){
			  codingErrors.add( new StyleCodingError( 1, "Das Dokument enthält keine QML-Absatzvorlagen"));
		  }


		  //Testen ob Datei mit gültigem Absatzformat beginnt
		  if( writerContentBuffer.size() > 0 ){
		      //Erster Absatz muss mit gültigem Absatzformat beginnen
	      int parStyle = ((WriterParagraph) writerContentBuffer.get( 0 )).parStyle;
		      if( ! validStartTags.contains( parStyle )){
			      codingErrors.add( new StyleCodingError( 1, "Das Dokument darf nicht mit der Absatzvorlage "
				    	  + (String) styleMap.get( parStyle ) + " beginnen"));
		      }
		  }


		  /*
		   *  Über alls Absätze iterieren
		   *
		   *  Für jeden Absatz wird maximal ein Fehlerobjekt erstellt, falls ein Fehler
		   *  aufgetreten ist, werden weitere Fehler nicht mehr geprüft.
		   *  Die Fehlerobjekte werden nicht mehr nach Absatznummern sortiert, sie müssen in der Reihenfolge
		   *  ihres auftretens angelegt werden.
		   *  Die Variable parHandled dient als flag.
		   *
		   *  Reihenfolge
		   *
		   */


		  //Zählen der Items, choices für Fehlermeldungsausgabe
		  int itemCounter = 0;
		  String itemCounterStr;

		  int questionCounter;

		  //Wurde bereits ein Fehlerobjekt für diesen Absatz generiert?
		  boolean parHandled;

		  //Iterieren über alle Fehler
		  for (int i = 0; i < writerContentBuffer.size(); i++){


			  /**************  Variablen für Fehlerprüfung Initialisieren ***************************/

			  parHandled = false;


			  //Variable aStyle initialisieren
			  par = writerContentBuffer.get( i );
			  aStyle =  par.parStyle;


			  //Variable aFormerStyle initialisieren
			  if ( i > 0 ){
				//nicht in der Schleife deklarieren?
			    aFormerStyle = ((WriterParagraph) writerContentBuffer.get( i-1 )).parStyle;
			  } else aFormerStyle = psNone;


			  //Variable aFollowingStyle initialisieren
			  if ( i == writerContentBuffer.size() - 1 ){
				  followingStyle = psNone;
			  }else{
			      followingStyle = ((WriterParagraph) writerContentBuffer.get( i+1 )).parStyle;
			  }


			  //Variablen für die Fehlerüberprüfung initialisieren

			  //A Start eines Items
			  //-ItemCounter zur Ausgabe der Fehlermeldungen setzen
	       	  //-Letzten Itemstart speichern
        	  //Erster Absatz
        	  if ( i == 0 ){
        		 if ( itemStartTags.contains( aStyle )){
        			 lastItemStart = i;
        			 itemState = ITEMSTATE_OPEN;
        			 itemCounter++;
        		 }
        	  }
        	  //Absatz 2 bis N
        	  else{
        		  if((itemStartTags.contains( aStyle )) && (! itemStartTags.contains( aFormerStyle ) )){
        			  lastItemStart = i;
        			  itemState = ITEMSTATE_OPEN;
        			  itemCounter++;
        		  }
        	  }
        	  if( itemCounter == 0 ) itemCounterStr = "";
        	  else itemCounterStr = "(Item " + itemCounter + " ) ";


        	  //B Start einer Antwortskala
        	  //Letzte Zeile Speichern, in der eine Antwortskala beginnt
			  //lastScaleStart auf zeilennummer setzen, falls in der aktuellen Zeile eine Antwortskala beginnt
        	  //Besser ArrayList mit Formaten verwenden
		      if( i == 0 ){
		    	  if( ( aStyle == psChoiceSingle ) || ( aStyle == psChoiceMultiple ) || ( aStyle == psChoiceOpen ) ||
		    		  ( aStyle == psChoiceOpenAddon )){
		    		  lastScaleStart = i;
		    		  if ( itemState == ITEMSTATE_OPEN ) itemState = ITEMSTATE_CLOSED;
		    	  }
		      }
		      //Zeile > 1
		      else {

		    	  if ( (( aStyle == psChoiceSingle ) ||  ( aStyle == psChoiceMultiple ) || ( aStyle == psChoiceOpen ) ||
		    	        ( aStyle == psChoiceOpenAddon ) || (aStyle == psChoiceSingleNonOpinion)) &&
		    	       (( aFormerStyle != psChoiceSingle ) &&  ( aFormerStyle != psChoiceMultiple ) && ( aFormerStyle != psChoiceOpen ) &&
				        ( aFormerStyle != psChoiceOpenAddon) && (aFormerStyle != psChoiceSingleNonOpinion))){

		    	      lastScaleStart = i;
		    	      if ( itemState == ITEMSTATE_OPEN ) itemState = ITEMSTATE_CLOSED;
		    	  }
		      } // Zeile > 1



		      //C Letzte Zeile Speichern, in der eine Matrixskala beginnt
		      //lastMatrixScaleStart auf Zeilennummer setzen, falls in der aktuellen Zeile eine Matrix-Antwortskala beginnt
		      if( i == 0 ){
		    	  if( ( aStyle == psMatrixHeadSingle ) || ( aStyle == psMatrixSingleNonOpinion ) ||
		    		  ( aStyle == psMatrixHeadMultiple ) || (aStyle == psMatrixOpen)){
		    		  lastMatrixScaleStart = i;
		    	  }
		      }
		      //Zeile > 1
		      else {

		    	  if ( (( aStyle == psMatrixHeadSingle ) ||  ( aStyle == psMatrixHeadMultiple ) ||
		    	        ( aStyle == psMatrixOpen ) || (aStyle == psMatrixSingleNonOpinion)) &&
		    	       (( aFormerStyle != psMatrixHeadSingle ) &&  ( aFormerStyle != psMatrixHeadMultiple ) &&
				        ( aFormerStyle != psMatrixOpen ) && (aFormerStyle != psMatrixSingleNonOpinion ))){

		    	      lastMatrixScaleStart = i;
		       	  }
		      } // Zeile > 1




		      //D Itemstate auf closed Setzen, wenn Matrix-Antwortskala oder Likerrtskala vollständig ist
		      if( aStyle == psMatrixItem || aStyle == psLikertRight){
		    	  if ( itemState == ITEMSTATE_OPEN ) itemState = ITEMSTATE_CLOSED;
		      }



        	  //E Index für den letzten Start einer Likert-Mid Reihe setzen
        	  if(( aStyle == psLikertMid ) && ( aFormerStyle != psLikertMid )){
        		  lastLikertPointStart = i;
        	  }



			  /**************** Prüfen der verschiedenen Fehler ***************************/



			  //Prüfen ob das aktuelle Absatzformat nach dem vorherigen erlaubt ist
			  //Nicht das letzte Element
			  if( i > 0  ){
				  boolean match = false;
			      for( int s=0; s< allowedStyleOrder.length; s++ ){
				      //Übereinstimmung?
				      if(( aFormerStyle == allowedStyleOrder[ s ][ 0 ]) && ( aStyle == allowedStyleOrder[ s ][ 1 ] )){
				        match = true;
				        break;
				      }
			      }
			      if( ! match ){
		    	    codingErrors.add( new StyleCodingError( i+1, itemCounterStr + "Nach " +
		    	    	(String) styleMap.get( aFormerStyle ) +
		    		    " (Absatz " + ( i ) + ")" +
		    	        " darf nicht " + (String) styleMap.get( aStyle ) +
		    	        " (Absatz " + ( i+1 ) + ")" +" folgen"));
			        //System.out.println( "Fehler"+ "["+  (i+2)  + "] "+ ": Nach " + aStyle + " darf nicht " + aFollowingStyle + " folgen");
		    	    parHandled = true;
		 	      }

			  }//Zeile 1 bis N-1




			  //Diese Absätze dürfen nicht leer sein
        	  if (! parHandled){
			      if( ( par.parContent.isEmpty() ) && (  styleMustNotBeEmpty.contains( par.parStyle )  )){
			          codingErrors.add( new StyleCodingError( i+1, itemCounterStr + "Ein Absatz vom Typ " +
			    		      (String) styleMap.get( par.parStyle ) + " darf nicht leer sein"));
			          parHandled = true;
			      }
        	  }




			  //Diese Absätze müssen leer sein
        	  if (! parHandled){
			      if( ( ! par.parContent.isEmpty() ) && (  styleMustBeEmpty.contains( par.parStyle )  )){
			          codingErrors.add( new StyleCodingError( i+1, itemCounterStr + "Ein Absatz vom Typ " +
			    		      (String) styleMap.get( par.parStyle ) + " muß leer sein"));
			          parHandled = true;
			      }
        	  }




        	  //Prüfen, ob Gruppe von ItemStartAbsätzen (itemIntro, question, instruction) gültig ist
        	  //Letzter Absatz einer Itemstart Absatzgruppe
        	  if( ( itemStartTags.contains( aStyle )) && ( ! itemStartTags.contains( followingStyle )) ){
        		  //Itemstart trat bereits auf
        	      if( lastItemStart >= 0){

        	    	  //Über Absätzte Iterieren
        			  questionCounter = 0;
       				  for( int s= lastItemStart; s <= i; s++ ){
       					  WriterParagraph itemPar = writerContentBuffer.get( s );
  			              if( itemPar.parStyle == psQuestion ){
       					      questionCounter++;
       					  }
       				  }

       				  if( questionCounter == 0 ){
       					codingErrors.add( new StyleCodingError( i+1, itemCounterStr +
       						  "Eine Gruppe von Item Absatzformaten" +
       						  " (Absatz [" + ( lastItemStart + 1 ) + "] bis Absatz [" +(i+1)+ "])"+
       						  " muss genau ein Format vom Typ " +
       						  (String) styleMap.get( psQuestion ) +
       						  " enthalten"));

      				  }
       				  if( questionCounter > 1 ){
       				      codingErrors.add( new StyleCodingError( i+1, itemCounterStr +
           					"Eine Gruppe von Item Formaten" +
           					" (Absatz [" + ( lastItemStart + 1 ) + "] bis Absatz [" +(i+1)+ "])"+
           					" darf nicht mehr als ein " +
           					(String) styleMap.get( psQuestion ) +
           					" Format enthalten"));
           			  }
       				  //System.out.println("Abs: " + (i+1) + " QCounter: " + questionCounter );
       			  }//Itemstart trat bereits auf
       	      }//Letzter Absatz Itemstart






         	  //Likertskalen Prüfen, dokumentieren
        	  //LikertMid 1. Schritt
        	  //Diese Absätze dürfen nur Zahlen oder leere Strings enthalten
        	  if( aStyle == psLikertMid ){

        		  //testen, ob der Inhalt des Absatzes eine Zahl darstellt
        		  if( par.parContent.length() != 0){
        		      try{
        		          Integer.parseInt( par.parContent );
        		      }
        		      catch( NumberFormatException e){
        			      //String ist keine Zahl
        		    	  codingErrors.add( new StyleCodingError( i+1, itemCounterStr + "Ein Absatz vom Typ " +
        		    			  (String) styleMap.get( aStyle ) + " darf nur Leerzeichen oder " +
        		    			  "Zahlen enthalten"));
        		    	  //Absatz als abgehandelt markieren
        		    	  parHandled = true;
        		      }
        		  }//Absatz ist nicht leer
        	   }





        	  /*
        	  Likert BLock nach Reihenfolge testen?
        	  Konsistenz der Likert Skala Testen
        	  Absätze vom Typ Likert_MID dürfen nur leer sein
        	  oder nur Zahlen enthalten. Zahlen dürfen nur in aufsteigender
        	  Reihenfolge oder in absteigender Reihenfolge auftreten. LIkertskalen müssen mindestens
        	  zwei Skalenpunkte enhalten

              Ausprobieren: Nur abtesten, wenn kein Reihenfolgefehler aufgetreten ist
        	  (Erst Reihenfolge, dann rückwärts Skalenkosistenz)
        	  */


        	  //LikertMid: Schritt 2
        	  //Likert muss zwei Kategorien haben
        	  if ((! parHandled ) && (aStyle == psLikertMid) && (followingStyle != psLikertMid)){
        		  //Likertskala hat midestens zwei Skalenpunkte
        		  if(( i - lastLikertPointStart) < 1){

        			  //Fehlermeldung ausgeben
        			  codingErrors.add( new StyleCodingError( i+1, itemCounterStr +
      		    	    	"Eine Absatzgruppe des Typs " + (String) styleMap.get( par.parStyle )   +
      		    	    	" (Absatz [" + (i+1) + "]" +
      		    		   " muss mindestens zwei Elemente enthalten"));
        			  //Absatz als abschließend behandelt markieren
        			  parHandled = true;

        		  }
        	  }


        	  //LikertMid 3. Schritt
        	  //Abarbeiten, wenn aktueller style = mid und nächster != mid
        	  //Nur wenn noch kein Fehler im Absatz aufgetreten ist
        	  if( ! parHandled ){

        		  //Letzter Absatz einer Reihe von likertMid Absätzen
        		  if ((aStyle == psLikertMid) && (aFormerStyle == psLikertMid) && (followingStyle != psLikertMid)){

        		      //Likertskalenpunkte sind bereits mindestens einmal aufgetaucht
        		      if( lastLikertPointStart != -1 ){

        		          //System.out.println("Absatz " + (i+1) + ": Letzter Skalenstart " + (lastLikertPointStart + 1));

        			      //Typ des ersten Likertskalenpunktes ermitteln
        			      boolean firstPointIsEmpty;
        			      String parContent = ((WriterParagraph) writerContentBuffer.get( lastLikertPointStart )).parContent;
        		          if ( parContent.length() == 0 ) firstPointIsEmpty = true; else firstPointIsEmpty = false;


        		          //Alle Zellen sind einheitlich leer oder mit Zahlen besetzt
        		          WriterParagraph likertScalePar;
        		          boolean errorsFound = false;
        		          boolean pointIsEmpty;
        		          for( int s = lastLikertPointStart + 1; s <= i; s++){

        		    	      //Inhaltstyp des Skalenpunkte ermitteln
        			          likertScalePar = writerContentBuffer.get( s );
        			          if( likertScalePar.parContent.length() == 0 ) pointIsEmpty = true; else pointIsEmpty = false;

        			          //System.out.println( "Test: " + (lastLikertPointStart+1) +  " " +  firstPointIsEmpty + " und " + (s+1)        			    		  + " " + pointIsEmpty);

        			          //Konsistenz der beiden Skalenpunkte testen
        			          if ( firstPointIsEmpty != pointIsEmpty ){
        			    	      errorsFound = true;
        			    	      break;
        			          }


        		          }// Skalenpunkte

        		          if( errorsFound ){
        		    	      codingErrors.add( new StyleCodingError( i+1, itemCounterStr +
        		    	    	"Eine Absatzgruppe des Typs " + (String) styleMap.get( par.parStyle )   +
        		    	    	" (Absatz [" + (lastLikertPointStart+1) + "] bis [" + (i+1) + "]" +
        		    		   " muss einheitlich leer sein oder einheitlich Zahlen enthalten"));
        		    	      //Absatz wurde abschließend behandelt
        		    	      parHandled = true;
        		          }



        		          //Likert Schritt 4
        		          //Alle Skalenpunkte enthalten gültige Zahlen
        		          if ((! parHandled) && ( ! firstPointIsEmpty )){

        		        	  //Sind die Skalenpunkte einheitlich aufsteigend oder absteigend sortiert?
        		        	  //Wert erste Zelle
        		        	  int firstValue = Integer.parseInt(((WriterParagraph) writerContentBuffer.get( lastLikertPointStart )).parContent);
        		        	  int secondValue = Integer.parseInt(((WriterParagraph) writerContentBuffer.get( lastLikertPointStart+1 )).parContent);
        		        	  int delta = secondValue - firstValue;

        		        	  //Delta in Schleife testen
        		        	  errorsFound = false;
        		        	  for( int s = lastLikertPointStart + 1; s <= i; s++){
        	        		      //Wert des Skalenpunkte ermitteln
            			          int value0 = Integer.parseInt(((WriterParagraph) writerContentBuffer.get( s -1 )).parContent);
            			          int value1 = Integer.parseInt(((WriterParagraph) writerContentBuffer.get( s  )).parContent);

            			          if( ! ((value1 - value0) == delta)){
            			        	  errorsFound = true;
            			        	  break;
            			          }
        		        	  }
        		        	  //Fehler gefunden
        		        	  if(errorsFound){
               		    	      codingErrors.add( new StyleCodingError( i+1, itemCounterStr +
                  		    	  	"Eine Absatzgruppe des Typs " + (String) styleMap.get( par.parStyle )   +
                  		    	    " (Absatz [" + (lastLikertPointStart+1) + "] bis [" + (i+1) + "]" +
                  		    		" muss gleichmäßig aufsteigend oder absteigend nummeriert sein "));
                   	        		    	//Absatz wurde abschließend behandelt
        	        		    	parHandled = true;

        		        	  }

        		          }


        		      }//Variable lastLikertPointStart midestens ein mal gesetzt

        		  } //Letzter Absatz des Dokuments der letzter Absatz einer Reihen von Likert-Mid-Skalenpunkten

        	  } //Ende psLikertMid




		      //Vorherige Einträge einer Antwortskala auf Konsitenz prüfen
		      //Wenn Absatz das Antwortformat Single oder SingleNonOpinion oder Multiple hat
        	  //Wird für jede Antwortoption getestet, Evtl. erst am Ende der Antworstskala testen
   	          if( ! parHandled ){
   	        	  //Falls Antwortskalaelement
		          if(( aStyle == psChoiceSingle ) || ( aStyle == psChoiceSingleNonOpinion )  || ( aStyle == psChoiceMultiple )){

		    	    //Vorherige Absätze bis zum Start der Skala überprüfen
		    	    int forbiddenStyle;

		    	    //Single und Multiple dürfen nicht gemischt werden
		    	    //SingleNonOpinion kann nur als letzte Antwortalternative auftreten
		    	    if( aStyle == psChoiceSingle || aStyle == psChoiceSingleNonOpinion ) forbiddenStyle = psChoiceMultiple;
		    	    else forbiddenStyle = psChoiceSingle;

		    	    //Zeile > 1.: vorherige Skaleneinträge überprüfen
		    	    if( i > 0){

		    		    boolean foundError = false;
		    		    int formerPar;
		    		    for( int s = i - 1; s >= lastScaleStart; s--){

		    			    //Testausgabe der Schleifenparameter
		    			    //System.out.println( "    Schleife: "+ (i+1) + "->" +(s+1) );

		    			    //Besitzen vorherige Skaleneinträge die gleiche Typgruppe?
		    			    formerPar = ((WriterParagraph) writerContentBuffer.get( s )).parStyle;
		    	    	    if( formerPar == forbiddenStyle ){
		    	    	        //Fehlerbeschreibungsobjekt erzeugen
		    	    		    codingErrors.add( new StyleCodingError( i+1, itemCounterStr + "Die Formate " +
		    	    			   	(String) styleMap.get( aStyle ) +
		    		    		    " (Absatz " + (i+1) + ")" +
		    		    	        " und " +
		    		    	        (String) styleMap.get( formerPar ) +
		    		    	        " (Absatz " + (s+1) + ")" +
		    		    	        " dürfen nicht in einer gemeinsamen Antwortskala verwendet werden"));

		    	    		     foundError = true;
		    	    		     //System.out.println("Fehler: " + (i+1) + "->" + (s+1));
		    	    		     parHandled = true;
		    	    	    }
		    		        if ( foundError ) break;

		    	        }// Elemente bis zum Ende der Antwortskala

		    	    } // Zeile > 1

		          }// Absatz ist Antwortformat

		      }//parHandled = false;




   	          //Hat Einfachnennung mehr als eine Antwortköglichkeit?
   	          //Falls Antwortskala nicht konsisten ist (Einfach, Mehrfachnennung)
   	          //wurde bereits oben () ein Fehlerobjekt für diesen Absatz erzeugt
   	          //Evtl. erst prüfen nach Prüfung letzte Antwortskala vollständig
   	          if( (singleChoiceTags.contains( aStyle )) && ( ! singleChoiceTags.contains( followingStyle ))  ){
   	              if(! parHandled ){
   	            	  //Über Skalenelemente iterieren
   	            	  int choiceCounter = 0;
   	            	  boolean isSingle = false;
   	            	  for( int s = lastScaleStart; s <= i; s++){
   	            		  //Skala kann Mehrfach- oder EInfachnennung sein
   	  					  WriterParagraph scalePar = writerContentBuffer.get( s );
   	  					  //Skala kann einfachnennung oder Mehrfachnennung sein
  			              if(( scalePar.parStyle == psChoiceSingle)|| (scalePar.parStyle == psChoiceSingleNonOpinion)){
  			            	  isSingle = true;
  			              }
  				    	  choiceCounter++;
   	            	  }
   	            	  if( isSingle && choiceCounter < 2 ){
	    	    		    codingErrors.add( new StyleCodingError( i+1, itemCounterStr +
	    	    		      " Antwortskalen mit Einfachnennung" +
	    	    		      " (Absatz [" + (i+1) + "])" +
	    	    		      " müssen mindestens zwei Antwortvorgaben besitzen"));

   	            	  }
   	              }
   	          }// Letzter Absatz Antwortskala


		      //Falls MatrixAntwortskalaelement
		      //Vorherige Einträge der Matrixantwortskala auf Konsitenz prüfen
  //Testen, ob Fehlermeldungen mit nonopiniomkombinationen ausgegeben werden  (wenn das nicht das letzte ist)
		      if( ! parHandled ){
		    	//Wenn Absatz das Antwortformat Matrix-Single oder SingleNonOpinion oder Multiple hat
		          if(( aStyle == psMatrixHeadSingle ) || ( aStyle == psMatrixSingleNonOpinion )  || ( aStyle == psMatrixHeadMultiple )){
		    	      //Vorherige Absätze bis zum Start der Skala überprüfen
		    	    int forbiddenStyle;

		    	    //Single und Multiple dürfen nicht gemischt werden
		    	    //SingleNonOpinion kann nur als letzte Antwortalternative auftreten
		    	    if( aStyle == psMatrixHeadSingle || aStyle == psMatrixSingleNonOpinion ) forbiddenStyle = psMatrixHeadMultiple;
		    	    else forbiddenStyle = psMatrixHeadSingle;

		    	    //Zeile > 1.: vorherige Skaleneinträge überprüfen
		    	    if( i > 0){

		    		    boolean foundError = false;
		    		    int formerPar;
		    		    for( int s = i - 1; s >= lastMatrixScaleStart; s--){

		    			    //Testausgabe der Schleifenparameter
		    			    //System.out.println( "    Schleife: "+ (i+1) + "->" +(s+1) );

		    			    //Besitzen vorherige Skaleneinträge den geleichen Typ?
		    			    formerPar = ((WriterParagraph) writerContentBuffer.get( s )).parStyle;
		    	    	    if( formerPar == forbiddenStyle ){
		    	    	        //Fehlerbeschreibungsobjekt erzeugen
		    	    		    codingErrors.add( new StyleCodingError( i+1, itemCounterStr + "Die Formate " +
		    	    				(String) styleMap.get( aStyle ) +
		    		    		    " (Absatz " + (i+1) + ")" +
		    		    	        " und " +
		    		    	        (String) styleMap.get( formerPar ) +
		    		    	        " (Absatz " + (s+1) + ")" +
		    		    	        " dürfen nicht in einer gemeinsamen Antwortskala verwendet werden"));

		    	    		    foundError = true;
		    	    		    parHandled = true;
		    	    		    //System.out.println("Fehler: " + (i+1) + "->" + (s+1));
		    	    	    }
		    		        if ( foundError ) break;

		    	        }// Elemente bis zum Ende der Antwortskala

		    	    } // Zeile > 1

		          }// Absatz ist Matrix-Antwortformat
		      }// Absatz wurde noch nicht behandelt




  	          //Hat Einfachnennung Matrix mehr als eine Antwortköglichkeit?
   	          //Falls Antwortskala nicht konsisten ist (Einfach, Mehrfachnennung)
   	          //wurde bereits oben () ein Fehlerobjekt für diesen Absatz erzeugt
   	          //Evtl. erst prüfen nach Prüfung letzte Antwortskala vollständig
   	          if( (mxSingleTags.contains( aStyle )) && ( ! mxSingleTags.contains( followingStyle ))  ){
   	              if(! parHandled ){
   	            	  //Über Skalenelemente iterieren
   	            	  int choiceCounter = 0;
   	            	  boolean isSingle = false;
   	            	  for( int s = lastMatrixScaleStart; s <= i; s++){

   	            		  //Skala kann Mehrfach- oder EInfachnennung sein
   	  					  WriterParagraph scalePar = writerContentBuffer.get( s );
   	  					  //Skala kann einfachnennung oder Mehrfachnennung sein
  			              if(( scalePar.parStyle == psMatrixHeadSingle)|| (scalePar.parStyle == psMatrixSingleNonOpinion)){
  			            	  isSingle = true;
  			              }
  				    	  choiceCounter++;
   	            	  }
   	            	  if( isSingle && choiceCounter < 2 ){
	    	    		    codingErrors.add( new StyleCodingError( i+1, itemCounterStr +
	    	    		      " Antwortskalen einer Matrix mit Einfachnennung" +
	    	    		      " (Absatz [" + (i+1) + "])" +
	    	    		      " müssen mindestens zwei Antwortvorgaben haben"));

   	            	  }
   	              }
   	          }// Letzter Absatz Antwortskala





	          //Am Ende des Fragebogens testen, ob Item abgeschlossen ist
		      //ItemState kann die Werte None, Open, Closed annehmen
		      //Itemstate wird an entsprechender Stelle (Skalenstart, erstes Antwortskaleformat, erstes Matrixitem)
		      //gesetzt

		      //letzter Absatz
		      if ( i == writerContentBuffer.size() - 1 ){
		    	  //Für diesen Absatz wurde nich kein Fehlerobjekt erzeugt
		    	  if( ! parHandled ){
		    	      //hat Item gültige Antwortskala=
		    	      if( itemState == ITEMSTATE_OPEN){
  	    		          codingErrors.add( new StyleCodingError( i+1, itemCounterStr +
    	    			    " Das letzte Item besitzt keine vollständige Antwortskala"));
                      }
		    	  }//parHandled
		      }//Letzter Absatz


		      //if( ! itemclosed ){}

		      //Testausgabe, Absatznummer und Start der letzten Antwortskala
		      //System.out.println( " " + (i) + " lastscale: " + lastScaleStart );


		  }// for writerContentBuffer



		  if( codingErrors.isEmpty()) return true;
		  else return false;


	  }//checkParStyles





	  // ---- SAX DefaultHandler methods ----

	  public void startDocument()
	  throws SAXException
	  {
		  //System.out.println( "Sax Event: startDocument" );

	  }

	  public void endDocument()
	  throws SAXException
	  {
		  //System.out.println( "valueCount: " + valueCount );
		  //System.out.println( "Locator Position: " + locator.getLineNumber());
		  //System.out.println( "Sax Event: endDocument" );

		  //q&d: kann das nicht an eine andere Stelle?

		  //System.out.println("Datei gelesen!" + aFileName );
		  //System.out.println("");
		  //qstn.printComponent();


		  //Ausgabe des Fragebogens als QML Version 3
		  //q&d Dateiname aus instanzvariable OOoDataReader

		  //Zum Testen writerContenBuffer ausgeben
		  /*
		  System.out.println( "writerContentBuffer:" );
		  for (int i=0; i < writerContentBuffer.size(); i++){
			  WriterParagraph par = writerContentBuffer.get( i );
			  System.out.print( "["+  (i+1)  + "] " );
			  System.out.print( par.parStyle + "->" );
			  System.out.println( par.parContent );
		  }
		 */


		  //Zum Testen Array ausgeben
		  /*
		  System.out.println( "\n allowedStyleOrder:" );
		  for(int i=0; i< allowedStyleOrder.length; i++){
			System.out.print(allowedStyleOrder[ i ][ 0 ] + "-");
			System.out.println(allowedStyleOrder[ i ][ 1 ]);
		  }
		  */


		  /*
		  //Zum Testen Codingerrors ausgeben

		  StyleCodingError error;
		  for(int i=0; i< codingErrors.size(); i++){
		    error = codingErrors.get( i );
		    System.out.println( "Fehler ["+ error.parNumber + "] " + error.errorDescription );

		  }
		  */

		  //Überprüfen der eingelesenen Absätze
		  //move the call to writer2qml, move the declaration to TextParagraphList
		  //boolean resOK = false;

		  try{
		  //resOK =  checkParStyles();
		  checkParStyles();
		  }catch(Exception e){
				 //MessageBox messageBox = new MessageBox( shell, SWT.ICON_ERROR );
				 //messageBox.setText( "Writer2QML: Fehler" );
				 //messageBox.setMessage( "Die ausgewählte Datei ist keine gültige OpenOffice.org Writer Datei" );
				 //messageBox.open();
		  }


		  /*
		  if ( resOK ){

			  convertParagraphs();

			//Als QML Exportieren, FileName hat keine Bedeutung
			//  Model2QmlConverter converter = new Model2QmlConverter( qstn, new File( aFileName + ".xml" ));
			//  StringBuffer qml = converter.convert();




		  }
          */





	  }



	  public void startElement( String namespaceURI,
	                            String localName,   // local name
	                            String qName,       // qualified name
	                            Attributes attrs )
	  throws SAXException
	  {

		 String textStyleName;
		 String parentStyleName;
		 String styleName;


	    //Hier Unterscheidung nach Elementen


		//Abgeleitete Formatvorlagen
		//Bei Formatierungsänderungen an Absätzen mit Formatvorlagen werden intern neue Absatzvorlagennamen
		//vergeben. Diese werden in style:style Elementen gespeichert. Alle Vorlagennamen einer Parentvorlage
		//werden hier in einer ArrayList gespeichert um später entscheiden zu können, von welchem Typ der
		//interne Formatname abgeleitet ist
		if( qName.equals( "style:style" )){

			parentStyleName = attrs.getValue("style:parent-style-name");
			styleName = attrs.getValue("style:name");


			//Aus Kompatibilitätsgründen werden alte  Formate zu neuen gemappt
			String newStyleName = compatibilityMap.get( parentStyleName );
			//System.out.println("1: " + parentStyleName + " -> " + newStyleName );
			if( newStyleName != null ) parentStyleName = newStyleName;





			//Attribute style:parent-style-name und style:name sind gesetzt:
			//Überschriebene Formatvorlage
			if( parentStyleName != null && styleName != null ){


				//styleMap.put( psCaption, "qml:caption");



			  //Titel
		      if( parentStyleName.equals( "qml_3a_title" ) ){
		 		  qstnTitleTags.add( styleName );
			  }



			  //Intro Section
			  if( parentStyleName.equals( "qml_3a_intro" ) ){
			      introTags.add( styleName );
			  }

			  //Intro Item
			  if( parentStyleName.equals( "qml_3a_intro_5f_item" ) ){
			      introItemTags.add( styleName );
			  }



			  //Caption
			  if( parentStyleName.equals( "qml_3a_caption" ) ){
				  captionTags.add( styleName );
			  }


			  //CaptionNewPage
			  if( parentStyleName.equals( "qml_3a_caption_5f_newpage" ) ){
				  captionNewPageTags.add( styleName );
			  }


			  //Kategorie Einfachnennung
			  if( parentStyleName.equals( "qml_3a_choice_5f_single" ) ){
				choiceSingleTags.add( styleName );
			  }



			  //Kategorie Einfachnennung Nonopinion
			  if( parentStyleName.equals( "qml_3a_choice_5f_single_5f_nonopinion" ) ){
				  choiceSingleNonOpinionTags.add( styleName );
			  }

			  //Kategorie Mehrfachnennung
			  if( parentStyleName.equals( "qml_3a_choice_5f_multiple" ) ){
				  choiceMultipleTags.add( styleName );
			  }



			  //Offene Zusatzkategorie
			  if( parentStyleName.equals( "qml_3a_choice_5f_openaddon" ) ){
				  openaddonTags.add( styleName );
				  System.out.println("hihi");
			  }

			  //Großes offenes Textfeld
			  if( parentStyleName.equals( "qml_3a_choice_5f_open" ) ){
				  choiceOpenTags.add( styleName );
			  }


			  //Question
			  if( parentStyleName.equals( "qml_3a_question" ) ){
				questionTags.add( styleName );
			  }

			  //Instruction
			  if( parentStyleName.equals( "qml_3a_instruction" ) ){
				  instructionTags.add( styleName );
			  }

			  //Matrix Kopf Einfachnennung
			  if( parentStyleName.equals( "qml_3a_matrix_5f_head_5f_single" ) ){
				  matrixHeadSingleTags.add( styleName );
			  }



			  //Matrix Kopf Mehrachnennung
			  if( parentStyleName.equals( "qml_3a_matrix_5f_head_5f_multiple" ) ){
			    matrixHeadMultipleTags.add( styleName );
			  }



			//Matrix Offen
			  if( parentStyleName.equals( "qml_3a_matrix_5f_open" ) ){
			    matrixOpenTags.add( styleName );
			  }

			//Matrix SingleNonOpinion
			  if( parentStyleName.equals( "qml_3a_matrix_5f_single_5f_nonopinion" ) ){
			    matrixSingleNonOpinionTags.add( styleName );
			  }


			  //Matrix Item
			  if( parentStyleName.equals( "qml_3a_matrix_5f_item" ) ){
				  matrixItemTags.add( styleName );
			  }



			  //Likertskala Start
			  if( parentStyleName.equals( "qml_3a_likert_5f_left" ) ){
				  likertLeftTags.add( styleName );
			  }


			  //Likertskala Mitte
			  if( parentStyleName.equals( "qml_3a_likert_5f_mid" ) ){
				  likertMidTags.add( styleName );
			  }


			  //Likertskala Ende
			  if( parentStyleName.equals( "qml_3a_likert_5f_right" ) ){
				  likertRightTags.add( styleName );
			  }


			 //TDO restliche Elemente ergänzen, gibts noch welche?

			}

		}// Element Abgeleitete Formatvorlagen




	    //Textabsätze
	    if ( qName.equals("text:p")){


	    	/* Neue Logik implementieren:
	    	 *
	    	 * Zuerst werden  Absätze in Liste geschrieben.
	    	 * Diese Liste wird dann geprüft und bei Erfolg
	    	 * Konvertiert.
	    	 *
	    	 * Bei startElement() wird nur je nach Absatzvorlage
	    	 * StatusVariable gesetzt. Bei chracters() Variable mit übergebenen
	    	 * Zeichen füllen. Dann bei endElememt() Status (welche Vorlage) und
	    	 * String in Liste schreiben.
	    	 *
	    	 *     Dann Liste weiterverarbeiten
	    	 *
	    	 */


	    	parsingState = psNone;


	    	/*
	    	 *
	    	 * OOo codiert Absätze mit kurzbezeichnung weiter (s.u.), das mussberücksichtigt werden (P16-> qml_3a_category_5f_single )
	    	 *  </style:style><style:style style:name="P16" style:family="paragraph" style:parent-style-name="qml_3a_category_5f_single">
	    	 *
	    	 */


	    	/*
	    	 * Formatierungen im Text werden als XML Elemente darsestellt. Dies muss beim einlesen berücksichtigt werden:
	    	 * Der Text einer Frage wird beispielsweise durch den Aufruf mehrerer characters() funktionen übergeben    	 *
	    	 *
	    	 */



	    	//Die Zuweisung der Absatzvorlagen findet im Attribut "text:style-name" statt

	    	//Problem: An dieser Stelle (Startelement) oder bei Textknoten
	    	//Eigenschaften (z.B. offen) eingügen? Hier!

	    	//Wert des Attributes
	    	textStyleName = attrs.getValue("text:style-name");



	    	//Aus Kompatibilitätsgründen werden alte  Formate zu neuen gemappt
			String newStyleName = compatibilityMap.get( textStyleName );
			//System.out.println( "2: " + textStyleName + " -> " + newStyleName );
			if( newStyleName != null ) textStyleName = newStyleName;




			//Title
		    if( qstnTitleTags.contains( textStyleName )){
		        debugEcho( "title->");
		        parsingState = psTitle;
		    }



		  //Intro Section
		    if( introTags.contains( textStyleName )){
		    	debugEcho( "intro->");
		    	parsingState = psIntro;
		    }

		  //Intro Item
		    if( introItemTags.contains( textStyleName )){
		    	debugEcho( "intro_item->");
		    	parsingState = psIntroItem;
		    }


	    	//Caption
	    	if( captionTags.contains( textStyleName )){
	            debugEcho( "caption->");
	            parsingState = psCaption;
	        }

	     	//Caption
	    	if( captionNewPageTags.contains( textStyleName )){
	            debugEcho( "caption_newpage->");
	            parsingState = psCaptionNewPage;
	        }


	    	//Question
	    	if ( questionTags.contains( textStyleName )){
	    	  debugEcho( "question->");
	          parsingState = psQuestion;
	        }

	        //Instruction
	    	if ( instructionTags.contains( textStyleName )){
	    		debugEcho( "insruction->");
	    		parsingState = psInstruction;
	        }

	        //Einfachnennung
	        if ( choiceSingleTags.contains( textStyleName )){
	    		debugEcho( "choice_single->");
	        	parsingState = psChoiceSingle;
	        }



	        //Einfachnennung Nonopinion
	        if ( choiceSingleNonOpinionTags.contains( textStyleName )){
	    		debugEcho( "choice_single_nonopinion->");
	        	parsingState = psChoiceSingleNonOpinion;
	        }



	        //Mehrfachnennung
	        if( choiceMultipleTags.contains( textStyleName )){
	    		debugEcho( "choice_multiple->");
	        	parsingState = psChoiceMultiple;
	        }



	        //Offene Zusatzkategorie
	        if( openaddonTags.contains( textStyleName )){
	    		debugEcho( "openaddon->");
	        	parsingState = psChoiceOpenAddon;
	        }





	        //Offenes Textfeld
	        if( choiceOpenTags.contains( textStyleName )){
	    		debugEcho( "choice_open->");
	        	parsingState = psChoiceOpen;
	        }




	        //Matrix Kopf Antwortkategorien Einfachnennung
	        if( matrixHeadSingleTags.contains( textStyleName )){
	    		debugEcho( "matrix_head_single->");
	        	parsingState = psMatrixHeadSingle;
			}



	        //Matrix Kopf Antwortkategorien Mehrfachnennung
	        if( matrixHeadMultipleTags.contains( textStyleName )){
	    		debugEcho( "matrix_head_multipe->");
	        	parsingState = psMatrixHeadMultiple;
			}




	        //Matrix Offen
	        if( matrixOpenTags.contains( textStyleName )){
	    		debugEcho( "matrix_open->");
	        	parsingState = psMatrixOpen;
			}


	        //Matrix Einfachnennung Nonopinion
	        if( matrixSingleNonOpinionTags.contains( textStyleName )){
	    		debugEcho( "matrix_single_nonopinion->");
	        	parsingState = psMatrixSingleNonOpinion;
			}


	        //Matrix Item
	        if(matrixItemTags.contains( textStyleName )){
	    		debugEcho( "matrix_item?matrixitem?->");
	        	parsingState = psMatrixItem;
			}


	        //Likertskala Start
	        if( likertLeftTags.contains( textStyleName )){
	    		debugEcho( "likert_left->");

	        	parsingState = psLikertLeft;
	        }

	        //Likertskala Mitte
	        if( likertMidTags.contains( textStyleName )){
	    		debugEcho( "liker_mid->");
	        	parsingState = psLikertMid;

	        }

	        //Likertskala Ende
	        if( likertRightTags.contains( textStyleName )){
	        	debugEcho( "liker_rigth->");
	        	parsingState = psLikertRight;

	        }


	    }//Textabsätze


	  }// Start Element









	  public void characters( char[] buf, int offset, int len )
	  throws SAXException
	  {

		//evtl mit chars nicht mit string arbeiten,
		//da sonst zuviele Strings erzeigt werden
		String s = new String( buf, offset, len );


	    //Textknoten ist nicht leer
	    if (( s.trim() ).length() != 0 ){

	    	 //System.out.println("  Textknoten: " + s );
	     //    System.out.println( s );

	    /*
	     * Wenn Texte einer Formatzuweisung (z.B. Frage) im OOo Dokument formatiert sind (z.B.) unterstrichen,
	     * wird der Text durch mehrfachen aufruf der characters() Funktion übergeben. Aus diesem Grund
	     * werden die aktuellen Texte an evtl. bestehende angefügt.
	     *
	     * TDO Evtl. Tabs und hervorhebungen gesondert behandeln
	     */


	    /*
	     * hier nur Variablen captionText etc. mit Text füllen
	     * Variablen vorher deklarieren
	     *
	     */


	      //Textknoten je nach Kontext behandeln
	      switch ( parsingState ){

	      //Section
	      case psTitle:{
	        titleText.append( s );
	    	break;
	      }


	      //Intro Section
	      case psIntro:{
	        introText.append( s );
	    	break;
	      }

	      //Intro Qstn
	      case psIntroItem:{
	        introItemText.append( s );
	    	break;
	      }

	      //Section
	      case psCaption:{
	        captionText.append( s );
	    	break;
	      }

	      //SectionNewPage
	      case psCaptionNewPage:{
	        captionNewPageText.append( s );
	    	break;
	      }


	      case psQuestion:{
	    	questionText.append( s );
	        break;
	      }

	      case psInstruction:{
	        instructionText.append( s );
	        break;
	      }

	      case psChoiceSingle:{
	        choiceSingleText.append( s );
	       	break;
	      }



          case psChoiceSingleNonOpinion:{
  	        choiceSingleNonOpinionText.append( s );
  	       	break;
  	      }

          case psChoiceMultiple:{
        	  choiceMultipleText.append( s );
	    	    break;
	      }


          case psChoiceOpenAddon:{
        	  choiceOpenAddonText.append( s );
        	  break;
          }

	      case psChoiceOpen:{
	        choiceOpenText.append( s );
	        break;
	      }

	      case psMatrixHeadSingle:{
	        matrixHeadSingleText.append( s );
	    	break;
	      }


	      case psMatrixHeadMultiple:{
	        matrixHeadMultipleText.append( s );
	        break;
	      }


	      case psMatrixOpen:{
		        matrixOpenText.append( s );
		        break;
		  }

	      case psMatrixSingleNonOpinion:{
		        matrixSingleNonOpinionText.append( s );
		        break;
		  }

	      case psMatrixItem:{
	    	  matrixItemText.append( s );
	          break;
	      }

	      case psLikertLeft:{
        	likertLeftText.append( s );
	       	break;
	      }

	      case psLikertMid:{
            likertMidText.append( s );
	        break;
	      }

          case psLikertRight:{
            likertRightText.append( s );
	       	break;
	      }

	    }//switch




	     }//Textknoten nicht leer
	  }



 public void endElement( String namespaceURI,
             String localName,     // local name
             String qName )        // qualified name
 throws SAXException
 {
//Textabsätze
if ( qName.equals("text:p")){

	/*
	 * Funktioniert das, bei Textabsätzen den ParsingState abzufragen?
	 * Kann persingstate evtl zu früh zurückgesetzt werden?
	 *
	 */


	//Kann switching durch parametrisierung vermieden werden?
	//erstmal so lassen
  switch ( parsingState ){



  //Hier müssen Elementnamen (Formatnamen) abgefragt werden und dann parsingstate
  //Zurückgesetzt werden


  //Titel des Fragebogens
  case psTitle:{
textParagraphList.addParagraph(new TextParagraph(parsingState, titleText.toString()));
	  writerContentBuffer.add( new WriterParagraph( parsingState, titleText.toString()));
	  debugEcho( titleText.toString() + "\n");
	  titleText.delete(0, titleText.length());
      parsingState = psNone;
      break;
  }


    //Intro der Section
    case psIntro:{
    textParagraphList.addParagraph( new TextParagraph( parsingState, introText.toString()));
		writerContentBuffer.add( new WriterParagraph( parsingState, introText.toString()));
		debugEcho( introText.toString() + "\n");
		introText.delete(0, introText.length());
	    parsingState = psNone;
	    break;
    }

    //Intro des Items
    case psIntroItem:{
    	textParagraphList.addParagraph( new TextParagraph( parsingState, introItemText.toString()));
		writerContentBuffer.add( new WriterParagraph( parsingState, introItemText.toString()));
		debugEcho( introItemText.toString() + "\n");
		introItemText.delete(0, introItemText.length());
	    parsingState = psNone;
	    break;
    }


  //Section
  case psCaption:{
	  textParagraphList.addParagraph( new TextParagraph( parsingState, captionText.toString()));
	writerContentBuffer.add( new WriterParagraph( parsingState, captionText.toString()));
	debugEcho( captionText.toString() + "\n");
	captionText.delete(0, captionText.length());
	parsingState = psNone;
	break;
  }

  //SectionNewPage
  case psCaptionNewPage:{
	  textParagraphList.addParagraph( new TextParagraph( parsingState, captionNewPageText.toString()));
	  writerContentBuffer.add( new WriterParagraph( parsingState, captionNewPageText.toString()));
	debugEcho( captionNewPageText.toString() + "\n");
	captionNewPageText.delete(0, captionNewPageText.length());
	parsingState = psNone;
	break;
  }

  case psQuestion:{
	  textParagraphList.addParagraph( new TextParagraph( parsingState, questionText.toString()));
	  writerContentBuffer.add( new WriterParagraph( parsingState, questionText.toString()));
	  debugEcho( questionText.toString() + "\n");
	  questionText.delete(0, questionText.length());
	  parsingState = psNone;
      break;
  }

  case psInstruction:{
	  textParagraphList.addParagraph( new TextParagraph( parsingState, instructionText.toString()));
	  writerContentBuffer.add( new WriterParagraph( parsingState, instructionText.toString()));
	  debugEcho( instructionText.toString() + "\n");
	  instructionText.delete(0, instructionText.length());
	parsingState = psNone;
    break;
  }

  case psChoiceSingle:{
	  textParagraphList.addParagraph( new TextParagraph( parsingState, choiceSingleText.toString()));
	  writerContentBuffer.add( new WriterParagraph( parsingState, choiceSingleText.toString()));
	  debugEcho( choiceSingleText.toString() + "\n");
	  choiceSingleText.delete(0, choiceSingleText.length());
      parsingState = psNone;
   	  break;
  }



  case psChoiceSingleNonOpinion:{
	  textParagraphList.addParagraph( new TextParagraph( parsingState, choiceSingleNonOpinionText.toString()));
    writerContentBuffer.add( new WriterParagraph( parsingState, choiceSingleNonOpinionText.toString()));
	debugEcho( choiceSingleNonOpinionText.toString() + "\n" );
	choiceSingleNonOpinionText.delete(0, choiceSingleNonOpinionText.length());
	parsingState = psNone;
   	break;
  }

  case psChoiceMultiple:{
	  textParagraphList.addParagraph( new TextParagraph( parsingState, choiceMultipleText.toString()));
     writerContentBuffer.add( new WriterParagraph( parsingState, choiceMultipleText.toString()));
	debugEcho( choiceMultipleText.toString() + "\n" );
	choiceMultipleText.delete(0, choiceMultipleText.length());
	parsingState = psNone;
	break;
  }



  case psChoiceOpenAddon:{

	  textParagraphList.addParagraph( new TextParagraph( parsingState, choiceOpenAddonText.toString()));
	  writerContentBuffer.add( new WriterParagraph( parsingState, choiceOpenAddonText.toString()));
      debugEcho( choiceOpenAddonText.toString() + "\n" );
      choiceOpenAddonText.delete(0, choiceOpenAddonText.length());
	  parsingState = psNone;
	  break;
  }

  case psChoiceOpen:{
	  textParagraphList.addParagraph( new TextParagraph( parsingState, choiceOpenText.toString()));
    writerContentBuffer.add( new WriterParagraph( parsingState, choiceOpenText.toString()));
  	debugEcho( choiceOpenText.toString() + "\n" );
	choiceOpenText.delete(0, choiceOpenText.length());
	  parsingState = psNone;
	  break;
  }

  case psMatrixHeadSingle:{
	  textParagraphList.addParagraph( new TextParagraph( parsingState, matrixHeadSingleText.toString()));
	  writerContentBuffer.add( new WriterParagraph( parsingState, matrixHeadSingleText.toString()));
	  	debugEcho( matrixHeadSingleText.toString() + "\n" );
	  	matrixHeadSingleText.delete(0, matrixHeadSingleText.length());
	  parsingState = psNone;
	break;
  }



  case psMatrixHeadMultiple:{
	  textParagraphList.addParagraph( new TextParagraph( parsingState, matrixHeadMultipleText.toString()));
	  writerContentBuffer.add( new WriterParagraph( parsingState, matrixHeadMultipleText.toString()));
	  	debugEcho( matrixHeadMultipleText.toString() + "\n" );
	  	matrixHeadMultipleText.delete(0, matrixHeadMultipleText.length());
	  parsingState = psNone;
    break;
  }




  case psMatrixOpen:{
	  textParagraphList.addParagraph( new TextParagraph( parsingState, matrixOpenText.toString()));
	  writerContentBuffer.add( new WriterParagraph( parsingState, matrixOpenText.toString()));
	  	debugEcho( matrixOpenText.toString() + "\n" );
	  	matrixOpenText.delete(0, matrixOpenText.length());
	  parsingState = psNone;
    break;
  }



  case psMatrixSingleNonOpinion:{
	  textParagraphList.addParagraph( new TextParagraph( parsingState, matrixSingleNonOpinionText.toString()));
	  writerContentBuffer.add( new WriterParagraph( parsingState, matrixSingleNonOpinionText.toString()));
	  debugEcho( matrixSingleNonOpinionText.toString() + "\n" );
	  matrixSingleNonOpinionText.delete(0, matrixSingleNonOpinionText.length());
	  parsingState = psNone;
    break;
  }




  case psMatrixItem:{
	  textParagraphList.addParagraph( new TextParagraph( parsingState, matrixItemText.toString()));
	  writerContentBuffer.add( new WriterParagraph( parsingState, matrixItemText.toString()));
	  	debugEcho( matrixItemText.toString() + "\n" );
	  	matrixItemText.delete(0, matrixItemText.length());
	  parsingState = psNone;
      break;
  }

  case psLikertLeft:{
	  textParagraphList.addParagraph( new TextParagraph( parsingState, likertLeftText.toString()));
	  writerContentBuffer.add( new WriterParagraph( parsingState, likertLeftText.toString()));
	  	debugEcho( likertLeftText.toString() + "\n" );
	  	likertLeftText.delete(0, likertLeftText.length());
	  parsingState = psNone;
   	break;
  }

  case psLikertMid:{
	  textParagraphList.addParagraph( new TextParagraph( parsingState, likertMidText.toString()));
	  writerContentBuffer.add( new WriterParagraph( parsingState, likertMidText.toString()));
	  	debugEcho( likertMidText.toString() + "\n" );
	  	likertMidText.delete(0, likertMidText.length());
	  parsingState = psNone;
    break;
  }

  case psLikertRight:{
	  textParagraphList.addParagraph( new TextParagraph( parsingState, likertRightText.toString()));
	  writerContentBuffer.add( new WriterParagraph( parsingState, likertRightText.toString()));
	  	debugEcho( likertRightText.toString() + "\n" );
	  	likertRightText.delete(0, likertRightText.length());
	  parsingState = psNone;
   	break;
  }

}//switch





    }//Textabsätze
  }  //endElement()
}