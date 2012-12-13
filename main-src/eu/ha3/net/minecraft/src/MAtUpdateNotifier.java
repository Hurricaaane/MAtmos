package net.minecraft.src;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

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

public class MAtUpdateNotifier extends Thread// implements Ha3Personalizable
{
	private MAtMod mod;
	
	private int lastFound = MAtMod.VERSION;
	
	private int displayCount = 3;
	private int displayRemaining = 0;
	private boolean enabled = true;
	
	MAtUpdateNotifier(MAtMod mAtmosHaddon)
	{
		this.mod = mAtmosHaddon;
	}
	
	public void attempt()
	{
		if (!this.enabled)
			return;
		
		start();
		
	}
	
	@Override
	public void run()
	{
		try
		{
			URL url = new URL("http://ha3extra.googlecode.com/svn/trunk/matmos/version.xml");
			
			InputStream contents = url.openStream();
			
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(contents);
			
			XPathFactory xpf = XPathFactory.newInstance();
			XPath xp = xpf.newXPath();
			
			NodeList nl = doc.getElementsByTagName("release");
			
			int maxvn = 0;
			for (int i = 0; i < nl.getLength(); i++)
			{
				Node release = nl.item(i);
				String versionnumber = xp.evaluate("./version", release);
				if (versionnumber != null)
				{
					int vn = Integer.parseInt(versionnumber);
					if (vn > maxvn)
					{
						maxvn = vn;
					}
					
				}
				
			}
			MAtMod.LOGGER.info("Update version found: " + maxvn);
			
			try
			{
				Thread.sleep(10000);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
				return;
				
			}
			
			if (maxvn > MAtMod.VERSION)
			{
				boolean needsSave = false;
				if (maxvn != this.lastFound)
				{
					this.lastFound = maxvn;
					this.displayRemaining = this.displayCount;
					
					needsSave = true;
					this.mod.getConfig().setProperty("update_found.version", this.lastFound);
					this.mod.getConfig().setProperty("update_found.display.remaining.value", this.displayRemaining);
					
				}
				
				if (this.displayRemaining > 0)
				{
					this.displayRemaining = this.displayRemaining - 1;
					this.mod.getConfig().setProperty("update_found.display.remaining.value", this.displayRemaining);
					
					int vc = maxvn - MAtMod.VERSION;
					this.mod.printChat(
						Ha3Utility.COLOR_GOLD, "A ", Ha3Utility.COLOR_WHITE, "r" + maxvn, Ha3Utility.COLOR_GOLD,
						" update is available (You're ", Ha3Utility.COLOR_WHITE, vc, Ha3Utility.COLOR_GOLD, " version"
							+ (vc > 1 ? "s" : "") + " late).");
					
					if (this.displayRemaining > 0)
					{
						this.mod.printChat(
							Ha3Utility.COLOR_GRAY, "This message will display ", Ha3Utility.COLOR_WHITE,
							this.displayRemaining, Ha3Utility.COLOR_GRAY, " more time"
								+ (this.displayRemaining > 1 ? "s" : "") + ".");
					}
					else
					{
						this.mod.printChat(
							Ha3Utility.COLOR_GRAY, "You won't be notified anymore until a newer version.");
					}
					
					needsSave = true;
					
				}
				
				if (needsSave)
				{
					this.mod.saveConfig();
				}
				
			}
			
		}
		catch (XPathExpressionException e)
		{
			e.printStackTrace();
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
		}
		catch (ParserConfigurationException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (SAXException e)
		{
			e.printStackTrace();
		}
		finally
		{
			
		}
	}
	
	public void loadConfig(ConfigProperty configuration)
	{
		this.enabled = configuration.getBoolean("update_found.enabled");
		this.lastFound = configuration.getInteger("update_found.version");
		this.displayRemaining = configuration.getInteger("update_found.display.remaining.value");
		this.displayCount = configuration.getInteger("update_found.display.count.value");
		
	}
	
}
