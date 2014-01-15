package eu.ha3.matmos.engine0.core.implem;

/* x-placeholder */

public class TimedEvent extends Component
{
	Machine machine;
	
	public String event;
	
	public float volMod;
	public float pitchMod;
	
	public float delayMin;
	public float delayMax;
	
	public float delayStart;
	
	public long nextPlayTime;
	
	TimedEvent(Machine machineIn)
	{
		//event = eventIn;
		
		this.event = "";
		
		this.machine = machineIn;
		this.volMod = 1F;
		this.pitchMod = 1F;
		
		this.delayMin = 10F;
		this.delayMax = 10F;
		
		this.delayStart = 0F;
		
	}
	
	void setMachine(Machine machineIn)
	{
		this.machine = machineIn;
		
	}
	
	public void routine()
	{
		if (this.machine.knowledge.getTimeMillis() < this.nextPlayTime)
			return;
		
		if (this.machine.knowledge.getEventsKeySet().contains(this.event))
		{
			this.machine.knowledge.getEvent(this.event).playSound(this.volMod, this.pitchMod);
		}
		
		if (this.delayMin == this.delayMax && this.delayMin > 0)
		{
			while (this.nextPlayTime < this.machine.knowledge.getTimeMillis())
			{
				this.nextPlayTime = this.nextPlayTime + (long) (this.delayMin * 1000);
				
			}
			
		}
		else
		{
			this.nextPlayTime =
				this.machine.knowledge.getTimeMillis()
					+ (long) ((this.delayMin + this.machine.knowledge.getRNG().nextFloat()
						* (this.delayMax - this.delayMin)) * 1000);
		}
		
	}
	
	public void restart()
	{
		if (this.delayStart == 0)
		{
			this.nextPlayTime =
				this.machine.knowledge.getTimeMillis()
					+ (long) (this.machine.knowledge.getRNG().nextFloat() * this.delayMax * 1000);
		}
		else
		{
			this.nextPlayTime = this.machine.knowledge.getTimeMillis() + (long) (this.delayStart * 1000);
		}
		
	}
}
