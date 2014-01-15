package eu.ha3.matmos.engine0.core.implem;

/*
--filenotes-placeholder
*/

public class LongFloatSimplificator
{
	public static Long longOf(Object o)
	{
		if (o == null)
			return null;
		
		if (o instanceof Long)
			return (Long) o;
		
		if (o instanceof String)
		{
			try
			{
				return Long.parseLong((String) o);
			}
			catch (NumberFormatException e)
			{
				return null;
			}
		}
		
		return null;
	}
	
	public static Float floatOf(Object o)
	{
		if (o == null)
			return null;
		
		if (o instanceof Float)
			return (Float) o;
		
		if (o instanceof String)
		{
			try
			{
				return Float.parseFloat((String) o);
			}
			catch (NumberFormatException e)
			{
				return null;
			}
		}
		
		return null;
	}
}
