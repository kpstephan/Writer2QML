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
import java.util.Iterator;

 /*
 * Abstrakte Komponenten-Implementierung des Composite Musters
 * Von dieser Klasse werden die einzelnen Bestandteile eines
 * Fragebogens abgeleitet
 */

//Überarbeiten: Welche Eigenschaften sollten für Komponenten vorgesehen werden


public class QstnComponent {

	//Methoden, die die hierarchische Struktur abbilden

	//Hinzufügen von Kindkomponenten
	public void add( QstnComponent qstnComponent ){
		throw new UnsupportedOperationException();
	}

	//Entfernen von Kindkomponenten
	public void remove( QstnComponent qstnComponent ){
		throw new UnsupportedOperationException();
	}


	public QstnComponent getChild( int i){
		throw new UnsupportedOperationException();
	}

	//childCount() ??
	//Wird nicht benötigt, wenn mit Iteratoren gearbeitet wird
	//aber wozu wird getChild dann benötigt?

	//Zugriff auf Instanzvariablen

	public String getName(){
		throw new UnsupportedOperationException();
	}


	public Iterator<QstnComponent> iterator(){
		throw new UnsupportedOperationException();
	}


	//Methoden, die Operationen auf den Fragebogenkomponenten Implementieren

	public void printComponent(){
		throw new UnsupportedOperationException();
	}



}
