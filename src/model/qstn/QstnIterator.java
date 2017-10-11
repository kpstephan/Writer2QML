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
 * KonpositumIterator für QstnComonents
 *
 *  TDO //Klassenhierarchie ändern ,
 *   neue QstnComponent erweitern zu klasse , die Kinder hat
 *   und aus der SstnSection und Qstn abgeleitet sind
 *   Sonst muss bei jeder Klasse, mit dieser Eigenschaft
 *   QstnOperator.next() überarbeitet werden
 */


import java.util.*;

public class QstnIterator implements Iterator<QstnComponent> {

	Stack<Iterator<QstnComponent>> stack = new Stack<Iterator<QstnComponent>>();

	public QstnIterator(Iterator<QstnComponent> iterator){
		stack.push( iterator );
	}

	public boolean hasNext() {
	    if( stack.empty()){
		    return false;
	    }
	    else{
		    Iterator<QstnComponent> iterator = (Iterator<QstnComponent>) stack.peek();
		    if( ! iterator.hasNext() ){
		        stack.pop();
			    return hasNext();
		    }
		    else{
			    return true;
		    }
	    }

    }


	public QstnComponent next() {
      if( hasNext()){
    	  Iterator<QstnComponent> iterator = (Iterator<QstnComponent>) stack.peek();
    	  QstnComponent qstnComponent = (QstnComponent) iterator.next();
    	  //Hier übergeordnete Klasse, aus der section und qstn abgeleitet sind
    	  if( qstnComponent instanceof QstnSection){
    		  stack.push( qstnComponent.iterator());
    	  }
    	  return qstnComponent;
      }
      else{
    	  return null;
      }

	}


	//Zum Löschen aktuelle QstnComponent in Instanzvariable speichern
	//Dann hier löschen, aus welchem Container?
	public void remove() {
		throw new UnsupportedOperationException();
	}



}
