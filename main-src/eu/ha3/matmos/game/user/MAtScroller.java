package eu.ha3.matmos.game.user;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.FontRenderer;
import net.minecraft.src.Minecraft;
import net.minecraft.src.ScaledResolution;
import eu.ha3.matmos.game.system.MAtMod;
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
	final private String MESSAGE_TITLE = "MAtmos Volume";
	final private String MESSAGE_HINT = "<Look up/down>";
	final private String MESSAGE_MORE = "+";
	final private String MESSAGE_LESS = "-";
	
	private MAtMod mod;
	private float prevPitch;
	
	private boolean knowsHowToUse;
	private float doneValue;
	
	public MAtScroller(MAtMod mod)
	{
		super(Minecraft.getMinecraft());
		this.mod = mod;
		
		this.knowsHowToUse = false;
		
	}
	
	@Override
	protected void doDraw(float fspan)
	{
		Minecraft mc = Minecraft.getMinecraft();
		FontRenderer fontRenderer = mc.fontRenderer;
		
		final String msgper = (int) Math.floor(this.doneValue * 100) + "%";
		
		ScaledResolution screenRes = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
		
		int scrWidth = screenRes.getScaledWidth();
		int scrHeight = screenRes.getScaledHeight();
		
		int uwidth = getWidthOf("_");
		int uposx = (scrWidth - uwidth) / 2 + getWidthOf(this.MESSAGE_TITLE) / 2;
		
		fontRenderer.drawStringWithShadow(this.MESSAGE_TITLE, uposx + uwidth * 2, scrHeight / 2, 0xffffff);
		
		fontRenderer.drawStringWithShadow(msgper, uposx + uwidth * 2, scrHeight / 2 + 10, 255 << 16
			| (int) (200 + 55 * (this.doneValue < 1 ? 1 : 2 - this.doneValue)) << 8);
		
		if (!this.knowsHowToUse)
		{
			float glocount = this.mod.util().getClientTick() + fspan;
			int blink = (int) (200 + 55 * (Math.sin(glocount * Math.PI * 0.07) + 1) / 2F);
			fontRenderer.drawStringWithShadow(
				this.MESSAGE_HINT, uposx + uwidth * 2, scrHeight / 2 + 10 * 2, blink << 16 | blink << 8 | blink);
			
			if (Math.abs(getInitialPitch() - getPitch()) > 60)
			{
				this.knowsHowToUse = true;
				
			}
			
		}
		
		fontRenderer.drawStringWithShadow(
			this.MESSAGE_MORE, uposx + uwidth * 2, scrHeight / 2 - scrHeight / 6 + 3, 0xffff00);
		
		fontRenderer.drawStringWithShadow(
			this.MESSAGE_LESS, uposx + uwidth * 2, scrHeight / 2 + scrHeight / 6 + 3, 0xffff00);
		
		final int ucount = 8;
		final float speedytude = 20;
		for (int i = 0; i < ucount; i++)
		{
			float perx = ((getPitch() + 90F) % speedytude / speedytude + i) / ucount;
			double pirx = Math.cos(Math.PI * perx);
			
			fontRenderer.drawStringWithShadow(
				"_", uposx, scrHeight / 2 + (int) Math.floor(pirx * scrHeight / 6), 0xffff00);
			
		}
		
	}
	
	private int getWidthOf(String s)
	{
		return Minecraft.getMinecraft().fontRenderer.getStringWidth(s);
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
			
			EntityPlayer ply = Minecraft.getMinecraft().thePlayer;
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
