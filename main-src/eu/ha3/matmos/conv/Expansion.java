package eu.ha3.matmos.conv;

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

import net.sf.practicalxml.DomUtil;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import eu.ha3.easy.TimeStatistic;
import eu.ha3.matmos.engine.Event;
import eu.ha3.matmos.engine.Knowledge;
import eu.ha3.matmos.engine.MAtmosException;
import eu.ha3.matmos.engine.UtilityLoader;
import eu.ha3.matmos.engineinterfaces.Data;
import eu.ha3.matmos.engineinterfaces.SoundRelay;
import eu.ha3.matmos.experimental.Collation;
import eu.ha3.matmos.experimental.RequiremForAKnowledge;
import eu.ha3.matmos.experimental.Requirements;
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

public class Expansion implements CustomVolume
{
	private DocumentBuilder documentBuilder;
	private Document document;
	private Knowledge knowledge;
	
	private String userDefinedIdentifier;
	
	private String docName;
	private String docDescription;
	
	private boolean isReady;
	private ExpansionError error;
	
	private boolean hasStructure;
	
	private int dataFrequency;
	private int dataCyclic;
	
	private SoundRelay soundManager;
	
	private ConfigProperty myConfiguration;
	private String friendlyName;
	private boolean isBuilding;
	
	private Requirements requirements;
	private Collation collation;
	
	public Expansion(String userDefinedIdentifier, File configurationSource)
	{
		this.userDefinedIdentifier = userDefinedIdentifier;
		this.isReady = false;
		this.hasStructure = false;
		this.error = ExpansionError.NO_DOCUMENT;
		
		this.docName = userDefinedIdentifier;
		this.docDescription = "";
		
		this.knowledge = new Knowledge();
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		
		this.dataFrequency = 1;
		this.dataCyclic = 0;
		
		this.myConfiguration = new ConfigProperty();
		this.myConfiguration.setProperty("volume", 1f);
		this.myConfiguration.setProperty("friendlyname", "");
		this.myConfiguration.commit();
		try
		{
			this.myConfiguration.setSource(configurationSource.getCanonicalPath());
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
	
	private String eltString(String tagName, Element ele)
	{
		return textOf(DomUtil.getChild(ele, tagName));
	}
	
	private String textOf(Element ele)
	{
		if (ele == null || ele.getFirstChild() == null)
			return null;
		
		return ele.getFirstChild().getNodeValue();
	}
	
	public void inputStructure(InputStream stream)
	{
		this.hasStructure = false;
		try
		{
			this.document = this.documentBuilder.parse(stream);
			NodeList explist = this.document.getElementsByTagName("expansion");
			if (explist.getLength() == 1)
			{
				Element exp = (Element) explist.item(0);
				
				String name = eltString("name", exp);
				String desc = eltString("description", exp);
				String dataFreq = eltString("data", exp);
				
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
						
						MAtmosConvLogger.fine("Set "
							+ this.userDefinedIdentifier + " frequency to " + this.dataFrequency);
						
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
			this.error = ExpansionError.COULD_NOT_PARSE_XML;
			e.printStackTrace();
			
		}
		catch (IOException e)
		{
			this.error = ExpansionError.COULD_NOT_PARSE_XML;
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
			
			this.requirements = new RequiremForAKnowledge(this.knowledge);
			
		}
		catch (MAtmosException e)
		{
			this.error = ExpansionError.COULD_NOT_MAKE_KNOWLEDGE;
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
	
	public ExpansionError getError()
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
			this.knowledge.cacheSounds();
			
			MAtmosConvLogger.info("Expansion "
				+ getUserDefinedName() + " loaded (" + stat.getSecondsAsString(3) + "s).");
			this.isBuilding = false;
		}
		
		if (this.isReady)
		{
			this.knowledge.turnOn();
			this.collation.addRequirements(this.userDefinedIdentifier, this.requirements);
		}
		
	}
	
	public void turnOff()
	{
		if (!this.isReady || !isRunning())
			return;
		
		this.knowledge.turnOff();
		this.collation.removeRequirements(this.userDefinedIdentifier);
		
	}
	
	@Override
	public float getVolume()
	{
		if (this.soundManager instanceof CustomVolume)
			return ((CustomVolume) this.soundManager).getVolume();
		
		return 1;
		
	}
	
	@Override
	public void setVolume(float volume)
	{
		if (this.soundManager instanceof CustomVolume)
		{
			((CustomVolume) this.soundManager).setVolume(volume);
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
	
	public void clear()
	{
		turnOff();
		this.knowledge.patchKnowledge();
		this.collation.removeRequirements(this.userDefinedIdentifier);
		this.isReady = false;
	}
	
	public void setCollation(Collation collation)
	{
		this.collation = collation;
	}
	
}
