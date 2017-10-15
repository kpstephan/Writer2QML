/*

 Copyright 2004-2008, 2017 Karsten Stephan

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

package model.writerparagraphs;

import java.util.ArrayList;
import java.util.Iterator;

public class TextParagraphList {

	ArrayList<TextParagraph> paragraphList = new ArrayList<TextParagraph>();

	public void addParagraph(TextParagraph paragraph){
		paragraphList.add(paragraph);
	}

	public Iterator<TextParagraph> iterator(){
		return paragraphList.iterator();
	}

	public int size(){
		return paragraphList.size();
	}

	public TextParagraph get(int index){
		return paragraphList.get(index);
	}

	public boolean isEmpty(){
		return paragraphList.isEmpty();
	}

	public String toString(){
		StringBuffer sb = new StringBuffer();
		String ls = System.getProperty( "line.separator" );
		for ( Iterator<TextParagraph> it = iterator(); it.hasNext(); ){
		  TextParagraph writerParagraph = it.next();
		  sb.append( writerParagraph.toString() + ls);
		}
		return sb.toString();
	}

}