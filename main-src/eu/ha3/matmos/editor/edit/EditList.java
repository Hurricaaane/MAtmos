package eu.ha3.matmos.editor.edit;

import eu.ha3.matmos.editor.interfaces.IFlaggable;
import eu.ha3.matmos.jsonformat.serializable.expansion.SerialList;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/*
--filenotes-placeholder
*/

@SuppressWarnings("serial")
public class EditList extends JPanel implements IFlaggable
{
	private final EditPanel edit;
	private final SerialList serialList;
	private SetRemoverPanel listRemover;
	private JTextField textField;
	
	public EditList(EditPanel parentConstruct, SerialList serialListConstruct)
	{
		this.edit = parentConstruct;
		this.serialList = serialListConstruct;
		setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "Internal", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		add(panel, BorderLayout.CENTER);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] { 100, 0 };
		gbl_panel.rowHeights = new int[] { 14, 130, 0 };
		gbl_panel.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_panel.rowWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		panel.setLayout(gbl_panel);
		
		JLabel lblMustBeActive = new JLabel("List contents:");
		GridBagConstraints gbc_lblMustBeActive = new GridBagConstraints();
		gbc_lblMustBeActive.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblMustBeActive.insets = new Insets(0, 0, 5, 0);
		gbc_lblMustBeActive.gridx = 0;
		gbc_lblMustBeActive.gridy = 0;
		panel.add(lblMustBeActive, gbc_lblMustBeActive);
		
		this.listRemover = new SetRemoverPanel(this, this.serialList.entries);
		GridBagConstraints gbc_listRemover = new GridBagConstraints();
		gbc_listRemover.fill = GridBagConstraints.BOTH;
		gbc_listRemover.gridx = 0;
		gbc_listRemover.gridy = 1;
		panel.add(this.listRemover, gbc_listRemover);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(null, "Add", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		add(panel_1, BorderLayout.SOUTH);
		panel_1.setLayout(new BorderLayout(0, 0));
		
		this.textField = new JTextField();
		this.textField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				addLine();
			}
		});
		panel_1.add(this.textField, BorderLayout.CENTER);
		this.textField.setColumns(10);
		
		JButton btnAddenter = new JButton("Add (ENTER)");
		btnAddenter.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				addLine();
			}
		});
		panel_1.add(btnAddenter, BorderLayout.EAST);
		
		this.listRemover.getList().addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent arg0)
			{
				pickupSelection();
			}
		});
		
		updateValues();
	}
	
	private void pickupSelection()
	{
		Object value = this.listRemover.getList().getSelectedValue();
		if (value == null || !(value instanceof String))
			return;
		
		this.textField.setText((String) value);
	}
	
	private void addLine()
	{
		String cts = this.textField.getText();
		if (cts.equals(""))
			return;
		
		if (this.serialList.entries.contains(cts))
		{
			this.listRemover.getList().setSelectedValue(cts, true);
			return;
		}
		
		this.serialList.entries.add(cts);
		flagChange();
		this.listRemover.getList().setSelectedValue(cts, true);
	}
	
	@Override
	public void flagChange()
	{
		this.edit.flagChange();
		updateValues();
	}
	
	private void updateValues()
	{
		this.listRemover.fillWithValues();
	}
}