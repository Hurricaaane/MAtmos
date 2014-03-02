package eu.ha3.matmos.editor;

import eu.ha3.matmos.engine.core.implem.abstractions.ProviderCollection;
import eu.ha3.matmos.engine.core.interfaces.Data;

/*
--filenotes-placeholder
*/

public interface PluggableIntoMinecraft
{
	public boolean isReadOnly();
	
	public ProviderCollection getProviders();
	
	public Data getData();
	
	public void pushJason(String jason);
	
	public void overrideMachine(String machineName, boolean overrideOnStatus);
	
	public void liftOverrides();
	
	public void reloadFromDisk();
}
