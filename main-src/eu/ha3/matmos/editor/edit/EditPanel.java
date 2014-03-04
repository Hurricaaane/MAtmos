package eu.ha3.matmos.editor.edit;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import eu.ha3.matmos.editor.interfaces.EditorModel;
import eu.ha3.matmos.editor.interfaces.IEditNamedItem;

/*
--filenotes-placeholder
*/

@SuppressWarnings("serial")
public class EditPanel extends JPanel implements IEditNamedItem
{
	private final EditorModel model;
	
	private boolean noPane = true;
	private String nameOfItem = "";
	private Object editFocus = null;
	
	//
	
	private JTextField textField;
	private JButton btnRename;
	private JButton btnDelete;
	
	public EditPanel(EditorModel modelConstruct)
	{
		this.model = modelConstruct;
		setLayout(new BorderLayout(0, 0));
		
		JPanel name = new JPanel();
		add(name, BorderLayout.NORTH);
		name.setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(
			UIManager.getBorder("TitledBorder.border"), "Editing item", TitledBorder.LEADING, TitledBorder.TOP, null,
			null));
		name.add(panel);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 86, 71, 63, 0 };
		gridBagLayout.rowHeights = new int[] { 23, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, 0.0, 0.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
		panel.setLayout(gridBagLayout);
		
		this.textField = new JTextField();
		this.textField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent e)
			{
				evaluateRename();
			}
			
			@Override
			public void removeUpdate(DocumentEvent e)
			{
				evaluateRename();
			}
			
			@Override
			public void insertUpdate(DocumentEvent e)
			{
				evaluateRename();
			}
		});
		this.textField.setFont(new Font("Tahoma", Font.BOLD, 12));
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(0, 0, 0, 5);
		gbc.gridx = 0;
		gbc.gridy = 0;
		panel.add(this.textField, gbc);
		this.textField.setColumns(10);
		
		this.btnRename = new JButton("Rename");
		GridBagConstraints gbc_1 = new GridBagConstraints();
		gbc_1.fill = GridBagConstraints.VERTICAL;
		gbc_1.anchor = GridBagConstraints.WEST;
		gbc_1.insets = new Insets(0, 0, 0, 5);
		gbc_1.gridx = 1;
		gbc_1.gridy = 0;
		panel.add(this.btnRename, gbc_1);
		this.btnRename.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				EditPanel.this.model.renameItem(
					EditPanel.this.nameOfItem, EditPanel.this.editFocus, EditPanel.this.textField.getText());
			}
		});
		
		this.btnDelete = new JButton("Delete");
		GridBagConstraints gbc_2 = new GridBagConstraints();
		gbc_2.fill = GridBagConstraints.VERTICAL;
		gbc_2.anchor = GridBagConstraints.WEST;
		gbc_2.gridx = 2;
		gbc_2.gridy = 0;
		panel.add(this.btnDelete, gbc_2);
		this.btnDelete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				int saveOption =
					JOptionPane.showConfirmDialog(
						EditPanel.this, "Are you sure you want to delete the following item:\n"
							+ EditPanel.this.nameOfItem + "\n("
							+ EditPanel.this.editFocus.getClass().getSimpleName().replace("Serial", "") + ")",
						"Deleting item", JOptionPane.CANCEL_OPTION);
				
				if (saveOption == JOptionPane.YES_OPTION)
				{
					EditPanel.this.model.deleteItem(EditPanel.this.nameOfItem, EditPanel.this.editFocus);
				}
				
			}
		});
		
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
		this.btnRename.setEnabled(false);
		this.btnDelete.setEnabled(true);
	}
	
	private void noPane()
	{
		this.noPane = true;
		this.textField.setText("<no item selected>");
		this.textField.setEditable(false);
		this.btnRename.setEnabled(false);
		this.btnDelete.setEnabled(false);
	}
	
	private void evaluateRename()
	{
		if (this.noPane)
		{
			this.btnRename.setEnabled(false);
			return;
		}
		
		if (this.textField.getText().equals(this.nameOfItem) || !isValidName(this.textField.getText()))
		{
			this.btnRename.setEnabled(false);
		}
		else
		{
			this.btnRename.setEnabled(true);
		}
	}
}
