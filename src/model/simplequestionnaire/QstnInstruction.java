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
import java.util.Iterator;

//Instruction


public class QstnInstruction extends QstnComponent{

	private String name;

	//??Die Funtionen des parTextObjektes nach außen kapseln oder
	//das parTextObjekt selbst übergeben??
	//Hier wurde zunächst der erste Weg gewählt
	private ParText parText = new ParText();


	//Konstruktor
	public QstnInstruction(){

	}

	//Konstruktor
	public QstnInstruction( String text ){
		parText.addParagraph( text );
	}

	//Konstruktor
	public QstnInstruction( ParText parText ){
	  //kann das Probleme geben?
	  this.parText = parText;
	}





	public String getName(){
		  return name;
	}


	//besser ParText Objekt übergeben und dessen Methoden aufrufen?
    public Iterator<String> getTextParIterator(){
    	return parText.getParIterator();
    }

    //besser ParText Objekt übergeben und dessen Methoden aufrufen?
    public int getParCount(){
    	return parText.getParCount();
    }

    //besser ParText Objekt übergeben und dessen Methoden aufrufen?
    public void addTextParagraph(String text){
    	parText.addParagraph( text );
    }


	/*
	//Getter und Setter
	public String getText(){
		return ;
	}

	public void setText( String text ){
		this.text = text;
	}
*/


	//Ausgabe der Instruction
	public void printComponent(){
		System.out.println( "Instruction: ");
		Iterator<String> it = parText.getParIterator();
		while( it.hasNext() ){
			System.out.println( (String) it.next() );
		}
	}

	//Iterator der Klasse QstnComponent, gibt Nulliterator zurück
	// da Instructions keine QstnComonents aufnehmen können
	public Iterator<QstnComponent> iterator(){
		return new NullIterator();
	}


}
