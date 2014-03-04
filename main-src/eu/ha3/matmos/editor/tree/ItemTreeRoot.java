package eu.ha3.matmos.editor.tree;

import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.tree.DefaultMutableTreeNode;

import eu.ha3.matmos.editor.interfaces.ISerialUpdate;
import eu.ha3.matmos.jsonformat.serializable.SerialRoot;

/*
--filenotes-placeholder
*/

@SuppressWarnings("serial")
public class ItemTreeRoot extends DefaultMutableTreeNode implements ISerialUpdate
{
	private ItemTreeNode condition = new ItemTreeBranch("Conditions");
	private ItemTreeNode set = new ItemTreeBranch("Sets");
	private ItemTreeNode machine = new ItemTreeBranch("Machines");
	
	private ItemTreeNode list = new ItemTreeBranch("Lists");
	private ItemTreeNode dynamic = new ItemTreeBranch("Dynamics");
	private ItemTreeNode event = new ItemTreeBranch("Events");
	
	public ItemTreeRoot()
	{
		super("JTree");
		
		ItemTreeNode logic = new ItemTreeNode("Logic");
		ItemTreeNode support = new ItemTreeNode("Support");
		add(logic);
		add(support);
		
		logic.add(this.condition);
		logic.add(this.set);
		logic.add(this.machine);
		
		support.add(this.list);
		support.add(this.dynamic);
		support.add(this.event);
	}
	
	@Override
	public void updateSerial(SerialRoot root)
	{
		updateSubSerial(root.condition.keySet(), this.condition);
		updateSubSerial(root.set.keySet(), this.set);
		updateSubSerial(root.machine.keySet(), this.machine);
		updateSubSerial(root.list.keySet(), this.list);
		updateSubSerial(root.dynamic.keySet(), this.dynamic);
		updateSubSerial(root.event.keySet(), this.event);
	}
	
	private void updateSubSerial(Collection<String> keys, ItemTreeNode treeNode)
	{
		Set<String> names = new HashSet<String>();
		Set<String> keysCopy = new HashSet<String>(keys);
		
		@SuppressWarnings("unchecked")
		Enumeration<? extends ItemTreeNode> nenum = treeNode.children();
		while (nenum.hasMoreElements())
		{
			ItemTreeNode next = nenum.nextElement();
			String name = next.getItemName();
			names.add(name);
		}
		
		keysCopy.addAll(names);
		if (keysCopy.size() != names.size())
		{
			replaceSubSerial(keys, treeNode);
		}
	}
	
	private void replaceSubSerial(Collection<String> keys, ItemTreeNode treeNode)
	{
		treeNode.removeAllChildren();
		TreeSet<String> treeSet = new TreeSet<String>(keys);
		
		for (String name : treeSet)
		{
			treeNode.add(new ItemTreeNode(name));
		}
	}
}