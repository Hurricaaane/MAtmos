package eu.ha3.matmos.tools;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/*
--filenotes-placeholder
*/

public class Jason
{
	public static String toJson(Object blob)
	{
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		return gson.toJson(blob);
	}
	
	public static String toJsonPretty(Object blob)
	{
		Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
		return gson.toJson(blob);
	}
}
