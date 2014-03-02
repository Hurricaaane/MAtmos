package eu.ha3.matmos.expansions.agents;

import eu.ha3.matmos.engine.core.implem.Knowledge;
import eu.ha3.matmos.expansions.ExpansionIdentity;
import eu.ha3.matmos.tools.JasonExpansions_Engine1Deserializer2000;

/*
--filenotes-placeholder
*/

public class RawJasonLoadingAgent implements LoadingAgent
{
	private final String jasonString;
	
	public RawJasonLoadingAgent(String jasonString)
	{
		this.jasonString = jasonString;
	}
	
	@Override
	public boolean load(ExpansionIdentity identity, Knowledge knowledge)
	{
		try
		{
			return new JasonExpansions_Engine1Deserializer2000().loadJson(this.jasonString, identity, knowledge);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
}
