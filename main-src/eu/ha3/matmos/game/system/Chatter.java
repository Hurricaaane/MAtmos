package eu.ha3.matmos.game.system;

import eu.ha3.mc.haddon.implem.Ha3Utility;
import eu.ha3.mc.haddon.Haddon;

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
