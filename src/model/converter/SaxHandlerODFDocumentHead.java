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

package model.converter;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import model.writerparagraphs.TextParagraph;
import model.writerparagraphs.TextParagraphList;


/*
 * Handler für Sax-Parser
 * Liest Absatz mit Formatvorlage "qml:title" aus XML Datei
 *
 */


public class SaxWriterHeadParReader extends DefaultHandler {


	private TextParagraphList textParagraphList;

	//Referenz auf Liste zur Speicherung der gelesenen Absätze

	//Liste zum Speichern der Formatvorlagennamen,
	//die intern für qml:title verwendet werden
	ArrayList<String> qstnTitleTags;

	//StringBuffer zum Speichern der Zeichen
	StringBuffer titleText = new StringBuffer("");

	//Statusvariable für Parsing
	int parsingState;
	static final int psNone = 0;
	static final int psTitle = 1;



	//Konstruktor
	public SaxWriterHeadParReader( TextParagraphList textParagraphList ){

		this.textParagraphList = textParagraphList;
		//this.writerContentBuffer = writerContentBuffer;

		qstnTitleTags = new ArrayList<String>();
		qstnTitleTags.add("qml_3a_title");

	}


	// ------ SAX DefaultHandler Methoden --------
	public void startDocument()
	  throws SAXException
	  {	  }

	  public void endDocument()
	  throws SAXException
	  {	  }



	  public void startElement( String namespaceURI,
	                            String localName,   // local name
	                            String qName,       // qualified name
	                            Attributes attrs )
	  throws SAXException
	  {

		 String textStyleName;
		 String parentStyleName;
		 String styleName;


	    //Hier Unterscheidung nach Elementen


		//Abgeleitete Formatvorlagen
		//Bei Formatierungsänderungen an Absätzen mit Formatvorlagen werden intern neue Absatzvorlagennamen
		//vergeben. Diese werden in style:style Elementen gespeichert. Alle Vorlagennamen einer Parentvorlage
		//werden hier in einer ArrayList gespeichert um später entscheiden zu können, von welchem Typ der
		//interne Formatname abgeleitet ist
		if( qName.equals( "style:style" )){

			parentStyleName = attrs.getValue("style:parent-style-name");
			styleName = attrs.getValue("style:name");

			//Attribute style:parent-style-name und style:name sind gesetzt:
			//Überschriebene Formatvorlage
			if( parentStyleName != null && styleName != null ){

			    //Titel
		        if( parentStyleName.equals( "qml_3a_title" ) ){
		 		    qstnTitleTags.add( styleName );
			    }

			}

		}// Element Abgeleitete Formatvorlagen

		//Textabsätze
	    if ( qName.equals("text:p")){

	    	parsingState = psNone;

	    	//Die Zuweisung der Absatzvorlagen findet im Attribut "text:style-name" statt
	    	//Wert des Attributes
	    	textStyleName = attrs.getValue("text:style-name");


			//Title
		    if( qstnTitleTags.contains( textStyleName )){
		        parsingState = psTitle;
		    }


	    }//Textabsätze


	  }// Start Element


      //Zeichen
	  public void characters( char[] buf, int offset, int len )
	  throws SAXException
	  {

		//evtl mit chars nicht mit string arbeiten,
		//da sonst zuviele Strings erzeigt werden
		String s = new String( buf, offset, len );


	    //Textknoten ist nicht leer
	    if (( s.trim() ).length() != 0 ){


            //Textknoten je nach Kontext behandeln
	        if( parsingState == psTitle ){
	            titleText.append( s );
	        }
	     }//Textknoten nicht leer

	  } //characters



      public void endElement( String namespaceURI,
           String localName,     // local name
           String qName )        // qualified name
           throws SAXException
      {
        //Textabsätze
        if ( qName.equals("text:p")){

	        if( parsingState == psTitle ){
	         textParagraphList.addParagraph(new TextParagraph(parsingState, titleText.toString()));
 			    titleText.delete(0, titleText.length());
                parsingState = psNone;
            }

        }//Textabsätze
      }//endElement()






}
