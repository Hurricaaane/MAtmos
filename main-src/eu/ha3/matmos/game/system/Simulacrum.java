package eu.ha3.matmos.game.system;

import eu.ha3.easy.TimeStatistic;
import eu.ha3.matmos.expansions.Expansion;
import eu.ha3.matmos.expansions.ExpansionManager;
import eu.ha3.matmos.expansions.volume.VolumeUpdatable;
import eu.ha3.matmos.game.data.ModularDataGatherer;
import eu.ha3.matmos.game.user.VisualDebugger;
import eu.ha3.matmos.log.MAtLog;
import eu.ha3.mc.haddon.supporting.SupportsFrameEvents;
import eu.ha3.mc.haddon.supporting.SupportsTickEvents;
import java.io.File;
import java.util.Locale;
import java.util.Map;

public class Simulacrum implements SupportsTickEvents, SupportsFrameEvents
{
	private final MAtMod mod;
	private ExpansionManager expansionManager;
	private ModularDataGatherer dataGatherer;
	private VisualDebugger visualDebugger;
	private boolean isBrutallyInterrupted;

	private boolean hasResourcePacks;
	private boolean hasDisabledResourcePacks;

	public Simulacrum(MAtMod mod)
	{
		this.mod = mod;

		this.expansionManager =
			new ExpansionManager(new File(mod.util().getModsFolder(), "matmos/expansions_r29_userconfig/"), mod);
		this.expansionManager.setVolumeAndUpdate(mod.getConfig().getFloat("globalvolume.scale"));

		TimeStatistic stat = new TimeStatistic(Locale.ENGLISH);

		this.dataGatherer = new ModularDataGatherer(mod);
		this.dataGatherer.load();
		this.visualDebugger = new VisualDebugger(mod, this.dataGatherer);
		this.expansionManager.setData(this.dataGatherer.getData());
		this.expansionManager.setCollector(this.dataGatherer);
		this.expansionManager.loadExpansions();

		this.hasResourcePacks = true;
		if (this.expansionManager.getExpansions().size() == 0)
		{
			MAtResourcePackDealer dealer = new MAtResourcePackDealer();
			if (dealer.findResourcePacks().size() == 0)
			{
				this.hasResourcePacks = false;
				this.hasDisabledResourcePacks = dealer.findDisabledResourcePacks().size() > 0;
			}
		}

		this.expansionManager.synchronize();

		MAtLog.info("Expansions loaded (" + stat.getSecondsAsString(1) + "s).");
	}

	public void interruptBrutally()
	{
		this.isBrutallyInterrupted = true;
	}

	public void dispose()
	{
		if (!isBrutallyInterrupted)
			expansionManager.dispose();
	}

	public void onFrame(float semi)
	{
		expansionManager.onFrame(semi);
		visualDebugger.onFrame(semi);
	}

	public void onTick()
	{
		this.dataGatherer.process();
		this.expansionManager.onTick();
	}

	public boolean hasResourcePacks()
	{
		return hasResourcePacks;
	}

	public boolean hasDisabledResourcePacks()
	{
		return hasDisabledResourcePacks;
	}

	public Map<String, Expansion> getExpansions()
	{
		return expansionManager.getExpansions();
	}

	public void synchronize()
	{
		expansionManager.synchronize();
	}

	public void saveConfig()
	{
		expansionManager.saveConfig();
	}

	public VisualDebugger getVisualDebugger()
	{
		return visualDebugger;
	}

	public VolumeUpdatable getGlobalVolumeControl()
	{
		return expansionManager;
	}

}
