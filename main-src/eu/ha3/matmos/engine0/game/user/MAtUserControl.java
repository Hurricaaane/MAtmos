package eu.ha3.matmos.engine0.game.user;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.KeyBinding;

import org.lwjgl.input.Keyboard;

import eu.ha3.easy.TimeStatistic;
import eu.ha3.matmos.engine0.game.gui.MAtGuiMenu;
import eu.ha3.matmos.engine0.game.system.MAtMod;
import eu.ha3.matmos.engine0.game.system.MAtModPhase;
import eu.ha3.matmos.v170helper.Version170Helper;
import eu.ha3.mc.convenience.Ha3HoldActions;
import eu.ha3.mc.convenience.Ha3KeyHolding;
import eu.ha3.mc.convenience.Ha3KeyManager;
import eu.ha3.mc.quick.chat.ChatColorsSimple;

/* x-placeholder */

public class MAtUserControl implements Ha3HoldActions
{
	private MAtMod mod;
	
	private KeyBinding keyBindingMain;
	private Ha3KeyManager keyManager;
	private MAtScroller scroller;
	
	private int loadingCount;
	
	public MAtUserControl(MAtMod mAtmosHaddon)
	{
		this.mod = mAtmosHaddon;
	}
	
	public void load()
	{
		// new KeyBinding registers it rightaway to the list of keys
		this.keyBindingMain = new KeyBinding("MAtmos", 65, "key.categories.misc");
		this.keyManager = new Ha3KeyManager();
		
		this.scroller = new MAtScroller(this.mod);
		
		this.keyManager.addKeyBinding(this.keyBindingMain, new Ha3KeyHolding(this, 7));
	}
	
	public String getKeyBindingMainFriendlyName()
	{
		if (this.keyBindingMain == null)
			return "undefined";
		
		return Keyboard.getKeyName(this.keyBindingMain.getKeyCode()); // OBF getKeyCode(), or .keyCode
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
	
	public void printUnusualMessages()
	{
		if (!this.mod.isReady())
		{
			MAtModPhase phase = this.mod.getPhase();
			if (!this.mod.isFatalError())
			{
				this.mod.getChatter().printChat(ChatColorsSimple.COLOR_GOLD, "MAtmos is not loaded.");
			}
			else if (phase == MAtModPhase.NOT_INITIALIZED)
			{
				this.mod.getChatter().printChat(
					ChatColorsSimple.COLOR_GOLD, "MAtmos will not load due to a fatal error. ",
					ChatColorsSimple.COLOR_GRAY, "(Some MAtmos modules are not initialized)");
			}
		}
		else
		{
			if (Version170Helper.getSoundVolume() <= 0f)
			{
				this.mod.getChatter().printChat(
					ChatColorsSimple.COLOR_RED, "Warning: ", ChatColorsSimple.COLOR_WHITE,
					"Sounds are turned off in your game settings!");
			}
		}
		
	}
	
	@Override
	public void beginHold()
	{
		if (this.mod.getConfig().getBoolean("reversed.controls"))
		{
			displayMenu();
		}
		else if (this.mod.isRunning())
		{
			this.scroller.start();
		}
		
	}
	
	@Override
	public void shortPress()
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
	
	@Override
	public void endHold()
	{
		if (this.scroller.isRunning())
		{
			this.scroller.stop();
			this.mod.getConfig().setProperty("globalvolume.scale", this.mod.getGlobalVolumeControl().getVolume());
			this.mod.saveConfig();
		}
		
		whenWantsForcing();
		printUnusualMessages();
		
	}
	
	private void whenWantsToggle()
	{
		if (this.mod.isRunning())
		{
			this.mod.stopRunning();
			this.mod.getChatter().printChat(
				ChatColorsSimple.COLOR_YELLOW, "Stopped. Press ", ChatColorsSimple.COLOR_WHITE,
				getKeyBindingMainFriendlyName(), ChatColorsSimple.COLOR_YELLOW, " to re-enable.");
			
		}
		else if (this.mod.isReady())
		{
			if (this.loadingCount != 0)
			{
				this.mod.getChatter().printChat(ChatColorsSimple.COLOR_BRIGHTGREEN, "Loading...");
			}
			else
			{
				this.mod.getChatter().printChat(
					ChatColorsSimple.COLOR_BRIGHTGREEN, "Loading...", ChatColorsSimple.COLOR_YELLOW, " (Hold ",
					ChatColorsSimple.COLOR_WHITE, getKeyBindingMainFriendlyName() + " down",
					ChatColorsSimple.COLOR_YELLOW, " to tweak the volume)");
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
		this.mod.getChatter().printChat(
			ChatColorsSimple.COLOR_BRIGHTGREEN, "Loading for the first time (" + stat.getSecondsAsString(2) + "s)");
	}
	
	private void whenWantsForcing()
	{
		if (!this.mod.isRunning() && this.mod.isReady())
		{
			TimeStatistic stat = new TimeStatistic();
			this.mod.reloadAndStart();
			this.mod.getChatter().printChat(
				ChatColorsSimple.COLOR_BRIGHTGREEN, "Reloading expansions (" + stat.getSecondsAsString(2) + "s)");
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
			// OBF displayGuiScreen
			Minecraft.getMinecraft().displayGuiScreen(
				new MAtGuiMenu((GuiScreen) this.mod.util().getCurrentScreen(), this.mod));
		}
	}
	
	@Override
	public void beginPress()
	{
	}
	
	@Override
	public void endPress()
	{
	}
	
}
