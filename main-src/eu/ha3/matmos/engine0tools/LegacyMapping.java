package eu.ha3.matmos.engine0tools;

import java.util.AbstractMap;

/*
--filenotes-placeholder
*/

@SuppressWarnings("serial")
public class LegacyMapping extends AbstractMap.SimpleEntry<String, String>
{
	public LegacyMapping(String sheet, String index)
	{
		super(sheet, index);
	}
}