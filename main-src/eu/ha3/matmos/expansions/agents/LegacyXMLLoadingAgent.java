package eu.ha3.matmos.expansions.agents;

import eu.ha3.matmos.engine.core.implem.Knowledge;
import eu.ha3.matmos.expansions.ExpansionIdentity;
import eu.ha3.matmos.jsonformat.serializable.expansion.SerialRoot;
import eu.ha3.matmos.tools.Jason;
import eu.ha3.matmos.tools.JasonExpansions_Engine1Deserializer2000;
import eu.ha3.matmos.tools.LegacyXMLExpansions_Engine1;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/*
--filenotes-placeholder
*/

public class LegacyXMLLoadingAgent implements LoadingAgent
{
	private final File jsonOutput;
	
	public LegacyXMLLoadingAgent()
	{
		this(null);
	}
	
	public LegacyXMLLoadingAgent(File jsonOutput)
	{
		this.jsonOutput = jsonOutput;
	}
	
	@Override
	public boolean load(ExpansionIdentity identity, Knowledge knowledge)
	{
		try
		{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = dbf.newDocumentBuilder();
			
			Document document = documentBuilder.parse(identity.getPack().getInputStream(identity.getLocation()));
			
			SerialRoot root = new LegacyXMLExpansions_Engine1().loadXMLtoSerial(document);
			try
			{
				if (!this.jsonOutput.exists())
				{
					this.jsonOutput.createNewFile();
				}
				
				FileWriter write = new FileWriter(this.jsonOutput);
				write.append(Jason.toJsonPretty(root));
				write.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			new JasonExpansions_Engine1Deserializer2000().loadSerial(root, identity, knowledge);
			
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
}
