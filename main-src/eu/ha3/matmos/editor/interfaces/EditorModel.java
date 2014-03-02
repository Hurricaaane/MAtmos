package eu.ha3.matmos.editor.interfaces;

import java.io.File;

import eu.ha3.matmos.engine.core.implem.abstractions.ProviderCollection;

/*
--filenotes-placeholder
*/

public interface EditorModel
{
	public void trySetAndLoadFile(File file);
	
	public void minecraftReloadFromDisk();
	
	public boolean hasValidFile();
	
	public boolean isMinecraftControlled();
	
	public ProviderCollection getProviderCollection();
	
	public File getWorkingDirectory();
	
	public boolean hasUnsavedChanges();
	
	public File getFile();
	
	public String generateJson(boolean pretty);
	
	public void minecraftPushCurrentState();
}
