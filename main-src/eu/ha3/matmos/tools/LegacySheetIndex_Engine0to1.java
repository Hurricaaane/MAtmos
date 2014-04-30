package eu.ha3.matmos.tools;

import java.util.Map;
import java.util.TreeMap;

import eu.ha3.matmos.engine.core.implem.LongFloatSimplificator;
import eu.ha3.matmos.engine.core.interfaces.SheetIndex;
import eu.ha3.matmos.game.data.abstractions.module.ModuleProcessor;

/*
--filenotes-placeholder
*/

public class LegacySheetIndex_Engine0to1 implements SheetIndex
{
	private static final Map<String, LegacyMapping> forward;
	private static final Map<String, LegacyMapping> blocks;
	private static final Map<String, LegacyMapping> items;
	private static String sheetWork;
	private static String deltaWork;
	
	private final String sheet;
	private final String index;
	private boolean isItem;
	private boolean isBlock;
	
	static
	{
		forward = new TreeMap<String, LegacyMapping>();
		blocks = new TreeMap<String, LegacyMapping>();
		items = new TreeMap<String, LegacyMapping>();
		
		setWork("Instants", "Deltas");
		
		// Frequent
		add(0, "cb_light", "sky");
		add(1, "cb_light", "lamp");
		add(2, "cb_light", "final");
		add(9, "cb_light", "see_sky");
		
		add(20, "cb_pos", "x");
		add(4, "cb_pos", "y");
		add(21, "cb_pos", "z");
		
		add(3, "w_general", "time_modulo24k");
		add(7, "w_general", "rain");
		add(8, "w_general", "thunder");
		add(25, "w_general", "dimension");
		add(11, "w_general", "light_subtracted");
		
		add(6, "ply_general", "in_water");
		add(19, "ply_general", "wet");
		add(22, "ply_general", "on_ground");
		add(39, "ply_general", "burning");
		add(49, "ply_general", "burning"); // 39 and 49 are the same
		add(42, "ply_general", "jumping");
		add(44, "ply_general", "in_web");
		add(57, "ply_general", "on_ladder");
		add(60, "ply_general", "blocking");
		add(63, "ply_general", "sprinting");
		add(64, "ply_general", "sneaking");
		add(65, "ply_general", "airborne");
		add(66, "ply_general", "using_item");
		add(67, "ply_general", "riding");
		add(70, "ply_general", "creative");
		
		add(40, "ply_action", "swing_progress16");
		add(41, "ply_action", "swinging");
		add(43, "ply_action", "fall_distance1k");
		add(58, "ply_action", "item_use_duration");
		
		add(23, "ply_stats", "oxygen");
		add(50, "ply_stats", "armor");
		add(51, "ply_stats", "food");
		add(52, "ply_stats", "saturation1k");
		add(53, "ply_stats", "exhaustion1k");
		add(54, "ply_stats", "experience1k");
		add(55, "ply_stats", "experience_level");
		add(56, "ply_stats", "experience_total");
		
		//add(32, "ply_inventory", "current_item"); -> moved to player current item as number
		add(46, "ply_inventory", "held_slot");
		add(62, "ply_inventory", "current_damage");
		
		add(47, "legacy_hitscan", "mouse_over_something");
		add(48, "legacy_hitscan", "mouse_over_what_remapped"); // Remap odinal()
		add(86, "legacy_hitscan", "block_as_number");
		add(87, "legacy_hitscan", "meta_as_number");
		// XXX: ISSUE_WARNING(48)
		
		add(33, "ply_motion", "x_1k");
		add(34, "ply_motion", "y_1k");
		add(35, "ply_motion", "z_1k");
		add(45, "ply_motion", "sqrt_xx_zz");
		
		add(71, "ride_motion", "x_1k");
		add(72, "ride_motion", "y_1k");
		add(73, "ride_motion", "z_1k");
		add(74, "ride_motion", "sqrt_xx_zz");
		
		tagAsBlock(36, "cb_column", "y-1");
		tagAsBlock(37, "cb_column", "y-2");
		tagAsBlock(94, "cb_column", "y0");
		tagAsBlock(95, "cb_column", "y1");
		add(36, "legacy_column", "y-1_as_number");
		add(37, "legacy_column", "y-2_as_number");
		add(94, "legacy_column", "y0_as_number");
		add(95, "legacy_column", "y1_as_number");
		/*add(36, "cb_column", "y-1");
		add(37, "cb_column", "y-2");
		add(94, "cb_column", "y0");
		add(95, "cb_column", "y1");*/
		add(26, "cb_column", "can_rain_reach");
		add(27, "cb_column", "topmost_block");
		add(28, "cb_column", "thickness_overhead");
		
		add(38, "meta_mod", "mod_tick");
		
		add(24, "legacy", "player_health_ceil");
		add(10, "legacy", "world_nether");
		add(32, "legacy", "player_current_item_as_number");
		add(61, "legacy", "72000_minus_item_use_duration");
		add(68, "legacy", "riding_minecart");
		add(69, "legacy", "riding_boat");
		add(89, "legacy", "armor_0_as_number");
		add(90, "legacy", "armor_1_as_number");
		add(91, "legacy", "armor_2_as_number");
		add(92, "legacy", "armor_3_as_number");
		add(97, "legacy", "gui_instanceof_container");
		add(100, "legacy", "riding_horse");
		
		add(26, "cb_column", "can_rain_reach");
		add(59, "UNKNOWN", "unknown_59");
		add(96, "UNKNOWN", "unknown_96");
		
		add(75, "server_info", "has_server_info");
		add(76, "server_info", "addressinput_hashcode");
		add(77, "server_info", "motd_hashcode");
		add(78, "server_info", "name_hashcode");
		add(79, "server_info", "ip_computed_hashcode");
		add(80, "server_info", "port_computed");
		
		add(5, "w_general", "dimension"); // XXX: Identical to 25?
		add(12, "w_general", "remote");
		add(88, "w_general", "moon_phase");
		
		add(13, "legacy_random", "dice_a");
		add(14, "legacy_random", "dice_b");
		add(15, "legacy_random", "dice_c");
		add(16, "legacy_random", "dice_d");
		add(17, "legacy_random", "dice_e");
		add(18, "legacy_random", "dice_f");
		
		add(29, "UNKNOWN", "unknown_29_biome_deprecated");
		
		add(30, "legacy", "seed_higher");
		add(31, "legacy", "seed_lower");
		
		add(93, "w_biome", "id");
		
		// Tags
		tagAsBlock(86, "hitscan", "block");
		tagAsItem(32, "ply_inventory", "current_item");
		tagAsItem(89, "ply_inventory", "armor_0");
		tagAsItem(90, "ply_inventory", "armor_1");
		tagAsItem(91, "ply_inventory", "armor_2");
		tagAsItem(92, "ply_inventory", "armor_3");
		
		// Relaxed
		//
		
		setWork("Options");
		add(0, "meta_option", "altitudes_high");
		add(1, "meta_option", "altitudes_low");
		
		setWork("ConfigVars");
		fillOut(64, "legacy_configvars");
		
		// fillOut (done)
		
		setWork("CurrentItemEnchantments");
		fillOut(256, "ench_current");
		
		setWork("Armor1Enchantments");
		fillOut(256, "ench_armor1");
		
		setWork("Armor2Enchantments");
		fillOut(256, "ench_armor2");
		
		setWork("Armor3Enchantments");
		fillOut(256, "ench_armor3");
		
		setWork("Armor4Enchantments");
		fillOut(256, "ench_armor4");
		
		setWork("PotionEffectsPower");
		fillOut(256, "potion_power");
		
		setWork("PotionEffectsDuration");
		fillOut(256, "potion_duration");
	}
	
	private static void tagAsBlock(int id, String sheet, String index)
	{
		blocks.put(sheetWork + "@@@" + id, new LegacyMapping(sheet, index));
		
		if (deltaWork != null)
		{
			blocks.put(deltaWork + "@@@" + id, new LegacyMapping(sheet + ModuleProcessor.DELTA_SUFFIX, index));
		}
	}
	
	private static void tagAsItem(int id, String sheet, String index)
	{
		items.put(sheetWork + "@@@" + id, new LegacyMapping(sheet, index));
		
		if (deltaWork != null)
		{
			items.put(deltaWork + "@@@" + id, new LegacyMapping(sheet + ModuleProcessor.DELTA_SUFFIX, index));
		}
	}
	
	public boolean isItem()
	{
		return this.isItem;
	}
	
	public boolean isBlock()
	{
		return this.isBlock;
	}
	
	private static void fillOut(int count, String module)
	{
		for (int i = 0; i < count; i++)
		{
			add(i, module, Integer.toString(i));
		}
	}
	
	private static void setWork(String sheet, String delta)
	{
		sheetWork = sheet;
		deltaWork = delta;
	}
	
	private static void setWork(String sheet)
	{
		setWork(sheet, null);
	}
	
	private static void add(int id, String sheet, String index)
	{
		forward.put(sheetWork + "@@@" + id, new LegacyMapping(sheet, index));
		
		if (deltaWork != null)
		{
			forward.put(deltaWork + "@@@" + id, new LegacyMapping(sheet + ModuleProcessor.DELTA_SUFFIX, index));
		}
	}
	
	public LegacySheetIndex_Engine0to1(String sheet, String index)
	{
		Long id = LongFloatSimplificator.longOf(index);
		if (id != null && forward.containsKey(sheet + "@@@" + index))
		{
			Map.Entry<String, String> mapping = forward.get(sheet + "@@@" + index);
			this.sheet = mapping.getKey();
			this.index = mapping.getValue();
		}
		else
		{
			// XXX: Put block conversion directly into this?
			if (!sheet.startsWith("scan_"))
			{
				System.err.println("No forward found, using raw: " + sheet + ", " + index);
			}
			
			this.sheet = sheet;
			this.index = index;
		}
		
		this.isItem = LegacySheetIndex_Engine0to1.items.containsKey(sheet + "@@@" + index);
		this.isBlock = LegacySheetIndex_Engine0to1.blocks.containsKey(sheet + "@@@" + index);
	}
	
	@Override
	public String getSheet()
	{
		return this.sheet;
	}
	
	@Override
	public String getIndex()
	{
		return this.index;
	}
}
