package eu.ha3.matmos.pluggable;

import java.io.File;

/*
--filenotes-placeholder
*/

public interface PluggableIntoMinecraft
{
	public boolean isReadOnly();
	
	public File getFileIfAvailable();
	
	public File getWorkingDirectoryIfAvailable();

	public void pushJason(String jason);

	public void reloadFromDisk();
	
	public void onEditorClosed();
}
