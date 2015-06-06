package eu.ha3.matmos.editor.tree;

import eu.ha3.matmos.editor.KnowledgeItemType;
import eu.ha3.matmos.editor.interfaces.ISerialUpdate;
import eu.ha3.matmos.jsonformat.serializable.expansion.SerialRoot;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.*;

/*
--filenotes-placeholder
*/

@SuppressWarnings("serial")
public class ItemTreeRoot extends DefaultMutableTreeNode implements ISerialUpdate
{
	private ItemTreeNode condition = new ItemTreeBranch("Conditions", Selector.CONDITION);
	private ItemTreeNode set = new ItemTreeBranch("Sets", Selector.SET);
	private ItemTreeNode machine = new ItemTreeBranch("Machines", Selector.MACHINE);
	
	private ItemTreeNode list = new ItemTreeBranch("Lists", Selector.LIST);
	private ItemTreeNode dynamic = new ItemTreeBranch("Dynamics", Selector.DYNAMIC);
	private ItemTreeNode event = new ItemTreeBranch("Events", Selector.EVENT);
	
	public ItemTreeRoot()
	{
		super("JTree");
		
		ItemTreeNode logic = new ItemTreeBranch("Logic", Selector.LOGIC);
		ItemTreeNode support = new ItemTreeBranch("Support", Selector.SUPPORT);
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
	
	public ItemTreeNode getKnowledgeNode(KnowledgeItemType item)
	{
		switch (item)
		{
		case CONDITION:
			return this.condition;
		case DYNAMIC:
			return this.dynamic;
		case EVENT:
			return this.event;
		case LIST:
			return this.list;
		case MACHINE:
			return this.machine;
		case SET:
			return this.set;
		default:
			return null;
			
		}
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
		else
		{
			keysCopy.removeAll(names);
			if (keysCopy.size() != names.size())
			{
				replaceSubSerial(keys, treeNode);
			}
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