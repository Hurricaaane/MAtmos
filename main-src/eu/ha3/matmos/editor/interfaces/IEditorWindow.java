package eu.ha3.matmos.editor.interfaces;

import java.awt.Component;

import eu.ha3.matmos.editor.interfaces.serial.IConditionSerial;
import eu.ha3.matmos.editor.interfaces.serial.IMachineSerial;
import eu.ha3.matmos.editor.interfaces.serial.ISetSerial;

/*
--filenotes-placeholder
*/

public interface IEditorWindow extends ISerialUpdate, IConditionSerial, ISetSerial, IMachineSerial
{
	public Component asComponent();
	
	public void display();
	
	public void refreshFileState();
}
