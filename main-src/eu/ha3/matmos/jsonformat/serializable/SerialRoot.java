package eu.ha3.matmos.jsonformat.serializable;

import java.util.Map;
import java.util.TreeMap;

/*
--filenotes-placeholder
*/

public class SerialRoot
{
	public Map<String, SerialEvent> event = new TreeMap<String, SerialEvent>();
	public Map<String, SerialList> list = new TreeMap<String, SerialList>();
	public Map<String, SerialDynamic> dynamic = new TreeMap<String, SerialDynamic>();
	public Map<String, SerialCondition> condition = new TreeMap<String, SerialCondition>();
	public Map<String, SerialSet> set = new TreeMap<String, SerialSet>();
	public Map<String, SerialMachine> machine = new TreeMap<String, SerialMachine>();
}
