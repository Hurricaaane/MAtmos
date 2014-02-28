package eu.ha3.matmos.engine0.core.implem;

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
	
	private final String symbol;
	private final String serializedForm;
	
	private Operator(String serializedForm, String symbol)
	{
		this.serializedForm = serializedForm;
		this.symbol = symbol;
	}
	
	@Override
	public String toString()
	{
		return this.serializedForm;
	}
	
	public String getSymbol()
	{
		return this.symbol;
	}
}
