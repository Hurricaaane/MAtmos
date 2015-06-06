package eu.ha3.matmos.editor.edit;

import eu.ha3.matmos.editor.InstantTextField;
import eu.ha3.matmos.engine.core.interfaces.Operator;
import eu.ha3.matmos.jsonformat.serializable.expansion.SerialCondition;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/*
--filenotes-placeholder
*/

@SuppressWarnings("serial")
public class EditCondition extends JPanel
{
	private final EditPanel edit;
	private final SerialCondition condition;
	
	//
	
	private InstantTextField sheet;
	private InstantTextField index;
	private InstantTextField value;
	private JComboBox comboBox;
	
	public EditCondition(EditPanel parentConstruct, SerialCondition conditionConstruct)
	{
		this.edit = parentConstruct;
		this.condition = conditionConstruct;
		setLayout(new BorderLayout(0, 0));
		
		JPanel activation = new JPanel();
		activation.setBorder(new TitledBorder(
			UIManager.getBorder("TitledBorder.border"), "Activation", TitledBorder.LEADING, TitledBorder.TOP, null,
			null));
		add(activation);
		GridBagLayout gbl_activation = new GridBagLayout();
		gbl_activation.columnWidths = new int[] { 50, 0, 0 };
		gbl_activation.rowHeights = new int[] { 0, 0, 0, 0, 0 };
		gbl_activation.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		gbl_activation.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		activation.setLayout(gbl_activation);
		
		JLabel lblSheet = new JLabel("Sheet");
		GridBagConstraints gbc_lblSheet = new GridBagConstraints();
		gbc_lblSheet.anchor = GridBagConstraints.EAST;
		gbc_lblSheet.insets = new Insets(0, 0, 5, 5);
		gbc_lblSheet.gridx = 0;
		gbc_lblSheet.gridy = 0;
		activation.add(lblSheet, gbc_lblSheet);
		
		this.sheet = new InstantTextField() {
			@Override
			protected void editEvent()
			{
				String text = getText();
				if (!EditCondition.this.condition.sheet.equals(text))
				{
					EditCondition.this.condition.sheet = text;
					EditCondition.this.edit.flagChange();
				}
			};
		};
		GridBagConstraints gbc_sheet = new GridBagConstraints();
		gbc_sheet.fill = GridBagConstraints.HORIZONTAL;
		gbc_sheet.insets = new Insets(0, 0, 5, 0);
		gbc_sheet.gridx = 1;
		gbc_sheet.gridy = 0;
		activation.add(this.sheet, gbc_sheet);
		
		JLabel lblIndex = new JLabel("Index");
		GridBagConstraints gbc_lblIndex = new GridBagConstraints();
		gbc_lblIndex.anchor = GridBagConstraints.EAST;
		gbc_lblIndex.insets = new Insets(0, 0, 5, 5);
		gbc_lblIndex.gridx = 0;
		gbc_lblIndex.gridy = 1;
		activation.add(lblIndex, gbc_lblIndex);
		
		this.index = new InstantTextField() {
			@Override
			protected void editEvent()
			{
				String text = getText();
				if (!EditCondition.this.condition.index.equals(text))
				{
					EditCondition.this.condition.index = text;
					EditCondition.this.edit.flagChange();
				}
			};
		};
		GridBagConstraints gbc_index = new GridBagConstraints();
		gbc_index.fill = GridBagConstraints.HORIZONTAL;
		gbc_index.insets = new Insets(0, 0, 5, 0);
		gbc_index.gridx = 1;
		gbc_index.gridy = 1;
		activation.add(this.index, gbc_index);
		
		this.comboBox = new JComboBox();
		this.comboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				if (Operator.fromSerializedForm(EditCondition.this.condition.symbol) != EditCondition.this.comboBox
					.getSelectedItem())
				{
					EditCondition.this.condition.symbol =
						((Operator) EditCondition.this.comboBox.getSelectedItem()).getSerializedForm();
					EditCondition.this.edit.flagChange();
				}
			}
		});
		GridBagConstraints gbc_comboBox = new GridBagConstraints();
		gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBox.insets = new Insets(0, 0, 5, 0);
		gbc_comboBox.gridx = 1;
		gbc_comboBox.gridy = 2;
		activation.add(this.comboBox, gbc_comboBox);
		this.comboBox.setModel(new DefaultComboBoxModel(Operator.values()));
		
		JLabel lblValue = new JLabel("Value");
		GridBagConstraints gbc_lblValue = new GridBagConstraints();
		gbc_lblValue.anchor = GridBagConstraints.EAST;
		gbc_lblValue.insets = new Insets(0, 0, 0, 5);
		gbc_lblValue.gridx = 0;
		gbc_lblValue.gridy = 3;
		activation.add(lblValue, gbc_lblValue);
		
		this.value = new InstantTextField() {
			@Override
			protected void editEvent()
			{
				String text = getText();
				if (!EditCondition.this.condition.value.equals(text))
				{
					EditCondition.this.condition.value = text;
					EditCondition.this.edit.flagChange();
				}
			};
		};
		GridBagConstraints gbc_value = new GridBagConstraints();
		gbc_value.fill = GridBagConstraints.HORIZONTAL;
		gbc_value.gridx = 1;
		gbc_value.gridy = 3;
		activation.add(this.value, gbc_value);
		
		JPanel options = new JPanel();
		options.setBorder(new TitledBorder(
			UIManager.getBorder("TitledBorder.border"), "Options", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		add(options, BorderLayout.SOUTH);
		options.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JButton btnSheet = new JButton("Sheet...");
		options.add(btnSheet);
		
		JButton btnDynamic = new JButton("Dynamic...");
		options.add(btnDynamic);
		
		JButton btnSelectList = new JButton("List...");
		options.add(btnSelectList);
		
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
