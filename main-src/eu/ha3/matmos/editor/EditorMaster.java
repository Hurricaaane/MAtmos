package eu.ha3.matmos.editor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Scanner;

import javax.swing.UIManager;

import com.google.gson.stream.MalformedJsonException;

import eu.ha3.matmos.editor.interfaces.EditorModel;
import eu.ha3.matmos.editor.interfaces.IEditorWindow;
import eu.ha3.matmos.editor.tree.Selector;
import eu.ha3.matmos.engine.core.implem.abstractions.ProviderCollection;
import eu.ha3.matmos.jsonformat.serializable.SerialRoot;
import eu.ha3.matmos.tools.Jason;
import eu.ha3.matmos.tools.JasonExpansions_Engine1Deserializer2000;

/*
--filenotes-placeholder
*/

public class EditorMaster implements Runnable, EditorModel, UnpluggedListener
{
	private final IEditorWindow window;
	
	private final PluggableIntoMinecraft minecraft;
	private boolean isUnplugged;
	
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
		File potentialFile = this.file;
		
		this.minecraft = minecraft;
		if (minecraft != null)
		{
			minecraft.addUnpluggedListener(this);
			
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
		
		this.window = new EditorWindow(this);
		reset();
		// Set file after reset
		
		this.file = potentialFile;
	}
	
	@Override
	public void run()
	{
		System.out.println("Loading designer...");
		this.window.display();
		System.out.println("Loaded.");
		
		if (this.file != null)
		{
			trySetAndLoadFile(this.file);
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
		updateFileAndContentsState();
	}
	
	private void updateFileAndContentsState()
	{
		java.awt.EventQueue.invokeLater(new Runnable() {
			@Override
			public void run()
			{
				EditorMaster.this.window.refreshFileState();
				EditorMaster.this.window.updateSerial(EditorMaster.this.root);
			}
		});
	}
	
	private void updateFileState()
	{
		java.awt.EventQueue.invokeLater(new Runnable() {
			@Override
			public void run()
			{
				EditorMaster.this.window.refreshFileState();
			}
		});
	}
	
	private void showErrorPopup(final String error)
	{
		java.awt.EventQueue.invokeLater(new Runnable() {
			@Override
			public void run()
			{
				EditorMaster.this.window.showErrorPopup(error);
			}
		});
	}
	
	@Override
	public void minecraftReloadFromDisk()
	{
		if (!isPlugged())
			return;
		
		this.minecraft.reloadFromDisk();
	}
	
	@Override
	public boolean hasValidFile()
	{
		return this.file != null;
	}
	
	@Override
	public ProviderCollection getProviderCollectionIfAvailable()
	{
		if (!isPlugged())
			return null;
		
		return this.minecraft.getProviders();
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
		if (!isPlugged())
			return;
		
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
			
			final String error = "Writing to disk resulted in an error: " + e.getLocalizedMessage();
			java.awt.EventQueue.invokeLater(new Runnable() {
				@Override
				public void run()
				{
					EditorMaster.this.window.showErrorPopup(error);
				}
			});
			return false;
		}
		
		return true;
	}
	
	@Override
	public void onUnpluggedEvent(PluggableIntoMinecraft pluggable)
	{
		this.isUnplugged = true;
		java.awt.EventQueue.invokeLater(new Runnable() {
			@Override
			public void run()
			{
				EditorMaster.this.window.disableMinecraftCapabilitites();
			}
		});
	}
	
	@Override
	public boolean isPlugged()
	{
		return isMinecraftControlled() && !this.isUnplugged;
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
			final String itemNameFinal = itemName;
			final Object item = map.get(itemName);
			
			java.awt.EventQueue.invokeLater(new Runnable() {
				@Override
				public void run()
				{
					EditorMaster.this.window.setEditFocus(itemNameFinal, item);
				}
			});
		}
	}
	
	@Override
	public void renameItem(String oldName, Object editFocus, String newName)
	{
		if (oldName.equals(newName))
			return;
		
		if (newName == null || newName.equals("") || newName.contains("\"") || newName.contains("\\"))
		{
			java.awt.EventQueue.invokeLater(new Runnable() {
				@Override
				public void run()
				{
					showErrorPopup("Name must not be empty or include the characters \" and \\");
				}
			});
			return;
		}
		
		try
		{
			SerialManipulator.rename(this.root, editFocus, oldName, newName);
			flagChange(true);
			this.window.setEditFocus(newName, editFocus);
		}
		catch (final ItemNamingException e)
		{
			java.awt.EventQueue.invokeLater(new Runnable() {
				@Override
				public void run()
				{
					showErrorPopup(e.getMessage());
				}
			});
		}
	}
	
	@Override
	public void deleteItem(String nameOfItem, Object editFocus)
	{
		try
		{
			SerialManipulator.delete(this.root, editFocus, nameOfItem);
			flagChange(true);
			this.window.setEditFocus(null, null);
		}
		catch (final ItemNamingException e)
		{
			java.awt.EventQueue.invokeLater(new Runnable() {
				@Override
				public void run()
				{
					showErrorPopup(e.getMessage());
				}
			});
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
}
