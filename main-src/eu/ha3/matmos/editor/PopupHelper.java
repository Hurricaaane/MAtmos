package eu.ha3.matmos.editor;

import javax.swing.*;

/*
--filenotes-placeholder
*/

public class PopupHelper
{
	private static KnowledgeItemType lastChoice = KnowledgeItemType.CONDITION;
	
	public static KnowledgeItemType askForType(EditorWindow editorWindow, String title)
	{
		KnowledgeItemType choice =
			(KnowledgeItemType) JOptionPane.showInputDialog(
				editorWindow, "Select new item type", title, JOptionPane.PLAIN_MESSAGE, null,
				KnowledgeItemType.values(), lastChoice);
		if (choice != null)
		{
			lastChoice = choice;
		}
		return choice;
	}
	
	public static String askForName(EditorWindow editorWindow, String title, String def)
	{
		String name =
			(String) JOptionPane.showInputDialog(
				editorWindow, "Choose a name", title, JOptionPane.PLAIN_MESSAGE, null, null, def);
		
		return name;
	}
}
