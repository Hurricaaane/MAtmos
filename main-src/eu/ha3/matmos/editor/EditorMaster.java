package eu.ha3.matmos.editor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;

import javax.swing.UIManager;

import com.google.gson.stream.MalformedJsonException;

import eu.ha3.matmos.editor.interfaces.EditorModel;
import eu.ha3.matmos.editor.interfaces.IEditorWindow;
import eu.ha3.matmos.engine.core.implem.abstractions.ProviderCollection;
import eu.ha3.matmos.jsonformat.serializable.SerialRoot;
import eu.ha3.matmos.tools.Jason;
import eu.ha3.matmos.tools.JasonExpansions_Engine1Deserializer2000;

/*
--filenotes-placeholder
*/

public class EditorMaster implements Runnable, EditorModel
{
	private final IEditorWindow window;
	
	private final PluggableIntoMinecraft minecraft;
	private File file;
	
	private SerialRoot root = new SerialRoot();
	
	private boolean hasModifiedContents;
	
	public EditorMaster()
	{
		this(null, null);
	}
	
	public EditorMaster(PluggableIntoMinecraft minecraft, File aFileToEdit)
	{
		this.minecraft = minecraft;
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
		
		this.file = aFileToEdit;
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
			updateFileState();
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
		updateFileState();
	}
	
	private void updateFileState()
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
		if (!isMinecraftControlled())
			return;
		
		this.minecraft.reloadFromDisk();
	}
	
	@Override
	public boolean hasValidFile()
	{
		return this.file != null;
	}
	
	@Override
	public ProviderCollection getProviderCollection()
	{
		if (!isMinecraftControlled())
			return null;
		
		return this.minecraft.getProviders();
	}
	
	@Override
	public File getWorkingDirectory()
	{
		return new File(System.getProperty("user.dir"));
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
		if (!isMinecraftControlled())
			return;
		
		this.minecraft.pushJason(Jason.toJson(this.root));
	}
}
