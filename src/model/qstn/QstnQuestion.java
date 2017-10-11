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


    public class QstnQuestion extends QstnComponent{

		private String name;
		private String text = "";


		//Konstruktor
		public QstnQuestion(){

		}

		//Konstruktor
		public QstnQuestion( String text ){
    		this.text = text;
		}


		public String getName(){
        	return name;
		}

		//Getter und Setter
		public String getText(){
			return text;
		}

		public void setText( String text ){
			this.text = text;
		}



		//Ausgabe der Frage
		public void printComponent(){
			System.out.println( "Question: " + text );
		}

		//Iterator der Klasse QstnComponent, gibt Nulliterator zurück
		// da Questions keine QstnComonents aufnehmen können
		public Iterator iterator(){
			return new NullIterator();
		}


	}// QstnQustion

