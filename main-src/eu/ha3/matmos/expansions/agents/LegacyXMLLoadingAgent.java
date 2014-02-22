package eu.ha3.matmos.expansions.agents;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import eu.ha3.matmos.engine0.core.implem.Knowledge;
import eu.ha3.matmos.engine0tools.LegacyXMLExpansions_Engine1;
import eu.ha3.matmos.expansions.Expansion;

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
	public boolean load(Expansion expansion, Knowledge knowledge)
	{
		try
		{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = dbf.newDocumentBuilder();
			
			Document document =
				documentBuilder.parse(expansion
					.getIdentity().getPack().getInputStream(expansion.getIdentity().getLocation()));
			
			return new LegacyXMLExpansions_Engine1().loadKnowledge_andConvertToJason(
				expansion.getName(), knowledge, document, this.jsonOutput);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
}
