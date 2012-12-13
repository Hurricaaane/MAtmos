package net.minecraft.src;

import net.minecraft.client.Minecraft;
import eu.ha3.mc.convenience.Ha3Scroller;

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

public class MAtScroller extends Ha3Scroller
{
	final String msgd = "MAtmos Volume";
	final String msgup = "+";
	final String msgdown = "-";
	
	private MAtMod mod;
	private float prevPitch;
	
	private boolean knowsHowToUse;
	private float doneValue;
	
	public MAtScroller(MAtMod mod2)
	{
		super(mod2.manager().getMinecraft());
		this.mod = mod2;
		
		this.knowsHowToUse = false;
		
	}
	
	@Override
	protected void doDraw(float fspan)
	{
		String msgper;
		
		Minecraft mc = getMinecraft();
		
		msgper = (int) Math.floor(this.doneValue * 100) + "%";
		
		ScaledResolution screenRes = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
		
		int scrWidth = screenRes.getScaledWidth();
		int scrHeight = screenRes.getScaledHeight();
		
		int uwidth = width("_");
		int uposx = (scrWidth - uwidth) / 2 + width(this.msgd) / 2;
		
		mc.fontRenderer.drawStringWithShadow(this.msgd, uposx + uwidth * 2, scrHeight / 2, 0xffffff);
		
		mc.fontRenderer.drawStringWithShadow(msgper, uposx + uwidth * 2, scrHeight / 2 + 10, 255 << 16
			| (int) (200 + 55 * (this.doneValue < 1 ? 1 : 2 - this.doneValue)) << 8);
		
		if (!this.knowsHowToUse)
		{
			float glocount = this.mod.util().getClientTick() + fspan;
			int blink = (int) (200 + 55 * (Math.sin(glocount * Math.PI * 0.07) + 1) / 2F);
			mc.fontRenderer.drawStringWithShadow(
				"<Look up/down>", uposx + uwidth * 2, scrHeight / 2 + 10 * 2, blink << 16 | blink << 8 | blink);
			
			if (Math.abs(getInitialPitch() - getPitch()) > 60)
			{
				this.knowsHowToUse = true;
				
			}
			
		}
		
		mc.fontRenderer.drawStringWithShadow(
			this.msgup, uposx + uwidth * 2, scrHeight / 2 - scrHeight / 6 + 3, 0xffff00);
		
		mc.fontRenderer.drawStringWithShadow(
			this.msgdown, uposx + uwidth * 2, scrHeight / 2 + scrHeight / 6 + 3, 0xffff00);
		
		final int ucount = 8;
		final float speedytude = 20;
		for (int i = 0; i < ucount; i++)
		{
			float perx = ((getPitch() + 90F) % speedytude / speedytude + i) / ucount;
			double pirx = Math.cos(Math.PI * perx);
			
			mc.fontRenderer.drawStringWithShadow(
				"_", uposx, scrHeight / 2 + (int) Math.floor(pirx * scrHeight / 6), 0xffff00);
			
		}
		
	}
	
	private int width(String s)
	{
		return this.mod.manager().getMinecraft().fontRenderer.getStringWidth(s);
		
	}
	
	public float getValue()
	{
		return this.doneValue;
		
	}
	
	@Override
	protected void doRoutineBefore()
	{
		final int caps = 10;
		if (this.mod.getConfig().getBoolean("sound.autopreview")
			&& Math.floor((this.prevPitch + 90F) / caps) != Math.floor((getPitch() + 90F) / caps))
		{
			// Calculate volume from 0f to 2f
			float hgn = (-getPitch() + 90F) / 90F;
			
			// Calculate pitch
			float res = (float) Math.pow(2, -Math.floor(getPitch() / caps) / 12);
			
			EntityPlayer ply = this.mod.manager().getMinecraft().thePlayer;
			float posX = (float) ply.posX;
			float posY = (float) ply.posY;
			float posZ = (float) ply.posZ;
			
			this.mod.getSoundCommunicator().playSoundViaManager("random.click", posX, posY, posZ, hgn, res);
			
		}
		
		this.doneValue = -getPitch() / 90F + 1F;
		if (Math.abs(getPitch()) < 3)
		{
			this.doneValue = 1F;
			
		}
		if (Math.abs(this.doneValue - 0.2F) < 0.05F)
		{
			this.doneValue = 0.2F;
			
		}
		
		this.prevPitch = getPitch();
		
	}
	
	@Override
	protected void doRoutineAfter()
	{
		
	}
	
	@Override
	protected void doStart()
	{
		this.prevPitch = getPitch();
		
	}
	
	@Override
	protected void doStop()
	{
		
	}
	
}
