package eu.ha3.matmos.editor;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import eu.ha3.matmos.editor.interfaces.ISerialUpdate;
import eu.ha3.matmos.editor.interfaces.serial.ILDETrioSerial;
import eu.ha3.matmos.jsonformat.serializable.SerialRoot;

/*
--filenotes-placeholder
*/

@SuppressWarnings("serial")
public class LDEPanelSimpler extends JPanel implements ILDETrioSerial
{
	private ListerPanel list;
	private ListerPanel dynamic;
	private ListerPanel event;
	
	public LDEPanelSimpler()
	{
		setLayout(new BorderLayout(0, 0));
		
		JSplitPane machineSplit = new JSplitPane();
		machineSplit.setResizeWeight(0.7);
		add(machineSplit);
		
		JSplitPane conditionalSplit = new JSplitPane();
		conditionalSplit.setResizeWeight(0.7);
		machineSplit.setLeftComponent(conditionalSplit);
		
		this.list = new ListerPanel() {
			@Override
			public void updateSerial(SerialRoot root)
			{
				updateWith(root.list);
			}
		};
		this.list.setBorder(null);
		this.list.setTitle("Lists");
		conditionalSplit.setLeftComponent(this.list);
		
		this.dynamic = new ListerPanel() {
			@Override
			public void updateSerial(SerialRoot root)
			{
				updateWith(root.dynamic);
			}
		};
		this.dynamic.setBorder(null);
		this.dynamic.setTitle("Dynamics");
		conditionalSplit.setRightComponent(this.dynamic);
		
		this.event = new ListerPanel() {
			@Override
			public void updateSerial(SerialRoot root)
			{
				updateWith(root.event);
			}
		};
		BorderLayout borderLayout = (BorderLayout) this.event.getLayout();
		borderLayout.setVgap(1);
		borderLayout.setHgap(1);
		this.event.setBorder(null);
		this.event.setTitle("Events");
		machineSplit.setRightComponent(this.event);
	}
	
	@Override
	public ISerialUpdate getList()
	{
		return this.list;
	}
	
	@Override
	public ISerialUpdate getDynamic()
	{
		return this.dynamic;
	}
	
	@Override
	public ISerialUpdate getEvent()
	{
		return this.event;
	}
}