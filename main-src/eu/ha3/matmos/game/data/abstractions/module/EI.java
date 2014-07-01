package eu.ha3.matmos.game.data.abstractions.module;

/*
--filenotes-placeholder
*/

public class EI
{
	private String name;
	private String desc;
	
	public EI(String name, String description)
	{
		this.name = name;
		this.desc = description;
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public String getDescription()
	{
		return this.desc;
	}
}
