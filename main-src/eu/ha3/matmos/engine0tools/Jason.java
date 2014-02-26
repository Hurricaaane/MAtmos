package eu.ha3.matmos.engine0tools;

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
	
	public static String toJsonPretty(Object blob)
	{
		Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
		return gson.toJson(blob);
	}
	
	public static Blob blob()
	{
		return X.new Blob();
	}
	
	public static Blob blob(Object... oxox)
	{
		return X.new Blob().blob(oxox);
	}
	
	public static Plot plot()
	{
		return X.new Plot();
	}
	
	public static Plot plot(Object... ox)
	{
		return X.new Plot().plot(ox);
	}
	
	public static Uniq uniq()
	{
		return X.new Uniq();
	}
	
	public static Uniq uniq(Object... ox)
	{
		return X.new Uniq().uniq(ox);
	}
	
	@SuppressWarnings("serial")
	public class Blob extends LinkedHashMap<String, Object>
	{
		public Blob blob(Object... oxox)
		{
			if (oxox.length % 2 != 0)
				throw new RuntimeException("Jason Blob has an odd number of args");
			
			int i = 0;
			while (i < oxox.length)
			{
				put(oxox[i].toString(), oxox[i + 1]);
				i = i + 2;
			}
			
			return this;
		}
	}
	
	@SuppressWarnings("serial")
	public class Plot extends ArrayList<Object>
	{
		public Plot plot(Object ox)
		{
			addAll(Arrays.asList(ox));
			
			return this;
		}
	}
	
	@SuppressWarnings("serial")
	public class Uniq extends LinkedHashSet<Object>
	{
		public Uniq uniq(Object ox)
		{
			addAll(Arrays.asList(ox));
			
			return this;
		}
	}
}
