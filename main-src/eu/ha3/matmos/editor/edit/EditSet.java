package eu.ha3.matmos.editor.edit;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

import eu.ha3.matmos.jsonformat.serializable.SerialSet;

/*
--filenotes-placeholder
*/

@SuppressWarnings("serial")
public class EditSet extends JPanel implements IFlaggable
{
	private final EditPanel edit;
	private final SerialSet set;
	
	//
	
	private JPanel panel;
	private SetRemoverPanel activeSet;
	private SetRemoverPanel inactiveSet;
	private JPanel panel_1;
	private JScrollPane scrollPane;
	private JList list;
	private JPanel panel_2;
	private JButton btnActive;
	private JButton btnInactive;
	
	public EditSet(EditPanel parentConstruct, SerialSet setConstruct)
	{
		this.edit = parentConstruct;
		this.set = setConstruct;
		setLayout(new BorderLayout(0, 0));
		
		this.panel = new JPanel();
		this.panel.setBorder(new TitledBorder(null, "Internal", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		add(this.panel, BorderLayout.NORTH);
		this.panel.setLayout(new FormLayout(new ColumnSpec[] { ColumnSpec.decode("333px:grow"), }, new RowSpec[] {
			RowSpec.decode("21px"), RowSpec.decode("60px"), FormFactory.RELATED_GAP_ROWSPEC,
			FormFactory.DEFAULT_ROWSPEC, FormFactory.LINE_GAP_ROWSPEC, RowSpec.decode("60px"), }));
		
		JLabel lblMustBeActive = new JLabel("Must be active:");
		this.panel.add(lblMustBeActive, "1, 1, left, center");
		
		this.activeSet = new SetRemoverPanel(this.edit, this.set.yes);
		this.panel.add(this.activeSet, "1, 2, fill, top");
		
		JLabel lblMustBeInactive = new JLabel("Must be inactive:");
		this.panel.add(lblMustBeInactive, "1, 4, left, center");
		
		this.inactiveSet = new SetRemoverPanel(this.edit, this.set.no);
		this.panel.add(this.inactiveSet, "1, 6, fill, top");
		
		this.panel_1 = new JPanel();
		this.panel_1.setBorder(new TitledBorder(null, "Add", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		add(this.panel_1, BorderLayout.CENTER);
		this.panel_1.setLayout(new BorderLayout(0, 0));
		
		this.scrollPane = new JScrollPane();
		this.panel_1.add(this.scrollPane, BorderLayout.CENTER);
		
		this.list = new JList();
		this.scrollPane.setViewportView(this.list);
		
		this.panel_2 = new JPanel();
		this.panel_1.add(this.panel_2, BorderLayout.EAST);
		this.panel_2.setLayout(new GridLayout(0, 1, 0, 0));
		
		this.btnActive = new JButton("Active");
		this.btnActive.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				addToActive();
			}
		});
		this.panel_2.add(this.btnActive);
		
		this.btnInactive = new JButton("Inactive");
		this.btnInactive.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				addToInactive();
			}
		});
		this.panel_2.add(this.btnInactive);
		
		updateValues();
	}
	
	protected void addToActive()
	{
		Object values[] = this.list.getSelectedValues();
		if (values.length == 0)
			return;
		
		int addedCount = 0;
		for (Object o : values)
		{
			String value = (String) o;
			if (!this.set.yes.contains(value))
			{
				this.set.yes.add(value);
				addedCount = addedCount + 1;
			}
		}
		
		if (addedCount > 0)
		{
			flagChange();
		}
	}
	
	protected void addToInactive()
	{
		Object values[] = this.list.getSelectedValues();
		if (values.length == 0)
			return;
		
		int addedCount = 0;
		for (Object o : values)
		{
			String value = (String) o;
			if (!this.set.no.contains(value))
			{
				this.set.no.add(value);
				addedCount = addedCount + 1;
			}
		}
		
		if (addedCount > 0)
		{
			flagChange();
		}
	}
	
	private void fillWithValues()
	{
		Set<String> unused = new TreeSet<String>(this.edit.getSerialRoot().set.keySet());
		unused.removeAll(this.set.yes);
		unused.removeAll(this.set.no);
		
		this.list.removeAll();
		this.list.setListData(unused.toArray(new String[unused.size()]));
	}
	
	@Override
	public void flagChange()
	{
		this.edit.flagChange();
		updateValues();
	}
	
	private void updateValues()
	{
		fillWithValues();
		this.activeSet.fillWithValues();
		this.inactiveSet.fillWithValues();
	}
}
