package eu.ha3.matmos.editor.filechooser;

import javax.swing.*;
import java.io.File;

/*
--missing-placeholder
*/

// Courtesy of
//   http://geek.starbean.net/?p=275
//
// << http://stackoverflow.com/questions/8581215/jfilechooser-and-checking-for-overwrite
@SuppressWarnings("serial")
public class OverwriteWarningJasonFileChooser extends JasonFileChooser
{
	public OverwriteWarningJasonFileChooser(File dir)
	{
		super(dir);
	}
	
	@Override
	public File getSelectedFile()
	{
		File selectedFile = super.getSelectedFile();
		
		if (selectedFile != null)
		{
			String name = selectedFile.getName();
			if (!name.contains("."))
			{
				selectedFile = new File(selectedFile.getParentFile(), name + '.' + this.dotlessExtension);
			}
		}
		
		return selectedFile;
	}
	
	@Override
	public void approveSelection()
	{
		if (getDialogType() == SAVE_DIALOG)
		{
			File selectedFile = getSelectedFile();
			if (selectedFile != null && selectedFile.exists())
			{
				int response =
					JOptionPane.showConfirmDialog(
						this, "The file "
							+ selectedFile.getName() + " already exists. Do you want to replace the existing file?",
						"Ovewrite file", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
				if (response != JOptionPane.YES_OPTION)
					return;
			}
		}
		
		super.approveSelection();
	}
}