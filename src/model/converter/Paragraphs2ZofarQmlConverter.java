package model.converter;

import model.writerparagraphs.TextParagraph;
import model.writerparagraphs.TextParagraphList;

public class Paragraphs2ZofarQmlConverter {


	//the qml generation will be implemented here
	public static void convertToZofarQml(TextParagraphList textParagraphList){

		//access paragraphs using paragraphs index
		int size = textParagraphList.size();
		for(int i=0; i < size; i++){
			TextParagraph par = textParagraphList.get( i );
			System.out.println( "paragraph index: " + i + ", style: " + par.getStyle() +  ", content: " + par.getContent());
		}


	}

}
