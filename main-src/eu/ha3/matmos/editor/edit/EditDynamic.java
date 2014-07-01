package eu.ha3.matmos.editor.edit;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import eu.ha3.matmos.editor.interfaces.IFlaggable;
import eu.ha3.matmos.jsonformat.serializable.expansion.SerialDynamic;
import eu.ha3.matmos.jsonformat.serializable.expansion.SerialDynamicSheetIndex;

/*
--filenotes-placeholder
*/

@SuppressWarnings("serial")
public class EditDynamic extends JPanel implements IFlaggable
{
	private final EditPanel edit;
	private final SerialDynamic serialDynamic;
	private DynamicRemoverPanel listRemover;
	private JTextField textFieldSheet;
	private JTextField textFieldIndex;
	
	public EditDynamic(EditPanel parentConstruct, SerialDynamic serialDynamicConstruct)
	{
		this.edit = parentConstruct;
		this.serialDynamic = serialDynamicConstruct;
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
		
		this.listRemover = new DynamicRemoverPanel(this, this.serialDynamic);
		GridBagConstraints gbc_listRemover = new GridBagConstraints();
		gbc_listRemover.fill = GridBagConstraints.BOTH;
		gbc_listRemover.gridx = 0;
		gbc_listRemover.gridy = 1;
		panel.add(this.listRemover, gbc_listRemover);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(null, "Add", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		add(panel_1, BorderLayout.SOUTH);
		panel_1.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_2 = new JPanel();
		panel_1.add(panel_2, BorderLayout.CENTER);
		GridBagLayout gbl_panel_2 = new GridBagLayout();
		gbl_panel_2.columnWidths = new int[] { 86, 0, 0 };
		gbl_panel_2.rowHeights = new int[] { 20, 0 };
		gbl_panel_2.columnWeights = new double[] { 1.0, 1.0, Double.MIN_VALUE };
		gbl_panel_2.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
		panel_2.setLayout(gbl_panel_2);
		
		this.textFieldSheet = new JTextField();
		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.fill = GridBagConstraints.BOTH;
		gbc_textField.insets = new Insets(0, 0, 0, 5);
		gbc_textField.gridx = 0;
		gbc_textField.gridy = 0;
		panel_2.add(this.textFieldSheet, gbc_textField);
		this.textFieldSheet.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				addLine();
			}
		});
		this.textFieldSheet.setColumns(10);
		
		this.textFieldIndex = new JTextField();
		GridBagConstraints gbc_textField_1 = new GridBagConstraints();
		gbc_textField_1.fill = GridBagConstraints.BOTH;
		gbc_textField_1.gridx = 1;
		gbc_textField_1.gridy = 0;
		panel_2.add(this.textFieldIndex, gbc_textField_1);
		this.textFieldIndex.setColumns(10);
		
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
		int value = this.listRemover.getList().getSelectedIndex();
		if (value == -1 || value >= this.serialDynamic.entries.size())
			return;
		
		this.textFieldSheet.setText(this.serialDynamic.entries.get(value).sheet);
		this.textFieldIndex.setText(this.serialDynamic.entries.get(value).index);
	}
	
	private void addLine()
	{
		String ctsOfSheet = this.textFieldSheet.getText();
		if (ctsOfSheet.equals(""))
			return;
		
		String ctsOfIndex = this.textFieldIndex.getText();
		if (ctsOfIndex.equals(""))
			return;
		
		this.serialDynamic.entries.add(new SerialDynamicSheetIndex(ctsOfSheet, ctsOfIndex));
		flagChange();
		this.listRemover.getList().setSelectedValue(ctsOfSheet + "@" + ctsOfIndex, true);
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