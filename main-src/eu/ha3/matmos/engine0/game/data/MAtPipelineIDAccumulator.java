package eu.ha3.matmos.engine0.game.data;

import java.util.HashMap;
import java.util.Map;

import eu.ha3.matmos.engine0.core.interfaces.Data;
import eu.ha3.matmos.engine0.core.interfaces.Sheet;
import eu.ha3.matmos.engine0.game.data.abstractions.scanner.MAtScanCoordsPipeline;
import eu.ha3.matmos.v170helper.Version170Helper;

/* x-placeholder */

public class MAtPipelineIDAccumulator extends MAtScanCoordsPipeline
{
	private Map<String, Integer> tempnormal;
	private int count;
	
	private String normalName;
	private String proportionnalName;
	
	private int proportionnalTotal;
	
	public MAtPipelineIDAccumulator(
		Data dataIn, String normalNameIn, String proportionnalNameIn, int proportionnalTotalIn)
	{
		super(dataIn);
		this.tempnormal = new HashMap<String, Integer>(0);
		
		this.normalName = normalNameIn;
		this.proportionnalName = proportionnalNameIn;
		this.proportionnalTotal = proportionnalTotalIn;
	}
	
	@Override
	public void doBegin()
	{
		this.count = 0;
		for (String key : this.tempnormal.keySet())
		{
			this.tempnormal.put(key, 0);
		}
	}
	
	@Override
	public void doInput(int x, int y, int z)
	{
		String blockName = Version170Helper.getNameAt(x, y, z, "");
		
		int n = this.tempnormal.containsKey(blockName) ? this.tempnormal.get(blockName) : 0;
		this.tempnormal.put(blockName, n + 1);
		
		this.count++;
	}
	
	@Override
	public void doFinish()
	{
		Sheet normal = data().getSheet(this.normalName);
		Sheet proportionnal = this.proportionnalName != null ? data().getSheet(this.proportionnalName) : null;
		
		for (String key : this.tempnormal.keySet())
		{
			int result = this.tempnormal.get(key);
			
			normal.set(key, Integer.toString(result));
			
			if (this.proportionnalName != null)
			{
				proportionnal.set(key, Integer.toString((int) (this.proportionnalTotal * result / (float) this.count)));
			}
		}
	}
}
