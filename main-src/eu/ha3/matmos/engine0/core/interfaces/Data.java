package eu.ha3.matmos.engine0.core.interfaces;

import java.util.Set;

/* x-placeholder */

public interface Data
{
	public abstract void flagUpdate();
	
	public abstract int getVersion();
	
	public abstract Set<String> getSheetNames();
	
	public abstract Sheet getSheet(String name);
	
	public abstract void setSheet(String name, Sheet sheet);
}