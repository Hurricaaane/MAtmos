package eu.ha3.matmos.editor.tree;

import eu.ha3.matmos.editor.interfaces.EditorModel;
import eu.ha3.matmos.engine0.core.implem.abstractions.Provider;
import eu.ha3.matmos.engine0.core.implem.abstractions.ProviderCollection;
import eu.ha3.matmos.engine0.core.interfaces.Stated;

/*
--filenotes-placeholder
*/

@SuppressWarnings("serial")
public class ConditionTreeNode extends StateReaderTreeNode
{
	public ConditionTreeNode(String name)
	{
		super(name);
	}
	
	@Override
	public void update(EditorModel model)
	{
	}
	
	@Override
	protected Provider<? extends Stated> getProvider(ProviderCollection collection)
	{
		return collection.getCondition();
	}
}
