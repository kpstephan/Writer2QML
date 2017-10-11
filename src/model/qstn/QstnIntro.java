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
 * QstnIntro
 *
 * QstnIntro (und QstnInstruction )enthalten ein ParText Objekt zum Speichern von Texten mit Absätzen.
 * Es definert Schnittstellen, die Zugriff auf einzelne Eigenschaften des
 * ParText Objektes (z.B. iterator(), count()) erlauben. Sollte statt dessen der Zugriff auf
 * das ParText Objekt selbst erlaubt werden?
 *
 *
 */


import java.util.Iterator;

	//Intro


	public class QstnIntro extends QstnComponent{

		private String name;
		//private String text = "";

		//??Die Funtionen des parTextObjektes nach außen kapseln oder
		//das parTextObjekt selbst übergeben??
		//Hier wurde zunächst der erste Weg gewählt
		private ParText parText = new ParText();


		//Konstruktor
		public QstnIntro(){

		}

		//Konstruktor
		public QstnIntro( String text ){
			parText.addParagraph( text );
		}

		//Konstruktor
		public QstnIntro( ParText parText ){
			  //kann das Probleme geben?
			  this.parText = parText;
		}



		public String getName(){
			  return name;
		}




		//Getter und Setter
	    //..





		//besser ParText Objekt übergeben und dessen Methoden aufrufen?
	    public Iterator getTextParIterator(){
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




		//Ausgabe des Intros
		public void printComponent(){
			System.out.println( "Intro: ");
			Iterator it = parText.getParIterator();
			while( it.hasNext() ){
				System.out.println( (String) it.next() );
			}
		}


		//Iterator der Klasse QstnComponent, gibt Nulliterator zurück
		// da Instros keine QstnComonents aufnehmen können
		public Iterator iterator(){
			return new NullIterator();
		}


	}






