package eu.ha3.matmos.engine0.core.implem;

import eu.ha3.matmos.engine0.core.interfaces.SheetIndex;

/*
--filenotes-placeholder
*/

public class SheetEntry implements SheetIndex
{
	private final String sheet;
	private final String index;
	
	public SheetEntry(String sheet, String index)
	{
		this.sheet = sheet;
		this.index = index;
	}
	
	@Override
	public String getSheet()
	{
		return this.sheet;
	}
	
	@Override
	public String getIndex()
	{
		return this.index;
	}
}
