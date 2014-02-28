package eu.ha3.matmos.jsonformat.serializable;

import java.util.LinkedHashMap;
import java.util.Map;

/*
--filenotes-placeholder
*/

public class SerialRoot
{
	public Map<String, SerialDynamic> dynamic = new LinkedHashMap<String, SerialDynamic>();
	public Map<String, SerialEvent> event = new LinkedHashMap<String, SerialEvent>();
	public Map<String, SerialList> list = new LinkedHashMap<String, SerialList>();
	public Map<String, SerialCondition> condition = new LinkedHashMap<String, SerialCondition>();
	public Map<String, SerialSet> set = new LinkedHashMap<String, SerialSet>();
	public Map<String, SerialMachine> machine = new LinkedHashMap<String, SerialMachine>();
}
