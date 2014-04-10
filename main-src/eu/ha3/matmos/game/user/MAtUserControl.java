package eu.ha3.matmos.game.user;

import net.minecraft.src.GuiScreen;
import eu.ha3.mc.haddon.SupportsFrameEvents;
import eu.ha3.mc.haddon.SupportsKeyEvents;
import eu.ha3.mc.haddon.SupportsTickEvents;
import eu.ha3.mc.haddon.implem.Ha3Utility;
import eu.ha3.mc.quick.keys.KeyWatcher;
import net.minecraft.src.KeyBinding;
import net.minecraft.src.Minecraft;

import org.apache.commons.lang3.ArrayUtils;
import org.lwjgl.input.Keyboard;

import eu.ha3.easy.TimeStatistic;
import eu.ha3.matmos.game.gui.MAtGuiMenu;
import eu.ha3.matmos.game.system.MAtMod;
import eu.ha3.matmos.game.system.MAtModPhase;
import eu.ha3.mc.convenience.Ha3HoldActions;
import eu.ha3.mc.convenience.Ha3KeyHolding;
import eu.ha3.mc.convenience.Ha3KeyManager;

/* x-placeholder-wtfplv2 */

public class MAtUserControl implements Ha3HoldActions, SupportsTickEvents, SupportsFrameEvents, SupportsKeyEvents
{
	private MAtMod mod;
	
	private KeyBinding keyBindingMain;
	private final KeyWatcher watcher = new KeyWatcher(this);
	private final Ha3KeyManager keyManager = new Ha3KeyManager();
	private MAtScroller scroller;
	
	private int loadingCount;
	
	public MAtUserControl(MAtMod mAtmosHaddon)
	{
		this.mod = mAtmosHaddon;
	}
	
	public void load()
	{
		this.keyBindingMain = new KeyBinding("key.matmos", 65);
		Minecraft.getMinecraft().gameSettings.keyBindings =
				ArrayUtils.addAll(Minecraft.getMinecraft().gameSettings.keyBindings, this.keyBindingMain);
			this.watcher.add(this.keyBindingMain);
			this.keyBindingMain.keyCode = this.mod.getConfig().getInteger("key.code");
			KeyBinding.resetKeyBindingArrayAndHash();
			
		this.scroller = new MAtScroller(this.mod);
		
		this.keyManager.addKeyBinding(this.keyBindingMain, new Ha3KeyHolding(this, 7));
	}
	
	public String getKeyBindingMainFriendlyName()
	{
		if (this.keyBindingMain == null)
			return "undefined";
		
		return Keyboard.getKeyName(this.keyBindingMain.keyCode);
	}
	@Override
	public void onKey(KeyBinding event)
	{
		this.keyManager.handleKeyDown(event);
	}
	
	@Override
	public void onTick()
	{
		this.watcher.onTick();
		this.keyManager.handleRuntime();
		
		this.scroller.routine();
		if (this.scroller.isRunning())
		{
			this.mod.getGlobalVolumeControl().setVolume(this.scroller.getValue());
		}
	}
	
	@Override
	public void onFrame(float fspan)
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
				this.mod.getChatter().printChat(Ha3Utility.COLOR_GOLD, "MAtmos is not loaded.");
			}
			else if (phase == MAtModPhase.NOT_INITIALIZED)
			{
				this.mod.getChatter().printChat(
					Ha3Utility.COLOR_GOLD, "MAtmos will not load due to a fatal error. ", Ha3Utility.COLOR_GRAY,
					"(Some MAtmos modules are not initialized)");
			}
		}
		else
		{
			if (Minecraft.getMinecraft().gameSettings.soundVolume <= 0)
			{
				this.mod.getChatter().printChat(
					Ha3Utility.COLOR_RED, "Warning: ", Ha3Utility.COLOR_WHITE,
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
				Ha3Utility.COLOR_YELLOW, "Stopped. Press ", Ha3Utility.COLOR_WHITE, getKeyBindingMainFriendlyName(),
				Ha3Utility.COLOR_YELLOW, " to re-enable.");
			
		}
		else if (this.mod.isReady())
		{
			if (this.loadingCount != 0)
			{
				this.mod.getChatter().printChat(Ha3Utility.COLOR_BRIGHTGREEN, "Loading...");
			}
			else
			{
				this.mod.getChatter().printChat(
					Ha3Utility.COLOR_BRIGHTGREEN, "Loading...", Ha3Utility.COLOR_YELLOW, " (Hold ",
					Ha3Utility.COLOR_WHITE, getKeyBindingMainFriendlyName() + " down", Ha3Utility.COLOR_YELLOW,
					" to tweak the volume)");
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
			Ha3Utility.COLOR_BRIGHTGREEN, "Loading for the first time (" + stat.getSecondsAsString(2) + "s)");
	}
	
	private void whenWantsForcing()
	{
		if (!this.mod.isRunning() && this.mod.isReady())
		{
			TimeStatistic stat = new TimeStatistic();
			this.mod.reloadAndStart();
			this.mod.getChatter().printChat(
				Ha3Utility.COLOR_BRIGHTGREEN, "Reloading expansions (" + stat.getSecondsAsString(2) + "s)");
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
