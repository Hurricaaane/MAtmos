package eu.ha3.matmos.editor.interfaces;

import java.io.File;

import eu.ha3.matmos.editor.KnowledgeItemType;
import eu.ha3.matmos.editor.tree.Selector;
import eu.ha3.matmos.engine.core.implem.ProviderCollection;
import eu.ha3.matmos.jsonformat.serializable.expansion.SerialRoot;

/*
--filenotes-placeholder
*/

public interface Editor
{
	public File getExpansionDirectory();
	
	public File getSoundDirectory();
	
	public File getWorkingDirectory();
	
	public void minecraftReloadFromDisk();
	
	public void minecraftPushCurrentState();
	
	public ProviderCollection getProviderCollectionIfAvailable();
	
	public boolean isMinecraftControlled();
	
	public boolean isPlugged();
	
	public boolean hasValidFile();
	
	public boolean hasUnsavedChanges();
	
	public File getFile();
	
	public String generateJson(boolean pretty);
	
	public void trySetAndLoadFile(File file);
	
	public boolean quickSave();
	
	public boolean longSave(File location, boolean setAsNewPointer);
	
	public void switchEditItem(Selector selector, String itemName);
	
	public void renameItem(String nameOfItem, Object editFocus, String text);
	
	public void deleteItem(String nameOfItem, Object editFocus);
	
	public boolean createItem(KnowledgeItemType choice, String name);
	
	public void informInnerChange();
	
	public SerialRoot getRootForCopyPurposes();
}
