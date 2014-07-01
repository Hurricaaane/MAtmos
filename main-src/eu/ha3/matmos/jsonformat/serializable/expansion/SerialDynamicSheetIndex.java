package eu.ha3.matmos.jsonformat.serializable.expansion;

/*
--filenotes-placeholder
*/

public class SerialDynamicSheetIndex
{
	public SerialDynamicSheetIndex(String sheet, String index)
	{
		this.sheet = sheet;
		this.index = index;
	}
	
	public SerialDynamicSheetIndex()
	{
	}
	
	public String sheet = "";
	public String index = "";
}
