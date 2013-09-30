package eu.ha3.matmos.engine0.core.serializers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import eu.ha3.matmos.engine0.core.implem.Condition;
import eu.ha3.matmos.engine0.core.implem.ConditionSet;
import eu.ha3.matmos.engine0.core.implem.ConditionType;
import eu.ha3.matmos.engine0.core.implem.Descriptible;
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

public class JasonExpansions_Engine1_Serializer
{
	private final Object ENGINEVERSION = 1;
	
	private Map<ConditionType, String> symbols;
	
	public JasonExpansions_Engine1_Serializer()
	{
		this.symbols.put(ConditionType.NOT_EQUAL, "!=");
		this.symbols.put(ConditionType.EQUAL, "==");
		this.symbols.put(ConditionType.GREATER, ">");
		this.symbols.put(ConditionType.GREATER_OR_EQUAL, ">=");
		this.symbols.put(ConditionType.LESSER_, "<");
		this.symbols.put(ConditionType.LESSER_OR_EQUAL, "<=");
		this.symbols.put(ConditionType.IN_LIST, "in");
		this.symbols.put(ConditionType.NOT_IN_LIST, "!in");
		this.symbols.put(ConditionType.ALWAYS_FALSE, "><");
	}
	
	public String generateJSON(Knowledge knowledge)
	{
		Map<String, Object> toJsonify = new LinkedHashMap<String, Object>();
		
		toJsonify.put("type", "expansion");
		toJsonify.put("engineversion", this.ENGINEVERSION);
		
		toJsonify.put("contents", buildContents(knowledge));
		
		//
		
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String jason = gson.toJson(toJsonify);
		
		return jason;
	}
	
	private Object buildContents(Knowledge knowledge)
	{
		Map<String, Object> toJsonify = new LinkedHashMap<String, Object>();
		Map<String, Object> capsule;
		
		//
		
		capsule = new LinkedHashMap<String, Object>();
		for (String cap : prepareSet(knowledge.getDynamicsKeySet()))
		{
			capsule.put(cap, buildDynamic(knowledge.getDynamic(cap)));
		}
		toJsonify.put("dynamics", capsule);
		
		//
		
		capsule = new LinkedHashMap<String, Object>();
		for (String cap : prepareSet(knowledge.getDynamicsKeySet()))
		{
			capsule.put(cap, buildCondition(knowledge.getCondition(cap)));
		}
		toJsonify.put("conditions", capsule);
		
		//
		
		capsule = new LinkedHashMap<String, Object>();
		for (String cap : prepareSet(knowledge.getConditionSetsKeySet()))
		{
			capsule.put(cap, buildSet(knowledge.getConditionSet(cap)));
		}
		toJsonify.put("sets", capsule);
		
		//
		
		return toJsonify;
	}
	
	private Object buildSet(ConditionSet conditionSet)
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	private Object buildDynamic(Dynamic dynamic)
	{
		Map<String, Object> toJsonify = new LinkedHashMap<String, Object>();
		appendMeta(toJsonify, dynamic);
		
		List<String> couples = new ArrayList<String>();
		for (Entry<String, String> entry : dynamic.getEntries())
		{
			couples.add(entry.getKey());
			couples.add(entry.getValue());
		}
		toJsonify.put("couples", couples);
		
		return toJsonify;
	}
	
	private Object buildCondition(Condition condition)
	{
		Map<String, Object> toJsonify = new LinkedHashMap<String, Object>();
		appendMeta(toJsonify, condition);
		
		toJsonify.put("dynamic", condition.isDynamic());
		if (condition.isDynamic())
		{
			toJsonify.put("sheet", condition.getDynamic());
		}
		else
		{
			toJsonify.put("sheet", condition.getSheet());
			toJsonify.put("key", condition.getKey());
		}
		
		toJsonify.put("symbol", this.symbols.get(condition.getConditionType()));
		toJsonify.put("constant", condition.getConstant());
		
		return toJsonify;
	}
	
	private List<String> prepareSet(Set<String> set)
	{
		List<String> setAsList = new ArrayList<String>(set);
		Collections.sort(setAsList);
		
		return setAsList;
	}
	
	private void appendMeta(Map<String, Object> toJsonify, Descriptible descriptible)
	{
		if (!descriptible.description.equals(""))
		{
			toJsonify.put("_desc", descriptible.description);
		}
	}
}
