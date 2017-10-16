/*

 Copyright 2004-2008, 2017 Karsten Stephan

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

package model.writerparagraphs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class TextParagraphList {

	   //Konstanten für Absätztypen, nur an einer Stelle definieren (=> TextparagraphList)
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

		//Konstanten für Absatzparsing
		private final static int ITEMSTATE_NONE = 0;
		private final static int ITEMSTATE_CLOSED = 1;
		private final static int ITEMSTATE_OPEN = 2;

		// Verbale Bezeichnungen für Fehlermeldungen
		//quick and dirty, kapseln
		public HashMap<Integer, String> styleMap = new HashMap<Integer, String>();

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


		//Formatlisten für Parsing
		private ArrayList<Integer> styleMustNotBeEmpty;
		private ArrayList<Integer> styleMustBeEmpty;


	ArrayList<TextParagraph> textParagraphList = new ArrayList<TextParagraph>();

	//quick and dirty, the list has to be encapsulated
	public ArrayList<StyleCodingError> codingErrors = new ArrayList<StyleCodingError>();


	public TextParagraphList(){
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


	}

	public void addParagraph(TextParagraph paragraph){
		textParagraphList.add(paragraph);
	}

	public Iterator<TextParagraph> iterator(){
		return textParagraphList.iterator();
	}

	public int size(){
		return textParagraphList.size();
	}

	public TextParagraph get(int index){
		return textParagraphList.get(index);
	}

	public boolean isEmpty(){
		return textParagraphList.isEmpty();
	}

	public void clear(){
		textParagraphList.clear();
		codingErrors.clear();
	}

	public String toString(){
		StringBuffer sb = new StringBuffer();
		String ls = System.getProperty( "line.separator" );
		for ( Iterator<TextParagraph> it = iterator(); it.hasNext(); ){
		  TextParagraph writerParagraph = it.next();
		  sb.append( writerParagraph.toString() + ls);
		}
		return sb.toString();
	}


	public Iterator<StyleCodingError> errorIterator(){
		return codingErrors.iterator();
	}


	  //Überprüft eingelesene Absätze auf Gültigkeit
	  //Bei Problemen werde der ArrayList codingError Fehlerbeschreibungsobjekte hinzugefügt
	  public boolean checkParStyles(){

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

		  TextParagraph par;

		  int lastScaleStart= -1;
		  int lastLikertPointStart = -1;
		  int lastMatrixScaleStart = -1;

		  int lastItemStart = -1;

		  //Flag, welches anzeigt, ob Item in einem Absatz gültig abgeschlosssen ist
		  //wird evtl. nicht mehr benötigt
		  int itemState = ITEMSTATE_NONE;


		  //Testen ob Datei gültige Absatzformate enthält
		  if( textParagraphList.size() == 0 ){
			  codingErrors.add( new StyleCodingError( 1, "Das Dokument enthält keine QML-Absatzvorlagen"));
		  }


		  //Testen ob Datei mit gültigem Absatzformat beginnt
		  if( textParagraphList.size() > 0 ){
		      //Erster Absatz muss mit gültigem Absatzformat beginnen
	      int parStyle = ((TextParagraph) textParagraphList.get( 0 )).getStyle();
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
		  for (int i = 0; i < textParagraphList.size(); i++){


			  /**************  Variablen für Fehlerprüfung Initialisieren ***************************/

			  parHandled = false;


			  //Variable aStyle initialisieren
			  par = textParagraphList.get( i );
			  aStyle =  par.getStyle();


			  //Variable aFormerStyle initialisieren
			  if ( i > 0 ){
				//nicht in der Schleife deklarieren?
			    aFormerStyle = ((TextParagraph) textParagraphList.get( i-1 )).getStyle();
			  } else aFormerStyle = psNone;


			  //Variable aFollowingStyle initialisieren
			  if ( i == textParagraphList.size() - 1 ){
				  followingStyle = psNone;
			  }else{
			      followingStyle = ((TextParagraph) textParagraphList.get( i+1 )).getStyle();
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
			      if( ( par.getContent().isEmpty() ) && (  styleMustNotBeEmpty.contains( par.getStyle() )  )){
			          codingErrors.add( new StyleCodingError( i+1, itemCounterStr + "Ein Absatz vom Typ " +
			    		      (String) styleMap.get( par.getStyle() ) + " darf nicht leer sein"));
			          parHandled = true;
			      }
      	  }




			  //Diese Absätze müssen leer sein
      	  if (! parHandled){
			      if( ( ! par.getContent().isEmpty() ) && (  styleMustBeEmpty.contains( par.getStyle() )  )){
			          codingErrors.add( new StyleCodingError( i+1, itemCounterStr + "Ein Absatz vom Typ " +
			    		      (String) styleMap.get( par.getStyle() ) + " muß leer sein"));
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
     					  TextParagraph itemPar = textParagraphList.get( s );
			              if( itemPar.getStyle() == psQuestion ){
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
      		  if( par.getContent().length() != 0){
      		      try{
      		          Integer.parseInt( par.getContent() );
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
    		    	    	"Eine Absatzgruppe des Typs " + (String) styleMap.get( par.getStyle() )   +
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
      			      String parContent = ((TextParagraph) textParagraphList.get( lastLikertPointStart )).getContent();
      		          if ( parContent.length() == 0 ) firstPointIsEmpty = true; else firstPointIsEmpty = false;


      		          //Alle Zellen sind einheitlich leer oder mit Zahlen besetzt
      		          TextParagraph likertScalePar;
      		          boolean errorsFound = false;
      		          boolean pointIsEmpty;
      		          for( int s = lastLikertPointStart + 1; s <= i; s++){

      		    	      //Inhaltstyp des Skalenpunkte ermitteln
      			          likertScalePar = textParagraphList.get( s );
      			          if( likertScalePar.getContent().length() == 0 ) pointIsEmpty = true; else pointIsEmpty = false;

      			          //System.out.println( "Test: " + (lastLikertPointStart+1) +  " " +  firstPointIsEmpty + " und " + (s+1)        			    		  + " " + pointIsEmpty);

      			          //Konsistenz der beiden Skalenpunkte testen
      			          if ( firstPointIsEmpty != pointIsEmpty ){
      			    	      errorsFound = true;
      			    	      break;
      			          }


      		          }// Skalenpunkte

      		          if( errorsFound ){
      		    	      codingErrors.add( new StyleCodingError( i+1, itemCounterStr +
      		    	    	"Eine Absatzgruppe des Typs " + (String) styleMap.get( par.getStyle() )   +
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
      		        	  //int firstValue = Integer.parseInt((TextParagraph) textParagraphList.get( lastLikertPointStart )).getContent());
      		        	  int firstValue = Integer.parseInt(((TextParagraph) textParagraphList.get( lastLikertPointStart)).getContent());
      		        	  int secondValue = Integer.parseInt(((TextParagraph) textParagraphList.get( lastLikertPointStart+1 )).getContent());
      		        	  int delta = secondValue - firstValue;

      		        	  //Delta in Schleife testen
      		        	  errorsFound = false;
      		        	  for( int s = lastLikertPointStart + 1; s <= i; s++){
      	        		      //Wert des Skalenpunkte ermitteln
          			          int value0 = Integer.parseInt(((TextParagraph) textParagraphList.get( s -1 )).getContent());
          			          int value1 = Integer.parseInt(((TextParagraph) textParagraphList.get( s  )).getContent());

          			          if( ! ((value1 - value0) == delta)){
          			        	  errorsFound = true;
          			        	  break;
          			          }
      		        	  }
      		        	  //Fehler gefunden
      		        	  if(errorsFound){
             		    	      codingErrors.add( new StyleCodingError( i+1, itemCounterStr +
                		    	  	"Eine Absatzgruppe des Typs " + (String) styleMap.get( par.getStyle() )   +
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
		    			    formerPar = ((TextParagraph) textParagraphList.get( s )).getStyle();
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
 	  					  TextParagraph scalePar = textParagraphList.get( s );
 	  					  //Skala kann einfachnennung oder Mehrfachnennung sein
			              if(( scalePar.getStyle() == psChoiceSingle)|| (scalePar.getStyle() == psChoiceSingleNonOpinion)){
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
		    			    formerPar = ((TextParagraph) textParagraphList.get( s )).getStyle();
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
 	  					  TextParagraph scalePar = textParagraphList.get( s );
 	  					  //Skala kann einfachnennung oder Mehrfachnennung sein
			              if(( scalePar.getStyle() == psMatrixHeadSingle)|| (scalePar.getStyle() == psMatrixSingleNonOpinion)){
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
		      if ( i == textParagraphList.size() - 1 ){
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
}