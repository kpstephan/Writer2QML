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
/*
 * Implementiert Antwortskalen
 * choices und matrixitems werden zunächst nicht
 * als QstnComponents implementiert.
 * Evtl. später ändern.
 *
 */

import java.util.ArrayList;
import java.util.Iterator;

//Antwortalternativen
public class QstnChoices extends QstnComponent{


		public static int CHOICETYPE_CHOICES = 1;
		public static int CHOICETYPE_MATRIX = 2;
		public static int CHOICETYPE_LIKERT = 3;

		public static int ORIENTATION_VERTICAL = 4;
		public static int ORIENTATIN_HORIZONTAL = 5;

		private int choiceType;
		private boolean multiple;
		private int orientation;

		//Choices und Matrixitems werden zunächst nicht als
		//QstnItem implementiert
		private ArrayList choices = new ArrayList();
		private ArrayList matrixItems = new ArrayList();



		//Später kapseln
		//Gehört zu choice
		//public String id;
		//public String text;
		//public String label;
		//public boolean open;
		//public int value;

		private String name = "";

		//Konstruktor
		public QstnChoices(){

			//Defaultwerte
			//Defaultwert orientation
			orientation = ORIENTATION_VERTICAL;

		}

		//Konstruktor
		public QstnChoices( int choiceType ){
			//Standardkonstruktor zum setzen von Defaultwerten aufrufen
			this();
			this.choiceType = choiceType;


		}


		public String getName(){
			  return name;
		}

		//Kategorie hinzufügen
		public void addChoice( Category category){
			choices.add( category );
		}

		//Iterator über Kategorien
		public Iterator getChoiceIterator(){
			return choices.iterator();
		}

		//Anzahl der Antwortalternativen zurückgeben
		public int choiceCount(){
			return choices.size();
		}

		//Antwortalternative zurückgeben
		public Category getCategory( int index ){
			return (Category) choices.get(index);
		}


		//Matrixitem hinzufügen
		public void addMatrixItem( Object o){
			matrixItems.add( o );
		}

		//Iterator über MatrixItems
		public Iterator getMatrixItemIterator(){
			return matrixItems.iterator();
		}


		//Getter und Setter
		//choiceType
		public void setChoiceType( int choiceType ){
			this.choiceType = choiceType;
		}

		public int getChoiceType(){
			return choiceType;
		}

		//Multiple
		public void setMultiple( boolean multiple ){
			this.multiple = multiple;

		}

		public boolean getMultiple(){
			return multiple;
		}

		//orientation
		public void setOrientation( int orientation ){
			this.orientation = orientation;

		}

		public int getOrientation(){
			return orientation;
		}





		//Iterator der Klasse QstnComponent, gibt Nulliterator zurück
		// da Matrixfragen keine QstnComonents aufnehmen können
		public Iterator iterator(){
			return new NullIterator();
		}


		//Ausgabe der Antwortskala
		public void printComponent(){

			String skalenTyp="keiner";
			if( choiceType == CHOICETYPE_CHOICES ) skalenTyp = "Choices";
			if( choiceType == CHOICETYPE_MATRIX ) skalenTyp = "Matrix";
			if( choiceType == CHOICETYPE_LIKERT ) skalenTyp = "Likert";


			System.out.println("--Antwortskala--");
			System.out.println("  Typ: " + skalenTyp );
			System.out.println("  Mehrfachnennung: " + multiple );

			//Kategorien
			Category aCategory;
			for ( int i=0; i < choices.size(); i++ ){

				aCategory = (Category)  choices.get( i );
			    System.out.print( "    " + aCategory.getText() );

			    if ( aCategory.getOpen() ){
			    	System.out.println( " (offen: " + aCategory.getLength() + ")" );
			    }
			    else System.out.println("");

		    }//Kategorien


			//Matrix Items
			if( choiceType == CHOICETYPE_MATRIX ){

				System.out.println("--Matrix-Items--");
				for ( int i=0; i < matrixItems.size(); i++ ){
					  System.out.println("  "+ matrixItems.get( i ));
				}
			} //MatrixItems





		}


		/*
		//Ausgabe der Matrix
		public void printComponent(){

			//q&d

			String Field;

			System.out.println( "[#?] " + question );
			if( instruction.length() > 0 ) System.out.println( instruction );

			if ( cat_multiple == true ) Field = "[ ] "; else Field = "( ) ";

		    //Ausgabe der Antwortskala
			Category aCategory;
			String Spaces = "";
			String Fields = "";
			String openField = "___ ";

			System.out.println("");

			//Ausgabe des Kopfes
			for ( int i=0; i < matrixCategories.size(); i++ ){

				aCategory = (Category)  matrixCategories.get( i );
			    System.out.println( Spaces + Field + aCategory.text );
			    Spaces = Spaces + "    ";
			    if ( aCategory.open ) Fields = Fields + openField; else
			      Fields = Fields + Field;
		    }

			System.out.println("");

			//Ausgabe der Zeilen
			for ( int i=0; i < matrixItems.size(); i++ ){
			  System.out.println( Fields  + matrixItems.get( i ));
			}

			System.out.println( "" );

		}

         */

	}






