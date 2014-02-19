package eu.ha3.matmos.expansions;

/*
--filenotes-placeholder
*/

public interface Stable
{
	public boolean isActivated();
	
	public void activate();
	
	public void deactivate();
	
	public void dispose();
	
	public void interrupt();
}
