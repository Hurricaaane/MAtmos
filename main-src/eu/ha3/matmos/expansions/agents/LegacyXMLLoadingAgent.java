package eu.ha3.matmos.expansions.agents;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import eu.ha3.matmos.engine.core.implem.Knowledge;
import eu.ha3.matmos.expansions.ExpansionIdentity;
import eu.ha3.matmos.tools.LegacyXMLExpansions_Engine1;

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
			
			return new LegacyXMLExpansions_Engine1().loadKnowledge_andConvertToJason(
				identity.getUniqueName(), knowledge, document, this.jsonOutput);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
}
