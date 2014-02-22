package eu.ha3.matmos.engine0tools;

import eu.ha3.matmos.engine0.core.implem.Knowledge;
import eu.ha3.matmos.expansions.ExpansionIdentity;

/*
--filenotes-placeholder
*/

public class JasonExpansions_Engine1
{
	public static final String GENERIC_ENTRIES = "entries";
	public static final String ROOT_DYNAMIC = "dynamic";
	public static final String ROOT_LIST = "list";
	public static final String ROOT_SET = "set";
	public static final String ROOT_EVENT = "event";
	public static final String ROOT_MACHINE = "machine";
	
	public JasonExpansions_Engine1()
	{
	}
	
	public boolean parseJson(String jasonString, ExpansionIdentity identity, Knowledge knowledge)
	{
		try
		{
			parseJSONUnsafe(jasonString, identity, knowledge);
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
	public void parseJSONUnsafe(String jasonString, ExpansionIdentity identity, Knowledge knowledge)
	{
	}
}
