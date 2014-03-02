package eu.ha3.matmos.game.debug;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import eu.ha3.matmos.editor.PluggableIntoMinecraft;
import eu.ha3.matmos.editor.UnpluggedListener;
import eu.ha3.matmos.engine.core.implem.abstractions.ProviderCollection;
import eu.ha3.matmos.engine.core.interfaces.Data;
import eu.ha3.matmos.expansions.Expansion;
import eu.ha3.matmos.game.system.MAtMod;
import eu.ha3.mc.quick.chat.ChatColorsSimple;

/*
--filenotes-placeholder
*/

public class Pluggable implements PluggableIntoMinecraft
{
	private MAtMod mod;
	private Expansion expansion;
	private File file;
	private List<UnpluggedListener> unpluggedListeners = new ArrayList<UnpluggedListener>();
	
	public Pluggable(MAtMod mod, Expansion expansion)
	{
		this.mod = mod;
		this.expansion = expansion;
		this.file = expansion.obtainDebugUnit().getExpansionFile();
	}
	
	@Override
	public ProviderCollection getProviders()
	{
		return this.expansion.obtainDebugUnit().obtainKnowledge().obtainProviders();
	}
	
	@Override
	public Data getData()
	{
		return null;
	}
	
	@Override
	public void pushJason(String jason)
	{
		final String jasonString = jason;
		this.mod.queueForNextTick(new Runnable() {
			@Override
			public void run()
			{
				Pluggable.this.mod.getChatter().printChat(
					ChatColorsSimple.COLOR_AQUA
						+ "Reloading from editor state: " + Pluggable.this.expansion.getName() + " " + getTimestamp());
				Pluggable.this.expansion.pushDebugJasonAndRefreshKnowledge(jasonString);
			}
		});
	}
	
	@Override
	public void overrideMachine(String machineName, boolean overrideOnStatus)
	{
	}
	
	@Override
	public void liftOverrides()
	{
	}
	
	@Override
	public void reloadFromDisk()
	{
		this.mod.queueForNextTick(new Runnable() {
			@Override
			public void run()
			{
				Pluggable.this.mod.getChatter().printChat(
					ChatColorsSimple.COLOR_BLUE
						+ "Reloading from disk: " + Pluggable.this.expansion.getName() + " " + getTimestamp());
				Pluggable.this.expansion.refreshKnowledge();
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
		return this.file != null;
	}
	
	@Override
	public void unplugged()
	{
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
}
