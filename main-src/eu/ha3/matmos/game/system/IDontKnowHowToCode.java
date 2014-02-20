package eu.ha3.matmos.game.system;

import java.util.HashSet;
import java.util.Set;

import eu.ha3.matmos.expansions.MAtmosConvLogger;
import eu.ha3.mc.quick.chat.ChatColorsSimple;
import eu.ha3.mc.quick.chat.Chatter;

/*
--filenotes-placeholder
*/

public class IDontKnowHowToCode
{
	private static Set<Integer> crash = new HashSet<Integer>();
	private static Set<Integer> warning = new HashSet<Integer>();
	
	public static void warnOnce(String message)
	{
		if (warning.contains(message.hashCode()))
			return;
		warning.add(message.hashCode());
		
		MAtmosConvLogger.warning(message);
	}
	
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
		if (crash.contains(crashToken))
			return;
		crash.add(crashToken);
		
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
