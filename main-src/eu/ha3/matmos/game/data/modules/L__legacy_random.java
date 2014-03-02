package eu.ha3.matmos.game.data.modules;

import java.util.Random;

import eu.ha3.matmos.engine.core.interfaces.Data;
import eu.ha3.matmos.game.data.abstractions.module.Module;
import eu.ha3.matmos.game.data.abstractions.module.ModuleProcessor;

/*
--filenotes-placeholder
*/

public class L__legacy_random extends ModuleProcessor implements Module
{
	private final Random random = new Random();
	
	public L__legacy_random(Data data)
	{
		super(data, "legacy_random");
	}
	
	@Override
	protected void doProcess()
	{
		setValue("dice_a", 1 + this.random.nextInt(100));
		setValue("dice_b", 1 + this.random.nextInt(100));
		setValue("dice_c", 1 + this.random.nextInt(100));
		setValue("dice_d", 1 + this.random.nextInt(100));
		setValue("dice_e", 1 + this.random.nextInt(100));
		setValue("dice_f", 1 + this.random.nextInt(100));
	}
}