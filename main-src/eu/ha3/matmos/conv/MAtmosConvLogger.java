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

public class MAtmosConvLogger
{
	final private static String modName = "MAtmos";
	
	private static int refinedness = 1;
	
	public static void setRefinedness(int refinedLevel)
	{
		refinedness = refinedLevel;
	}
	
	public static void fine(String message)
	{
		print(message, "FINE", 0);
	}
	
	public static void info(String message)
	{
		print(message, "INFO", 1);
	}
	
	public static void warning(String message)
	{
		print(message, "WARNING", 2);
	}
	
	public static void severe(String message)
	{
		print(message, "SEVERE", 3);
	}
	
	private static void print(String message, String type, int refinedLevel)
	{
		if (refinedLevel >= refinedness)
		{
			System.out.println("(" + modName + ": " + type + ") " + message);
		}
	}
}
