package eu.ha3.matmos.editor.tree;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;

import eu.ha3.matmos.engine.core.interfaces.Stated;

/*
--filenotes-placeholder
*/

@SuppressWarnings("serial")
public class CSMPanel extends JPanel implements ICSMTrio
{
	public CSMPanel()
	{
		setLayout(new BorderLayout(0, 0));
		
		JSplitPane machineSplit = new JSplitPane();
		machineSplit.setResizeWeight(0.55);
		add(machineSplit);
		
		JSplitPane conditionalSplit = new JSplitPane();
		conditionalSplit.setResizeWeight(0.5);
		machineSplit.setLeftComponent(conditionalSplit);
		
		JScrollPane scrollPane = new JScrollPane();
		conditionalSplit.setLeftComponent(scrollPane);
		
		JTree tree = new JTree();
		tree.setShowsRootHandles(true);
		tree.setModel(new DefaultTreeModel(new DefaultMutableTreeNode("JTree") {
			{
				DefaultMutableTreeNode node_1;
				node_1 = new DefaultMutableTreeNode("Only water");
				node_1.add(new DefaultMutableTreeNode("thing(city) >= 15"));
				add(node_1);
				node_1 = new DefaultMutableTreeNode("Aim is water");
				node_1.add(new DefaultMutableTreeNode("aim(block) == minecraft:air"));
				node_1.add(new DefaultMutableTreeNode("minecraft:block"));
				add(node_1);
				add(new DefaultMutableTreeNode("red"));
				add(new DefaultMutableTreeNode("yellow"));
				add(new DefaultMutableTreeNode("basketball"));
				add(new DefaultMutableTreeNode("soccer"));
				add(new DefaultMutableTreeNode("football"));
				add(new DefaultMutableTreeNode("hockey"));
				add(new DefaultMutableTreeNode("hot dogs"));
				add(new DefaultMutableTreeNode("pizza"));
				add(new DefaultMutableTreeNode("ravioli"));
				add(new DefaultMutableTreeNode("bananas"));
			}
		}));
		tree.setRootVisible(false);
		tree.setCellRenderer(new DefaultTreeCellRenderer() {
			private Icon leafIcon = UIManager.getIcon("Tree.leafIcon");
			private Icon fileIcon = UIManager.getIcon("FileView.fileIcon");
			
			@Override
			public Component getTreeCellRendererComponent(
				JTree tree, Object value, boolean selected, boolean expanded, boolean isLeaf, int row, boolean focused)
			{
				Component c = super.getTreeCellRendererComponent(tree, value, selected, expanded, isLeaf, row, focused);
				if (!isLeaf)
				{
					if (value instanceof Stated)
					{
						setIcon(this.leafIcon);
					}
					else
					{
						setIcon(this.fileIcon);
					}
				}
				return c;
			}
		});
		scrollPane.setViewportView(tree);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		conditionalSplit.setRightComponent(scrollPane_1);
		
		JScrollPane scrollPane_2 = new JScrollPane();
		machineSplit.setRightComponent(scrollPane_2);
	}
}
