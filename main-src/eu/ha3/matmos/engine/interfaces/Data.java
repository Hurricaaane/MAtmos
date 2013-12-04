package eu.ha3.matmos.engine.interfaces;

import java.util.Set;

/* x-placeholder */

public interface Data
{
	public abstract void flagUpdate();
	
	public abstract int getVersion();
	
	public abstract Set<String> getSheetNames();
	
	public abstract Sheet<Integer> getSheet(String name);
	
	public abstract void setSheet(String name, Sheet<Integer> sheet);
}