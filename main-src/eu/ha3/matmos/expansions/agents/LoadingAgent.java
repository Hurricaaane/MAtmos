package eu.ha3.matmos.expansions.agents;

import eu.ha3.matmos.engine0.core.implem.Knowledge;
import eu.ha3.matmos.expansions.ExpansionIdentity;

/*
--filenotes-placeholder
*/

public interface LoadingAgent
{
	public boolean load(ExpansionIdentity identity, Knowledge knowledge);
}
