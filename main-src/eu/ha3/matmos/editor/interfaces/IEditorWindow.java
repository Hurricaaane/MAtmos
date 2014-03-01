package eu.ha3.matmos.editor.interfaces;

import java.awt.Component;

/*
--filenotes-placeholder
*/

public interface IEditorWindow
{
	public Component asComponent();
	
	public void display();
	
	public void refreshFileState();
}
