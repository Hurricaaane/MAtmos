package eu.ha3.matmos.editor.edit;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

import eu.ha3.matmos.editor.InstantTextField;
import eu.ha3.matmos.engine.core.interfaces.Operator;
import eu.ha3.matmos.jsonformat.serializable.SerialCondition;

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
	private JPanel panel;
	private JPanel panel_1;
	private JButton btnSheet;
	private JButton btnDynamic;
	private JButton btnSelectList;
	
	public EditCondition(EditPanel parentConstruct, SerialCondition conditionConstruct)
	{
		this.edit = parentConstruct;
		this.condition = conditionConstruct;
		setLayout(new BorderLayout(0, 0));
		
		this.panel = new JPanel();
		this.panel.setBorder(new TitledBorder(null, "Internal", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		add(this.panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] { 50, 0, 0 };
		gbl_panel.rowHeights = new int[] { 0, 0, 0, 0, 0 };
		gbl_panel.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		gbl_panel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		this.panel.setLayout(gbl_panel);
		
		JLabel lblSheet = new JLabel("Sheet");
		GridBagConstraints gbc_lblSheet = new GridBagConstraints();
		gbc_lblSheet.anchor = GridBagConstraints.EAST;
		gbc_lblSheet.insets = new Insets(0, 0, 5, 5);
		gbc_lblSheet.gridx = 0;
		gbc_lblSheet.gridy = 0;
		this.panel.add(lblSheet, gbc_lblSheet);
		
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
		this.panel.add(this.sheet, gbc_sheet);
		
		JLabel lblIndex = new JLabel("Index");
		GridBagConstraints gbc_lblIndex = new GridBagConstraints();
		gbc_lblIndex.anchor = GridBagConstraints.EAST;
		gbc_lblIndex.insets = new Insets(0, 0, 5, 5);
		gbc_lblIndex.gridx = 0;
		gbc_lblIndex.gridy = 1;
		this.panel.add(lblIndex, gbc_lblIndex);
		
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
		this.panel.add(this.index, gbc_index);
		
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
		this.panel.add(this.comboBox, gbc_comboBox);
		this.comboBox.setModel(new DefaultComboBoxModel(Operator.values()));
		
		JLabel lblValue = new JLabel("Value");
		GridBagConstraints gbc_lblValue = new GridBagConstraints();
		gbc_lblValue.anchor = GridBagConstraints.EAST;
		gbc_lblValue.insets = new Insets(0, 0, 0, 5);
		gbc_lblValue.gridx = 0;
		gbc_lblValue.gridy = 3;
		this.panel.add(lblValue, gbc_lblValue);
		
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
		this.panel.add(this.value, gbc_value);
		
		this.panel_1 = new JPanel();
		this.panel_1.setBorder(new TitledBorder(
			UIManager.getBorder("TitledBorder.border"), "Options", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		add(this.panel_1, BorderLayout.SOUTH);
		this.panel_1.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		this.btnSheet = new JButton("Sheet...");
		this.panel_1.add(this.btnSheet);
		
		this.btnDynamic = new JButton("Dynamic...");
		this.panel_1.add(this.btnDynamic);
		
		this.btnSelectList = new JButton("List...");
		this.panel_1.add(this.btnSelectList);
		
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
