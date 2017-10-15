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
/*
 * Dies hier ist neues Item Objekt
 * Später zu QstnItem umbenennen
 *
 */


import java.util.ArrayList;
import java.util.Iterator;



	public class Item extends QstnComponent{

		//Item kann intro, instruction, question und choices aufnehmen
		private ArrayList<QstnComponent> QstnComponents = new ArrayList<QstnComponent>();

		//Variablen und Objekte sollten gekapselt werden
		//Erläuterung private- und qstn-objeke
		//Logik der Unterscheidung Private und qstn-objekte
		//zB. question, choice

		public String id;
		public String label;

		//Typ der Antwortkategorien
		//Achtung, ist in Choices und in Item definiert
		//Wird von Model2QMLCOnverter verwendet.
		//Ändern, wenn Struktur klar ist
		private int choiceType;

		//QstnObjekt oder "Itemobjekt"?
	    public boolean choices_multiple;

	    public ArrayList<Category> categories;
	    //constructor
	    public Item(){
	    	this.categories = new ArrayList<Category>();

	    }


		//Getter und Setter
		//choiceType
		public void setChoiceType( int choiceType ){
			this.choiceType = choiceType;
		}

		public int getChoiceType(){
			return choiceType;
		}


	    //Hinzufügen von Kindkomponenten
		public void add( QstnComponent qstnComponent ){
			QstnComponents.add( qstnComponent );
		}

		//Entfernen von Kindkomponenten
		public void remove( QstnComponent qstnComponent ){
			QstnComponents.remove( qstnComponent );
		}


		public QstnComponent getChild( int i){
			return (QstnComponent)  QstnComponents.get( i );
		}



		//Externer Iterator Iteriert über alle QstnComponent-Objekte
		public Iterator<QstnComponent> iterator(){
			return new QstnIterator( QstnComponents.iterator() );
		}



		//Ausgabe des Items
		public void printComponent(){

			System.out.println("--Item T:" + choiceType + "--");

			//Hier Ausgabe der Kinder
			Iterator<QstnComponent> it = QstnComponents.iterator();

			while( it.hasNext()){

				QstnComponent qstnComponent = (QstnComponent) it.next();
				qstnComponent.printComponent();
			}
		}//PrintComponent




	}