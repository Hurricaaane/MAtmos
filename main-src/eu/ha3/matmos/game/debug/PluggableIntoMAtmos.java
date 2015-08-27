package eu.ha3.matmos.game.debug;

import com.google.common.base.Optional;
import eu.ha3.matmos.expansions.Expansion;
import eu.ha3.matmos.expansions.debugunit.FolderResourcePackEditableEDU;
import eu.ha3.matmos.game.system.MAtMod;
import eu.ha3.matmos.pluggable.PluggableIntoMinecraft;
import java.io.File;
import net.minecraft.util.EnumChatFormatting;

/*
--filenotes-placeholder
*/

public class PluggableIntoMAtmos implements PluggableIntoMinecraft
{
	private MAtMod mod;
	private String expansionName;
	
	private boolean isReadOnly;
	
	private File file;
	private File workingDirectory;
	
	public PluggableIntoMAtmos(MAtMod mod, Expansion expansion)
	{
		this.mod = mod;
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
		
		this.expansionName = expansion.getName();
	}
	
	@Override
	public void pushJason(String jason)
	{
		final String jasonString = jason;
		this.mod.queueForNextTick(new Runnable() {
			@Override
			public void run()
			{
				Optional<Expansion> opt = mod.getExpansionEffort(expansionName);

				if (opt.isPresent())
				{
					Expansion expansion = opt.get();

					PluggableIntoMAtmos.this.mod.getChatter().printChat(
						EnumChatFormatting.AQUA
							+ "Reloading from editor state: " + expansion.getName() + " "
							+ getTimestamp());
					expansion.pushDebugJasonAndRefreshKnowledge(jasonString);
				}
			}
		});
	}
	
	@Override
	public void reloadFromDisk()
	{
		this.mod.queueForNextTick(new Runnable() {
			@Override
			public void run()
			{
				Optional<Expansion> opt = mod.getExpansionEffort(expansionName);

				if (opt.isPresent())
				{
					Expansion expansion = opt.get();

					PluggableIntoMAtmos.this.mod.getChatter().printChat(
						EnumChatFormatting.BLUE
							+ "Reloading from disk: " + expansion.getName() + " " + getTimestamp());
					expansion.refreshKnowledge();
				}
			}
		});
	}
	
	protected String getTimestamp()
	{
		return EnumChatFormatting.BLACK + "(" + System.currentTimeMillis() + ")";
	}
	
	@Override
	public boolean isReadOnly()
	{
		return this.isReadOnly;
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
