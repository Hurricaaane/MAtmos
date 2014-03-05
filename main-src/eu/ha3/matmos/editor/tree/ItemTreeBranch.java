package eu.ha3.matmos.editor.tree;

/*
--filenotes-placeholder
*/

@SuppressWarnings("serial")
public class ItemTreeBranch extends ItemTreeNode
{
	private final Selector rootSelector;
	
	public ItemTreeBranch(String name, Selector rootSelector)
	{
		super(name);
		this.rootSelector = rootSelector;
	}
	
	public Selector getSelector()
	{
		return this.rootSelector;
	}
	
	@Override
	public boolean isLeaf()
	{
		return false;
	}
}
