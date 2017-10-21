package main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

public class Utilities {


	  public static void writeStringtoIsoFile( String fileName, String fileContent){

		  File file = new File( fileName );
	      try {
	          Writer fileOut = new BufferedWriter(new OutputStreamWriter(new FileOutputStream( file ), "ISO-8859-1"));
	          fileOut.write( fileContent ); // in die Datei schreiben
	          fileOut.flush(); // Puffer leeren
	          fileOut.close(); // Datei schliessen
	      } catch (UnsupportedEncodingException e) {
	          // sollte nicht vorkommen
	          e.printStackTrace();
	      } catch (FileNotFoundException e) {
	          // sollte nicht vorkommen
	          e.printStackTrace();
	      } catch (IOException e) {
	          // Schreibfehler
	          e.printStackTrace();
	          // weiter Fehlerbehandlung ergänzen....
	      }
	  }



	  public static void copyFile(File src, File dst){

		  try{
	      InputStream in = new FileInputStream(src);
	      OutputStream out = new FileOutputStream(dst);

	      // Transfer bytes from in to out
	      byte[] buf = new byte[1024];
	      int len;
	      while ((len = in.read(buf)) > 0) {
	          out.write(buf, 0, len);
	      }
	      in.close();
	      out.close();
		  }catch(IOException e){
			  //behandeln
			  e.printStackTrace();
		  }
	  }




}
