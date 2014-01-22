package eu.ha3.matmos.engine0.debug;

import eu.ha3.matmos.engine0.core.interfaces.Data;
import eu.ha3.matmos.engine0.core.interfaces.Sheet;

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
