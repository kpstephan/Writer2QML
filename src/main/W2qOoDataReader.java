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

		@SuppressWarnings("unused")
		private Locator locator;

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



		//Variablen für Parsing
		//nur an einer Stelle deklarieren
		int parsingState;

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



		//Aus Kompatibilitätserwägungen werden Absatzformatnamen
		//aus älteren Versionen akzeptiert
		private HashMap<String, String> compatibilityMap = new HashMap<String, String>();



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


		//Funktion zum Ausgeben von Debug Informationen
		private void debugEcho( String echo ){
	      if (debugMode ){
		    System.out.print( echo );
		  }
		}



		//Konstruktor
		public W2qOoDataReader( TextParagraphList textParagraphList){

			this.textParagraphList = textParagraphList;

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




	  public void setDocumentLocator (Locator locator){
		  this.locator = locator;
	  }



	  // ---- SAX DefaultHandler methods ----

	  public void startDocument()
	  throws SAXException
	  {
		  //System.out.println( "Sax Event: startDocument" );

	  }

	  public void endDocument()
	  throws SAXException
	  {

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

		String s = new String( buf, offset, len );


	    //Textknoten ist nicht leer
	    if (( s.trim() ).length() != 0 ){


	    /*
	     * Wenn Texte einer Formatzuweisung (z.B. Frage) im OOo Dokument formatiert sind (z.B.) unterstrichen,
	     * wird der Text durch mehrfachen aufruf der characters() Funktion übergeben. Aus diesem Grund
	     * werden die aktuellen Texte an evtl. bestehende angefügt.
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


  switch ( parsingState ){



  //Titel des Fragebogens
  case psTitle:{
textParagraphList.addParagraph(new TextParagraph(parsingState, titleText.toString()));
	  debugEcho( titleText.toString() + "\n");
	  titleText.delete(0, titleText.length());
      parsingState = psNone;
      break;
  }


    //Intro der Section
    case psIntro:{
    textParagraphList.addParagraph( new TextParagraph( parsingState, introText.toString()));
		debugEcho( introText.toString() + "\n");
		introText.delete(0, introText.length());
	    parsingState = psNone;
	    break;
    }

    //Intro des Items
    case psIntroItem:{
    	textParagraphList.addParagraph( new TextParagraph( parsingState, introItemText.toString()));
		debugEcho( introItemText.toString() + "\n");
		introItemText.delete(0, introItemText.length());
	    parsingState = psNone;
	    break;
    }


  //Section
  case psCaption:{
	  textParagraphList.addParagraph( new TextParagraph( parsingState, captionText.toString()));
	debugEcho( captionText.toString() + "\n");
	captionText.delete(0, captionText.length());
	parsingState = psNone;
	break;
  }

  //SectionNewPage
  case psCaptionNewPage:{
	  textParagraphList.addParagraph( new TextParagraph( parsingState, captionNewPageText.toString()));
	debugEcho( captionNewPageText.toString() + "\n");
	captionNewPageText.delete(0, captionNewPageText.length());
	parsingState = psNone;
	break;
  }

  case psQuestion:{
	  textParagraphList.addParagraph( new TextParagraph( parsingState, questionText.toString()));
	  debugEcho( questionText.toString() + "\n");
	  questionText.delete(0, questionText.length());
	  parsingState = psNone;
      break;
  }

  case psInstruction:{
	  textParagraphList.addParagraph( new TextParagraph( parsingState, instructionText.toString()));
	  debugEcho( instructionText.toString() + "\n");
	  instructionText.delete(0, instructionText.length());
	parsingState = psNone;
    break;
  }

  case psChoiceSingle:{
	  textParagraphList.addParagraph( new TextParagraph( parsingState, choiceSingleText.toString()));
	  debugEcho( choiceSingleText.toString() + "\n");
	  choiceSingleText.delete(0, choiceSingleText.length());
      parsingState = psNone;
   	  break;
  }



  case psChoiceSingleNonOpinion:{
	  textParagraphList.addParagraph( new TextParagraph( parsingState, choiceSingleNonOpinionText.toString()));
    debugEcho( choiceSingleNonOpinionText.toString() + "\n" );
	choiceSingleNonOpinionText.delete(0, choiceSingleNonOpinionText.length());
	parsingState = psNone;
   	break;
  }

  case psChoiceMultiple:{
	  textParagraphList.addParagraph( new TextParagraph( parsingState, choiceMultipleText.toString()));
    debugEcho( choiceMultipleText.toString() + "\n" );
	choiceMultipleText.delete(0, choiceMultipleText.length());
	parsingState = psNone;
	break;
  }



  case psChoiceOpenAddon:{

	  textParagraphList.addParagraph( new TextParagraph( parsingState, choiceOpenAddonText.toString()));
	  debugEcho( choiceOpenAddonText.toString() + "\n" );
      choiceOpenAddonText.delete(0, choiceOpenAddonText.length());
	  parsingState = psNone;
	  break;
  }

  case psChoiceOpen:{
	  textParagraphList.addParagraph( new TextParagraph( parsingState, choiceOpenText.toString()));
    debugEcho( choiceOpenText.toString() + "\n" );
	choiceOpenText.delete(0, choiceOpenText.length());
	  parsingState = psNone;
	  break;
  }

  case psMatrixHeadSingle:{
	  textParagraphList.addParagraph( new TextParagraph( parsingState, matrixHeadSingleText.toString()));
	  	debugEcho( matrixHeadSingleText.toString() + "\n" );
	  	matrixHeadSingleText.delete(0, matrixHeadSingleText.length());
	  parsingState = psNone;
	break;
  }



  case psMatrixHeadMultiple:{
	  textParagraphList.addParagraph( new TextParagraph( parsingState, matrixHeadMultipleText.toString()));
	  	debugEcho( matrixHeadMultipleText.toString() + "\n" );
	  	matrixHeadMultipleText.delete(0, matrixHeadMultipleText.length());
	  parsingState = psNone;
    break;
  }




  case psMatrixOpen:{
	  textParagraphList.addParagraph( new TextParagraph( parsingState, matrixOpenText.toString()));
	  	debugEcho( matrixOpenText.toString() + "\n" );
	  	matrixOpenText.delete(0, matrixOpenText.length());
	  parsingState = psNone;
    break;
  }



  case psMatrixSingleNonOpinion:{
	  textParagraphList.addParagraph( new TextParagraph( parsingState, matrixSingleNonOpinionText.toString()));
	  debugEcho( matrixSingleNonOpinionText.toString() + "\n" );
	  matrixSingleNonOpinionText.delete(0, matrixSingleNonOpinionText.length());
	  parsingState = psNone;
    break;
  }




  case psMatrixItem:{
	  textParagraphList.addParagraph( new TextParagraph( parsingState, matrixItemText.toString()));
	  	debugEcho( matrixItemText.toString() + "\n" );
	  	matrixItemText.delete(0, matrixItemText.length());
	  parsingState = psNone;
      break;
  }

  case psLikertLeft:{
	  textParagraphList.addParagraph( new TextParagraph( parsingState, likertLeftText.toString()));
	  	debugEcho( likertLeftText.toString() + "\n" );
	  	likertLeftText.delete(0, likertLeftText.length());
	  parsingState = psNone;
   	break;
  }

  case psLikertMid:{
	  textParagraphList.addParagraph( new TextParagraph( parsingState, likertMidText.toString()));
	  	debugEcho( likertMidText.toString() + "\n" );
	  	likertMidText.delete(0, likertMidText.length());
	  parsingState = psNone;
    break;
  }

  case psLikertRight:{
	  textParagraphList.addParagraph( new TextParagraph( parsingState, likertRightText.toString()));
	  	debugEcho( likertRightText.toString() + "\n" );
	  	likertRightText.delete(0, likertRightText.length());
	  parsingState = psNone;
   	break;
  }

}//switch





    }//Textabsätze
  }  //endElement()
}