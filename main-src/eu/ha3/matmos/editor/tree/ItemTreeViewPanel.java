package eu.ha3.matmos.editor.tree;

import eu.ha3.matmos.editor.KnowledgeItemType;
import eu.ha3.matmos.editor.interfaces.Editor;
import eu.ha3.matmos.editor.interfaces.ISerialUpdate;
import eu.ha3.matmos.editor.interfaces.NamedSerialEditor;
import eu.ha3.matmos.jsonformat.serializable.expansion.SerialRoot;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.util.Enumeration;

/*
--filenotes-placeholder
*/

@SuppressWarnings("serial")
public class ItemTreeViewPanel extends JPanel implements ISerialUpdate, NamedSerialEditor
{
	private final Editor model;
	
	private ItemTreeModel itemTreeModel = new ItemTreeModel();
	private JTree itemTree;
	
	public ItemTreeViewPanel(Editor modelConstruct)
	{
		this.model = modelConstruct;
		
		setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		add(scrollPane, BorderLayout.CENTER);
		
		this.itemTree = new JTree();
		this.itemTree.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent event)
			{
				try
				{
					Object component = event.getPath().getLastPathComponent();
					if (!(component instanceof ItemTreeNode))
						return;
					
					if (component instanceof ItemTreeBranch)
						return;
					
					ItemTreeNode item = (ItemTreeNode) component;
					ItemTreeBranch parent = (ItemTreeBranch) item.getParent();
					
					ItemTreeViewPanel.this.model.switchEditItem(parent.getSelector(), item.getItemName());
				}
				catch (ClassCastException e)
				{
				}
			}
		});
		this.itemTree.setShowsRootHandles(true);
		this.itemTree.setModel(this.itemTreeModel);
		this.itemTree.setRootVisible(false);
		scrollPane.setViewportView(this.itemTree);
	}
	
	@Override
	public void updateSerial(SerialRoot root)
	{
		this.itemTreeModel.updateSerial(root);
		this.itemTree.updateUI();
	}
	
	@Override
	public void setEditFocus(String name, Object item, boolean forceSelect)
	{
		if (item == null || name == null)
			return;
		
		if (!forceSelect)
			return;
		
		KnowledgeItemType k = KnowledgeItemType.fromSerialClass(item.getClass());
		if (k == null)
			return;
		
		if (this.itemTree.getSelectionPath().getLastPathComponent() instanceof ItemTreeNode
			&& ((ItemTreeNode) this.itemTree.getSelectionPath().getLastPathComponent()).getItemName().equals(name))
			return;
		
		@SuppressWarnings("unchecked")
		Enumeration<? extends ItemTreeNode> nenum = this.itemTreeModel.getItemTreeRoot().getKnowledgeNode(k).children();
		while (nenum.hasMoreElements())
		{
			ItemTreeNode next = nenum.nextElement();
			String itemName = next.getItemName();
			
			if (itemName.equals(name))
			{
				this.itemTree.setSelectionPath(new TreePath(this.itemTreeModel.getPathToRoot(next)));
				return;
			}
		}
	}
	
	public ItemTreeNode getSelectedITN()
	{
		return (ItemTreeNode) (this.itemTree.getSelectionPath().getLastPathComponent() instanceof ItemTreeNode
			? this.itemTree.getSelectionPath().getLastPathComponent() : null);
	}
}
