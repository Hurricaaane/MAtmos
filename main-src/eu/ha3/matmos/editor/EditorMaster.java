package eu.ha3.matmos.editor;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.stream.MalformedJsonException;
import eu.ha3.matmos.editor.interfaces.Editor;
import eu.ha3.matmos.editor.interfaces.Window;
import eu.ha3.matmos.editor.tree.Selector;
import eu.ha3.matmos.expansions.debugunit.ReadOnlyJasonStringEDU;
import eu.ha3.matmos.jsonformat.serializable.expansion.SerialEvent;
import eu.ha3.matmos.jsonformat.serializable.expansion.SerialRoot;
import eu.ha3.matmos.pluggable.PluggableIntoMinecraft;
import eu.ha3.matmos.tools.Jason;
import eu.ha3.matmos.tools.JasonExpansions_Engine1Deserializer2000;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Scanner;
import javax.swing.UIManager;

/*
--filenotes-placeholder
*/

public class EditorMaster implements Runnable, Editor
{
	//private IEditorWindow __WINDOW;
	private Window window__EventQueue;
	
	private final PluggableIntoMinecraft minecraft;
	
	private File file;
	private File workingDirectory = new File(System.getProperty("user.dir"));
	private SerialRoot root = new SerialRoot();
	private boolean hasModifiedContents;
	
	public EditorMaster()
	{
		this(null);
	}
	
	public EditorMaster(PluggableIntoMinecraft minecraft)
	{
		this(minecraft, null);
	}
	
	public EditorMaster(PluggableIntoMinecraft minecraft, File fileIn)
	{
		File potentialFile = fileIn;
		
		this.minecraft = minecraft;
		if (minecraft != null)
		{
			File fileIF = minecraft.getFileIfAvailable();
			File workingDirectoryIF = minecraft.getWorkingDirectoryIfAvailable();
			if (fileIF != null && workingDirectoryIF != null)
			{
				potentialFile = fileIF;
				this.workingDirectory = workingDirectoryIF;
			}
		}
		
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		this.root = new SerialRoot();
		this.hasModifiedContents = false;
		this.file = potentialFile;
	}
	
	@Override
	public void run()
	{
		System.out.println("Loading designer...");
		java.awt.EventQueue.invokeLater(new Runnable() {
			@Override
			public void run()
			{
				initializedWindow(new EditorWindow(EditorMaster.this));
			}
		});
	}
	
	private void initializedWindow(EditorWindow editorWindow)
	{
		//this.__WINDOW = editorWindow;
		this.window__EventQueue = new WindowEventQueue(editorWindow);
		this.window__EventQueue.display();
		
		System.out.println("Loaded.");
		
		if (this.file != null)
		{
			trySetAndLoadFile(this.file);
		}
		
		if (this.minecraft instanceof ReadOnlyJasonStringEDU)
		{
			flushFileAndSerial();
			this.root =
				new JasonExpansions_Engine1Deserializer2000().jsonToSerial(((ReadOnlyJasonStringEDU) this.minecraft)
					.obtainJasonString());
			updateFileAndContentsState();
		}
	}
	
	public static void main(String args[])
	{
		new EditorMaster().run();
	}
	
	@Override
	public boolean isMinecraftControlled()
	{
		return this.minecraft != null;
	}
	
	@Override
	public void trySetAndLoadFile(File potentialFile)
	{
		if (potentialFile == null)
		{
			showErrorPopup("Missing file pointer.");
			return;
		}
		
		if (potentialFile.isDirectory())
		{
			showErrorPopup("The selected file is actually a directory.");
			return;
		}
		
		if (!potentialFile.exists())
		{
			showErrorPopup("The selected file is inaccessible.");
			return;
		}
		
		try
		{
			loadFile(potentialFile);
			this.file = potentialFile;
		}
		catch (Exception e)
		{
			this.file = null;
			
			showErrorPopup("File could not be loaded:\n" + e.getLocalizedMessage());
			reset();
			updateFileAndContentsState();
		}
	}
	
	private void reset()
	{
		flushFileAndSerial();
		modelize();
	}
	
	private void flushFileAndSerial()
	{
		this.file = null;
		this.root = new SerialRoot();
		this.hasModifiedContents = false;
		this.window__EventQueue.setEditFocus(null, null, false);
	}
	
	private void modelize()
	{
		java.awt.EventQueue.invokeLater(new Runnable() {
			@Override
			public void run()
			{
			}
		});
	}
	
	private void loadFile(File potentialFile) throws IOException, MalformedJsonException
	{
		flushFileAndSerial();
		String jasonString = new Scanner(new FileInputStream(potentialFile)).useDelimiter("\\Z").next();
		System.out.println(jasonString);
		this.root = new JasonExpansions_Engine1Deserializer2000().jsonToSerial(jasonString);
		this.hasModifiedContents = false;
		updateFileAndContentsState();
	}
	
	private void mergeFile(File potentialFile) throws IOException, MalformedJsonException
	{
		String jasonString = new Scanner(new FileInputStream(potentialFile)).useDelimiter("\\Z").next();
		System.out.println(jasonString);
		SerialRoot mergeFrom = new JasonExpansions_Engine1Deserializer2000().jsonToSerial(jasonString);
		
		if (Collections.disjoint(this.root.condition.keySet(), mergeFrom.condition.keySet())
			&& Collections.disjoint(this.root.dynamic.keySet(), mergeFrom.dynamic.keySet())
			&& Collections.disjoint(this.root.event.keySet(), mergeFrom.event.keySet())
			&& Collections.disjoint(this.root.list.keySet(), mergeFrom.list.keySet())
			&& Collections.disjoint(this.root.machine.keySet(), mergeFrom.machine.keySet())
			&& Collections.disjoint(this.root.set.keySet(), mergeFrom.set.keySet()))
		{
		}
		else
		{
			showErrorPopup("The two expansions have elements in common.\n"
				+ "The elements in common will be overriden by the file you are currently importing for the merge.");
		}
		
		this.root.condition.putAll(mergeFrom.condition);
		this.root.dynamic.putAll(mergeFrom.dynamic);
		this.root.event.putAll(mergeFrom.event);
		this.root.list.putAll(mergeFrom.list);
		this.root.machine.putAll(mergeFrom.machine);
		this.root.set.putAll(mergeFrom.set);
		
		this.hasModifiedContents = true;
		updateFileAndContentsState();
	}
	
	private void updateFileAndContentsState()
	{
		this.window__EventQueue.refreshFileState();
		this.window__EventQueue.updateSerial(EditorMaster.this.root);
	}
	
	private void updateFileState()
	{
		this.window__EventQueue.refreshFileState();
	}
	
	private void showErrorPopup(String error)
	{
		this.window__EventQueue.showErrorPopup(error);
	}
	
	@Override
	public void minecraftReloadFromDisk()
	{
		this.minecraft.reloadFromDisk();
	}
	
	@Override
	public boolean hasValidFile()
	{
		return this.file != null;
	}

	@Override
	public File getWorkingDirectory()
	{
		return this.workingDirectory;
	}
	
	@Override
	public boolean hasUnsavedChanges()
	{
		return this.hasModifiedContents;
	}
	
	@Override
	public File getFile()
	{
		return this.file;
	}
	
	@Override
	public String generateJson(boolean pretty)
	{
		return pretty ? Jason.toJsonPretty(this.root) : Jason.toJson(this.root);
	}
	
	@Override
	public void minecraftPushCurrentState()
	{
		this.minecraft.pushJason(Jason.toJson(this.root));
	}
	
	@Override
	public boolean longSave(File location, boolean setAsNewPointer)
	{
		boolean success = writeToFile(location);
		
		if (success && setAsNewPointer)
		{
			this.file = location;
			this.hasModifiedContents = false;
			updateFileState();
		}
		
		return success;
	}
	
	@Override
	public boolean quickSave()
	{
		if (!hasValidFile())
			return false;
		
		boolean success = writeToFile(this.file);
		if (success)
		{
			this.hasModifiedContents = false;
			updateFileState();
		}
		return success;
	}
	
	private boolean writeToFile(File fileToWrite)
	{
		try
		{
			if (!fileToWrite.exists())
			{
				fileToWrite.createNewFile();
			}
			
			FileWriter write = new FileWriter(fileToWrite);
			write.append(Jason.toJsonPretty(this.root));
			write.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			
			this.window__EventQueue.showErrorPopup("Writing to disk resulted in an error: " + e.getLocalizedMessage());
			return false;
		}
		
		return true;
	}
	
	@Override
	public File getExpansionDirectory()
	{
		return new File(this.workingDirectory, "assets/matmos/expansions").exists() ? new File(
			this.workingDirectory, "assets/matmos/expansions") : this.workingDirectory;
	}
	
	@Override
	public File getSoundDirectory()
	{
		return new File(this.workingDirectory, "assets/minecraft/sounds").exists() ? new File(
			this.workingDirectory, "assets/minecraft/sounds") : this.workingDirectory;
	}
	
	@Override
	public void switchEditItem(Selector selector, String itemName)
	{
		Map<String, ? extends Object> map = null;
		
		switch (selector)
		{
		case CONDITION:
			map = this.root.condition;
			break;
		case SET:
			map = this.root.set;
			break;
		case MACHINE:
			map = this.root.machine;
			break;
		case LIST:
			map = this.root.list;
			break;
		case DYNAMIC:
			map = this.root.dynamic;
			break;
		case EVENT:
			map = this.root.event;
			break;
		case LOGIC:
			break;
		case SUPPORT:
			break;
		default:
			break;
		}
		
		if (map != null && map.containsKey(itemName))
		{
			this.window__EventQueue.setEditFocus(itemName, map.get(itemName), false);
		}
	}
	
	@Override
	public void renameItem(String oldName, Object editFocus, String newName)
	{
		if (oldName.equals(newName))
			return;
		
		if (newName == null || newName.equals("") || newName.contains("\"") || newName.contains("\\"))
		{
			showErrorPopup("Name must not be empty or include the characters \" and \\");
			return;
		}
		
		try
		{
			SerialManipulator.rename(this.root, editFocus, oldName, newName);
			flagChange(true);
			this.window__EventQueue.setEditFocus(newName, editFocus, true);
		}
		catch (ItemNamingException e)
		{
			showErrorPopup(e.getMessage());
		}
	}
	
	@Override
	public void deleteItem(String nameOfItem, Object editFocus)
	{
		try
		{
			SerialManipulator.delete(this.root, editFocus, nameOfItem);
			flagChange(true);
			this.window__EventQueue.setEditFocus(null, null, false);
		}
		catch (ItemNamingException e)
		{
			showErrorPopup(e.getMessage());
		}
	}
	
	private void flagChange(boolean treeWasDeeplyModified)
	{
		boolean previousStateIsFalse = !this.hasModifiedContents;
		this.hasModifiedContents = true;
		
		if (treeWasDeeplyModified)
		{
			updateFileAndContentsState();
		}
		else
		{
			if (previousStateIsFalse)
			{
				updateFileState();
			}
		}
	}
	
	@Override
	public boolean createItem(KnowledgeItemType choice, String name)
	{
		try
		{
			Object o = SerialManipulator.createNew(this.root, choice, name);
			flagChange(true);
			this.window__EventQueue.setEditFocus(name, o, true);
			return true;
		}
		catch (ItemNamingException e)
		{
			showErrorPopup(e.getMessage());
		}
		
		return false;
	}
	
	@Override
	public void informInnerChange()
	{
		flagChange(false);
	}
	
	@Override
	public SerialRoot getRootForCopyPurposes()
	{
		return this.root;
	}
	
	@Override
	public void duplicateItem(Selector selector, String name)
	{
		switch (selector)
		{
		case CONDITION:
			doDuplicateItem(selector, name, this.root.condition);
			break;
		case SET:
			doDuplicateItem(selector, name, this.root.set);
			break;
		case MACHINE:
			doDuplicateItem(selector, name, this.root.machine);
			break;
		case LIST:
			doDuplicateItem(selector, name, this.root.list);
			break;
		case DYNAMIC:
			doDuplicateItem(selector, name, this.root.dynamic);
			break;
		case EVENT:
			doDuplicateItem(selector, name, this.root.event);
			break;
		case LOGIC:
			break;
		case SUPPORT:
			break;
		default:
			break;
		}
	}
	
	private <T> void doDuplicateItem(Selector selector, String name, Map<String, T> map)
	{
		if (map == null)
			return;
		
		if (!map.containsKey(name))
			return;
		
		Class<? extends Object> serialClass = map.get(name).getClass();
		@SuppressWarnings("unchecked")
		T duplicate =
			(T) new Gson().fromJson(new JsonParser().parse(Jason.toJson(map.get(name))).getAsJsonObject(), serialClass);
		
		int add = 1;
		while (map.containsKey(name + " (" + add + ")"))
		{
			add = add + 1;
		}
		map.put(name + " (" + add + ")", duplicate);
		
		flagChange(true);
		this.window__EventQueue.setEditFocus(name + " (" + add + ")", duplicate, true);
	}
	
	@Override
	public void purgeLogic()
	{
		new PurgeOperator().purgeLogic(this.root);
		flagChange(true);
		this.window__EventQueue.setEditFocus(null, null, false);
	}
	
	@Override
	public void purgeSupports()
	{
		new PurgeOperator().purgeEvents(this.root);
		flagChange(true);
		this.window__EventQueue.setEditFocus(null, null, false);
	}
	
	@Override
	public void pushSound(SerialEvent event)
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void mergeFrom(File potentialFile)
	{
		if (potentialFile == null)
		{
			showErrorPopup("Missing file pointer.");
			return;
		}
		
		if (potentialFile.isDirectory())
		{
			showErrorPopup("The selected file is actually a directory.");
			return;
		}
		
		if (!potentialFile.exists())
		{
			showErrorPopup("The selected file is inaccessible.");
			return;
		}
		
		try
		{
			mergeFile(potentialFile);
		}
		catch (Exception e)
		{
			showErrorPopup("Merge error. The current state may be corrupt:\n" + e.getLocalizedMessage());
		}
	}
}
