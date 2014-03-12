package eu.ha3.matmos.editor.interfaces;

import java.awt.Component;

/*
--filenotes-placeholder
*/

public interface Window extends ISerialUpdate, NamedSerialEditor
{
	public Component asComponent();
	
	public void display();
	
	public void refreshFileState();
	
	public void showErrorPopup(String error);
	
	public void disableMinecraftCapabilitites();
}
