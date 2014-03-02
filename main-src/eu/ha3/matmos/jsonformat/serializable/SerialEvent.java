package eu.ha3.matmos.jsonformat.serializable;

import java.util.ArrayList;
import java.util.List;

/*
--filenotes-placeholder
*/

public class SerialEvent
{
	public float vol_min = 1f;
	public float vol_max = 1f;
	public float pitch_min = 1f;
	public float pitch_max = 1f;
	public int distance = 0;
	
	public List<String> path = new ArrayList<String>();
}
