package eu.ha3.matmos.engine0.game.data;

import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import eu.ha3.easy.TimeStatistic;
import eu.ha3.matmos.engine0.conv.MAtmosConvLogger;
import eu.ha3.matmos.engine0.conv.Processor;
import eu.ha3.matmos.engine0.conv.ProcessorModel;
import eu.ha3.matmos.engine0.core.implem.GenericSheet;
import eu.ha3.matmos.engine0.core.implem.StringData;
import eu.ha3.matmos.engine0.core.interfaces.Data;
import eu.ha3.matmos.engine0.game.system.MAtMod;
import eu.ha3.matmos.engine0.requirem.Requirements;
import eu.ha3.mc.convenience.Ha3StaticUtilities;
import eu.ha3.mc.quick.ChatColorsSimple;

/* x-placeholder */

public class MAtDataGatherer
{
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
	
	private MAtMod mod;
	
	private MAtScanVolumetricModel largeScanner;
	private MAtScanVolumetricModel smallScanner;
	
	private MAtScanCoordsPipeline largePipeline;
	private MAtScanCoordsPipeline smallPipeline;
	
	private Set<Processor> frequent;
	
	private ProcessorModel relaxedProcessor;
	private ProcessorModel configVarsProcessor;
	private ProcessorModel optionsProcessor;
	private ProcessorModel weatherpony_seasons_api_Processor;
	
	private StringData data;
	
	private int ticksPassed;
	
	private int lastLargeScanX;
	private int lastLargeScanY;
	private int lastLargeScanZ;
	private int lastLargeScanPassed;
	
	public MAtDataGatherer(MAtMod mAtmosHaddon)
	{
		this.mod = mAtmosHaddon;
		this.frequent = new LinkedHashSet<Processor>();
	}
	
	private void resetRegulators()
	{
		this.lastLargeScanPassed = MAX_LARGESCAN_PASS;
		this.ticksPassed = 0;
	}
	
	public void load(Requirements globalRequirements)
	{
		resetRegulators();
		
		this.data = new StringData(globalRequirements);
		prepareSheets();
		
		this.largeScanner = new MAtScanVolumetricModel();
		this.smallScanner = new MAtScanVolumetricModel();
		
		this.largePipeline = new MAtPipelineIDAccumulator(this.data, LARGESCAN, LARGESCAN_THOUSAND, 1000);
		this.smallPipeline = new MAtPipelineIDAccumulator(this.data, SMALLSCAN, SMALLSCAN_THOUSAND, 1000);
		
		this.largeScanner.setPipeline(this.largePipeline);
		this.smallScanner.setPipeline(this.smallPipeline);
		
		this.relaxedProcessor = new MAtProcessorRelaxed(this.mod, this.data, INSTANTS, DELTAS);
		this.configVarsProcessor = new MAtProcessorCVARS(this.mod, this.data, CONFIGVARS, null);
		this.optionsProcessor = new MAtProcessorOptions(this.mod, this.data, OPTIONS, null);
		
		if (Ha3StaticUtilities.classExists("WeatherPony.Seasons.api.Season", this)
			&& Ha3StaticUtilities.classExists("WeatherPony.Seasons.api.BiomeHelper", this))
		{
			MAtmosConvLogger.info("WeatherPony.Seasons.api seems to be installed. Installing processor for Seasons.");
			this.weatherpony_seasons_api_Processor =
				new MAtProcessorSeasonsModAPI(this.mod, this.data, "weatherpony_seasons_api", null);
		}
		
		this.frequent.add(new MAtProcessorFrequent(this.mod, this.data, INSTANTS, DELTAS));
		this.frequent.add(new MAtProcessorContact(this.mod, this.data, CONTACTSCAN, null));
		this.frequent.add(new MAtProcessorEnchantments(this.mod, this.data, CURRENTITEM_E, null) {
			@Override
			protected ItemStack getItem(EntityPlayer player)
			{
				return player.inventory.getCurrentItem();
			}
		});
		this.frequent.add(new MAtProcessorEnchantments(this.mod, this.data, ARMOR1_E, null) {
			@Override
			protected ItemStack getItem(EntityPlayer player)
			{
				return player.inventory.armorInventory[0];
			}
		});
		this.frequent.add(new MAtProcessorEnchantments(this.mod, this.data, ARMOR2_E, null) {
			@Override
			protected ItemStack getItem(EntityPlayer player)
			{
				return player.inventory.armorInventory[1];
			}
		});
		this.frequent.add(new MAtProcessorEnchantments(this.mod, this.data, ARMOR3_E, null) {
			@Override
			protected ItemStack getItem(EntityPlayer player)
			{
				return player.inventory.armorInventory[2];
			}
		});
		this.frequent.add(new MAtProcessorEnchantments(this.mod, this.data, ARMOR4_E, null) {
			@Override
			protected ItemStack getItem(EntityPlayer player)
			{
				return player.inventory.armorInventory[3];
			}
		});
		
		this.frequent.add(new MAtProcessorPotionQuality(this.mod, this.data, POTIONPOWER, null) {
			@Override
			protected int getQuality(PotionEffect effect)
			{
				return effect.getAmplifier() + 1;
			}
		});
		this.frequent.add(new MAtProcessorPotionQuality(this.mod, this.data, POTIONDURATION, null) {
			@Override
			protected int getQuality(PotionEffect effect)
			{
				return effect.getDuration();
			}
		});
		
		this.frequent.add(new MAtProcessorEntityDetector(
			this.mod, this.data, "DetectMinDist", "Detect", "_Deltas", ENTITYIDS_MAX, 2, 5, 10, 20, 50));
		
	}
	
	public Data getData()
	{
		return this.data;
		
	}
	
	private boolean anticrash = true;
	
	public void tickRoutine()
	{
		try
		{
			tickRoutineThrowsStupidProgrammingErrors();
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
	
	public void tickRoutineThrowsStupidProgrammingErrors()
	{
		if (this.ticksPassed % 64 == 0)
		{
			EntityPlayer player = Minecraft.getMinecraft().thePlayer;
			int x = (int) Math.floor(player.posX);
			int y = (int) Math.floor(player.posY);
			int z = (int) Math.floor(player.posZ);
			
			if (this.ticksPassed % 256 == 0
				&& (this.data.getRequirements().isRequired(LARGESCAN) || this.data.getRequirements().isRequired(
					LARGESCAN_THOUSAND)))
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
			
			if (this.data.getRequirements().isRequired(SMALLSCAN)
				|| this.data.getRequirements().isRequired(SMALLSCAN_THOUSAND))
			{
				this.smallScanner.startScan(x, y, z, 16, 8, 16, 2048, null);
			}
			this.relaxedProcessor.process();
			
			if (this.weatherpony_seasons_api_Processor != null)
			{
				this.weatherpony_seasons_api_Processor.process();
			}
			
			this.optionsProcessor.process();
			
			this.data.flagUpdate();
		}
		
		if (true)
		{
			for (Processor model : this.frequent)
			{
				model.process();
			}
			
			this.data.flagUpdate();
		}
		
		if (this.ticksPassed % 2048 == 0)
		{
			this.configVarsProcessor.process();
		}
		
		this.largeScanner.routine();
		this.smallScanner.routine();
		
		this.ticksPassed = this.ticksPassed + 1;
	}
	
	private void prepareSheets()
	{
		createSheet(LARGESCAN);
		createSheet(LARGESCAN_THOUSAND);
		
		createSheet(SMALLSCAN);
		createSheet(SMALLSCAN_THOUSAND);
		
		createSheet(CONTACTSCAN);
		
		createSheet(INSTANTS, COUNT_INSTANTS);
		createSheet(DELTAS, COUNT_INSTANTS);
		
		createSheet(POTIONPOWER, COUNT_POTIONEFFECTS);
		createSheet(POTIONDURATION, COUNT_POTIONEFFECTS);
		
		createSheet(CURRENTITEM_E);
		createSheet(ARMOR1_E);
		createSheet(ARMOR2_E);
		createSheet(ARMOR3_E);
		createSheet(ARMOR4_E);
		
		createSheet(SPECIAL_LARGE, 2);
		createSheet(SPECIAL_SMALL, 1);
		
		createSheet(CONFIGVARS, COUNT_CONFIGVARS);
		
		createSheet("DetectMinDist", ENTITYIDS_MAX);
		createSheet("Detect2", ENTITYIDS_MAX);
		createSheet("Detect5", ENTITYIDS_MAX);
		createSheet("Detect10", ENTITYIDS_MAX);
		createSheet("Detect20", ENTITYIDS_MAX);
		createSheet("Detect50", ENTITYIDS_MAX);
		createSheet("DetectMinDist_Deltas", ENTITYIDS_MAX);
		createSheet("Detect2_Deltas", ENTITYIDS_MAX);
		createSheet("Detect5_Deltas", ENTITYIDS_MAX);
		createSheet("Detect10_Deltas", ENTITYIDS_MAX);
		createSheet("Detect20_Deltas", ENTITYIDS_MAX);
		createSheet("Detect50_Deltas", ENTITYIDS_MAX);
		createSheet("weatherpony_seasons_api", 4);
		
		createSheet("Options", 16);
		
	}
	
	@Deprecated
	private void createSheet(String name, int __count__IGNORED)
	{
		createSheet(name);
	}
	
	private void createSheet(String name)
	{
		this.data.setSheet(name, new GenericSheet(""));
	}
	
	public void dataRoll()
	{
		TimeStatistic timeStatistic = new TimeStatistic(Locale.ENGLISH);
		tickRoutine();
		MAtmosConvLogger
			.info("Took " + timeStatistic.getSecondsAsString(3) + " seconds to perform delta tick routine.");
		
		timeStatistic = new TimeStatistic(Locale.ENGLISH);
		while (this.largeScanner.routine())
		{
		}
		this.smallScanner.routine();
		MAtmosConvLogger.info("Took " + timeStatistic.getSecondsAsString(3) + " seconds to perform data roll.");
		
	}
	
}
