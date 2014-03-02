package eu.ha3.matmos.editor.tree;

import javax.swing.tree.DefaultMutableTreeNode;

import eu.ha3.matmos.editor.interfaces.EditorModel;
import eu.ha3.matmos.editor.interfaces.IStateUpdatable;
import eu.ha3.matmos.editor.interfaces.IUpdatable;
import eu.ha3.matmos.engine.core.implem.abstractions.Provider;
import eu.ha3.matmos.engine.core.implem.abstractions.ProviderCollection;
import eu.ha3.matmos.engine.core.interfaces.Stated;

/*
--filenotes-placeholder
*/

@SuppressWarnings("serial")
public abstract class StateReaderTreeNode extends DefaultMutableTreeNode implements Stated, IUpdatable, IStateUpdatable
{
	protected final String name;
	protected boolean isActive;
	
	public StateReaderTreeNode(String name)
	{
		this.name = name;
	}
	
	@Override
	public boolean isActive()
	{
		return this.isActive;
	}
	
	@Override
	public void updateState(EditorModel model)
	{
		if (!model.isMinecraftControlled())
			return;
		
		this.isActive = getProvider(model.getProviderCollection()).exists(this.name);
	}
	
	abstract protected Provider<? extends Stated> getProvider(ProviderCollection collection);
}
