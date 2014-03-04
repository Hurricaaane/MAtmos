package eu.ha3.matmos.editor.tree;

/*
--filenotes-placeholder
*/

public class ItemTreeBranch extends ItemTreeNode
{
	public ItemTreeBranch(String name)
	{
		super(name);
	}
	
	@Override
	public boolean isLeaf()
	{
		return false;
	}
}
