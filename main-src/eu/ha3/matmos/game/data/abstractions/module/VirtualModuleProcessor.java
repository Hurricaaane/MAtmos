package eu.ha3.matmos.game.data.abstractions.module;

import eu.ha3.matmos.engine.core.interfaces.Data;

/*
--filenotes-placeholder
*/

/**
 * Implements doProcess with nothing inside it. This class is used for
 * placeholder modules where the logic of updating the sheet is outsourced to
 * another logic. One should call the set...() methods of this class to prepare
 * the new values, and calling process() will apply the virtual sheets that this
 * module processor contains.
 * 
 * @author Hurry
 */
public class VirtualModuleProcessor extends ModuleProcessor
{
	public VirtualModuleProcessor(Data data, String name)
	{
		super(data, name);
	}
	
	public VirtualModuleProcessor(Data data, String name, boolean doNotUseDelta)
	{
		super(data, name, doNotUseDelta);
	}
	
	@Override
	protected void doProcess()
	{
	}
}
