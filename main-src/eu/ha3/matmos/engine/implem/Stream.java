package eu.ha3.matmos.engine.implem;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

/* x-placeholder */

public class Stream extends Descriptible
{
	Machine machine;
	
	private int token;
	
	public String path = "";
	public float volume = 1f;
	public float pitch = 1f;
	public float delayBeforeFadeIn = 0f;
	public float delayBeforeFadeOut = 0f;
	public float fadeInTime = 0f;
	public float fadeOutTime = 0f;
	public boolean isLooping = true;
	public boolean isUsingPause = false;
	
	private boolean isTurnedOn;
	private boolean isPlaying;
	
	private long startTime;
	private long stopTime;
	
	private boolean firstCall;
	
	Stream(Machine machineIn)
	{
		this.machine = machineIn;
		
		this.firstCall = true;
		
		this.token = -1;
		
		this.startTime = 0;
		this.stopTime = 0;
		
	}
	
	void setMachine(Machine machineIn)
	{
		this.machine = machineIn;
		
	}
	
	public void signalPlayable()
	{
		if (this.isTurnedOn)
			return;
		
		this.startTime = this.machine.knowledge.getTimeMillis() + (long) (this.delayBeforeFadeIn * 1000);
		this.isTurnedOn = true;
		
	}
	
	public void signalStoppable()
	{
		if (!this.isTurnedOn)
			return;
		
		this.stopTime = this.machine.knowledge.getTimeMillis() + (long) (this.delayBeforeFadeOut * 1000);
		this.isTurnedOn = false;
		
	}
	
	public void clearToken()
	{
		if (this.firstCall)
			return;
		
		this.machine.knowledge.getSoundManager().eraseStreamingToken(this.token);
		this.isPlaying = false;
		
		this.token = -1;
		this.firstCall = true;
		
	}
	
	public void routine()
	{
		if (!this.isLooping && this.isUsingPause)
			return; // FIXME: A non-looping sound cannot use the pause scheme.
			
		if (this.isTurnedOn && !this.isPlaying)
		{
			if (this.machine.knowledge.getTimeMillis() > this.startTime)
			{
				this.isPlaying = true;
				
				if (this.firstCall)
				{
					this.token = this.machine.knowledge.getSoundManager().getNewStreamingToken();
					
					// FIXME: Blatent crash prevention: Find new implementation
					if (this.machine.knowledge.getSoundManager().setupStreamingToken(
						this.token, this.path, this.volume, this.pitch))
					{
						this.firstCall = false;
						this.machine.knowledge.getSoundManager().startStreaming(
							this.token, this.fadeInTime, this.isLooping ? 0 : 1);
						
					}
					
				}
				else
				{
					this.machine.knowledge.getSoundManager().startStreaming(
						this.token, this.fadeInTime, this.isLooping ? 0 : 1);
					
				}
				
			}
			
		}
		else if (!this.isTurnedOn && this.isPlaying)
		{
			if (this.machine.knowledge.getTimeMillis() > this.stopTime)
			{
				this.isPlaying = false;
				
				if (!this.isUsingPause)
				{
					this.machine.knowledge.getSoundManager().stopStreaming(this.token, this.fadeOutTime);
				}
				else
				{
					this.machine.knowledge.getSoundManager().pauseStreaming(this.token, this.fadeOutTime);
				}
				
			}
			
		}
		
	}
	
	@Override
	public String serialize(XMLEventWriter eventWriter) throws XMLStreamException
	{
		XMLEventFactory eventFactory = XMLEventFactory.newInstance();
		XMLEvent ret = eventFactory.createDTD("\n");
		XMLEvent tab = eventFactory.createDTD("\t");
		
		eventWriter.add(tab);
		eventWriter.add(eventFactory.createStartElement("", "", "stream"));
		eventWriter.add(ret);
		createNode(eventWriter, "path", this.path, 2);
		createNode(eventWriter, "volume", "" + this.volume, 2);
		createNode(eventWriter, "pitch", "" + this.pitch, 2);
		createNode(eventWriter, "fadeintime", "" + this.fadeInTime, 2);
		createNode(eventWriter, "fadeouttime", "" + this.fadeOutTime, 2);
		createNode(eventWriter, "delaybeforefadein", "" + this.delayBeforeFadeIn, 2);
		createNode(eventWriter, "delaybeforefadeout", "" + this.delayBeforeFadeOut, 2);
		createNode(eventWriter, "islooping", this.isLooping ? "1" : "0", 2);
		createNode(eventWriter, "isusingpause", this.isUsingPause ? "1" : "0", 2);
		eventWriter.add(tab);
		eventWriter.add(eventFactory.createEndElement("", "", "stream"));
		eventWriter.add(ret);
		
		return "";
	}
	
}
