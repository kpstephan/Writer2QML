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

public class TextParagraph {

	private int style;
	private String content;

	public TextParagraph( int style, String content ){
		this.style = style;
		this.content = content;
	}

	public int getStyle() {
		return style;
	}

	public void setStyle(int style) {
		this.style = style;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String toString(){
		return "style: " + getStyle() +  ", content: " + getContent();
	}
}