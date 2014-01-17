package eu.ha3.matmos.engine0.game.data;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import eu.ha3.matmos.engine0.conv.MAtmosConvLogger;
import eu.ha3.matmos.engine0.core.implem.GenericSheet;
import eu.ha3.matmos.engine0.core.implem.SelfGeneratingData;
import eu.ha3.matmos.engine0.core.interfaces.Data;
import eu.ha3.matmos.engine0.game.data.abstractions.Collector;
import eu.ha3.matmos.engine0.game.data.abstractions.Processor;
import eu.ha3.matmos.engine0.game.data.abstractions.processor.ProcessorModel;
import eu.ha3.matmos.engine0.game.data.abstractions.scanner.MAtScanCoordsPipeline;
import eu.ha3.matmos.engine0.game.data.abstractions.scanner.MAtScanVolumetricModel;
import eu.ha3.matmos.engine0.game.data.modules.AbstractEnchantmentModule;
import eu.ha3.matmos.engine0.game.data.modules.AbstractPotionQualityModule;
import eu.ha3.matmos.engine0.game.data.modules.Module;
import eu.ha3.matmos.engine0.game.data.modules.PassOnceModule;
import eu.ha3.matmos.engine0.game.system.MAtMod;
import eu.ha3.mc.quick.chat.ChatColorsSimple;

/* x-placeholder */

public class MAtDataGatherer implements Collector, Processor
{
	public static final String DELTA_SUFFIX = "_delta";
	
	final static String INSTANTS = "Instants";
	final static String DELTAS = "Deltas";
	final static String LARGESCAN = "LargeScan";
	final static String SMALLSCAN = "SmallScan";
	final static String LARGESCAN_THOUSAND = "LargeScanPerMil";
	final static String SMALLSCAN_THOUSAND = "SmallScanPerMil";
	final static String SPECIAL_LARGE = "SpecialLarge";
	final static String SPECIAL_SMALL = "SpecialSmall";
	final static String CONTACTSCAN = "ContactScan";
	final static String CONFIGVARS = "ConfigVars";
	
	final static String POTIONPOWER = "PotionEffectsPower";
	final static String POTIONDURATION = "PotionEffectsDuration";
	
	final static String CURRENTITEM_E = "CurrentItemEnchantments";
	final static String ARMOR1_E = "Armor1Enchantments";
	final static String ARMOR2_E = "Armor2Enchantments";
	final static String ARMOR3_E = "Armor3Enchantments";
	final static String ARMOR4_E = "Armor4Enchantments";
	
	final static String OPTIONS = "Options";
	
	final static int COUNT_WORLD_BLOCKS = 4096;
	final static int COUNT_INSTANTS = 128;
	final static int COUNT_CONFIGVARS = 256;
	
	final static int COUNT_POTIONEFFECTS = 32;
	final static int COUNT_ENCHANTMENTS = 64;
	
	final static int MAX_LARGESCAN_PASS = 10;
	private static final int ENTITYIDS_MAX = 256;
	
	public static final String NULL = "";
	
	private MAtScanVolumetricModel largeScanner;
	private MAtScanVolumetricModel smallScanner;
	
	private MAtScanCoordsPipeline largePipeline;
	private MAtScanCoordsPipeline smallPipeline;
	
	private Set<Processor> frequent;
	
	private ProcessorModel relaxedProcessor;
	private ProcessorModel configVarsProcessor;
	private ProcessorModel optionsProcessor;
	private ProcessorModel weatherpony_seasons_api_Processor;
	
	private Data data;
	
	private int ticksPassed;
	
	private int lastLargeScanX;
	private int lastLargeScanY;
	private int lastLargeScanZ;
	private int lastLargeScanPassed;
	
	//
	
	private final MAtMod mod;
	private final Map<String, Module> modules;
	private final Map<String, Set<String>> passOnceModules;
	private final Map<String, Set<String>> moduleStack;
	private final Set<String> requiredModules;
	
	private boolean anticrash = true;
	
	public MAtDataGatherer(MAtMod mAtmosHaddon)
	{
		this.mod = mAtmosHaddon;
		
		this.modules = new TreeMap<String, Module>();
		this.passOnceModules = new TreeMap<String, Set<String>>();
		this.requiredModules = new TreeSet<String>();
		this.moduleStack = new TreeMap<String, Set<String>>();
		
		this.frequent = new HashSet<Processor>();
	}
	
	private void addModule(Module module)
	{
		this.modules.put(module.getModuleName(), module);
		if (module instanceof PassOnceModule)
		{
			this.passOnceModules.put(module.getModuleName(), ((PassOnceModule) module).getSubModules());
		}
	}
	
	public void load()
	{
		this.data = new SelfGeneratingData(GenericSheet.class);
		
		/*addModule(new ModulePlayerPosition(this.data));
		addModule(new ModulePlayerHotbarItems(this.data));
		addModule(new AbstractEnchantmentModule(this.data, "ench_current") {
			@Override
			protected ItemStack getItem(EntityPlayer player)
			{
				return player.getCurrentEquippedItem();
			}
		});*/
		
		this.largeScanner = new MAtScanVolumetricModel();
		this.smallScanner = new MAtScanVolumetricModel();
		
		this.largePipeline = new MAtPipelineIDAccumulator(this.data, LARGESCAN, LARGESCAN_THOUSAND, 1000);
		this.smallPipeline = new MAtPipelineIDAccumulator(this.data, SMALLSCAN, SMALLSCAN_THOUSAND, 1000);
		
		this.largeScanner.setPipeline(this.largePipeline);
		this.smallScanner.setPipeline(this.smallPipeline);
		
		this.relaxedProcessor = new MAtProcessorRelaxed(this.mod, this.data, INSTANTS, DELTAS);
		this.configVarsProcessor = new MAtProcessorCVARS(this.mod, this.data, CONFIGVARS, null);
		this.optionsProcessor = new MAtProcessorOptions(this.mod, this.data, OPTIONS, null);
		
		// XXX 2014-01-17 Seasons Mod
		/*if (Ha3StaticUtilities.classExists("WeatherPony.Seasons.api.Season", this)
			&& Ha3StaticUtilities.classExists("WeatherPony.Seasons.api.BiomeHelper", this))
		{
			MAtmosConvLogger.info("WeatherPony.Seasons.api seems to be installed. Installing processor for Seasons.");
			this.weatherpony_seasons_api_Processor =
				new MAtProcessorSeasonsModAPI(this.mod, this.data, "weatherpony_seasons_api", null);
		} */
		
		this.frequent.add(new MAtProcessorFrequent(this.mod, this.data, INSTANTS, DELTAS));
		this.frequent.add(new ProcessorContact(this.data, CONTACTSCAN));
		this.frequent.add(new AbstractEnchantmentModule(this.data, CURRENTITEM_E) {
			@Override
			protected ItemStack getItem(EntityPlayer player)
			{
				return player.inventory.getCurrentItem();
			}
		});
		this.frequent.add(new AbstractEnchantmentModule(this.data, ARMOR1_E) {
			@Override
			protected ItemStack getItem(EntityPlayer player)
			{
				return player.inventory.armorInventory[0];
			}
		});
		this.frequent.add(new AbstractEnchantmentModule(this.data, ARMOR2_E) {
			@Override
			protected ItemStack getItem(EntityPlayer player)
			{
				return player.inventory.armorInventory[1];
			}
		});
		this.frequent.add(new AbstractEnchantmentModule(this.data, ARMOR3_E) {
			@Override
			protected ItemStack getItem(EntityPlayer player)
			{
				return player.inventory.armorInventory[2];
			}
		});
		this.frequent.add(new AbstractEnchantmentModule(this.data, ARMOR4_E) {
			@Override
			protected ItemStack getItem(EntityPlayer player)
			{
				return player.inventory.armorInventory[3];
			}
		});
		
		this.frequent.add(new AbstractPotionQualityModule(this.data, POTIONPOWER) {
			@Override
			protected int getQuality(PotionEffect effect)
			{
				return effect.getAmplifier() + 1;
			}
		});
		this.frequent.add(new AbstractPotionQualityModule(this.data, POTIONDURATION) {
			@Override
			protected int getQuality(PotionEffect effect)
			{
				return effect.getDuration();
			}
		});
		
		//this.frequent.add(new MAtProcessorEntityDetector(
		//	this.mod, this.data, "DetectMinDist", "Detect", "_Deltas", ENTITYIDS_MAX, 2, 5, 10, 20, 50));
	}
	
	public Data getData()
	{
		return this.data;
	}
	
	@Override
	public void process()
	{
		try
		{
			doProcess();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			
			if (this.anticrash)
			{
				this.anticrash = false;
				this.mod.getChatter().printChat(
					ChatColorsSimple.COLOR_RED, "MAtmos is crashing: ", ChatColorsSimple.COLOR_WHITE,
					e.getClass().getName(), ": ", e.getCause());
				
				int i = 0;
				for (StackTraceElement x : e.getStackTrace())
				{
					if (i <= 5 || x.toString().contains("MAt") || x.toString().contains("eu.ha3.matmos."))
					{
						this.mod.getChatter().printChat(ChatColorsSimple.COLOR_WHITE, x.toString());
					}
					i++;
				}
				
				this.mod.getChatter().printChat(ChatColorsSimple.COLOR_RED, "Please report this issue :(");
			}
		}
	}
	
	private void doProcess()
	{
		for (String requiredModule : this.requiredModules)
		{
			this.modules.get(requiredModule).process();
		}
		
		if (this.ticksPassed % 64 == 0)
		{
			EntityPlayer player = Minecraft.getMinecraft().thePlayer;
			int x = (int) Math.floor(player.posX);
			int y = (int) Math.floor(player.posY);
			int z = (int) Math.floor(player.posZ);
			
			if (this.ticksPassed % 256 == 0 && (requires(LARGESCAN) || requires(LARGESCAN_THOUSAND)))
			{
				if (this.lastLargeScanPassed >= MAX_LARGESCAN_PASS
					|| Math.abs(x - this.lastLargeScanX) > 16 || Math.abs(y - this.lastLargeScanY) > 8
					|| Math.abs(z - this.lastLargeScanZ) > 16)
				{
					this.lastLargeScanX = x;
					this.lastLargeScanY = y;
					this.lastLargeScanZ = z;
					this.lastLargeScanPassed = 0;
					this.largeScanner.startScan(x, y, z, 64, 32, 64, 8192, null);
					
				}
				else
				{
					this.lastLargeScanPassed++;
				}
			}
			
			if (requires(SMALLSCAN) || requires(SMALLSCAN_THOUSAND))
			{
				this.smallScanner.startScan(x, y, z, 16, 8, 16, 2048, null);
			}
			this.relaxedProcessor.process();
			
			if (this.weatherpony_seasons_api_Processor != null)
			{
				this.weatherpony_seasons_api_Processor.process();
			}
			
			//this.optionsProcessor.process();
		}
		
		if (true)
		{
			for (Processor model : this.frequent)
			{
				model.process();
			}
		}
		
		if (this.ticksPassed % 2048 == 0)
		{
			this.configVarsProcessor.process();
		}
		
		this.largeScanner.routine();
		this.smallScanner.routine();
		
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
		// Find missing modules first. We don't want to iterate and check through invalid modules.
		Set<String> missingModules = new HashSet<String>();
		for (String module : requiredModules)
		{
			if (!this.modules.containsKey(module))
			{
				MAtmosConvLogger.warning("Stack " + name + " requires missing module " + module);
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
	
	private void recomputeModuleStack()
	{
		this.requiredModules.clear();
		for (Set<String> stack : this.moduleStack.values())
		{
			this.requiredModules.addAll(stack);
		}
		
		for (Map.Entry<String, Set<String>> submodules : this.passOnceModules.entrySet())
		{
			// if the submodules have something in common with the required modules
			if (!Collections.disjoint(submodules.getValue(), this.requiredModules))
			{
				this.requiredModules.removeAll(submodules.getValue());
				this.requiredModules.add(submodules.getKey());
			}
		}
	}
}
