package net.minecraft.src;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import net.minecraft.client.Minecraft;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import eu.ha3.easy.TimeStatistic;
import eu.ha3.matmos.engine.Data;
import eu.ha3.matmos.engine.Event;
import eu.ha3.matmos.engine.Knowledge;
import eu.ha3.matmos.engine.MAtmosException;
import eu.ha3.matmos.engine.SoundRelay;
import eu.ha3.matmos.engine.UtilityLoader;
import eu.ha3.util.property.simple.ConfigProperty;

/*
            DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE 
                    Version 2, December 2004 

 Copyright (C) 2004 Sam Hocevar <sam@hocevar.net> 

 Everyone is permitted to copy and distribute verbatim or modified 
 copies of this license document, and changing it is allowed as long 
 as the name is changed. 

            DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE 
   TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION 

  0. You just DO WHAT THE FUCK YOU WANT TO. 
*/

public class MAtExpansion implements MAtCustomVolume
{
	private DocumentBuilder documentBuilder;
	private Document document;
	private XPath xpath;
	private Knowledge knowledge;
	
	private String userDefinedIdentifier;
	
	private String docName;
	private String docDescription;
	
	private boolean isReady;
	private MAtExpansionError error;
	
	private boolean hasStructure;
	
	private int dataFrequency;
	private int dataCyclic;
	
	private SoundRelay soundManager;
	
	private ConfigProperty myConfiguration;
	private String friendlyName;
	private boolean isBuilding;
	
	public MAtExpansion(String userDefinedIdentifier)
	{
		this.userDefinedIdentifier = userDefinedIdentifier;
		this.isReady = false;
		this.hasStructure = false;
		this.error = MAtExpansionError.NO_DOCUMENT;
		
		this.docName = userDefinedIdentifier;
		this.docDescription = "";
		
		this.knowledge = new Knowledge();
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		XPathFactory xpf = XPathFactory.newInstance();
		
		this.xpath = xpf.newXPath();
		
		this.dataFrequency = 1;
		this.dataCyclic = 0;
		
		this.myConfiguration = new ConfigProperty();
		this.myConfiguration.setProperty("volume", 1f);
		this.myConfiguration.setProperty("friendlyname", "");
		this.myConfiguration.commit();
		try
		{
			this.myConfiguration.setSource(new File(Minecraft.getMinecraftDir(), "matmos/expansions_r12_userconfig/"
				+ userDefinedIdentifier + ".cfg").getCanonicalPath());
			this.myConfiguration.load();
		}
		catch (IOException e1)
		{
			e1.printStackTrace();
		}
		this.friendlyName = this.myConfiguration.getString("friendlyname");
		if (this.friendlyName.equals(""))
		{
			this.friendlyName = userDefinedIdentifier;
		}
		
		try
		{
			this.documentBuilder = dbf.newDocumentBuilder();
			
		}
		catch (ParserConfigurationException e)
		{
			// FIXME: Unhandled recoverable error thrown as unrecoverable
			e.printStackTrace();
			throw new RuntimeException();
			
		}
		
	}
	
	public String getFriendlyName()
	{
		return this.friendlyName;
		
	}
	
	public void setSoundManager(SoundRelay soundManager)
	{
		this.knowledge.setSoundManager(soundManager);
		this.soundManager = soundManager;
		
		setVolume(this.myConfiguration.getFloat("volume"));
		
	}
	
	public void setData(Data data)
	{
		this.knowledge.setData(data);
		
	}
	
	public void inputStructure(InputStream stream)
	{
		System.out.println("inputting " + this.userDefinedIdentifier);
		
		this.hasStructure = false;
		try
		{
			this.document = this.documentBuilder.parse(stream);
			NodeList explist = this.document.getElementsByTagName("expansion");
			if (explist.getLength() == 1)
			{
				Node exp = explist.item(0);
				
				String name = this.xpath.evaluate("./name", exp);
				String desc = this.xpath.evaluate("./description", exp);
				String dataFreq = this.xpath.evaluate("./data", exp);
				
				if (name != null)
				{
					this.docName = name;
				}
				
				if (desc != null)
				{
					this.docDescription = desc;
				}
				
				if (dataFreq != null)
				{
					try
					{
						this.dataFrequency = Integer.parseInt(dataFreq);
						if (this.dataFrequency < 1)
						{
							this.dataFrequency = 1;
						}
						
						MAtMod.LOGGER.fine("Set " + this.userDefinedIdentifier + " frequency to " + this.dataFrequency);
						
					}
					catch (NumberFormatException e)
					{
						;
						
					}
					
				}
				
			}
			
			this.hasStructure = true;
			
		}
		catch (SAXException e)
		{
			this.error = MAtExpansionError.COULD_NOT_PARSE_XML;
			e.printStackTrace();
			
		}
		catch (IOException e)
		{
			this.error = MAtExpansionError.COULD_NOT_PARSE_XML;
			e.printStackTrace();
			
		}
		catch (XPathExpressionException e)
		{
			MAtMod.LOGGER.warning("Error with XPath on expansion " + this.userDefinedIdentifier);
			e.printStackTrace();
			
		}
		
	}
	
	public void buildKnowledge()
	{
		if (this.document == null)
			return;
		
		if (!this.hasStructure)
			return;
		
		try
		{
			this.knowledge.patchKnowledge();
			// loadKnowledge returns the validity of the knowledge
			this.isReady = UtilityLoader.getInstance().loadKnowledge(this.knowledge, this.document, false);
			
		}
		catch (MAtmosException e)
		{
			this.error = MAtExpansionError.COULD_NOT_MAKE_KNOWLEDGE;
			e.printStackTrace();
			
		}
		
	}
	
	public void soundRoutine()
	{
		if (this.isReady)
		{
			this.knowledge.soundRoutine();
			this.soundManager.routine();
		}
		
	}
	
	public void dataRoutine()
	{
		if (this.isReady)
		{
			if (this.dataFrequency > 1)
			{
				if (this.dataCyclic == 0)
				{
					this.knowledge.dataRoutine();
				}
				
				this.dataCyclic = (this.dataCyclic + 1) % this.dataFrequency;
				
			}
			else
			{
				this.knowledge.dataRoutine();
				
			}
			
		}
		
	}
	
	public void playSample()
	{
		if (!isRunning())
			return;
		
		Event event = this.knowledge.getEvent("__SAMPLE");
		if (event != null)
		{
			event.playSound(1f, 1f);
		}
		
	}
	
	public MAtExpansionError getError()
	{
		return this.error;
		
	}
	
	public String getUserDefinedName()
	{
		return this.userDefinedIdentifier;
		
	}
	
	public String getName()
	{
		return this.docName;
		
	}
	
	public String getDescription()
	{
		return this.docDescription;
		
	}
	
	public boolean isRunning()
	{
		return this.knowledge.isTurnedOn();
		
	}
	
	public void saveConfig()
	{
		if (this.myConfiguration.commit())
		{
			this.myConfiguration.save();
		}
		
	}
	
	public boolean isReady()
	{
		return this.isReady;
		
	}
	
	public boolean hasStructure()
	{
		return this.hasStructure;
		
	}
	
	public void turnOn()
	{
		if (isRunning())
			return;
		
		if (getVolume() <= 0f)
			return;
		
		if (this.isBuilding)
			return;
		
		if (!this.isReady && this.hasStructure)
		{
			this.isBuilding = true;
			
			TimeStatistic stat = new TimeStatistic(Locale.ENGLISH);
			buildKnowledge();
			
			MAtMod.LOGGER.info("Expansion " + getUserDefinedName() + " loaded (" + stat.getSecondsAsString(3) + "s).");
			this.isBuilding = false;
		}
		
		if (this.isReady)
		{
			this.knowledge.turnOn();
		}
		
	}
	
	public void turnOff()
	{
		if (!this.isReady || !isRunning())
			return;
		
		this.knowledge.turnOff();
		
	}
	
	@Override
	public float getVolume()
	{
		if (this.soundManager instanceof MAtCustomVolume)
			return ((MAtCustomVolume) this.soundManager).getVolume();
		
		return 1;
		
	}
	
	@Override
	public void setVolume(float volume)
	{
		if (this.soundManager instanceof MAtCustomVolume)
		{
			((MAtCustomVolume) this.soundManager).setVolume(volume);
			this.myConfiguration.setProperty("volume", getVolume());
		}
		
	}
	
	public String getDocumentStringForm()
	{
		if (this.document == null)
			return null;
		
		/*DOMImplementationLS domImplementation = (DOMImplementationLS) document
		.getImplementation();
		LSSerializer lsSerializer = domImplementation.createLSSerializer();
		return lsSerializer.writeToString(document);*/
		
		// http://stackoverflow.com/questions/1636792/domimplementationls-serialize-to-string-in-utf-8-in-java
		
		StringWriter output = new StringWriter();
		
		try
		{
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.transform(new DOMSource(this.document), new StreamResult(output));
		}
		catch (TransformerConfigurationException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (TransformerFactoryConfigurationError e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (TransformerException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return output.toString();
		
	}
	
	/*public void printKnowledge() // XXX Debugging function, remove me
	{
		try
		{
			System.out.println(this.knowledge.createXML());
		}
		catch (XMLStreamException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}*/
	
	public void clear()
	{
		turnOff();
		this.knowledge.patchKnowledge();
		this.isReady = false;
	}
	
}
