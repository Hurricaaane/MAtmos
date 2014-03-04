package eu.ha3.matmos.editor.edit;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import eu.ha3.matmos.editor.InstantTextField;
import eu.ha3.matmos.engine.core.interfaces.Operator;
import eu.ha3.matmos.jsonformat.serializable.SerialCondition;

/*
--filenotes-placeholder
*/

public class EditCondition extends JPanel
{
	private final SerialCondition condition;
	private InstantTextField sheet;
	private InstantTextField index;
	private InstantTextField value;
	private JComboBox comboBox;
	
	public EditCondition(EditPanel parent, SerialCondition condition)
	{
		this.condition = condition;
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 50, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);
		
		JLabel lblSheet = new JLabel("Sheet");
		GridBagConstraints gbc_lblSheet = new GridBagConstraints();
		gbc_lblSheet.anchor = GridBagConstraints.EAST;
		gbc_lblSheet.insets = new Insets(0, 0, 5, 5);
		gbc_lblSheet.gridx = 0;
		gbc_lblSheet.gridy = 0;
		add(lblSheet, gbc_lblSheet);
		
		this.sheet = new InstantTextField();
		GridBagConstraints gbc_sheet = new GridBagConstraints();
		gbc_sheet.insets = new Insets(0, 0, 5, 0);
		gbc_sheet.fill = GridBagConstraints.HORIZONTAL;
		gbc_sheet.gridx = 1;
		gbc_sheet.gridy = 0;
		add(this.sheet, gbc_sheet);
		
		JLabel lblIndex = new JLabel("Index");
		GridBagConstraints gbc_lblIndex = new GridBagConstraints();
		gbc_lblIndex.anchor = GridBagConstraints.EAST;
		gbc_lblIndex.insets = new Insets(0, 0, 5, 5);
		gbc_lblIndex.gridx = 0;
		gbc_lblIndex.gridy = 1;
		add(lblIndex, gbc_lblIndex);
		
		this.index = new InstantTextField();
		GridBagConstraints gbc_index = new GridBagConstraints();
		gbc_index.insets = new Insets(0, 0, 5, 0);
		gbc_index.fill = GridBagConstraints.HORIZONTAL;
		gbc_index.gridx = 1;
		gbc_index.gridy = 1;
		add(this.index, gbc_index);
		
		this.comboBox = new JComboBox();
		this.comboBox.setModel(new DefaultComboBoxModel(Operator.values()));
		GridBagConstraints gbc_comboBox = new GridBagConstraints();
		gbc_comboBox.insets = new Insets(0, 0, 5, 0);
		gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBox.gridx = 1;
		gbc_comboBox.gridy = 2;
		add(this.comboBox, gbc_comboBox);
		
		JLabel lblValue = new JLabel("Value");
		GridBagConstraints gbc_lblValue = new GridBagConstraints();
		gbc_lblValue.insets = new Insets(0, 0, 0, 5);
		gbc_lblValue.anchor = GridBagConstraints.EAST;
		gbc_lblValue.gridx = 0;
		gbc_lblValue.gridy = 3;
		add(lblValue, gbc_lblValue);
		
		this.value = new InstantTextField();
		GridBagConstraints gbc_value = new GridBagConstraints();
		gbc_value.fill = GridBagConstraints.HORIZONTAL;
		gbc_value.gridx = 1;
		gbc_value.gridy = 3;
		add(this.value, gbc_value);
		
		updateValues();
	}
	
	private void updateValues()
	{
		this.sheet.setText(this.condition.sheet);
		this.index.setText(this.condition.index);
		this.comboBox.setSelectedItem(Operator.fromSerializedForm(this.condition.symbol));
		this.value.setText(this.condition.value);
	}
}
