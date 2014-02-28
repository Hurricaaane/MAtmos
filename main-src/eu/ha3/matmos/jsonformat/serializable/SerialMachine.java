package eu.ha3.matmos.jsonformat.serializable;

/*
--filenotes-placeholder
*/

public class SerialMachine
{
	public float fadein;
	public float fadeout;
	public float delay_fadein;
	public float delay_fadeout;
	
	public SerialMachineEvent[] event = new SerialMachineEvent[0];
	public SerialMachineStream stream = null;
	
	public String[] allow = new String[0];
	public String[] restrict = new String[0];
}
