package eu.ha3.matmos.editor.tree;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;

import eu.ha3.matmos.editor.interfaces.EditorModel;
import eu.ha3.matmos.editor.interfaces.ISerialUpdate;
import eu.ha3.matmos.jsonformat.serializable.SerialRoot;

/*
--filenotes-placeholder
*/

@SuppressWarnings("serial")
public class ItemTreeViewPanel extends JPanel implements ISerialUpdate
{
	private final EditorModel model;
	
	private ItemTreeModel itemTreeModel = new ItemTreeModel();
	private JTree itemTree;
	
	public ItemTreeViewPanel(EditorModel model)
	{
		this.model = model;
		
		setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		add(scrollPane, BorderLayout.CENTER);
		
		this.itemTree = new JTree();
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
}
