package eu.ha3.matmos.editor;

import java.util.Iterator;
import java.util.Map;

import eu.ha3.matmos.engine.core.implem.Dynamic;
import eu.ha3.matmos.engine.core.interfaces.Operator;
import eu.ha3.matmos.jsonformat.serializable.expansion.SerialCondition;
import eu.ha3.matmos.jsonformat.serializable.expansion.SerialDynamic;
import eu.ha3.matmos.jsonformat.serializable.expansion.SerialEvent;
import eu.ha3.matmos.jsonformat.serializable.expansion.SerialList;
import eu.ha3.matmos.jsonformat.serializable.expansion.SerialMachine;
import eu.ha3.matmos.jsonformat.serializable.expansion.SerialMachineEvent;
import eu.ha3.matmos.jsonformat.serializable.expansion.SerialRoot;
import eu.ha3.matmos.jsonformat.serializable.expansion.SerialSet;

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
		else if (editFocus instanceof SerialSet)
		{
			renameSet(root, oldName, newName);
		}
		else if (editFocus instanceof SerialMachine)
		{
			renameMachine(root, oldName, newName);
		}
		else if (editFocus instanceof SerialList)
		{
			renameList(root, oldName, newName);
		}
		else if (editFocus instanceof SerialDynamic)
		{
			renameDynamic(root, oldName, newName);
		}
		else if (editFocus instanceof SerialEvent)
		{
			renameEvent(root, oldName, newName);
		}
	}
	
	private static void renameCondition(SerialRoot root, String oldName, String newName) throws ItemNamingException
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
	}
	
	private static void renameSet(SerialRoot root, String oldName, String newName) throws ItemNamingException
	{
		boolean isRename = newName != null;
		if (isRename)
		{
			replaceInMap(root.set, oldName, newName);
		}
		else
		{
			deleteInMap(root.set, oldName);
		}
		
		for (SerialMachine machine : root.machine.values())
		{
			if (machine.allow.contains(oldName))
			{
				machine.allow.remove(oldName);
				if (isRename)
				{
					machine.allow.add(newName);
				}
			}
			
			if (machine.restrict.contains(oldName))
			{
				machine.restrict.remove(oldName);
				if (isRename)
				{
					machine.restrict.add(newName);
				}
			}
		}
	}
	
	private static void renameMachine(SerialRoot root, String oldName, String newName) throws ItemNamingException
	{
		boolean isRename = newName != null;
		if (isRename)
		{
			replaceInMap(root.machine, oldName, newName);
		}
		else
		{
			deleteInMap(root.machine, oldName);
		}
	}
	
	private static void renameList(SerialRoot root, String oldName, String newName) throws ItemNamingException
	{
		boolean isRename = newName != null;
		if (isRename)
		{
			replaceInMap(root.list, oldName, newName);
		}
		else
		{
			deleteInMap(root.list, oldName);
		}
		
		for (SerialCondition condition : root.condition.values())
		{
			Operator op = Operator.fromSerializedForm(condition.symbol);
			if (op == Operator.IN_LIST || op == Operator.NOT_IN_LIST)
			{
				if (condition.value.equals(oldName))
				{
					if (isRename)
					{
						condition.value = newName;
					}
					else
					{
						condition.value = "_REMOVED_LIST:" + oldName;
					}
				}
			}
		}
		
	}
	
	private static void renameDynamic(SerialRoot root, String oldName, String newName) throws ItemNamingException
	{
		boolean isRename = newName != null;
		if (isRename)
		{
			replaceInMap(root.dynamic, oldName, newName);
		}
		else
		{
			deleteInMap(root.dynamic, oldName);
		}
		
		for (SerialCondition condition : root.condition.values())
		{
			if (condition.sheet.equals(Dynamic.DEDICATED_SHEET))
			{
				if (condition.index.equals(oldName))
				{
					if (isRename)
					{
						condition.index = newName;
					}
					else
					{
						condition.index = "_REMOVED_DYNAMIC:" + oldName;
					}
				}
			}
		}
	}
	
	private static void renameEvent(SerialRoot root, String oldName, String newName) throws ItemNamingException
	{
		boolean isRename = newName != null;
		if (isRename)
		{
			replaceInMap(root.event, oldName, newName);
		}
		else
		{
			deleteInMap(root.event, oldName);
		}
		
		for (SerialMachine machine : root.machine.values())
		{
			for (Iterator<SerialMachineEvent> iter = machine.event.iterator(); iter.hasNext();)
			{
				SerialMachineEvent machineEvent = iter.next();
				if (machineEvent.event.equals(oldName))
				{
					if (isRename)
					{
						machineEvent.event = newName;
					}
					else
					{
						iter.remove();
					}
				}
			}
		}
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
	
	public static Object createNew(SerialRoot root, KnowledgeItemType choice, String name) throws ItemNamingException
	{
		Object o = null;
		switch (choice)
		{
		case CONDITION:
			o = instanciate(root.condition, SerialCondition.class, name);
			break;
		case DYNAMIC:
			o = instanciate(root.dynamic, SerialDynamic.class, name);
			break;
		case EVENT:
			o = instanciate(root.event, SerialEvent.class, name);
			break;
		case LIST:
			o = instanciate(root.list, SerialList.class, name);
			break;
		case MACHINE:
			o = instanciate(root.machine, SerialMachine.class, name);
			break;
		case SET:
			o = instanciate(root.set, SerialSet.class, name);
			break;
		default:
			break;
		}
		return o;
		
	}
	
	private static <T> Object instanciate(Map<String, T> map, Class<T> klass, String name) throws ItemNamingException
	{
		if (map.containsKey(name))
			throw new ItemNamingException("This name is already in use.");
		
		try
		{
			map.put(name, klass.newInstance());
			return map.get(name);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			
			// XXX Dirty use of exception
			throw new ItemNamingException("Severe exception! " + e.getMessage());
		}
	}
}
