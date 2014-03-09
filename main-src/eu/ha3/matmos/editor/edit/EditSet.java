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
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

import eu.ha3.matmos.editor.interfaces.IFlaggable;
import eu.ha3.matmos.jsonformat.serializable.expansion.SerialSet;

/*
--filenotes-placeholder
*/

@SuppressWarnings("serial")
public class EditSet extends JPanel implements IFlaggable
{
	private final EditPanel edit;
	private final SerialSet set;
	private SetRemoverPanel activeSet;
	private SetRemoverPanel inactiveSet;
	private JList list;
	
	public EditSet(EditPanel parentConstruct, SerialSet setConstruct)
	{
		this.edit = parentConstruct;
		this.set = setConstruct;
		setLayout(new BorderLayout(0, 0));
		
		JPanel activationPanel = new JPanel();
		activationPanel.setBorder(new TitledBorder(
			UIManager.getBorder("TitledBorder.border"), "Activation", TitledBorder.LEADING, TitledBorder.TOP, null,
			null));
		add(activationPanel, BorderLayout.CENTER);
		activationPanel.setLayout(new FormLayout(new ColumnSpec[] { ColumnSpec.decode("333px:grow"), }, new RowSpec[] {
			RowSpec.decode("21px"), RowSpec.decode("60px"), FormFactory.RELATED_GAP_ROWSPEC,
			FormFactory.DEFAULT_ROWSPEC, FormFactory.LINE_GAP_ROWSPEC, RowSpec.decode("60px"),
			FormFactory.RELATED_GAP_ROWSPEC, RowSpec.decode("default:grow"), }));
		
		JLabel lblMustBeActive = new JLabel("Must be active:");
		activationPanel.add(lblMustBeActive, "1, 1, left, center");
		
		this.activeSet = new SetRemoverPanel(this, this.set.yes);
		activationPanel.add(this.activeSet, "1, 2, fill, top");
		
		JLabel lblMustBeInactive = new JLabel("Must be inactive:");
		activationPanel.add(lblMustBeInactive, "1, 4, left, center");
		
		this.inactiveSet = new SetRemoverPanel(this, this.set.no);
		activationPanel.add(this.inactiveSet, "1, 6, fill, top");
		
		JPanel panel = new JPanel();
		activationPanel.add(panel, "1, 8");
		panel.setBorder(new TitledBorder(null, "Add", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		panel.add(scrollPane, BorderLayout.CENTER);
		
		this.list = new JList();
		scrollPane.setViewportView(this.list);
		
		JPanel panel_1 = new JPanel();
		panel.add(panel_1, BorderLayout.EAST);
		panel_1.setLayout(new GridLayout(0, 1, 0, 0));
		
		JButton btnActive = new JButton("Active");
		btnActive.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				addToActive();
			}
		});
		panel_1.add(btnActive);
		
		JButton btnInactive = new JButton("Inactive");
		btnInactive.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				addToInactive();
			}
		});
		panel_1.add(btnInactive);
		
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
		Set<String> unused = new TreeSet<String>(this.edit.getSerialRoot().condition.keySet());
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
