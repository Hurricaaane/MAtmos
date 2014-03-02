package eu.ha3.matmos.editor;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import eu.ha3.matmos.editor.interfaces.ISerialUpdate;
import eu.ha3.matmos.editor.interfaces.serial.ICSMTrioSerial;
import eu.ha3.matmos.jsonformat.serializable.SerialRoot;

/*
--filenotes-placeholder
*/

@SuppressWarnings("serial")
public class CSMPanelSimpler extends JPanel implements ICSMTrioSerial
{
	private ListerPanel condition;
	private ListerPanel set;
	private ListerPanel machine;
	
	public CSMPanelSimpler()
	{
		setLayout(new BorderLayout(0, 0));
		
		JSplitPane machineSplit = new JSplitPane();
		machineSplit.setResizeWeight(0.7);
		add(machineSplit);
		
		JSplitPane conditionalSplit = new JSplitPane();
		conditionalSplit.setResizeWeight(0.7);
		machineSplit.setLeftComponent(conditionalSplit);
		
		this.condition = new ListerPanel() {
			@Override
			public void updateSerial(SerialRoot root)
			{
				updateWith(root.condition);
			}
		};
		this.condition.setBorder(null);
		this.condition.setTitle("Conditions");
		conditionalSplit.setLeftComponent(this.condition);
		
		this.set = new ListerPanel() {
			@Override
			public void updateSerial(SerialRoot root)
			{
				updateWith(root.set);
			}
		};
		this.set.setBorder(null);
		this.set.setTitle("Sets");
		conditionalSplit.setRightComponent(this.set);
		
		this.machine = new ListerPanel() {
			@Override
			public void updateSerial(SerialRoot root)
			{
				updateWith(root.machine);
			}
		};
		BorderLayout borderLayout = (BorderLayout) this.machine.getLayout();
		borderLayout.setVgap(1);
		borderLayout.setHgap(1);
		this.machine.setBorder(null);
		this.machine.setTitle("Machines");
		machineSplit.setRightComponent(this.machine);
	}
	
	@Override
	public ISerialUpdate getCondition()
	{
		return this.condition;
	}
	
	@Override
	public ISerialUpdate getSet()
	{
		return this.set;
	}
	
	@Override
	public ISerialUpdate getMachine()
	{
		return this.machine;
	}
}
