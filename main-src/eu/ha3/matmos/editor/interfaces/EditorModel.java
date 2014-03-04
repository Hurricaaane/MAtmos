package eu.ha3.matmos.editor.interfaces;

import java.io.File;

import eu.ha3.matmos.editor.tree.Selector;
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
	
	public ProviderCollection getProviderCollectionIfAvailable();
	
	public File getWorkingDirectory();
	
	public boolean hasUnsavedChanges();
	
	public File getFile();
	
	public String generateJson(boolean pretty);
	
	public void minecraftPushCurrentState();
	
	public boolean quickSave();
	
	public boolean longSave(File location, boolean setAsNewPointer);
	
	public boolean isPlugged();
	
	public File getExpansionDirectory();
	
	public File getSoundDirectory();
	
	public void switchEditItem(Selector selector, String itemName);
	
	public void renameItem(String nameOfItem, Object editFocus, String text);
}
