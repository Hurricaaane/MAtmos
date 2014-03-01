package eu.ha3.matmos.editor;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * A simplified file chooser. There is zero handling of actions performed when a
 * file has been chosen or not for simplicity's sake.
 * 
 * @author Hurry
 * 
 */
public class FileChooser extends JPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8411093771682526789L;
	private JTextField textField;
	
	public FileChooser()
	{
		setLayout(new BorderLayout(0, 0));
		
		JButton btnBrowse = new JButton("Browse...");
		btnBrowse.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				dialog();
				
			}
		});
		add(btnBrowse, BorderLayout.EAST);
		
		textField = new JTextField();
		add(textField, BorderLayout.CENTER);
		textField.setColumns(10);
	}
	
	/**
	 * Sets the contents of the text field.
	 * 
	 * @param contents
	 */
	public void setContents(String contents)
	{
		textField.setText(contents);
		
	}
	
	/**
	 * Gets the contents of the text field.
	 * 
	 * @param contents
	 */
	public String getContents()
	{
		return textField.getText();
		
	}
	
	private void dialog()
	{
		JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));
		int returnValue = fc.showOpenDialog(this);
		if (returnValue == JFileChooser.APPROVE_OPTION)
		{
			File file = fc.getSelectedFile();
			usingFile(file);
			
		}
		else
		{
			// do nothing
		}
		
	}
	
	private void usingFile(File file)
	{
		textField.setText(file.getAbsolutePath());
		
	}
	
}
