package eu.ha3.matmos.editor;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/*
--filenotes-placeholder
*/

@SuppressWarnings("serial")
public class InstantTextField extends JTextField
{
	public InstantTextField()
	{
		super();
		
		getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent e)
			{
				editEvent();
			}
			
			@Override
			public void removeUpdate(DocumentEvent e)
			{
				editEvent();
			}
			
			@Override
			public void insertUpdate(DocumentEvent e)
			{
				editEvent();
			}
		});
	}
	
	protected void editEvent()
	{
	};
}
