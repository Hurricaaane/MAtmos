package eu.ha3.matmos.engine0.core.implem;

import eu.ha3.matmos.engine0.core.implem.abstractions.MultistateComponent;
import eu.ha3.matmos.engine0.core.interfaces.Provider;
import eu.ha3.matmos.engine0.core.interfaces.ReferenceTime;
import eu.ha3.matmos.engine0.core.interfaces.Simulated;
import eu.ha3.matmos.engine0.core.interfaces.SoundRelay;

/* x-placeholder */

public class StreamInformation extends MultistateComponent implements Simulated
{
	
	private String path = "";
	private float volume = 1f;
	private float pitch = 1f;
	private float delayBeforeFadeIn = 0f;
	private float delayBeforeFadeOut = 0f;
	private float fadeInTime = 0f;
	private float fadeOutTime = 0f;
	private boolean isLooping = true;
	private boolean usesPause = false;
	
	//
	
	private final String machineName;
	private final Provider<Machine> provider;
	private final ReferenceTime time;
	private final SoundRelay relay;
	
	private boolean initialized;
	private int token;
	
	private boolean isPlaying;
	private long startTime;
	private long stopTime;
	
	public StreamInformation(String machineName, Provider<Machine> provider, ReferenceTime time, SoundRelay relay)
	{
		super("_STREAM:" + machineName);
		
		this.machineName = machineName;
		this.provider = provider;
		this.time = time;
		this.relay = relay;
		
		this.token = -1;
	}
	
	@Override
	public void evaluate()
	{
		if (!this.provider.exists(this.machineName))
			return;
		
		boolean active = this.provider.get(this.machineName).isActive();
		if (active != this.isActive)
		{
			this.isActive = active;
			incrementVersion();
			if (active)
			{
				signalPlayable();
			}
			else
			{
				signalStoppable();
			}
		}
	}
	
	private void signalPlayable()
	{
		this.startTime = this.time.getMilliseconds() + (long) (this.delayBeforeFadeIn * 1000);
	}
	
	private void signalStoppable()
	{
		this.stopTime = this.time.getMilliseconds() + (long) (this.delayBeforeFadeOut * 1000);
	}
	
	@Override
	public void simulate()
	{
		if (!this.isLooping && this.usesPause)
			return; // FIXME: A non-looping sound cannot use the pause scheme. 
			
		if (this.isActive && !this.isPlaying)
		{
			if (this.time.getMilliseconds() > this.startTime)
			{
				this.isPlaying = true;
				
				if (!this.initialized)
				{
					this.token = this.relay.getNewStreamingToken();
					
					if (this.relay.setupStreamingToken(
						this.token, this.path, this.volume, this.pitch, this.isLooping, this.usesPause))
					{
						this.initialized = true;
						this.relay.startStreaming(this.token, this.fadeInTime);
					}
				}
				else
				{
					this.relay.startStreaming(this.token, this.fadeInTime);
				}
			}
		}
		else if (!this.isActive && this.isPlaying)
		{
			if (this.time.getMilliseconds() > this.stopTime)
			{
				this.isPlaying = false;
				this.relay.stopStreaming(this.token, this.fadeOutTime);
			}
		}
	}
}
