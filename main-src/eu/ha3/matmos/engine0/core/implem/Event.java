package eu.ha3.matmos.engine0.core.implem;

import java.util.ArrayList;
import java.util.Iterator;

/* x-placeholder */

public class Event extends Component
{
	private Knowledge knowledge;
	
	public ArrayList<String> paths;
	
	public float volMin;
	public float volMax;
	public float pitchMin;
	public float pitchMax;
	
	public int metaSound;
	
	public Event(Knowledge knowledgeIn)
	{
		this.paths = new ArrayList<String>();
		this.knowledge = knowledgeIn;
		
		this.volMin = 1F;
		this.volMax = 1F;
		this.pitchMin = 1F;
		this.pitchMax = 1F;
		
		this.metaSound = 0;
	}
	
	void setKnowledge(Knowledge knowledgeIn)
	{
		this.knowledge = knowledgeIn;
	}
	
	public void cacheSounds()
	{
		for (Iterator<String> iter = this.paths.iterator(); iter.hasNext();)
		{
			this.knowledge.getSoundManager().cacheSound(iter.next());
		}
	}
	
	public void playSound(float volMod, float pitchMod)
	{
		if (this.paths.isEmpty())
			return;
		
		float volume = this.volMax - this.volMin;
		float pitch = this.pitchMax - this.pitchMin;
		volume = this.volMin + (volume > 0 ? this.knowledge.getRNG().nextFloat() * volume : 0);
		pitch = this.pitchMin + (pitch > 0 ? this.knowledge.getRNG().nextFloat() * pitch : 0);
		
		String path = this.paths.get(this.knowledge.getRNG().nextInt(this.paths.size()));
		
		volume = volume * volMod;
		pitch = pitch * pitchMod;
		
		this.knowledge.getSoundManager().playSound(path, volume, pitch, this.metaSound);
	}
}
