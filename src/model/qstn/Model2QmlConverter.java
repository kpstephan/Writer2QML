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

package model.qstn;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;


/*
 * Diese Klasse dient der Konvertierung eines QstnComponent-Kompositums
 * Nach QML.
 *
 */


public class Model2QmlConverter {

	private File exportFile;
	private QstnComponent qstnComponent;
	private PrintWriter f;


	//Zähler zur Nummerierung der Items und Section
	private int itemCounter;
	private int sectionCounter;

	//Variablen zur Formatierug der Einrückung.
	private int indentLevel;

	//Konstruktor
	public Model2QmlConverter( QstnComponent qstnComponent, File exportFile  ){

		this.qstnComponent = qstnComponent;
		this.exportFile = exportFile;

	}


	//Hilfsfunktion zur Formatierung
	private String spc( int indentLevel){
		int step = 4;
		int leftmargin = 1;
		StringBuffer sb = new StringBuffer("");
		for( int i=0; i <  (leftmargin + ( indentLevel * step )) ; i++ ){
			sb.append(" ");
		}
		return sb.toString();
	}




	//Ausgabe ItemIntro
	//Ein Intro innerhalb eines Items wird als Intro kodiert
    //Mehrere nachfolgende Intros in Writer werden als ein
    //Intro mit mehreren Absätzen gespeichert.
	private void printItemIntro( QstnComponent itemComponent ){

		QstnIntro intro = (QstnIntro) itemComponent;
    	Iterator textParIt = intro.getTextParIterator();

    	//Text der Instruction hat 0 oder 1 Absatz
    	if ( intro.getParCount() < 2 ){

    		String parContent = "";
    	    if( intro.getParCount() == 1) parContent = (String) textParIt.next();

    		f.println( spc(indentLevel) + "<qml:intro>" + parContent  + "</qml:intro>" );
    	}
    	//Text hat mehrere Absätze
    	else{
    		f.println( spc(indentLevel) + "<qml:intro>");
    		indentLevel++;
    		while( textParIt.hasNext() ){
    			f.println( spc(indentLevel) + "<qml:textPar>" + (String) textParIt.next() + "</qml:textPar>");
    		}
    		indentLevel--;
    		f.println( spc(indentLevel) + "</qml:intro>");
    	}// Text hat mehrere Absätze


	}


	//Ausgabe ItemInstruction
    //Instruction darf in Writer nur innerhalb eines
    //Items vorkommen
    //Instruction
    private void printItemInstruction( QstnComponent itemComponent ){

    	QstnInstruction instruction = (QstnInstruction) itemComponent;
    	Iterator textParIt = instruction.getTextParIterator();

    	//Hat Text der Instruction mehrere Absätze?
    	if ( instruction.getParCount() < 2 ){

    		String parContent = "";
    	    if( instruction.getParCount() == 1) parContent = (String) textParIt.next();

    		f.println( spc(indentLevel) + "<qml:instruction>" + parContent  + "</qml:instruction>" );
    	}
    	//Text hat mehrere Absätze
    	else{
    		f.println( spc(indentLevel) + "<qml:instruction>");
    		indentLevel++;
    		while( textParIt.hasNext() ){
    			f.println( spc(indentLevel) + "<qml:textPar>" + (String) textParIt.next() + "</qml:textPar>");
    		}
    		indentLevel--;
    		f.println( spc(indentLevel) + "</qml:instruction>");
    	}

	}



	//Ausgabe der ItemQuestion
	private void printItemQuestion( QstnComponent itemComponent ){
		f.println( spc(indentLevel) + "<qml:question>" + ((QstnQuestion) itemComponent).getText() + "</qml:question>" );

	}


	//Ausgabe der MatrixQuestion
	private void printMatrixQuestion( QstnComponent itemComponent ){
		f.println( spc(indentLevel) + "<qml:matrixQuestion>" + ((QstnQuestion) itemComponent).getText() + "</qml:matrixQuestion>" );

	}



	//Ausgabe von Antwortskalen, wird von Items und MatrixItems verwendet
	private void printItemResponses( QstnComponent itemComponent, String itemId ){

		QstnChoices choices = ( QstnChoices ) itemComponent;

        f.print( spc(indentLevel) + "<qml:responses" );

        //Mehrfach/Einfachnennung
		if( choices.getMultiple()) f.print(" type=\"multiple\"");
		else f.print(" type=\"single\"");


		//Skalenausrichtung
		if( choices.getOrientation() == choices.ORIENTATION_VERTICAL ){
		    f.print(" orientation=\"vertical\"");
		} else{
			f.print(" orientation=\"horizontal\"");
		}

		f.println(">");
		indentLevel++;


		//Ausgabe der Choice-Elemente
		String idAtt, typeAtt="", lengthAtt,  valueAtt;
		String choiceId;
		int length;
		int choiceCounter;

		choiceCounter = 0;
		Iterator choiceIt = choices.getChoiceIterator();
		while( choiceIt.hasNext() ){

			choiceCounter++;
		    Category category = (Category) choiceIt.next();

		    //Attribut open und length
		    if( category.getOpen()== true){
		    	typeAtt = " type=\"open\"";
		    	length = category.getLength();
		    	//Länge sollte gesetzt sein, wird per Default auf 50 gesetzt
		    	if ( length == 0 ) length = 50;
		    	lengthAtt = " length=\"" + length +"\"";
		    } else{

		    	lengthAtt = "";
		    	if( category.getNonOpinion() == true ){
		    		typeAtt = " type=\"nonOpinion\"";
		    	}
		    	else { typeAtt = " type=\"closed\"";}

		    }

		    //Attribut NonOpinion
	        //
	        //else nonOpinionAtt = "";



	        //Attribut value wird bei einfachnennungen mit mehr
		    //ale einer Kategorie gesetzt
	        if((choices.getMultiple() == false ) && ( choices.choiceCount() > 1 )){
	            valueAtt = " value=\""+ choiceCounter + "\"";
	        }else valueAtt = "";


		    choiceId = itemId + "C" + choiceCounter;

		    //Ausgabe der Kategorie
		    f.print( spc(indentLevel) +  "<qml:choice");
		    f.print( " id=\""+ choiceId + "\"" );
		    f.print( typeAtt );
		    f.print( lengthAtt );
		    f.print( valueAtt );
		    f.print( ">" );
		    f.print( category.getText());
		    f.println("</qml:choice>");

		}

		//f.print( " id=\"" + itemId + "C" + categoryCounter + "\"" );
		indentLevel--;
        f.println( spc(indentLevel) + "</qml:responses>");


	} //


	private boolean isValue( String string){

		boolean result = true;
		try{
		    Integer.parseInt( string );
		}catch(NumberFormatException e){
			result = false;
		}
		return result;
	}

	//Ausgabe von Antwortskalen, wird von Likertitems verwendet
	//Evtl. mit printItemResponses zusammenfügen
	private void printLikertItemResponses( Item item, QstnComponent itemComponent, String itemId ){

		//TDO: nicht likert code entfernen

		QstnChoices choices = ( QstnChoices ) itemComponent;




/* TDO: Achtung: Die Anzahl der Kategorien darf bei Likertitems nicht kleiner als 4 sein.
 * Wird beim Einlesen getestet
 *
 *  Die Konsistenz der Werte muss überprüft werden. Falls Inkonsistenzen auftreten
 *  werden Defaultwerte gesetzt.
 *
 */

		    String startValueStr;
		    String stopValueStr;
	   		int pointCount;
	   		int startValue;
	   		int stopValue;
	   		String leftLabel;
	   		String rightLabel;
	   		String showValuesAtt;

	   		//Wenn die Skalenpunkte der Likertkategorie mit Werten belegt sind
	   		//Werden die Werte zur Nummerierung verwendet und die NUmmern werden angezeigt
	   		//Ansonsten werden die Skalenpunkte von 1 bis N durchnummeriert und nicht angezeigt

	   		//TDO Alle Werte überprüfen!!!
	   		//Hier alle Werte überprüfen und bei Problemen Defaultwerte verwenden
	   		//parseInt() absichern
	   		//Bei Input durch Writer2QML wurden alle Werte geprüft
	   		/*
	   		if( pointCount >= 4 ){
	   			leftLabel = ((Category) choices.getCategory( 0 )).getText();
		   		rightLabel = ((Category) choices.getCategory( pointCount - 1 )).getText();
		   	}
	   		//Defaultwerte
	   		else{
	   		   leftLabel = "";
	   		   rightLabel = "";
	   		   pointCount = 4;
	   		}
	   		*/



	   		//Werte des Modells lesen
	   		pointCount = choices.choiceCount();
	   		startValueStr = ((Category)choices.getCategory( 1 )).getText();
	   		stopValueStr = ((Category)choices.getCategory( pointCount-2 )).getText();

	   		//Defaultwerte bei inkonsistenten Daten hier einbauen
	   		//Anzahl der Kategorien muss >=4 sein
	   		//getCategory(1) muss eine Zahl oder leer sein
	   		//getCategory(2) muss eine Zahl oder leer sein
	   		//sonst: pointCount = 4, left ="", right = "", startValue=1, StopValue=2, showValue="false"


	   	    //Werte sind Konsistent
	   		if( (( pointCount >= 4) && ( isValue( startValueStr )) && ( isValue( stopValueStr )))
	   		 || (( pointCount >= 4) && ( startValueStr.length() == 0 ) && ( stopValueStr.length() == 0 ))){

	   		    //Werte für Skalenpunkte wurden angegeben
	   		    if(startValueStr.length() > 0){
	   		        startValue = Integer.parseInt( startValueStr );
	   		        stopValue = Integer.parseInt( stopValueStr );
	   		        showValuesAtt = " showValues=\"true\"";
	   		    }
	   		    //Werte wurden nicht angegeben: Defauktwerte 1..N für Value-Attribute
	   		    else{
	   			    startValue = 1;
	   			    stopValue = pointCount-2;
	   			    showValuesAtt = " showValues=\"false\"";
	   		    }

		   		leftLabel = ((Category) choices.getCategory( 0 )).getText();
		   		rightLabel = ((Category) choices.getCategory( pointCount - 1 )).getText();

	   		}//Werte sind Konsistent
	   		else{
	   			pointCount = 4;
	   			leftLabel = "";
	   			rightLabel = "";
	   			startValue=1;
	   			stopValue=2;
	   			showValuesAtt=" showValues=\"false\"";

	   		}












    	    f.print( spc(indentLevel) + "<qml:responses" );

	        //Einfachnennung
			f.print(" type=\"single\"");

			//Skalenausrichtung immer Horizontal
			f.print(" orientation=\"horizontal\"");

			//Likert
			f.print(" likert=\"true\"");

			//Werte immer zeigen
			f.print( showValuesAtt );



	   		f.print(" scalepoints=\"" + (pointCount-2) + "\"");


			f.println(">");


			indentLevel++;
	   		//Ausgabe der Choice Elemente

			String choiceId1 = itemId + "C" + "1";
			String choiceId2 = itemId + "C" + "2";

             //Erster Skalenpunkt
			 f.print( spc(indentLevel) +  "<qml:choice");
			 f.print( " id=\""+ choiceId1 + "\"" );
			 f.print( " type=\"closed\"" );
			 f.print( " position=\"first\"" );
			 f.print( " value=\""+ startValue + "\"");
			 f.print( ">" );
			 f.println( leftLabel + "</qml:choice>");

			 //Letzter Skalenpunkt
		       //Erster Skalenpunkt
			 f.print( spc(indentLevel) +  "<qml:choice");
			 f.print( " id=\""+ choiceId2 + "\"" );
			 f.print( " type=\"closed\"" );
			 f.print( " position=\"last\"" );
			 f.print( " value=\""+ stopValue + "\"");
			 f.print( ">" );
			 f.println( rightLabel + "</qml:choice>");

			 indentLevel--;

			 f.println( spc(indentLevel) + "</qml:responses>" );

			 indentLevel--;



	 } // printLikertItemResponse();




	private void printMatrixResponses( QstnComponent itemComponent){

		String secId = "M" + itemCounter;
		String itemId;

		QstnChoices choices = (QstnChoices) itemComponent;
		Iterator matrixItemIt = choices.getMatrixItemIterator();


		int matrixItemCounter = 0;
		while( matrixItemIt.hasNext()){

			matrixItemCounter++;
            itemId = secId + "V" + matrixItemCounter;

			//Ausgabe der Items
			String matrixItemText = (String) matrixItemIt.next();

			//MatrixItems werden als Item.Question Elemente ausgegeben
			f.println( spc(indentLevel) +  "<qml:item id=\"" + itemId + "\">" );
			indentLevel++;
			f.println( spc(indentLevel) +  "<qml:question>" + matrixItemText + "</qml:question>" );

			//Ausgabe der Antwortskalen
			printItemResponses( itemComponent, itemId );

			indentLevel--;
			f.println( spc(indentLevel) +  "</qml:item>" );

		}


	}




    //Komponete im QML-Format ausgeben
    private void printQstnComponent( Iterator it ){


 	    while ( it.hasNext()){

	        //Element holen
	        QstnComponent component = (QstnComponent) it.next();


/* -------------- Verarbeitung der QstnComponent Elemente ------*/
// der Ebene unterhalb Page und Section. Elemente unterhalb Item werden "per Hand" verarbeitet




	      //Eine Section als KIndelement von Item wird als additionaltext kodiert
	      //Die Section dürfte nur eine Textzeile enthalten, sie dürfte nicht leer sein
		  if( component instanceof QstnIntro ){

			  QstnIntro intro = ( QstnIntro ) component;
		      Iterator textParIt = intro.getTextParIterator();
		      //Text dürfte nur eine Zeile enthalten
		      String parContent = "";
	    	  if( intro.getParCount() > 0) parContent = (String) textParIt.next();

	    	  f.println( spc(indentLevel) + "<qml:additionalText>" + parContent + "</qml:additionalText>" );

		  }



		    //Hier Likert Items einbauen!

	        //Item -Neu
	        if( component instanceof Item ){

	        	itemCounter++;
	        	Item item = (Item) component;

	        	//StandardItem und Likert-Item
	        	if (( item.getChoiceType() == QstnChoices.CHOICETYPE_CHOICES )
	        	|| (item.getChoiceType() == QstnChoices.CHOICETYPE_LIKERT)){


	        	    //Start Item
	        	    String itemId = "V" + itemCounter;
				    f.println( spc(indentLevel) + "<qml:item id=\"" + itemId + "\">" );
				    indentLevel++;

				    //Item Kinder
				    Iterator itemIt = item.iterator();
				    while ( itemIt.hasNext()){


					    //Element holen
				        QstnComponent itemComponent = (QstnComponent) itemIt.next();

				        //Intro
				        if( itemComponent instanceof QstnIntro )printItemIntro( itemComponent );

				        //Instruction
				        if( itemComponent instanceof QstnInstruction ) printItemInstruction( itemComponent );

			 	        //Question
				        if( itemComponent instanceof QstnQuestion )printItemQuestion( itemComponent );

				        //Antwortskala
				        if( itemComponent instanceof QstnChoices ){
				        	if ( item.getChoiceType() == QstnChoices.CHOICETYPE_CHOICES ){
				        		printItemResponses( itemComponent, itemId );
				        	}
				        	else{
				        		//Quick and Dirty: Ändern, hier wird das Item und das Kindelement
				        		//QstnCHoices übergeben
				        		printLikertItemResponses( item, itemComponent, itemId );
				        	}

				        }



				    }// Item Kinder


					//Ende Item
				    indentLevel--;
					f.println( spc(indentLevel) +  "</qml:item>" );
					f.println( "" );


	        	}//StandardItem



	        	//MatrixItem
	        	//StandardItem
	        	if (item.getChoiceType() == QstnChoices.CHOICETYPE_MATRIX){

	        	    //Start Matrixsection
	        	    String itemId = "M" + itemCounter;
				    f.println( spc(indentLevel) + "<qml:matrix id=\"" + itemId + "\">" );


				    //Item Kinder
				    indentLevel++;
				    Iterator itemIt = item.iterator();
				    while ( itemIt.hasNext()){


					    //Element holen
				        QstnComponent itemComponent = (QstnComponent) itemIt.next();

				        //Intro
				        if( itemComponent instanceof QstnIntro )printItemIntro( itemComponent );

				        //Instruction
				        if( itemComponent instanceof QstnInstruction ) printItemInstruction( itemComponent );

			 	        //Question
				        //Fragen zur Matrix werden als MatrixQuestion kodiert
				        if( itemComponent instanceof QstnQuestion )printMatrixQuestion( itemComponent );

				        //Antwortskala
				        if( itemComponent instanceof QstnChoices ) printMatrixResponses( itemComponent );



				    }// Item Kinder
				    indentLevel--;

					//Ende Item
					f.println( spc(indentLevel) +  "</qml:matrix>" );
					f.println( "" );


	        	}//StandardItem




	        }// Item








	        //Section
			if( component instanceof QstnSection ){

				sectionCounter++;
				String sectionId = "S" + sectionCounter;

				QstnSection section = ( QstnSection ) component;

				f.println( "" );
				f.print( spc(indentLevel) + "<qml:section" );

				//Id der Section
				f.print( " id=\"" + sectionId + "\"");


				//Typ der Section: page oder group
				if( section.getSectionType() == QstnSection.TYPE_PAGE){
					f.print( " type=\"page\"" );
				}
				else f.print( " type=\"group\"" );


				//Titel der Section
				String sectionTitle = section.getTitle();
				if( sectionTitle.length() > 0 ) f.print( " title=\"" + sectionTitle + "\"");
				f.println( ">" );

				f.println( "" );


			}




/* -------------- Bei Sections Iteration über weitere Kindelemente  -------*/



            if( component instanceof QstnSection){

                indentLevel++;

	            printQstnComponent( ((QstnSection) component).getChildren());

	            indentLevel--;


	            //Ausgabe der Abschluss-Elemente
				f.println( spc(indentLevel) + "</qml:section>");
				f.println("");

	        }


        } //QstnComponent Kindeobjekte
    } //printQstnComponent()


	public StringBuffer convert(){


		   String encodingStr = "ISO-8859-1";

		//try{

			//Konvertierungsergebnis wird zunächst als String zurückgegeben
			StringWriter stringWriter;
			stringWriter = new StringWriter();
		    f = new PrintWriter( stringWriter );
	        //f = new PrintWriter( new BufferedWriter( new FileWriter( exportFile )));

			f.println("<?xml version=\"1.0\" encoding=\"" + encodingStr + "\"?>");
			f.println("");

			//Titel des Fragebogens
			String qstnTitle = "";
			//Fragebogen in Instanzvariable. Das erste Element eines Fragebogens
			//ist vom Typ Qstn
			if( qstnComponent instanceof Qstn ){
			    qstnTitle = ((Qstn) qstnComponent).getQstnTitle();
			}


			//Document Element
			String qstnId="qstnId";
			f.println( "<qml:questionnaire xmlns:qml=\"www.qml.uni-siegen.de\" version=\"1.2\" title=\"" + qstnTitle + "\" id=\"" + qstnId +"\">" );
			f.println("");


	        itemCounter = 0;
	        sectionCounter = 0;
	        indentLevel = 0;
	        //QstnSection qstnComponent enthält Fragebogen
	        printQstnComponent(((QstnSection) qstnComponent).getChildren());

	        f.println("</qml:questionnaire>");

	        // Datei schließen
		    f.close();


		//}catch (IOException e){

		    //System.out.println("Fehler beim schreiben der Datei");

		//}

		return stringWriter.getBuffer();

	} //convert()


}
