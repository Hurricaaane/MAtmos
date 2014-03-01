package eu.ha3.matmos.editor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import eu.ha3.matmos.editor.interfaces.EditorModel;
import eu.ha3.matmos.editor.interfaces.IEditorWindow;
import eu.ha3.matmos.engine0.core.implem.abstractions.ProviderCollection;
import eu.ha3.matmos.engine0tools.JasonExpansions_Engine1Deserializer2000;
import eu.ha3.matmos.jsonformat.serializable.SerialRoot;

/*
--filenotes-placeholder
*/

public class EditorMaster implements Runnable, EditorModel
{
	private final IEditorWindow window;
	
	private final PluggableIntoMinecraft minecraft;
	private File file;
	
	private SerialRoot root;
	
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
			this.file = null;
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
			loadFile();
			this.file = potentialFile;
		}
		catch (IOException e)
		{
			this.file = null;
			
			showErrorPopup("File could not be loaded:\n" + e.getLocalizedMessage());
			reset();
		}
	}
	
	private void reset()
	{
		flush();
		modelize();
	}
	
	private void flush()
	{
		this.root = new SerialRoot();
	}
	
	private void modelize()
	{
	}
	
	private void loadFile() throws IOException
	{
		flush();
		String jasonString = new Scanner(new FileInputStream(this.file)).useDelimiter("\\Z").next();
		System.out.println(jasonString);
		this.root = new JasonExpansions_Engine1Deserializer2000().jsonToSerial(jasonString);
		
		this.window.refreshFileState();
	}
	
	private void showErrorPopup(String error)
	{
		JOptionPane.showMessageDialog(this.window.asComponent(), error, "Error", JOptionPane.ERROR_MESSAGE);
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
}
