package model.writerparagraphs;

import java.util.Iterator;

public class TestParagraphList {

	public static void main(String[] args) {

		TextParagraphList list = new TextParagraphList();

		//add paragraphd to the list
		list.addParagraph(new TextParagraph(2, "Hallo Welt"));
		list.addParagraph(new TextParagraph(13, "Wie geht es Dir?"));
		list.addParagraph(new TextParagraph(8, "Viele Grüße"));

		//access paragraphs using iterator
		Iterator<TextParagraph> it = list.iterator();
		while(it.hasNext()){
			TextParagraph par = it.next();
			System.out.println( "style: " + par.getStyle() +  " content: " + par.getContent());
		}

		System.out.println();

		//access paragraphs using paragraphs index
		int size = list.size();
		for(int i=0; i < size; i++){
			TextParagraph par = list.get( i );
			System.out.println( "paragraphindex: " + i + " style: " + par.getStyle() +  " content: " + par.getContent());
		}

	}

}
