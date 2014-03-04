package eu.ha3.matmos.editor;

import java.util.Map;

import eu.ha3.matmos.jsonformat.serializable.SerialCondition;
import eu.ha3.matmos.jsonformat.serializable.SerialRoot;
import eu.ha3.matmos.jsonformat.serializable.SerialSet;

/*
--filenotes-placeholder
*/

public class SerialManipulator
{
	public static void delete(SerialRoot root, Object editFocus, String itemName) throws ItemNamingException
	{
		rename(root, editFocus, itemName, null);
	}
	
	/**
	 * If newName is null, this deletes the item.
	 * 
	 * @param root
	 * @param editFocus
	 * @param oldName
	 * @param newName
	 * @throws ItemNamingException
	 */
	public static void rename(SerialRoot root, Object editFocus, String oldName, String newName)
		throws ItemNamingException
	{
		if (editFocus instanceof SerialCondition)
		{
			renameCondition(root, oldName, newName);
		}
		/*else if (editFocus instanceof SerialSet)
		{
			renameSet(oldName, newName);
		}
		else if (editFocus instanceof SerialMachine)
		{
			renameMachine(oldName, newName);
		}
		else if (editFocus instanceof SerialList)
		{
			renameList(oldName, newName);
		}
		else if (editFocus instanceof SerialDynamic)
		{
			renameDynamic(oldName, newName);
		}
		else if (editFocus instanceof SerialEvent)
		{
			renameEvent(oldName, newName);
		}*/
	}
	
	private static boolean renameCondition(SerialRoot root, String oldName, String newName) throws ItemNamingException
	{
		boolean isRename = newName != null;
		if (isRename)
		{
			replaceInMap(root.condition, oldName, newName);
		}
		else
		{
			deleteInMap(root.condition, oldName);
		}
		
		for (SerialSet set : root.set.values())
		{
			if (set.no.contains(oldName))
			{
				set.no.remove(oldName);
				if (isRename)
				{
					set.no.add(newName);
				}
			}
			
			if (set.yes.contains(oldName))
			{
				set.yes.remove(oldName);
				if (isRename)
				{
					set.yes.add(newName);
				}
			}
		}
		
		return true;
	}
	
	private static <T> void replaceInMap(Map<String, T> map, String oldName, String newName) throws ItemNamingException
	{
		if (!map.containsKey(oldName))
			throw new ItemNamingException("Invalid operation: This element doesn't exist.");
		
		if (map.containsKey(newName))
			throw new ItemNamingException("This name is already in use.");
		
		map.put(newName, map.remove(oldName));
	}
	
	private static <T> void deleteInMap(Map<String, T> map, String oldName) throws ItemNamingException
	{
		if (!map.containsKey(oldName))
			throw new ItemNamingException("Invalid operation: This element doesn't exist.");
		
		map.remove(oldName);
	}
}
