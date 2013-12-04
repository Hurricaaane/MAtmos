package eu.ha3.matmos.engine0.requirem;

import java.util.Map.Entry;

import eu.ha3.matmos.engine0.core.implem.Condition;
import eu.ha3.matmos.engine0.core.implem.Dynamic;
import eu.ha3.matmos.engine0.core.implem.Knowledge;

/* x-placeholder */

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
