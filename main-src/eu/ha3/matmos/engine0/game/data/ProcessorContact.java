package eu.ha3.matmos.engine0.game.data;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.Minecraft;
import eu.ha3.matmos.engine0.core.interfaces.Data;
import eu.ha3.matmos.engine0.game.data.abstractions.processor.ProcessorModel;
import eu.ha3.matmos.v170helper.Version170Helper;

/* x-placeholder */

public class ProcessorContact extends ProcessorModel
{
	private Map<String, Integer> tempnormal;
	
	public ProcessorContact(Data dataIn, String name)
	{
		super(dataIn, name, name + MAtDataGatherer.DELTA_SUFFIX);
		this.tempnormal = new HashMap<String, Integer>();
	}
	
	private void emptyContact()
	{
		for (String key : this.tempnormal.keySet())
		{
			this.tempnormal.put(key, 0);
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
				this.tempnormal.put(blockName, this.tempnormal.containsKey(blockName)
					? this.tempnormal.get(blockName) + 1 : 1);
			}
		}
		
		for (String key : this.tempnormal.keySet())
		{
			setValue(key, Integer.toString(this.tempnormal.get(key)));
		}
		
	}
	
}
