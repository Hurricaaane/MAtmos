package eu.ha3.matmos.game.debug;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import eu.ha3.matmos.engine.core.implem.ProviderCollection;
import eu.ha3.matmos.engine.core.interfaces.Data;
import eu.ha3.matmos.expansions.Expansion;
import eu.ha3.matmos.expansions.debugunit.FolderResourcePackEditableEDU;
import eu.ha3.matmos.game.system.MAtMod;
import eu.ha3.matmos.pluggable.PluggableIntoMinecraft;
import eu.ha3.matmos.pluggable.UnpluggedListener;
import eu.ha3.mc.quick.chat.ChatColorsSimple;

/*
--filenotes-placeholder
*/

public class PluggableIntoMAtmos implements PluggableIntoMinecraft
{
	private MAtMod mod;
	private Expansion expansion;
	private List<UnpluggedListener> unpluggedListeners = new ArrayList<UnpluggedListener>();
	
	private boolean isReadOnly;
	private boolean isUnplugged;
	
	private File file;
	private File workingDirectory;
	
	public PluggableIntoMAtmos(MAtMod mod, Expansion expansion)
	{
		this.mod = mod;
		this.expansion = expansion;
		if (expansion.obtainDebugUnit() instanceof FolderResourcePackEditableEDU)
		{
			this.file = ((FolderResourcePackEditableEDU) expansion.obtainDebugUnit()).obtainExpansionFile();
			this.workingDirectory =
				((FolderResourcePackEditableEDU) expansion.obtainDebugUnit()).obtainExpansionFolder();
			this.isReadOnly = false;
		}
		else
		{
			this.isReadOnly = true;
		}
	}
	
	@Override
	public ProviderCollection getProviders()
	{
		if (this.isUnplugged)
		{
			System.err.println("Trying to get providers from an unplugged instance!");
			Thread.dumpStack();
		}
		
		return this.expansion.obtainDebugUnit().obtainKnowledge().obtainProviders();
	}
	
	@Override
	public Data getData()
	{
		if (this.isUnplugged)
		{
			System.err.println("Trying to get data from an unplugged instance!");
			Thread.dumpStack();
		}
		
		return this.expansion.obtainDebugUnit().obtainData();
	}
	
	@Override
	public void pushJason(String jason)
	{
		if (this.isUnplugged)
			return;
		
		final String jasonString = jason;
		this.mod.queueForNextTick(new Runnable() {
			@Override
			public void run()
			{
				PluggableIntoMAtmos.this.mod.getChatter().printChat(
					ChatColorsSimple.COLOR_AQUA
						+ "Reloading from editor state: " + PluggableIntoMAtmos.this.expansion.getName() + " "
						+ getTimestamp());
				PluggableIntoMAtmos.this.expansion.pushDebugJasonAndRefreshKnowledge(jasonString);
			}
		});
	}
	
	@Override
	public void overrideMachine(String machineName, boolean overrideOnStatus)
	{
		if (this.isUnplugged)
			return;
		
	}
	
	@Override
	public void liftOverrides()
	{
		if (this.isUnplugged)
			return;
		
	}
	
	@Override
	public void reloadFromDisk()
	{
		if (this.isUnplugged)
			return;
		
		this.mod.queueForNextTick(new Runnable() {
			@Override
			public void run()
			{
				PluggableIntoMAtmos.this.mod.getChatter().printChat(
					ChatColorsSimple.COLOR_BLUE
						+ "Reloading from disk: " + PluggableIntoMAtmos.this.expansion.getName() + " " + getTimestamp());
				PluggableIntoMAtmos.this.expansion.refreshKnowledge();
			}
		});
	}
	
	protected String getTimestamp()
	{
		return ChatColorsSimple.COLOR_BLACK + "(" + System.currentTimeMillis() + ")";
	}
	
	@Override
	public boolean isReadOnly()
	{
		return this.isReadOnly;
	}
	
	@Override
	public void unplugged()
	{
		if (this.isUnplugged)
			return;
		
		this.isUnplugged = true;
		for (UnpluggedListener listener : this.unpluggedListeners)
		{
			listener.onUnpluggedEvent(this);
		}
	}
	
	@Override
	public void addUnpluggedListener(UnpluggedListener listener)
	{
		this.unpluggedListeners.add(listener);
	}
	
	@Override
	public void removeUnpluggedListener(UnpluggedListener listener)
	{
		this.unpluggedListeners.remove(listener);
	}
	
	@Override
	public File getWorkingDirectoryIfAvailable()
	{
		return this.workingDirectory;
	}
	
	@Override
	public File getFileIfAvailable()
	{
		return this.file;
	}
	
	@Override
	public void onEditorClosed()
	{
	}
}
