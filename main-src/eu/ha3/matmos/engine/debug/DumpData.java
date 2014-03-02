package eu.ha3.matmos.engine.debug;

import eu.ha3.matmos.engine.core.interfaces.Data;
import eu.ha3.matmos.engine.core.interfaces.Sheet;

/*
--filenotes-placeholder
*/

public class DumpData
{
	public static String dumpData(Data data)
	{
		StringBuilder s = new StringBuilder();
		for (String sheetName : data.getSheetNames())
		{
			Sheet sheet = data.getSheet(sheetName);
			
			s.append(sheetName + "\n");
			for (String index : sheet.keySet())
			{
				s.append("  " + index + ":" + sheet.get(index) + "\n");
			}
		}
		return s.toString();
	}
}
