package eu.ha3.matmos.conv;

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

public class AnyLogger
{
	final private static String modName = "MAtmos";
	
	public static void fine(String message)
	{
		//print(message, "FINE");
	}
	
	public static void info(String message)
	{
		print(message, "INFO");
	}
	
	public static void warning(String message)
	{
		print(message, "WARNING");
	}
	
	public static void severe(String message)
	{
		print(message, "SEVERE");
	}
	
	private static void print(String message, String type)
	{
		System.out.println("(" + modName + ": " + type + ") " + message);
	}
}
