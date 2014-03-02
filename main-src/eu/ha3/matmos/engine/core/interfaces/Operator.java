package eu.ha3.matmos.engine.core.interfaces;

import java.util.HashMap;
import java.util.Map;

/* x-placeholder */

public enum Operator
{
		ALWAYS_FALSE("ALWAYS_FALSE", "><"),
		ALWAYS_TRUE("ALWAYS_TRUE", "<>"),
		EQUAL("EQUAL", "=="),
		NOT_EQUAL("NOT_EQUAL", "!="),
		GREATER("GREATER", ">"),
		LESSER("LESSER", "<"),
		GREATER_OR_EQUAL("GREATER_OR_EQUAL", ">="),
		LESSER_OR_EQUAL("LESSER_OR_EQUAL", "<="),
		IN_LIST("IN_LIST", "in"),
		NOT_IN_LIST("NOT_IN_LIST", "!in");
	
	private static final Map<String, Operator> fromSerializedForm = new HashMap<String, Operator>();
	private static final Map<String, Operator> fromSymbol = new HashMap<String, Operator>();
	
	private final String symbol;
	private final String serializedForm;
	
	private Operator(String serializedForm, String symbol)
	{
		this.serializedForm = serializedForm;
		this.symbol = symbol;
	}
	
	static
	{
		for (Operator op : Operator.values())
		{
			fromSerializedForm.put(op.getSerializedForm(), op);
			fromSymbol.put(op.getSymbol(), op);
		}
	}
	
	/**
	 * Returns the same result as getSerializedForm().
	 */
	@Override
	public String toString()
	{
		return getSerializedForm();
	}
	
	public String getSerializedForm()
	{
		return this.serializedForm;
	}
	
	public String getSymbol()
	{
		return this.symbol;
	}
	
	/**
	 * Returns ALWAYS_FALSE in case the serialized form doesn't exist or is
	 * null.
	 * 
	 * @param serializedForm
	 * @return
	 */
	public static Operator fromSerializedForm(String serializedForm)
	{
		if (serializedForm == null || !fromSerializedForm.containsKey(serializedForm))
			return Operator.ALWAYS_FALSE;
		
		return fromSerializedForm.get(serializedForm);
	}
	
	/**
	 * Returns ALWAYS_FALSE in case the symbol doesn't exist or is null.
	 * 
	 * @param symbol
	 * @return
	 */
	public static Operator fromSymbol(String symbol)
	{
		if (symbol == null || !fromSymbol.containsKey(symbol))
			return Operator.ALWAYS_FALSE;
		
		return fromSymbol.get(symbol);
	}
}
