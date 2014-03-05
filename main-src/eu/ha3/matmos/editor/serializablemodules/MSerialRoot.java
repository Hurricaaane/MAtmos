package eu.ha3.matmos.editor.serializablemodules;

import java.util.Map;
import java.util.TreeMap;

/*
--filenotes-placeholder
*/

public class MSerialRoot
{
	public Map<String, MSerialModule> module = new TreeMap<String, MSerialModule>();
	public Map<String, MSerialAgency> agency = new TreeMap<String, MSerialAgency>();
}
