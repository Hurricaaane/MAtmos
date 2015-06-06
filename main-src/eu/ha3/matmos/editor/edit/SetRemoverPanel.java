package eu.ha3.matmos.editor.edit;

import eu.ha3.matmos.editor.interfaces.IFlaggable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;
import java.util.TreeSet;

/*
--filenotes-placeholder
*/

@SuppressWarnings("serial")
public class SetRemoverPanel extends JPanel
{
	private final IFlaggable parent;
	private final Set<String> set;
	private JList list;
	
	public SetRemoverPanel(IFlaggable parent, Set<String> original)
	{
		this.parent = parent;
		this.set = original;
		
		setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		add(panel, BorderLayout.CENTER);
		panel.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		panel.add(scrollPane, BorderLayout.CENTER);
		
		this.list = new JList();
		this.list.setVisibleRowCount(4);
		scrollPane.setViewportView(this.list);
		
		JPanel panel_1 = new JPanel();
		panel.add(panel_1, BorderLayout.EAST);
		
		JButton btnRemoveSelected = new JButton("Remove");
		btnRemoveSelected.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				removeSelected();
			}
		});
		panel_1.setLayout(new BorderLayout(0, 0));
		panel_1.add(btnRemoveSelected);
	}
	
	protected void removeSelected()
	{
		Object values[] = this.list.getSelectedValues();
		if (values.length == 0)
			return;
		
		int removedCount = 0;
		for (Object o : values)
		{
			String value = (String) o;
			if (this.set.contains(value))
			{
				this.set.remove(value);
				removedCount = removedCount + 1;
			}
		}
		
		if (removedCount > 0)
		{
			this.parent.flagChange();
			// Flagging should cause a call to fillWithValues
		}
	}
	
	public void fillWithValues()
	{
		this.list.removeAll();
		this.list.setListData(new TreeSet<String>(this.set).toArray(new String[this.set.size()]));
	}
	
	public JList getList()
	{
		return this.list;
	}
}
