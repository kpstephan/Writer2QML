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


package model.converter;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import model.writerparagraphs.TextParagraphList;

public class ODFParagraphsExtractor {


	public static TextParagraphList extractParagraphsFromODF(String fileName)
			throws IOException, ParserConfigurationException,  SAXException {

		
		TextParagraphList textParagraphList = new TextParagraphList();

		ZipFile zf;
		//try {

			zf = new ZipFile( fileName );

			//Titel aus style.xml lesen
			//style.xml aus Archiv öffnen
			ZipEntry styleEntry = zf.getEntry("styles.xml");
			if( styleEntry == null){ /*TODO handle this */}
			InputStream styleIs = zf.getInputStream( styleEntry );


			//styles.xml parsen
			SaxHandlerODFDocumentHead styleParsingHandler = new SaxHandlerODFDocumentHead( textParagraphList );
			SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
			saxParser.parse( styleIs, styleParsingHandler );

			//Inhalt aus content.xml
			ZipEntry entry = zf.getEntry("content.xml");
			InputStream is = zf.getInputStream( entry );


		    //w2qOoDataReader handler =  new w2qOoDataReader( fileName );
			SaxHandlerODFDocumentBody handler =  new SaxHandlerODFDocumentBody( textParagraphList );
			saxParser = SAXParserFactory.newInstance().newSAXParser();
			saxParser.parse( is, handler );

			//TODO move, has to be executed in every case
			zf.close();

		 //} catch( Throwable t ) {

			 //TODO HandleIOException, ParserConfigurationException, SAXException !
			// t.printStackTrace();
		 //}

		return textParagraphList;
	}
}