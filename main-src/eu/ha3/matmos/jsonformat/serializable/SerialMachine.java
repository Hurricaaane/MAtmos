package eu.ha3.matmos.jsonformat.serializable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/*
--filenotes-placeholder
*/

public class SerialMachine
{
	public Set<String> allow = new TreeSet<String>();
	public Set<String> restrict = new TreeSet<String>();
	
	public float fadein;
	public float fadeout;
	public float delay_fadein;
	public float delay_fadeout;
	
	public List<SerialMachineEvent> event = new ArrayList<SerialMachineEvent>();
	public SerialMachineStream stream = null;
}
