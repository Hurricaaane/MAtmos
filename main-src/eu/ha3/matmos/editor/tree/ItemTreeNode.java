package eu.ha3.matmos.editor.tree;

import javax.swing.tree.DefaultMutableTreeNode;

/*
--filenotes-placeholder
*/

@SuppressWarnings("serial")
public class ItemTreeNode extends DefaultMutableTreeNode
{
	private String itemName;
	
	public ItemTreeNode(String name)
	{
		super(name);
		this.itemName = name;
	}
	
	public String getItemName()
	{
		return this.itemName;
	}
}
