package net.minecraft.src;

import eu.ha3.mc.convenience.Ha3KeyActions;

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

public class MAtKeyMain implements Ha3KeyActions
{
	private MAtUserControl userControl;
	
	MAtKeyMain(MAtUserControl userControlIn)
	{
		this.userControl = userControlIn;
		
	}
	
	@Override
	public void doBefore()
	{
		// OK, do nothing.
		
	}
	
	private boolean isHolding;
	
	@Override
	public void doDuring(int curTime)
	{
		/*if (curTime == 1)
		{
			this.userControl.signalPress();
			
		}*/
		
		if (curTime >= 7 && !this.isHolding)
		{
			this.isHolding = true;
			
			this.userControl.beginHold();
			
		}
		
	}
	
	@Override
	public void doAfter(int curTime)
	{
		if (curTime < 6)
		{
			this.userControl.signalShortPress();
			
		} // Omit frame 7 : not anymore
		else if (this.isHolding)
		//if (curTime >= 7)
		{
			this.isHolding = false;
			
			this.userControl.endHold();
			
		}
		
	}
	
}