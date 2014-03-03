package eu.ha3.matmos.editor;

import java.io.File;

import eu.ha3.matmos.engine.core.implem.abstractions.ProviderCollection;
import eu.ha3.matmos.engine.core.interfaces.Data;

/*
--filenotes-placeholder
*/

public interface PluggableIntoMinecraft
{
	public boolean isReadOnly();
	
	public File getFileIfAvailable();
	
	public File getWorkingDirectoryIfAvailable();
	
	public ProviderCollection getProviders();
	
	public Data getData();
	
	public void pushJason(String jason);
	
	public void overrideMachine(String machineName, boolean overrideOnStatus);
	
	public void liftOverrides();
	
	public void reloadFromDisk();
	
	public void unplugged();
	
	public void addUnpluggedListener(UnpluggedListener listener);
	
	public void removeUnpluggedListener(UnpluggedListener listener);
	
	public void onEditorClosed();
}
