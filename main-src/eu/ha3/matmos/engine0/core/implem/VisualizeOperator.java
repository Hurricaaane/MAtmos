package eu.ha3.matmos.engine0.core.implem;

import java.util.HashMap;
import java.util.Map;

/*
--filenotes-placeholder
*/

public class VisualizeOperator
{
	private static Map<Operator, String> symbols = new HashMap<Operator, String>();
	static
	{
		symbols.put(Operator.NOT_EQUAL, "!=");
		symbols.put(Operator.EQUAL, "==");
		symbols.put(Operator.GREATER, ">");
		symbols.put(Operator.GREATER_OR_EQUAL, ">=");
		symbols.put(Operator.LESSER, "<");
		symbols.put(Operator.LESSER_OR_EQUAL, "<=");
		symbols.put(Operator.IN_LIST, "in");
		symbols.put(Operator.NOT_IN_LIST, "!in");
		symbols.put(Operator.ALWAYS_FALSE, "><");
	}
	
	public static String get(Operator op)
	{
		return VisualizeOperator.symbols.get(op);
	}
}
