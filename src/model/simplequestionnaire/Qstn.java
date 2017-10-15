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
//Qstn und Section aus neuer klasse ableiten, die QstnCompnet erweitert
//und Kinder haben kann;

public class Qstn extends QstnSection {

	private String qstnTitle = "";

	//Getter und Setter
	public void setQstnTitle( String title ){
		qstnTitle = title;
	}

	public String getQstnTitle(){
		return qstnTitle;
	}





} //qstn
