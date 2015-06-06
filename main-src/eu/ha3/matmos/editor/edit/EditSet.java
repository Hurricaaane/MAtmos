package eu.ha3.matmos.editor.edit;

import eu.ha3.matmos.editor.interfaces.IFlaggable;
import eu.ha3.matmos.jsonformat.serializable.expansion.SerialSet;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;
import java.util.TreeSet;

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
		GridBagLayout gbl_activationPanel = new GridBagLayout();
		gbl_activationPanel.columnWidths = new int[] { 438, 0 };
		gbl_activationPanel.rowHeights = new int[] { 14, 60, 14, 60, 105, 0 };
		gbl_activationPanel.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_activationPanel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		activationPanel.setLayout(gbl_activationPanel);
		
		JLabel lblMustBeActive = new JLabel("Must be active:");
		GridBagConstraints gbc_lblMustBeActive = new GridBagConstraints();
		gbc_lblMustBeActive.anchor = GridBagConstraints.WEST;
		gbc_lblMustBeActive.insets = new Insets(0, 0, 5, 0);
		gbc_lblMustBeActive.gridx = 0;
		gbc_lblMustBeActive.gridy = 0;
		activationPanel.add(lblMustBeActive, gbc_lblMustBeActive);
		
		this.activeSet = new SetRemoverPanel(this, this.set.yes);
		GridBagConstraints gbc_activeSet = new GridBagConstraints();
		gbc_activeSet.fill = GridBagConstraints.BOTH;
		gbc_activeSet.insets = new Insets(0, 0, 5, 0);
		gbc_activeSet.gridx = 0;
		gbc_activeSet.gridy = 1;
		activationPanel.add(this.activeSet, gbc_activeSet);
		
		JLabel lblMustBeInactive = new JLabel("Must be inactive:");
		GridBagConstraints gbc_lblMustBeInactive = new GridBagConstraints();
		gbc_lblMustBeInactive.anchor = GridBagConstraints.WEST;
		gbc_lblMustBeInactive.insets = new Insets(0, 0, 5, 0);
		gbc_lblMustBeInactive.gridx = 0;
		gbc_lblMustBeInactive.gridy = 2;
		activationPanel.add(lblMustBeInactive, gbc_lblMustBeInactive);
		
		this.inactiveSet = new SetRemoverPanel(this, this.set.no);
		GridBagConstraints gbc_inactiveSet = new GridBagConstraints();
		gbc_inactiveSet.fill = GridBagConstraints.BOTH;
		gbc_inactiveSet.insets = new Insets(0, 0, 5, 0);
		gbc_inactiveSet.gridx = 0;
		gbc_inactiveSet.gridy = 3;
		activationPanel.add(this.inactiveSet, gbc_inactiveSet);
		
		JPanel panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 4;
		activationPanel.add(panel, gbc_panel);
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
