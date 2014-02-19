package eu.ha3.matmos.engine0.debug;

import java.util.HashSet;
import java.util.Set;

import eu.ha3.mc.quick.chat.ChatColorsSimple;
import eu.ha3.mc.quick.chat.Chatter;

/*
--filenotes-placeholder
*/

public class IDontKnowHowToCode
{
	private static Set<Integer> tokens = new HashSet<Integer>();
	
	public static void whoops__printExceptionToChat(Chatter chatter, Exception e, Object caller)
	{
		whoops__printExceptionToChat(chatter, e, caller.getClass().getName().hashCode());
	}
	
	/**
	 * Call this to print an error to the player's chat. The crash token is
	 * meant to prevent the exceptions from a single source to print multiple
	 * times.
	 * 
	 * @param chatter
	 * @param e
	 * @param crashToken
	 */
	public static void whoops__printExceptionToChat(Chatter chatter, Exception e, int crashToken)
	{
		if (tokens.contains(crashToken))
			return;
		tokens.add(crashToken);
		
		chatter.printChat(ChatColorsSimple.COLOR_RED, "MAtmos is crashing: ", ChatColorsSimple.COLOR_WHITE, e
			.getClass().getName(), ": ", e.getCause());
		
		int i = 0;
		for (StackTraceElement x : e.getStackTrace())
		{
			if (i <= 5 || x.toString().contains("MAt") || x.toString().contains("eu.ha3.matmos."))
			{
				chatter.printChat(ChatColorsSimple.COLOR_WHITE, x.toString());
			}
			i++;
		}
		
		chatter.printChat(ChatColorsSimple.COLOR_RED, "Please report this issue :(");
	}
}
