package model.converter;

import java.util.ArrayList;

import model.simplequestionnaire.Category;
import model.simplequestionnaire.Item;
import model.simplequestionnaire.Qstn;
import model.simplequestionnaire.QstnChoices;
import model.simplequestionnaire.QstnInstruction;
import model.simplequestionnaire.QstnIntro;
import model.simplequestionnaire.QstnQuestion;
import model.simplequestionnaire.QstnSection;
import model.writerparagraphs.TextParagraph;
import model.writerparagraphs.TextParagraphList;



 public class Parapgraphs2SimpleQstnConverter {


    //Konstanten für Absätztypen, nur an einer Stelle definieren.
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


	public static Qstn convertToSimpleQuestionnaire(TextParagraphList textParagraphList){




			  /*
			   * Logik erklären. Page, Section, Item, Scale
			   *
			   */


			  //Neues Fragebogenobjekt
			  Qstn qstn = new Qstn();
			  //Zum testen Namen setzten wird in printcomponent() ausgegeben
			  qstn.setName( "Fragebogen" );


			  //Setzen des Fragebogentitels
			  //Ein Fragebogentitel ist immer das Erste Element
			  if ( ! textParagraphList.isEmpty() ){

				  int parStyle = ((TextParagraph) textParagraphList.get( 0 )).getStyle();

				  if (parStyle == psTitle){

					  String parContent = ((TextParagraph) textParagraphList.get( 0 )).getContent();
					  qstn.setQstnTitle( parContent );

				  }
			  }



			  //Variable für aktuelle Seite und aktuelle Section
			  QstnSection currentPage = qstn;

	          /*
	           * Logik mit currentPage und currentSection
	           * Wenn keine Seitenumbrüche existieren
	           *
	           *
	           */

			  QstnSection currentSection = qstn;

			  int pageCounter = 0;

			  //Wann wird ein neues Item erzeugt?
			  //Neues Item erzeugen
			  //Werden erzeugt um fehler may not have been initializes abzufangen
			  Item currentItem = new Item();
			  QstnChoices currentChoices = new QstnChoices();

			  //Referenz auf das aktuelle Instruktion Objekt
			  //Wird benötigt, da mehrere Writer-Instruktions hintereinander
			  //in mehrere Textabsätze einer QstnInstruction konvertiert werden.
			  QstnInstruction currentInstruction = new QstnInstruction();
			  //Objekt wird hier schon erzeugt um kompilerfehler zu umgehen
			  //(Objekt wird unten nur erzeugt, wenn bestimmte Bedingung zutrifft)

			  //Das gleiche gilt für Itemintro
			  QstnIntro currenItemIntro = new QstnIntro();



			  //Absatztypen, die in Items vorkommen können
			  ArrayList<Integer> itemTags = new ArrayList<Integer>();
			  itemTags.add( psIntroItem );
			  itemTags.add( psQuestion );
			  itemTags.add( psInstruction );


			  //Absatztypen, die in Skalen vorkommen können
			  ArrayList<Integer> scaleTags = new ArrayList<Integer>();
			  scaleTags.add( psChoiceSingle);
			  scaleTags.add( psChoiceMultiple );
			  scaleTags.add( psChoiceOpenAddon );
			  scaleTags.add( psChoiceOpen );
			  scaleTags.add( psChoiceSingleNonOpinion );
			  scaleTags.add( psMatrixHeadSingle );
			  scaleTags.add( psMatrixHeadMultiple );
			  scaleTags.add( psMatrixOpen );
			  scaleTags.add( psMatrixSingleNonOpinion);
			  scaleTags.add( psLikertLeft );
			  scaleTags.add( psLikertMid );
			  scaleTags.add( psLikertRight );

			  //Absatztypen Einfachnennungen
			  ArrayList<Integer> singleTags = new ArrayList<Integer>();
			  singleTags.add( psChoiceSingle );

			  //Absatztypen Einfachnennung Nonopinion
			  ArrayList<Integer> singleNonOpinionTags = new ArrayList<Integer>();
			  singleNonOpinionTags.add( psChoiceSingleNonOpinion );

			  //Absatztypen, die nur in mehrfachnennungen vorkommen
			  ArrayList<Integer> multipleTags = new ArrayList<Integer>();
			  multipleTags.add( psChoiceMultiple );

			  //Absatztypen, die nur in Matrix-Einfachnennung vorkommen
			  ArrayList<Integer> matrixSingleTags = new ArrayList<Integer>();
			  matrixSingleTags.add( psMatrixHeadSingle );
			  matrixSingleTags.add( psMatrixSingleNonOpinion );

			  //Absatztypen, die nur in Matrix-Mehrfachnennung vorkommen
			  ArrayList<Integer> matrixMultipleTags = new ArrayList<Integer>();
			  matrixMultipleTags.add( psMatrixHeadMultiple );

			  //Absatztypen, die nur in LIkert-Skalen vorkommen
			  ArrayList<Integer> likertTags = new ArrayList<Integer>();
			  likertTags.add( psLikertLeft );
			  likertTags.add( psLikertMid );
			  likertTags.add( psLikertRight );



			  //Enthält Dokument NewPage Absätze?
			  boolean pagesPresent = false;
			  for (int i = 0; i < textParagraphList.size(); i++){
				  if ( ((TextParagraph) textParagraphList.get( i )).getStyle() ==  psCaptionNewPage ){
					  pagesPresent = true;
					  break;
				  }
			  }



			  //Iterieren über alle Absätze
			  //String scale;
			  //String scaleType;
			  int parStyle, formerParStyle, nextParStyle;
			  int scalePointCount = 0;
			  //Prüfvariable, ob multiple-Eigenschaft gesetzt wurde
			  boolean multipleWasSet = false;
			  String parContent;
			  for (int i = 0; i < textParagraphList.size(); i++){


				  //scaleType = "";
				  parStyle = ((TextParagraph) textParagraphList.get( i )).getStyle();
				  parContent = ((TextParagraph) textParagraphList.get( i )).getContent();

	        	  //if( scaleTags.contains( parStyle )) scale = "Skala";
	        	  //else scale = "keine Skala";


	        	  //Neue Seite?
	        	  //Erster Absatz
	        	  if( i == 0 && parStyle != psCaptionNewPage ){
	        		  if( pagesPresent ){

	        			  //System.out.println("  Neue Seite");
	        			  //Neuen Seitenabschnitt erzeugen
	        			  pageCounter++;
	        			  currentPage = new QstnSection();
	        			  currentPage.setSectionType( QstnSection.TYPE_PAGE );
	        			  //Zum testen Namen setzten
	        			  currentPage.setName("Seite " + pageCounter );
	        			  qstn.add( currentPage );
	        			  //Kommentieren
	        			  currentSection = currentPage;

	        		  }
	        	  }
	        	  //Absatz 2-N
	        	  else{
	        		  if ( parStyle == psCaptionNewPage ){
	    				  //System.out.println("  Neue Seite");
	           			  pageCounter++;
	        			  currentPage = new QstnSection();
	        			  currentPage.setSectionType( QstnSection.TYPE_PAGE );
	        			  //Zum testen Namen setzten wird in printcomponent() ausgegeben
	        			  currentPage.setName("Seite " + pageCounter );
	        			  qstn.add( currentPage );
	        			  //Kommentieren
	        			  currentSection = currentPage;

	    			  }
	        	  }


	        	  //Neue Section
	        	  if ( parStyle == psCaption ){
					  //System.out.println("  Neue Section");
					  currentSection = new QstnSection();
					  currentSection.setSectionType( QstnSection.TYPE_SECTION );
					  currentSection.setTitle( parContent );
					  //Zum Testen Namen setzen (wird in printcomponent() ausgegeben)
					  currentSection.setName( "Caption: " + parContent );
					  currentPage.add( currentSection );

				  }


	        	  //Neues Item?
	        	  //Erster Absatz
	        	  if ( i == 0 ){
	        		 if ( itemTags.contains( parStyle )){
	        			 currentItem = new Item();
	        			 currentSection.add( currentItem );
	        			 //System.out.println("--neues Item--");
	        		 }
	        	  }
	        	  //Absatz 2 bis N
	        	  else{
	        		  formerParStyle = ((TextParagraph) textParagraphList.get( i-1 )).getStyle();
	    			  if((itemTags.contains( parStyle )) && (! itemTags.contains( formerParStyle ) )){
	    				  currentItem = new Item();
	         			  currentSection.add( currentItem );
	         			  //System.out.println("--neues Item--");
	    			  }
	        	  }



	        	  //Intro mit Elternelement Section
	        	  if ( parStyle == psIntro ){
	        		  QstnIntro intro = new QstnIntro( parContent );
	        		  currentSection.add( intro );
	        	  }





	        	  //Item-Intro
	        	  //Ein Intro innerhalb eines Items wird als Intro kodiert
				  //Mehrere nachfolgende Intros in Writer werden als ein
				  //Intro mit mehreren Absätzen gespeichert.

	        	  //currentItem.add( new QstnIntro( parContent ));

	        	  if ( parStyle == psIntroItem ){

	        		  //Referenz auf aktuelles Intro aktualisieren
	        		  //erster Absatz
	        		  if( i == 0){
	        			  currenItemIntro = new QstnIntro();
	        			  currentItem.add( currenItemIntro );
	        		  }
	        		  //Absatz 2 bin N
	        		  //Neues Introobjekt wird nur erzeugt, wenn
	        		  //vorheriger Absatz kein instrobjekt war
	        		  //da mehrere Writer QuestionInstros in einem Intro-Objekt
	        		  //mit mehreren Absätzen gespeichert werden.
	        		  else{
	        			//vorheriger Absatz war kein Intro
	        			if( ((TextParagraph) textParagraphList.get( i-1 )).getStyle() != psIntroItem ){
	        				currenItemIntro = new QstnIntro();
	          			    currentItem.add( currenItemIntro );       			}
	        		  }

	        		  //Text zum aktuellen Intro hinzufügen
	        		  currenItemIntro.addTextParagraph( parContent );


	        	  }// Itemintro



	        	  //(Item-)Instruction
	        	  if ( parStyle == psInstruction ){

	        		  //Referenz auf aktuelle Instruction aktualisieren
	        		  //erster Absatz
	        		  if( i == 0){
	        			  currentInstruction = new QstnInstruction();
	        			  currentItem.add( currentInstruction );
	        		  }
	        		  //Absatz 2 bin N
	        		  //Neues Instructionobjekt wird nur erzeugt, wenn
	        		  //vorheriger Absatz kein instructionobjekt war
	        		  //da mehrere Writer Instructions in einem Instruction-Objekt
	        		  //mit mehreren Absätzen gespeichert werden.
	        		  else{
	        			//vorheriger Absatz war keine Instruktion
	        			if( ((TextParagraph) textParagraphList.get( i-1 )).getStyle() != psInstruction ){
	        				currentInstruction = new QstnInstruction();
	        				currentItem.add( currentInstruction );
	        			}
	        		  }

	        		  //Text zur aktuellen Instruktion hinzufügen
	        		  //currentItem.add( new QstnInstruction( parContent ));
	        		  currentInstruction.addTextParagraph( parContent );
	        	  }





	        	  //Question
	        	  if ( parStyle == psQuestion ){
	        		  currentItem.add( new QstnQuestion( parContent ));
	        	  }




	        	  //Ist Absatz erster Absatz einer neue Antwortskala?
	           	  //Erster Absatz des Dokumentes
	        	  if ( i == 0 ){
	        		 if ( scaleTags.contains( parStyle )){
	        			 currentChoices = new QstnChoices();
	        			 currentItem.add( currentChoices );
	        			 scalePointCount = 0;
	        			 multipleWasSet = false;
	        			 //System.out.println("--neues Antwortskala--");

	        		 }
	        	  }
	        	  //Absatz 2 bis N
	        	  else{
	        		  formerParStyle = ((TextParagraph) textParagraphList.get( i-1 )).getStyle();
	    			  if((scaleTags.contains( parStyle )) && (! scaleTags.contains( formerParStyle ) )){
	    				  currentChoices = new QstnChoices();
	         			  currentItem.add( currentChoices );
	         			  scalePointCount = 0;
	         		      multipleWasSet = false;
	         			  //System.out.println("--neues Antwortskala--");
	    			  }
	        	  }


	  /*
	   *
	   * Logik beschreiben: ist bescheuert
	   *
	   */



	        	  //Absatz ist AntwortSkala
	        	  if( scaleTags.contains( parStyle )){

	        		  scalePointCount++;

	        		  //Skalentyp wird gesetzt
	        		  //Choice Objekt wird eingefügt

	        		  //Einfachnennung
	        		  if( singleTags.contains( parStyle )){
	        			  //Information zum Typ der Antwortskala wird im der Antwortskala und
	        			  //im Choivesobjekt gespeichert. Später ändern wenn Struktur klar ist
	        			  currentChoices.setChoiceType(QstnChoices.CHOICETYPE_CHOICES);
	        			  currentItem.setChoiceType( QstnChoices.CHOICETYPE_CHOICES );
	        			  currentChoices.setMultiple( false );
	        			  multipleWasSet = true;
	        			  Category category = new Category( parContent );
	        			  category.setOpen( false );
	        			  currentChoices.addChoice( category );
	        		  }
	        		  //Einfachnennung Nonopinion
	        		  if( singleNonOpinionTags.contains( parStyle )){
	        			  //Information zum Typ der Antwortskala wird im der Antwortskala und
	        			  //im Choivesobjekt gespeichert. Später ändern wenn Struktur klar ist
	        			  currentChoices.setChoiceType(QstnChoices.CHOICETYPE_CHOICES);
	        			  currentItem.setChoiceType( QstnChoices.CHOICETYPE_CHOICES );
	        			  currentChoices.setMultiple( false );
	        			  multipleWasSet = true;
	        			  Category category = new Category( parContent );
	        			  category.setOpen( false );
	        			  category.setNonOpinion( true );
	        			  currentChoices.addChoice( category );
	        		  }
	        		  //Mehrfachnennung
	        		  if( multipleTags.contains( parStyle )){
	        			  currentChoices.setChoiceType(QstnChoices.CHOICETYPE_CHOICES);
	        			  currentItem.setChoiceType(QstnChoices.CHOICETYPE_CHOICES);
	        			  currentChoices.setMultiple( true );
	        			  multipleWasSet = true;
	        			  Category category = new Category( parContent );
	        			  category.setOpen( false );
	        			  currentChoices.addChoice( category );
	        		  }
	            	  //Matrix-Einfachnennung (single, singleNonOpinion)
	        		  if( matrixSingleTags.contains( parStyle )){
	        			  currentChoices.setOrientation( QstnChoices.ORIENTATIN_HORIZONTAL );
	        			  currentChoices.setChoiceType( QstnChoices.CHOICETYPE_MATRIX );
	        			  currentItem.setChoiceType( QstnChoices.CHOICETYPE_MATRIX );
	        			  currentChoices.setMultiple( false );
	        			  multipleWasSet = true;
	        			  Category category = new Category( parContent );
	        			  category.setOpen( false );
	        			  if( parStyle == psMatrixSingleNonOpinion ){
	        				  category.setNonOpinion( true );
	        			  }
	        			  currentChoices.addChoice( category );
	        		  }
	            	  //Matrix Mehrfachnennung
	        		  if( matrixMultipleTags.contains( parStyle )){
	        			  currentChoices.setOrientation( QstnChoices.ORIENTATIN_HORIZONTAL );
	        			  currentChoices.setChoiceType( QstnChoices.CHOICETYPE_MATRIX );
	        			  currentItem.setChoiceType( QstnChoices.CHOICETYPE_MATRIX );
	        			  currentChoices.setMultiple( true );
	        			  multipleWasSet = true;
	        			  Category category = new Category( parContent );
	        			  category.setOpen( false );
	        			  currentChoices.addChoice( category );
	        		  }
	            	  //Likertskala
	        		  if( likertTags.contains( parStyle )){
	        			  currentChoices.setChoiceType( QstnChoices.CHOICETYPE_LIKERT );
	        			  currentItem.setChoiceType( QstnChoices.CHOICETYPE_LIKERT );
	        			  currentChoices.setMultiple( false );
	        			  multipleWasSet = true;
	        			  Category category = new Category( parContent );
	        			  category.setOpen( false );
	        			  currentChoices.addChoice( category );
	        		  }

	        		  //Offene (Zusatz)Kategorie
	        		  if( parStyle == psChoiceOpenAddon ){
	        			  currentChoices.setChoiceType( QstnChoices.CHOICETYPE_CHOICES );
	        			  currentItem.setChoiceType( QstnChoices.CHOICETYPE_CHOICES );
	        			  Category category = new Category( parContent );
	        			  category.setOpen( true );
	        			  category.setLength( 50 );
	        			  currentChoices.addChoice( category );
	        		  }

	        		  //Offene Matrix-Kategorie
	        		  if( parStyle == psMatrixOpen ){
	        			  currentChoices.setChoiceType( QstnChoices.CHOICETYPE_MATRIX );
	        			  currentItem.setChoiceType( QstnChoices.CHOICETYPE_MATRIX );
	        			  Category category = new Category( parContent );
	        			  category.setOpen( true );
	        			  category.setLength( 20 );
	        			  currentChoices.addChoice( category );
	        		  }

	        		  //Offene (Einzel)Kategorie
	        		  if( parStyle == psChoiceOpen ){
	        			  currentChoices.setChoiceType( QstnChoices.CHOICETYPE_CHOICES );
	        			  currentItem.setChoiceType( QstnChoices.CHOICETYPE_CHOICES );
	        			  Category category = new Category( parContent );
	        			  category.setOpen( true );
	        			  category.setLength( 250 );
	        			  currentChoices.addChoice( category );
	        		  }


	        	  }//Absatz ist Antwortskala


	        	  //MatrixItem
	        	  if( parStyle == psMatrixItem ){
	        		  currentChoices.addMatrixItem( parContent );
	        	  }


	        	  //Letzter Absatz Antwortskala?
	        	  //Skalentyp (multiple/single) festlegen, falls alle Antwortkategorien offen sind
	        	  //Absatz 2 bis N
	        	  if( i > 0 ){
	        		  if( i < textParagraphList.size()-1){

	        			  nextParStyle = ((TextParagraph) textParagraphList.get( i+1 )).getStyle();
	        		      if(( scaleTags.contains( parStyle )) && ( ! scaleTags.contains( nextParStyle ) )){
	        		    	  //Multiple/Single Wert festlegen, falls alle Antwortkategorien offen sind
	        		    	  if (! multipleWasSet ){
	        		    		  if ( scalePointCount > 1 ) currentChoices.setMultiple( true );
	        		    		  else currentChoices.setMultiple( false );
	        		    	  }

	        			      //System.out.println("--Ende Antwortskala " + scalePointCount );
	        		      }
	        		  }//Zeile 2 bis N-1
	        		  //Zeile N
	        		  else{
	        			  if( scaleTags.contains( parStyle )){
	        				  //Multiple/Single Wert festlegen, falls alle Antwortkategorien offen sind
	        				  if (! multipleWasSet ){
	        		    		  if ( scalePointCount > 1 ) currentChoices.setMultiple( true );
	        		    		  else currentChoices.setMultiple( false );
	        		    	  }
	        				  //System.out.println("--Ende Antwortskala " + scalePointCount);
	        			  }
	        		  } // Zeile N
	        	  } //Letzter Absatz einer Antwortskala


	        	  //WriterParagraph par = writerContentBuffer.get( i );
	        	  //System.out.println( "["+ i +"] " + (String) styleMap.get(par.parStyle) + "["+ scale +"] " + scaleType  );


	          } // Alle Absätze




			  //Testausgabe des Fragebogens

			  //System.out.println("******************* Fragebogen-Modell **********************");
			  //qstn.printComponent();

			  //String exportFileName = aFileName + ".xml";

			  //Konvertieren nach QML
			  //Model2QmlConverter qmlConverter = new Model2QmlConverter( qstn, new File( exportFileName ));
			  //qmlConverter.convert();

			  //System.out.println("schreibe: " + exportFileName );

			  return qstn;

		  }


	}


