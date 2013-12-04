package eu.ha3.matmos.engine0.game.data;

import net.minecraft.client.Minecraft;
import eu.ha3.matmos.engine0.core.implem.GenericSheet;
import eu.ha3.matmos.engine0.core.implem.StringData;
import eu.ha3.matmos.engine0.core.interfaces.Sheet;
import eu.ha3.matmos.engine0.game.system.MAtMod;
import eu.ha3.matmos.v170helper.Version170Helper;

/* x-placeholder */

public class MAtProcessorContact extends MAtProcessorModel
{
	private Sheet<Integer> tempnormal;
	
	public MAtProcessorContact(MAtMod modIn, StringData dataIn, String normalNameIn, String deltaNameIn)
	{
		super(modIn, dataIn, normalNameIn, deltaNameIn);
		this.tempnormal = new GenericSheet<Integer>(0);
	}
	
	private void emptyContact()
	{
		for (String key : this.tempnormal.keySet())
		{
			this.tempnormal.set(key, 0);
		}
	}
	
	@Override
	protected void doProcess()
	{
		Minecraft mc = Minecraft.getMinecraft();
		int x = (int) Math.floor(mc.thePlayer.posX);
		int y = (int) Math.floor(mc.thePlayer.posY) - 1;
		int z = (int) Math.floor(mc.thePlayer.posZ);
		int worldHeight = mc.theWorld.getHeight();
		
		int nx;
		int ny;
		int nz;
		
		emptyContact();
		
		for (int k = 0; k < 12; k++)
		{
			ny = y + (k > 7 ? k - 9 : k % 2);
			if (ny >= 0 && ny < worldHeight)
			{
				nx = x + (k < 4 ? k < 2 ? -1 : 1 : 0);
				nz = z + (k > 3 && k < 8 ? k < 6 ? -1 : 1 : 0);
				
				String blockName = Version170Helper.getNameAt(nx, ny, nz, "");
				this.tempnormal.set(blockName, this.tempnormal.get(blockName) + 1);
			}
		}
		
		for (String key : this.tempnormal.keySet())
		{
			setValue(key, Integer.toString(this.tempnormal.get(key)));
		}
		
	}
	
}
