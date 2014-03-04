package eu.ha3.matmos.editor.edit;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import eu.ha3.matmos.editor.interfaces.EditorModel;

/*
--filenotes-placeholder
*/

@SuppressWarnings("serial")
public class EditPanel extends JPanel implements IEditNamedItem
{
	private EditorModel model;
	
	private boolean noPane = true;
	private String nameOfItem = "";
	private Object editFocus = null;
	
	//
	
	private JTextField textField;
	private JButton btnRename;
	private JButton btnDelete;
	
	public EditPanel(EditorModel model)
	{
		this.model = model;
		setLayout(new BorderLayout(0, 0));
		
		JPanel name = new JPanel();
		add(name, BorderLayout.NORTH);
		GridBagLayout gbl_name = new GridBagLayout();
		gbl_name.columnWidths = new int[] { 0, 0, 0, 0, 0 };
		gbl_name.rowHeights = new int[] { 0, 0 };
		gbl_name.columnWeights = new double[] { 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE };
		gbl_name.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
		name.setLayout(gbl_name);
		
		JLabel lblNowEditing = new JLabel("Now editing:");
		GridBagConstraints gbc_lblNowEditing = new GridBagConstraints();
		gbc_lblNowEditing.insets = new Insets(0, 0, 0, 5);
		gbc_lblNowEditing.anchor = GridBagConstraints.EAST;
		gbc_lblNowEditing.gridx = 0;
		gbc_lblNowEditing.gridy = 0;
		name.add(lblNowEditing, gbc_lblNowEditing);
		
		this.textField = new JTextField();
		this.textField.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent arg0)
			{
				if (EditPanel.this.noPane)
				{
					EditPanel.this.btnRename.setEnabled(false);
					return;
				}
				
				if (EditPanel.this.textField.getText().equals(EditPanel.this.nameOfItem)
					|| !isValidName(EditPanel.this.textField.getText()))
				{
					EditPanel.this.btnRename.setEnabled(false);
				}
				else
				{
					EditPanel.this.btnRename.setEnabled(true);
				}
			}
		});
		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.insets = new Insets(0, 0, 0, 5);
		gbc_textField.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField.gridx = 1;
		gbc_textField.gridy = 0;
		name.add(this.textField, gbc_textField);
		this.textField.setColumns(10);
		
		this.btnRename = new JButton("Rename");
		this.btnRename.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
			}
		});
		GridBagConstraints gbc_btnRename = new GridBagConstraints();
		gbc_btnRename.insets = new Insets(0, 0, 0, 5);
		gbc_btnRename.gridx = 2;
		gbc_btnRename.gridy = 0;
		name.add(this.btnRename, gbc_btnRename);
		
		this.btnDelete = new JButton("Delete");
		this.btnDelete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
			}
		});
		GridBagConstraints gbc_btnDelete = new GridBagConstraints();
		gbc_btnDelete.gridx = 3;
		gbc_btnDelete.gridy = 0;
		name.add(this.btnDelete, gbc_btnDelete);
		
		JPanel editor = new JPanel();
		add(editor, BorderLayout.CENTER);
		
		refreshPane();
	}
	
	protected boolean isValidName(String text)
	{
		return !text.equals("");
	}
	
	@Override
	public void setEditFocus(String name, Object item)
	{
		this.nameOfItem = name;
		this.editFocus = item;
		
		refreshPane();
	}
	
	private void refreshPane()
	{
		if (this.nameOfItem == null || this.editFocus == null)
		{
			noPane();
			return;
		}
		
		this.noPane = false;
		this.textField.setEditable(true);
		this.textField.setText(this.nameOfItem);
		this.btnDelete.setEnabled(true);
	}
	
	private void noPane()
	{
		this.noPane = true;
		this.textField.setText("<nothing to edit>");
		this.textField.setEditable(false);
		this.btnRename.setEnabled(false);
		this.btnDelete.setEnabled(false);
	}
}
