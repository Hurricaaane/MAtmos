package eu.ha3.matmos.engine0.conv;

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
	
	public final static int SEVERE = 3;
	public final static int WARNING = 2;
	public final static int INFO = 1;
	public final static int FINE = 0;
	
	private static int refinedness = 1;
	
	public static void setRefinedness(int refinedLevel)
	{
		refinedness = refinedLevel;
	}
	
	public static void fine(String message)
	{
		print(message, "FINE", FINE);
	}
	
	public static void info(String message)
	{
		print(message, "INFO", INFO);
	}
	
	public static void warning(String message)
	{
		print(message, "WARNING", WARNING);
	}
	
	public static void severe(String message)
	{
		print(message, "SEVERE", SEVERE);
	}
	
	private static void print(String message, String type, int refinedLevel)
	{
		if (refinedLevel >= refinedness)
		{
			System.out.println("(" + modName + ": " + type + ") " + message);
		}
	}
}
