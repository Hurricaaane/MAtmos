package net.minecraft.src;

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

public class MAtGuiMore extends GuiScreen
{
	/**
	 * A reference to the screen object that created this. Used for navigating
	 * between screens.
	 */
	private GuiScreen parentScreen;
	
	/** The title string that is displayed in the top-center of the screen. */
	protected String screenTitle;
	
	private MAtMod mod;
	
	/** The ID of the button that has been pressed. */
	private int buttonId;
	
	public MAtGuiMore(GuiScreen par1GuiScreen, MAtMod matmos)
	{
		this.screenTitle = "MAtmos tweaks";
		this.buttonId = -1;
		this.parentScreen = par1GuiScreen;
		this.mod = matmos;
	}
	
	/**
	 * Adds the buttons (and other controls) to the screen in question.
	 */
	@Override
	public void initGui()
	{
		
	}
	
	/**
	 * Fired when a control is clicked. This is the equivalent of
	 * ActionListener.actionPerformed(ActionEvent e).
	 */
	@Override
	protected void actionPerformed(GuiButton par1GuiButton)
	{
		if (par1GuiButton.id == 200)
		{
			
		}
		
	}
	
	private void aboutToClose()
	{
	}
	
	@Override
	public void onGuiClosed()
	{
		aboutToClose();
		
	}
	
	/**
	 * Called when the mouse is clicked.
	 */
	@Override
	protected void mouseClicked(int par1, int par2, int par3)
	{
		if (this.buttonId >= 0)
		{
		}
		else
		{
			super.mouseClicked(par1, par2, par3);
		}
	}
	
	/**
	 * Draws the screen and all the components in it.
	 */
	@Override
	public void drawScreen(int par1, int par2, float par3)
	{
		drawDefaultBackground();
		drawCenteredString(this.fontRenderer, this.screenTitle, this.width / 2, 8, 0xffffff);
		
		super.drawScreen(par1, par2, par3);
		
	}
	
}
