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

package main;

public class WriterParagraph {

	//Wird in eigener Datei gespeichert, damit mehrere Klassen darauf zugreifen können
	//(Oberfläche muss über eingelesene Absätze iterieren)
	//geht das eleganter?
	//Evtl Buffer und Error in eigenem Objekt kapseln
	//Ein Absatz wird zunächst in writerItem Objekt gespeichert

		public int parStyle;
		public String parContent;
		//Konstruktor
		public WriterParagraph( int style, String content ){
			parStyle = style;
			parContent = content;
		}

	}



