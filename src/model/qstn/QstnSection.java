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
import java.util.ArrayList;
import java.util.Iterator;

/*
 * QstnSection repräsentiert einen Fragebogenteil, der weitere Teile oder Elemente enthalten kann
 * QstnSection entspricht einem Kompositum des Composite musters
 */

public class QstnSection extends QstnComponent{


	public final static int TYPE_PAGE = 1;
	public final static int TYPE_SECTION = 2;

	private String name;
    private ArrayList<QstnComponent> QstnComponents = new ArrayList<QstnComponent>();

    //Section Eigenschaften
    private String title = "";
    private int sectionType;

    //Später Aufzählungstyp oder int Konstanten
    //Alle möglichen Typen beschreiben, defautwert
    //typ für fragebogenmodell nicht nötig?
    //private String type;


	//Konstruktoren
    //Name ist nicht unbedingt relevant, evtl neuen Konstruktor schreiben
	public QstnSection( String name){
		this.name = name;

	}

	public QstnSection(){
        //Wie ging das nochmal den konstruktor oben mit einem Defaultwert aufzurufen?
	}

	//Getter und Setter
	public void setName( String name ){
		this.name = name;
	}



	public void setTitle( String title ){
		this.title = title;
		//System.out.println("Section.Title ist jetzt: " + title );
	}

	public void setSectionType( int type ){
		sectionType = type;
	}


	//Ist von Component abgeleitet: Struktur überdenken, muss nicht vorhanden sein.
	public String getName(){
		  return name;
		}

	public String getTitle(){
		return title;
	}

	public int getSectionType(){
		return sectionType;
	}

//	Später Aufzählungstyp oder int Konstanten
//	public void setType( String type ){
//		this.type = type;
//	}

//	Später Aufzählungstyp oder int Konstanten
//    public String getType(){
//    	return type;
//    }





	//Funktionen zum abbilden der hierarchischen Struktur

	//Hinzufügen von Kindkomponenten
	public void add( QstnComponent qstnComponent ){
		QstnComponents.add( qstnComponent );
	}

	//Entfernen von Kindkomponenten
	public void remove( QstnComponent qstnComponent ){
		QstnComponents.remove( qstnComponent );
	}


	public QstnComponent getChild( int i){
		return QstnComponents.get( i );
	}


    //childCount() ??
    //Wird nicht benötigt, wenn mit Iteratoren gearbeitet wird
	//wer braucht dann getChild

	//Iterator über Kinder:
	//wird verwendet, um über alle Kinder iterieren zu können
	//wird nur von Section, nicht in QstnComponent deklariert
	//später ändern?
	public Iterator<QstnComponent> getChildren(){
		return QstnComponents.iterator();
	}


	//Funktionen zum löschen

	//Löscht Kind
	public boolean deleteChild( QstnComponent child){
		return false;
	}

	//Funktion zum entfernen einer Section, wobei der Inhalt erhalten bleibt
	public void removeSectionComtainer( QstnComponent container){

	}




	//Externer Iterator Iteriert über alle QstnComponent-Objekte
	public Iterator<QstnComponent> iterator(){
		return new QstnIterator( QstnComponents.iterator() );
	}


	//Ausgabe der Section
	public void printComponent(){

		System.out.println("Section: " + getName());
		//Hier Ausgabe der Kinder
		Iterator<QstnComponent> it = QstnComponents.iterator();

		while( it.hasNext()){

			QstnComponent qstnComponent = it.next();
			qstnComponent.printComponent();
		}


	}


}
