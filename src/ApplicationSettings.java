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


/*
 * Erlaubt das komfortable Speichern und Laden von Systemeinstellungen
 * Die Systemeinstellungen werden als String key - String value Paare in
 * einer Map gespeichert, im Konstruktor ApplicationSettings(fileName) geladen
 * und durch die saveToFile() gespeichert
 *
 */


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map.Entry;


public class ApplicationSettings {

	//evtl. File verwenden
	private String fileName;
	private HashMap<String, String> map = new HashMap<String, String> ();


	//Konstruktor
	public ApplicationSettings(String fileName){
		setFileName( fileName );
	}


	//Getter und Setter
	public void setFileName( String fileName ){
		//Hier Testen: Datei muss existieren und schreibbar sein
		this.fileName = fileName;

		//Einlesen der Datei
		map.clear();
		BufferedReader f;
		String line;
		int pos;

		try{
			f = new BufferedReader( new FileReader( fileName ));

			while(  (line = f.readLine()) != null ){

				//System.out.println( line );
				pos = line.indexOf("=");
				if(  pos != -1){
					String key = line.substring( 0, pos );
					String value = line.substring(pos + 1, line.length());
					map.put( key, value );
				}


			}


		}catch (IOException e){
			//System.out.println("Fehler beim Lesen der datei");
		}





	}

	public String getFileName(){
		return fileName;
	}

	//Einfügen oder Überschreiben eines neuen Wertes
	public void setValue(String key, String value ){
		map.put(key, value);

	}

	//Lesen eines Wertes
	public String getValue( String key){
		return map.get( key );
	}

	//Werte speichern
	public void saveToFile(){

		PrintWriter f;
		try{

	        f = new PrintWriter( new BufferedWriter(
  		      new FileWriter( new File( fileName ) )));

			f.println("#configuration settings");

			//Schlüssel Wert Paare schreiben
			Iterator it = map.entrySet().iterator();
			while( it.hasNext() ){
				Entry entry = (Entry) it.next();
				f.println( (String) entry.getKey() + "=" + (String) entry.getValue());

			}

			f.close();



		}catch (IOException e){
		    //System.out.println("Fehler beim schreiben der Datei");
			//itgenwie behandeln?
		}
	} //saveToFile

}
