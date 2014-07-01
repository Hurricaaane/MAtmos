package eu.ha3.matmos.log;

/* x-placeholder */

public class MAtLog
{
	private static final String modName = "MAtmos";
	
	public static final int SEVERE = 3;
	public static final int WARNING = 2;
	public static final int INFO = 1;
	public static final int FINE = 0;
	
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
