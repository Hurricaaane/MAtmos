package eu.ha3.matmos.engine0.requirem;

import java.util.Map.Entry;

import eu.ha3.matmos.engine0.core.implem.Condition;
import eu.ha3.matmos.engine0.core.implem.Dynamic;
import eu.ha3.matmos.engine0.core.implem.Knowledge;

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

public class RequiremForAKnowledge extends FlatRequirements
{
	public RequiremForAKnowledge(Knowledge knowledge)
	{
		super();
		
		for (String conditionName : knowledge.getConditionsKeySet())
		{
			Condition condition = knowledge.getCondition(conditionName);
			if (!condition.isDynamic())
			{
				String sheet = condition.getSheet();
				ensureSheetExists(sheet);
				
				this.sheets.get(sheet).add(condition.getKey());
			}
		}
		
		for (String dynamicName : knowledge.getDynamicsKeySet())
		{
			Dynamic dynamic = knowledge.getDynamic(dynamicName);
			
			for (Entry<String, String> entry : dynamic.getEntries())
			{
				String sheet = entry.getKey();
				ensureSheetExists(sheet);
				
				this.sheets.get(sheet).add(entry.getValue());
			}
		}
		
	}
}
