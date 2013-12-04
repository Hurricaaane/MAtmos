package eu.ha3.matmos.game.system;

import eu.ha3.mc.haddon.implem.Ha3Utility;
import eu.ha3.mc.haddon.Haddon;

/* x-placeholder */

public class Chatter
{
	private final Haddon mod;
	private final String prefix;
	
	public Chatter(Haddon mod, String prefix)
	{
		this.mod = mod;
		this.prefix = prefix;
	}
	
	public void printChat(Object... args)
	{
		printChat(new Object[] { Ha3Utility.COLOR_WHITE, this.prefix + ": " }, args);
	}
	
	public void printChatShort(Object... args)
	{
		printChat(new Object[] { Ha3Utility.COLOR_WHITE, "" }, args);
	}
	
	protected void printChat(final Object[] in, Object... args)
	{
		Object[] dest = new Object[in.length + args.length];
		System.arraycopy(in, 0, dest, 0, in.length);
		System.arraycopy(args, 0, dest, in.length, args.length);
		
		this.mod.getManager().getUtility().printChat(dest);
	}
}
