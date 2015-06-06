package eu.ha3.matmos.editor.edit;

import eu.ha3.matmos.editor.interfaces.IFlaggable;
import eu.ha3.matmos.jsonformat.serializable.expansion.SerialDynamic;
import eu.ha3.matmos.jsonformat.serializable.expansion.SerialDynamicSheetIndex;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/*
--filenotes-placeholder
*/

@SuppressWarnings("serial")
public class DynamicRemoverPanel extends JPanel
{
	private final IFlaggable parent;
	private final SerialDynamic dynamic;
	private JList list;
	
	private ArrayList<String> things;
	
	public DynamicRemoverPanel(IFlaggable parent, SerialDynamic original)
	{
		this.parent = parent;
		this.dynamic = original;
		
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
			if (this.things.contains(value))
			{
				this.dynamic.entries.remove(this.things.indexOf(value));
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
		
		this.things = new ArrayList<String>();
		for (SerialDynamicSheetIndex si : this.dynamic.entries)
		{
			this.things.add(si.sheet + "@" + si.index);
		}
		
		this.list.setListData(this.things.toArray(new String[this.things.size()]));
	}
	
	public JList getList()
	{
		return this.list;
	}
}
