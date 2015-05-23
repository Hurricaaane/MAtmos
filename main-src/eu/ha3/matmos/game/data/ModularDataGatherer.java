package eu.ha3.matmos.game.data;

import eu.ha3.easy.StopWatchStatistic;
import eu.ha3.easy.TimeStatistic;
import eu.ha3.matmos.engine.core.implem.GenericSheet;
import eu.ha3.matmos.engine.core.implem.SelfGeneratingData;
import eu.ha3.matmos.engine.core.interfaces.Data;
import eu.ha3.matmos.game.data.abstractions.Collector;
import eu.ha3.matmos.game.data.abstractions.Processor;
import eu.ha3.matmos.game.data.abstractions.module.Module;
import eu.ha3.matmos.game.data.abstractions.module.ModuleProcessor;
import eu.ha3.matmos.game.data.abstractions.module.PassOnceModule;
import eu.ha3.matmos.game.data.abstractions.module.ProcessorModel;
import eu.ha3.matmos.game.data.abstractions.scanner.Progress;
import eu.ha3.matmos.game.data.abstractions.scanner.ScannerModule;
import eu.ha3.matmos.game.data.modules.*;
import eu.ha3.matmos.game.system.IDontKnowHowToCode;
import eu.ha3.matmos.game.system.MAtMod;
import eu.ha3.matmos.log.MAtLog;

import java.util.*;

/*
--filenotes-placeholder
*/

public class ModularDataGatherer implements Collector, Processor
{
	private final MAtMod mod;
	
	private Data data;
	private int ticksPassed;
	
	private StopWatchStatistic watch = new StopWatchStatistic();
	
	public static final String LEGACY_PREFIX = "legacy";
	
	//
	
	private final Map<String, Module> modules;
	private final Map<String, Set<String>> passOnceModules;
	private final Set<String> passOnceSubmodules;
	private final Set<String> requiredModules;
	private final Set<String> iteratedThroughModules;
	private final Map<String, Set<String>> moduleStack;
	
	private ScannerModule largeScanner;
	
	//
	
	public ModularDataGatherer(MAtMod mAtmosHaddon)
	{
		this.mod = mAtmosHaddon;
		
		this.modules = new TreeMap<String, Module>();
		this.passOnceModules = new TreeMap<String, Set<String>>();
		this.passOnceSubmodules = new HashSet<String>();
		this.requiredModules = new TreeSet<String>();
		this.iteratedThroughModules = new TreeSet<String>();
		this.moduleStack = new TreeMap<String, Set<String>>();
	}
	
	private void addModule(Module module)
	{
		this.modules.put(module.getModuleName(), module);
		if (module instanceof PassOnceModule)
		{
			this.passOnceModules.put(module.getModuleName(), ((PassOnceModule) module).getSubModules());
			this.passOnceSubmodules.addAll(((PassOnceModule) module).getSubModules());
		}
	}
	
	/**
	 * Adds a module after setting an cycle on it, if it's an instance of a
	 * ProcessorModel. Cycle: Every n ticks. Cycle = 1: Every ticks.
	 * 
	 * @param module
	 * @param cycle
	 */
	private void addModule(Module module, int cycle)
	{
		if (module instanceof ProcessorModel)
		{
			((ProcessorModel) module).setInterval(cycle - 1);
		}
		addModule(module);
	}
	
	public void load()
	{
		this.data = new SelfGeneratingData(GenericSheet.class);
		
		addModule(new L__legacy_column(this.data));
		addModule(new L__legacy_hitscan(this.data));
		addModule(new L__legacy_random(this.data));
		addModule(new L__legacy(this.data));
		addModule(new L__meta_mod(this.data, this.mod));
		addModule(new M__cb_column(this.data));
		addModule(new M__cb_light(this.data));
		addModule(new M__cb_pos(this.data));
		addModule(new M__gui_general(this.data));
		addModule(new M__ply_action(this.data));
		addModule(new M__ply_armor(this.data));
		addModule(new M__ply_general(this.data, this.mod.util()));
		addModule(new M__ply_inventory(this.data));
		addModule(new M__ply_motion(this.data));
		addModule(new M__ply_stats(this.data));
		addModule(new M__ride_general(this.data));
		addModule(new M__ride_horse(this.data));
		addModule(new M__ride_motion(this.data));
        addModule(new M_timed_random(this.data), 20);
		addModule(new M__w_biome(this.data, this.mod), 20);
		addModule(new M__w_general(this.data));
		addModule(new R__legacy_configvars(this.data, this.mod), 10000);
		addModule(new R__meta_option(this.data, this.mod), 200);
		addModule(new R__server_info(this.data), 200);
		addModule(new S__detect(this.data, this, "detect_mindist", "detect_radius", 256, 2, 5, 10, 20, 50));
		addModule(new S__ench_armor(this.data, 0));
		addModule(new S__ench_armor(this.data, 1));
		addModule(new S__ench_armor(this.data, 2));
		addModule(new S__ench_armor(this.data, 3));
		addModule(new S__ench_current(this.data));
		addModule(new S__ply_hitscan(this.data));
		addModule(new S__ply_leash(this.data));
		addModule(new S__potion_duration(this.data));
		addModule(new S__potion_power(this.data));
		addModule(new S__scan_contact(this.data));
		
		//this.frequent.add(new MAtProcessorEntityDetector(
		//	this.mod, this.data, "DetectMinDist", "Detect", "_Deltas", ENTITYIDS_MAX, 2, 5, 10, 20, 50));
        // 16 * 8 * 16
		this.largeScanner =
			new ScannerModule(
				this.data, "_POM__scan_large", "scan_large", true, 8, 20 /*256*/, 64, 32, 64, 1024/*64 * 64 * 2*/);
		addModule(this.largeScanner);
        // 16 * 4 * 16
		addModule(new ScannerModule(
			this.data, "_POM__scan_small", "scan_small", true, -1, 2 /*64*/, 16, 8, 16, 512));
		// Each ticks, check half of the small scan
		
		MAtLog.info("Modules initialized: " + Arrays.toString(new TreeSet<String>(this.modules.keySet()).toArray()));
	}
	
	public Data getData()
	{
		return this.data;
	}
	
	@Override
	public void process()
	{
		TimeStatistic stat = new TimeStatistic();
		for (String requiredModule : this.iteratedThroughModules)
		{
			this.watch.reset();
			try
			{
				this.modules.get(requiredModule).process();
			}
			catch (Exception e)
			{
				e.printStackTrace();
				IDontKnowHowToCode.whoops__printExceptionToChat(this.mod.getChatter(), e, requiredModule.hashCode());
			}
			this.watch.stop();
			if (this.watch.getMilliseconds() > 50 && this.mod.isDebugMode())
			{
				MAtLog.warning("WARNING: Module " + requiredModule + " took " + stat.getMilliseconds() + "ms!!!");
			}
		}
		this.ticksPassed = this.ticksPassed + 1;
	}
	
	@Override
	public boolean requires(String moduleName)
	{
		return this.requiredModules.contains(moduleName);
	}
	
	@Override
	public void addModuleStack(String name, Set<String> requiredModules)
	{
		// Recompact required modules to piece deltas.
		Set<String> deltaModules = new HashSet<String>();
		Set<String> actualModules = new HashSet<String>();
		for (String module : requiredModules)
		{
			if (module.endsWith(ModuleProcessor.DELTA_SUFFIX))
			{
				deltaModules.add(module);
				actualModules.add(module.substring(0, module.length() - ModuleProcessor.DELTA_SUFFIX.length()));
			}
		}
		requiredModules.removeAll(deltaModules);
		requiredModules.addAll(actualModules);
		
		// Find missing modules. We don't want to iterate and check through invalid modules.
		Set<String> missingModules = new HashSet<String>();
		for (String module : requiredModules)
		{
			if (!this.modules.containsKey(module) && !this.passOnceSubmodules.contains(module))
			{
				MAtLog.severe("Stack " + name + " requires missing module " + module);
				missingModules.add(module);
			}
		}
		
		for (String missingModule : missingModules)
		{
			requiredModules.remove(missingModule);
		}
		
		this.moduleStack.put(name, requiredModules);
		
		recomputeModuleStack();
	}
	
	@Override
	public void removeModuleStack(String name)
	{
		this.moduleStack.remove(name);
		recomputeModuleStack();
	}
	
	public void forceRecomputeModuleStack_debugModeChanged()
	{
		recomputeModuleStack();
	}
	
	private void recomputeModuleStack()
	{
		if (this.mod.isDebugMode())
		{
			this.requiredModules.clear();
			this.iteratedThroughModules.clear();
			
			this.requiredModules.addAll(this.modules.keySet());
			this.requiredModules.removeAll(this.passOnceModules.keySet());
			this.requiredModules.addAll(this.passOnceSubmodules);
			
			this.iteratedThroughModules.addAll(this.modules.keySet());
			
			return;
		}
		
		this.requiredModules.clear();
		this.iteratedThroughModules.clear();
		for (Set<String> stack : this.moduleStack.values())
		{
			this.requiredModules.addAll(stack);
			this.iteratedThroughModules.addAll(stack);
		}
		
		for (Map.Entry<String, Set<String>> submodules : this.passOnceModules.entrySet())
		{
			// if the submodules have something in common with the required modules
			if (!Collections.disjoint(submodules.getValue(), this.iteratedThroughModules))
			{
				this.iteratedThroughModules.removeAll(submodules.getValue());
				this.iteratedThroughModules.add(submodules.getKey());
			}
		}
	}
	
	public Progress getLargeScanProgress()
	{
		return this.largeScanner;
	}
}