package eu.ha3.matmos.editor.interfaces;

import eu.ha3.matmos.editor.KnowledgeItemType;
import eu.ha3.matmos.editor.tree.Selector;
import eu.ha3.matmos.jsonformat.serializable.expansion.SerialEvent;
import eu.ha3.matmos.jsonformat.serializable.expansion.SerialRoot;
import java.io.File;

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
	
	public boolean isMinecraftControlled();
	
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
	
	public void duplicateItem(Selector selector, String name);
	
	public void purgeLogic();
	
	public void purgeSupports();
	
	public void pushSound(SerialEvent event);
	
	public void mergeFrom(File file);
}
