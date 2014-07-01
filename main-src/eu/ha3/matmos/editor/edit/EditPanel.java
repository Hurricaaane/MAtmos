package eu.ha3.matmos.editor.edit;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

import eu.ha3.matmos.editor.InstantTextField;
import eu.ha3.matmos.editor.interfaces.Editor;
import eu.ha3.matmos.editor.interfaces.IFlaggable;
import eu.ha3.matmos.editor.interfaces.NamedSerialEditor;
import eu.ha3.matmos.jsonformat.serializable.expansion.SerialCondition;
import eu.ha3.matmos.jsonformat.serializable.expansion.SerialDynamic;
import eu.ha3.matmos.jsonformat.serializable.expansion.SerialEvent;
import eu.ha3.matmos.jsonformat.serializable.expansion.SerialList;
import eu.ha3.matmos.jsonformat.serializable.expansion.SerialMachine;
import eu.ha3.matmos.jsonformat.serializable.expansion.SerialRoot;
import eu.ha3.matmos.jsonformat.serializable.expansion.SerialSet;

/*
--filenotes-placeholder
*/

@SuppressWarnings("serial")
public class EditPanel extends JPanel implements NamedSerialEditor, IFlaggable
{
	private final Editor model;
	
	private boolean noPane = true;
	private String nameOfItem = "";
	private Object editFocus = null;
	
	//
	
	private JTextField textField;
	private JButton btnRename;
	private JButton btnDelete;
	private JPanel editor;
	
	//
	
	private JPanel currentEdit = null;
	
	public EditPanel(Editor modelConstruct)
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
		
		this.textField = new InstantTextField() {
			@Override
			protected void editEvent()
			{
				evaluateRename();
			}
		};
		this.textField.setFont(new Font("Tahoma", Font.BOLD, 12));
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(0, 0, 0, 5);
		gbc.gridx = 0;
		gbc.gridy = 0;
		panel.add(this.textField, gbc);
		this.textField.setColumns(10);
		this.textField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				EditPanel.this.model.renameItem(
					EditPanel.this.nameOfItem, EditPanel.this.editFocus, EditPanel.this.textField.getText());
			}
		});
		
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
		
		this.editor = new JPanel();
		add(this.editor, BorderLayout.CENTER);
		this.editor.setLayout(new BorderLayout(0, 0));
		
		refreshPane();
	}
	
	protected boolean isValidName(String text)
	{
		return !text.equals("");
	}
	
	@Override
	public void setEditFocus(String name, Object item, boolean forceSelect)
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
		
		BorderLayout lay = (BorderLayout) this.editor.getLayout();
		Component c = lay.getLayoutComponent(BorderLayout.CENTER);
		if (c != null)
		{
			this.editor.remove(c);
		}
		
		if (this.editFocus instanceof SerialCondition)
		{
			showEdit(new EditCondition(this, (SerialCondition) this.editFocus));
		}
		else if (this.editFocus instanceof SerialSet)
		{
			showEdit(new EditSet(this, (SerialSet) this.editFocus));
		}
		else if (this.editFocus instanceof SerialList)
		{
			showEdit(new EditList(this, (SerialList) this.editFocus));
		}
		else if (this.editFocus instanceof SerialEvent)
		{
			showEdit(new EditEvent(this, (SerialEvent) this.editFocus));
		}
		else if (this.editFocus instanceof SerialMachine)
		{
			showEdit(new EditMachine(this, (SerialMachine) this.editFocus));
		}
		else if (this.editFocus instanceof SerialDynamic)
		{
			showEdit(new EditDynamic(this, (SerialDynamic) this.editFocus));
		}
		else
		{
			showEdit(null);
		}
	}
	
	private void showEdit(JPanel panel)
	{
		if (this.currentEdit != null)
		{
			this.editor.remove(this.currentEdit);
		}
		
		this.currentEdit = panel;
		
		if (this.currentEdit != null)
		{
			this.editor.add(this.currentEdit, BorderLayout.CENTER);
		}
		revalidate();
		repaint();
	}
	
	private void noPane()
	{
		this.noPane = true;
		this.textField.setText("<no item selected>");
		this.textField.setEditable(false);
		this.btnRename.setEnabled(false);
		this.btnDelete.setEnabled(false);
		
		showEdit(null);
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
	
	@Override
	public void flagChange()
	{
		this.model.informInnerChange();
	}
	
	public SerialRoot getSerialRoot()
	{
		return this.model.getRootForCopyPurposes();
	}
	
	public File getSoundDirectory()
	{
		return this.model.getSoundDirectory();
	}
	
	public Editor getModel()
	{
		return this.model;
	}
}
