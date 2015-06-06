package eu.ha3.matmos.game.system;

import eu.ha3.matmos.log.MAtLog;
import eu.ha3.mc.quick.chat.Chatter;
import net.minecraft.util.EnumChatFormatting;

import java.util.HashSet;
import java.util.Set;

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
		
		MAtLog.warning(message);
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
		
		chatter.printChat(EnumChatFormatting.RED, "MAtmos is crashing: ", EnumChatFormatting.WHITE, e.getClass().getName(), ": ", e.getCause());
		
		int i = 0;
		for (StackTraceElement x : e.getStackTrace())
		{
			if (i <= 5 || x.toString().contains("MAt") || x.toString().contains("eu.ha3.matmos."))
			{
				chatter.printChat(EnumChatFormatting.WHITE, x.toString());
			}
			i++;
		}
		
		chatter.printChat(EnumChatFormatting.RED, "Please report this issue :(");
	}
}
