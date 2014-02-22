package eu.ha3.matmos.expansions.agents;

import eu.ha3.matmos.engine0.core.implem.Knowledge;
import eu.ha3.matmos.expansions.ExpansionIdentity;

/*
--filenotes-placeholder
*/

public interface LoadingAgent
{
	/**
	 * Load whatever serialized data into the knowledge. It should be assumed
	 * the knowledge is empty before this is called, however it may not
	 * necessarily be the case.
	 * 
	 * @param identity
	 * @param knowledge
	 * @return
	 */
	public boolean load(ExpansionIdentity identity, Knowledge knowledge);
}
