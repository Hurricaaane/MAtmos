package eu.ha3.matmos.editor;


/*
--filenotes-placeholder
*/

public enum KnowledgeItemType
{
	CONDITION("Condition"), SET("Set"), MACHINE("Machine"), LIST("List"), DYNAMIC("Dynamic"), EVENT("Event");
	
	private String name;
	
	private KnowledgeItemType(String name)
	{
		this.name = name;
	}
	
	@Override
	public String toString()
	{
		return this.name;
	}
}
