package eu.ha3.matmos.engine.interfaces;

import javax.xml.stream.XMLStreamException;


/*
            DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE 
                    Version 2, December 2004 

 Copyright (C) 2004 Sam Hocevar <sam@hocevar.net> 

 Everyone is permitted to copy and distribute verbatim or modified 
 copies of this license document, and changing it is allowed as long 
 as the name is changed. 

            DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE 
   TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION 

  0. You just DO WHAT THE FUCK YOU WANT TO. 
*/

public interface Data
{
	
	public abstract void flagUpdate();
	
	public abstract int getVersion();
	
	public abstract Sheet<Integer> getSheet(String name);
	
	public abstract void setSheet(String name, Sheet<Integer> sheet);
	
	public abstract String createXML() throws XMLStreamException;
	
}