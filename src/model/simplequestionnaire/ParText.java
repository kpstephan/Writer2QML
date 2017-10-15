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

package model.simplequestionnaire;
/* Text von Intro, Instruction Komponenten
 *
 */



import java.util.ArrayList;
import java.util.Iterator;




public class ParText {


			private ArrayList<String> paragraphs = new ArrayList<String>();

			private String name = "";


			//Konstruktor
			public ParText(){

			}

			//Konstruktor
			public ParText( String text ){
				paragraphs.add( text );
			}



			public String getName(){
				  return name;
			}

			//Absatz hinzufügen
			public void addParagraph( String paragraph){
				paragraphs.add( paragraph );
			}

			//Iterator über Absätze
			public Iterator<String> getParIterator(){
				return paragraphs.iterator();
			}

			public int getParCount(){
				return paragraphs.size();
			}



			//Getter und Setter



		/*	Überschreiben der Methoden von QstnComponent

			//Iterator der Klasse QstnComponent, gibt Nulliterator zurück
			// da Matrixfragen keine QstnComonents aufnehmen können
			public Iterator iterator(){
				return new NullIterator();
			}


			//Ausgabe des Textes
			public void printComponent(){

				for ( int i=0; i < paragraphs.size(); i++ ){
					String textPar = (String)  paragraphs.get( i );
				    System.out.print( textPar );
			    }//Absätze



			}

			*/
		}






