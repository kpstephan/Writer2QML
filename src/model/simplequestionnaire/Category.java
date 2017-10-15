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

//Soll Category auch als qstnComponent behandelt werden?
//Oder Soll ein Choices-Objekt als QstnComponent behandelt werden?
//Zunächst wird Category-Objekt

public class Category {
	//später Kapseln
	public String id;
	public String label;

	private String text = "";
	private boolean open = false;
	//private int value = 0;
	private int length = 50;
	private boolean nonOpinion = false;

	public Category(){

	}

	public Category( String text ){
		this.text = text;
	}

	//Getter und Setter
	public void setOpen( boolean open ){
		this.open = open;
	}

	public boolean getOpen(){
		return open;
	}

	public void setNonOpinion( boolean nonOpinion ){
		this.nonOpinion = nonOpinion;
	}

	public boolean getNonOpinion(){
		return nonOpinion;
	}

	public void setLength( int length ){
		this.length = length;
	}

	public int getLength(){
		return length;
	}


	public void setText( String text ){
		this.text = text;
	}

	public String getText(){
		return text;
	}



} //category


