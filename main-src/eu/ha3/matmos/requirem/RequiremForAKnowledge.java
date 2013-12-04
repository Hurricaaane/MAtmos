package eu.ha3.matmos.requirem;

import eu.ha3.matmos.engine.implem.Condition;
import eu.ha3.matmos.engine.implem.Dynamic;
import eu.ha3.matmos.engine.implem.Knowledge;

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
			int size = dynamic.getSheets().size();
			
			for (int i = 0; i < size; i++)
			{
				String sheet = dynamic.getSheet(i);
				ensureSheetExists(sheet);
				
				this.sheets.get(sheet).add(dynamic.getKey(i));
			}
		}
		
	}
}
