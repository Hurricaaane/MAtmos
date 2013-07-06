package net.minecraft.src;

import org.lwjgl.input.Keyboard;

import eu.ha3.easy.TimeStatistic;
import eu.ha3.mc.convenience.Ha3KeyManager;

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

public class MAtUserControl
{
	private MAtMod mod;
	
	private KeyBinding keyBindingMain;
	private Ha3KeyManager keyManager;
	private MAtScroller scroller;
	
	public MAtUserControl(MAtMod mAtmosHaddon)
	{
		this.mod = mAtmosHaddon;
		
	}
	
	public void load()
	{
		this.keyBindingMain = new KeyBinding("key.matmos", 65);
		this.keyManager = new Ha3KeyManager();
		
		this.scroller = new MAtScroller(this.mod);
		
		this.mod.manager().addKeyBinding(this.keyBindingMain, "MAtmos");
		this.keyManager.addKeyBinding(this.keyBindingMain, new MAtKeyMain(this));
		
	}
	
	public String getKeyBindingMainFriendlyName()
	{
		if (this.keyBindingMain == null)
			return "undefined";
		
		return Keyboard.getKeyName(this.keyBindingMain.keyCode);
	}
	
	public void tickRoutine()
	{
		this.keyManager.handleRuntime();
		
		this.scroller.routine();
		if (this.scroller.isRunning())
		{
			this.mod.getGlobalVolumeControl().setVolume(this.scroller.getValue());
			
		}
		
	}
	
	public void frameRoutine(float fspan)
	{
		this.scroller.draw(fspan);
		
	}
	
	public void communicateKeyBindingEvent(KeyBinding event)
	{
		this.keyManager.handleKeyDown(event);
		
	}
	
	public void signalPress()
	{
	}
	
	public void printUnusualMessages()
	{
		if (!this.mod.isReady())
		{
			MAtModPhase phase = this.mod.getPhase();
			if (!this.mod.isFatalError())
			{
				this.mod.printChat(Ha3Utility.COLOR_GOLD, "MAtmos is not loaded.");
			}
			else if (phase == MAtModPhase.NOT_INITIALIZED)
			{
				this.mod.printChat(
					Ha3Utility.COLOR_GOLD, "MAtmos will not load due to a fatal error. ", Ha3Utility.COLOR_GRAY,
					"(Some MAtmos modules are not initialized)");
			}
		}
		else
		{
			if (this.mod.manager().getMinecraft().gameSettings.soundVolume <= 0)
			{
				this.mod.printChat(
					Ha3Utility.COLOR_RED, "Warning: ", Ha3Utility.COLOR_WHITE,
					"Sounds are turned off in your game settings!");
				
			}
		}
		
	}
	
	private int loadingCount;
	
	public void beginHold()
	{
		if (this.mod.getConfig().getBoolean("reversed.controls"))
		{
			displayMenu();
		}
		else
		{
			if (this.mod.isRunning())
			{
				this.scroller.start();
			}
			else
			{
				//whenWantsToggle();
			}
		}
		
	}
	
	public void signalShortPress()
	{
		if (this.mod.getConfig().getBoolean("reversed.controls"))
		{
			whenWantsToggle();
		}
		else
		{
			if (!this.mod.isRunning())
			{
				whenWantsToggle();
			}
			else
			{
				displayMenu();
			}
		}
		
		printUnusualMessages();
		
	}
	
	public void endHold()
	{
		if (this.scroller.isRunning())
		{
			this.scroller.stop();
			this.mod.getConfig().setProperty("globalvolume.scale", this.mod.getGlobalVolumeControl().getVolume());
			this.mod.saveConfig();
		}
		
		//if (this.mod.getConfig().getBoolean("reversed.controls"))
		//{
		whenWantsForcing();
		//}
		
		printUnusualMessages();
		
	}
	
	private void whenWantsToggle()
	{
		if (this.mod.isRunning())
		{
			this.mod.stopRunning();
			this.mod.printChat(
				Ha3Utility.COLOR_YELLOW, "Stopped. Press ", Ha3Utility.COLOR_WHITE, getKeyBindingMainFriendlyName(),
				Ha3Utility.COLOR_YELLOW, " to re-enable.");
			
		}
		else if (this.mod.isReady())
		{
			if (this.loadingCount != 0)
			{
				this.mod.printChat(Ha3Utility.COLOR_BRIGHTGREEN, "Loading...");
			}
			else
			{
				if (this.mod.getConfig().getBoolean("reversed.controls"))
				{
					this.mod.printChat(
						Ha3Utility.COLOR_BRIGHTGREEN, "Loading...", Ha3Utility.COLOR_YELLOW, " (Hold ",
						Ha3Utility.COLOR_WHITE, getKeyBindingMainFriendlyName() + " down", Ha3Utility.COLOR_YELLOW,
						" to tweak the volume)");
				}
				else
				{
					this.mod.printChat(
						Ha3Utility.COLOR_BRIGHTGREEN, "Loading...", Ha3Utility.COLOR_YELLOW, " (Press ",
						Ha3Utility.COLOR_WHITE, getKeyBindingMainFriendlyName() + "", Ha3Utility.COLOR_YELLOW,
						" to tweak the volume)");
				}
			}
			
			this.loadingCount++;
			this.mod.startRunning();
			
		}
		else if (this.mod.getPhase() == MAtModPhase.NOT_YET_ENABLED)
		{
			whenUninitializedAction();
		}
		
	}
	
	private void whenUninitializedAction()
	{
		if (this.mod.getPhase() != MAtModPhase.NOT_YET_ENABLED)
			return;
		
		TimeStatistic stat = new TimeStatistic();
		this.mod.initializeAndEnable();
		this.mod.printChat(Ha3Utility.COLOR_BRIGHTGREEN, "Loading for the first time ("
			+ stat.getSecondsAsString(2) + "s)");
		
		if (this.mod.manager().getMinecraft().gameSettings.soundVolume <= 0)
		{
			this.mod.printChat(
				Ha3Utility.COLOR_RED, "Warning: ", Ha3Utility.COLOR_WHITE,
				"Sounds are turned off in your game settings!");
			
		}
		
	}
	
	private void whenWantsForcing()
	{
		if (!this.mod.isRunning() && this.mod.isReady())
		{
			TimeStatistic stat = new TimeStatistic();
			this.mod.reloadAndStart();
			this.mod.printChat(Ha3Utility.COLOR_BRIGHTGREEN, "Reloading expansions ("
				+ stat.getSecondsAsString(2) + "s)");
		}
		else if (this.mod.getPhase() == MAtModPhase.NOT_YET_ENABLED)
		{
			whenUninitializedAction();
		}
	}
	
	private void displayMenu()
	{
		if (this.mod.isRunning() && this.mod.util().isCurrentScreen(null))
		{
			this.mod
				.manager().getMinecraft()
				.displayGuiScreen(new MAtGuiMenu((GuiScreen) this.mod.util().getCurrentScreen(), this.mod));
		}
		
	}
	
}
