package model.converter.zofar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.xmlbeans.XmlObject;

import model.writerparagraphs.TextParagraph;

public abstract class AbstractMap {
	
	protected List<TextParagraph> headerElement = new ArrayList<TextParagraph>();
	protected List<Object> childElement = new ArrayList<Object>();
	private static final Set<Integer> VALIDHEADERSTYLES = new HashSet<Integer>(Arrays.asList(1, 5, 7,8));

	
	// This method do a Null check and some validation before adding an headerElements to the List.
	public void addHeaderElement(final TextParagraph element)throws ZofarMapException{
		if(element == null) {
			throw new ZofarMapException("headerElement is null");
		}
		if(!VALIDHEADERSTYLES.contains(element.getStyle())){
			throw new ZofarMapException("not a valid headerElement");
		}
		headerElement.add(element);
	}
	
	// This method do a Null check before removing an headerElements from the List to 
	// prevent a NullPointerException.
	public void removeHeaderElement(final TextParagraph element)throws ZofarMapException {
		if(element == null) {
			throw new ZofarMapException("headerElements is null");
		}
		headerElement.remove(element);
	}
	
		
	
	// This method do a Null check and some validation before adding an childElement to the List.
	protected void addchildElement(final Object element)throws ZofarMapException{
//		if(element == null) {
//			throw new ZofarMapException("childElement is null");
//		}
		childElement.add(element);
	}
		
	// This method do a Null check before removing an childElement from the List to 
	// prevent a NullPointerException.
	public void removechildElement(final Object element)throws ZofarMapException {
		if(element == null) {
			throw new ZofarMapException("childElement is null");
		}
		childElement.remove(element);
	}
	
	public abstract XmlObject convert();
	
	
	protected Type type;
	
	public enum Type{
		SC, MC, OPEN, MSC, LIKS, SECTION, PAGE, DOCUMENT, UNKNOWN
	}
	
	abstract void setType(Type type) throws ZofarMapException;
	
	public Type getType() {
		return this.type;
	}
}


