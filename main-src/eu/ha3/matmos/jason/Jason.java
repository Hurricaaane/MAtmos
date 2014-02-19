package eu.ha3.matmos.jason;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/*
--filenotes-placeholder
*/

public class Jason
{
	private static Jason X = new Jason();
	
	public static String toJson(Object blob)
	{
		Gson gson = new GsonBuilder().create();
		return gson.toJson(blob);
	}
	
	public static Blob blob()
	{
		return X.new Blob();
	}
	
	public static Blob blob(Object... oxox)
	{
		Blob blob = X.new Blob();
		
		if (oxox.length % 2 != 0)
			throw new RuntimeException("Jason Blob has an odd number of args");
		
		int i = 0;
		while (i < oxox.length)
		{
			blob.put(oxox[i].toString(), oxox[i + 1]);
			i = i + 2;
		}
		
		return blob;
	}
	
	public static Plot plot()
	{
		return X.new Plot();
	}
	
	public static Plot plot(Object... ox)
	{
		Plot plot = X.new Plot();
		plot.addAll(Arrays.asList(ox));
		return plot;
	}
	
	public static Uniq uniq()
	{
		return X.new Uniq();
	}
	
	public static Uniq uniq(Object... ox)
	{
		Uniq uniq = X.new Uniq();
		uniq.addAll(Arrays.asList(ox));
		return uniq;
	}
	
	@SuppressWarnings("serial")
	public class Blob extends LinkedHashMap<String, Object>
	{
	}
	
	@SuppressWarnings("serial")
	public class Plot extends ArrayList<Object>
	{
	}
	
	@SuppressWarnings("serial")
	public class Uniq extends LinkedHashSet<Object>
	{
	}
}
