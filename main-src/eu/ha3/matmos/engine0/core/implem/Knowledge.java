package eu.ha3.matmos.engine0.core.implem;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import eu.ha3.matmos.engine0.core.interfaces.Data;
import eu.ha3.matmos.engine0.core.interfaces.SoundRelay;
import eu.ha3.matmos.engine0.core.interfaces.Stated;
import eu.ha3.matmos.engine0.requirem.FlatRequirements;

/* x-placeholder */

/**
 * Stores a Knowledge.
 */
public class Knowledge implements Stated
{
	private Map<String, Dynamic> dynamics;
	private Map<String, SugarList> lists;
	
	private Map<String, Condition> conditions;
	private Map<String, ConditionSet> sets;
	private Map<String, Machine> machines;
	
	private Map<String, Event> events;
	
	private Data data;
	private SoundRelay soundRelay;
	private RunningClock clock;
	
	private boolean isRunning;
	private int dataLastVersion;
	
	private Random random;
	
	public Knowledge()
	{
		this.data = new StringData(new FlatRequirements());
		this.soundRelay = null;
		
		this.dataLastVersion = 0;
		this.isRunning = false;
		
		this.random = new Random(System.currentTimeMillis());
		
		this.clock = new RunningClock();
		
		patchKnowledge();
		
	}
	
	public Random getRNG()
	{
		return this.random;
	}
	
	/**
	 * Closes the Knowledge, annihilates all references to libraries of objects
	 * from the current knowledge, and instantiates new ones.
	 * 
	 */
	public void patchKnowledge()
	{
		turnOff();
		
		this.dynamics = new LinkedHashMap<String, Dynamic>();
		this.lists = new LinkedHashMap<String, SugarList>();
		
		this.conditions = new LinkedHashMap<String, Condition>();
		this.sets = new LinkedHashMap<String, ConditionSet>();
		this.machines = new LinkedHashMap<String, Machine>();
		
		this.events = new LinkedHashMap<String, Event>();
		
	}
	
	public void turnOn()
	{
		if (this.soundRelay == null)
			return;
		
		if (this.isRunning)
			return;
		
		ensureChildrenBound();
		this.isRunning = true;
		
		// Machines have to be powered on for their routines to run even if the machines are turned off
		for (Machine machine : this.machines.values())
		{
			machine.powerOn();
		}
		
	}
	
	public void turnOff()
	{
		if (!this.isRunning)
			return;
		
		this.isRunning = false;
		
		// Machines have to be powered on for their routines to run even if the machines are turned off
		for (Machine machine : this.machines.values())
		{
			machine.powerOff();
		}
	}
	
	public boolean isTurnedOn()
	{
		return this.isRunning;
		
	}
	
	public Set<String> getDynamicsKeySet()
	{
		return this.dynamics.keySet();
		
	}
	
	public Set<String> getListsKeySet()
	{
		return this.lists.keySet();
		
	}
	
	public Set<String> getConditionsKeySet()
	{
		return this.conditions.keySet();
		
	}
	
	public Set<String> getConditionSetsKeySet()
	{
		return this.sets.keySet();
		
	}
	
	public Set<String> getMachinesKeySet()
	{
		return this.machines.keySet();
		
	}
	
	public Set<String> getEventsKeySet()
	{
		return this.events.keySet();
		
	}
	
	private void ensureChildrenBound()
	{
		turnOff();
		
		for (Dynamic dynamic : this.dynamics.values())
		{
			dynamic.setKnowledge(this);
		}
		
		// Lists don't have to be tied with the knowledge
		
		for (Condition condition : this.conditions.values())
		{
			condition.setKnowledge(this);
		}
		
		for (ConditionSet cset : this.sets.values())
		{
			cset.setKnowledge(this);
		}
		
		for (Machine machine : this.machines.values())
		{
			machine.setKnowledge(this);
		}
		
		for (Event event : this.events.values())
		{
			event.setKnowledge(this);
		}
	}
	
	public int purgeUnused()
	{
		int purgedTotal = 0;
		Set<String> toPurge = new HashSet<String>();
		
		// SETS
		
		toPurge.clear();
		for (String o : this.sets.keySet())
		{
			toPurge.add(o);
		}
		for (Machine o : this.machines.values())
		{
			for (String keepable : o.getAllows())
			{
				toPurge.remove(keepable);
			}
			for (String keepable : o.getRestricts())
			{
				toPurge.remove(keepable);
			}
		}
		for (String removable : toPurge)
		{
			purgedTotal = purgedTotal + 1;
			removeConditionSet(removable);
		}
		
		// CONDITIONS
		
		toPurge.clear();
		for (String o : this.conditions.keySet())
		{
			toPurge.add(o);
		}
		for (ConditionSet o : this.sets.values())
		{
			for (String keepable : o.getSet().keySet())
			{
				toPurge.remove(keepable);
			}
		}
		for (String removable : toPurge)
		{
			purgedTotal = purgedTotal + 1;
			removeCondition(removable);
		}
		
		// LISTS
		
		toPurge.clear();
		for (String o : this.lists.keySet())
		{
			toPurge.add(o);
		}
		for (Condition o : this.conditions.values())
		{
			if (o.isListBased())
				if (o.getConstant() != null && o.getConstant() != "")
				{
					toPurge.remove(o.getConstant());
				}
		}
		for (String removable : toPurge)
		{
			purgedTotal = purgedTotal + 1;
			removeList(removable);
		}
		
		return purgedTotal;
	}
	
	public void setSoundManager(SoundRelay soundManagerIn)
	{
		this.soundRelay = soundManagerIn;
	}
	
	public SoundRelay getSoundManager()
	{
		return this.soundRelay;
	}
	
	public void cacheSounds()
	{
		for (Event event : this.events.values())
		{
			event.cacheSounds();
		}
		
	}
	
	public void setClock(RunningClock clockIn)
	{
		this.clock = clockIn;
	}
	
	public void setData(Data dataIn)
	{
		this.data = dataIn;
	}
	
	public Data getData()
	{
		return this.data;
	}
	
	public long getTimeMillis()
	{
		return this.clock.getMilliseconds();
	}
	
	public Event getEvent(String name)
	{
		return this.events.get(name);
		
	}
	
	public boolean addEvent(String name)
	{
		if (this.events.containsKey(name))
			return false;
		
		this.events.put(name, new Event(this));
		this.events.get(name).name = name;
		
		return true;
		
	}
	
	public boolean removeEvent(String name)
	{
		if (!this.events.containsKey(name))
			return false;
		
		this.events.remove(name);
		
		return true;
		
	}
	
	public boolean renameEvent(String name, String newName)
	{
		if (!this.events.containsKey(name))
			return false; // Error?
			
		if (this.events.containsKey(newName))
			return false;
		
		this.events.put(newName, this.events.get(name));
		this.events.remove(name);
		this.events.get(newName).name = newName;
		
		for (Machine machine : this.machines.values())
		{
			for (TimedEvent etime : machine.getTimedEvents())
			{
				if (etime.event.equals(name))
				{
					etime.event = newName;
				}
				
			}
			
		}
		
		return true;
		
	}
	
	public Dynamic getDynamic(String name)
	{
		return this.dynamics.get(name);
		
	}
	
	public boolean addDynamic(String name)
	{
		if (this.dynamics.containsKey(name))
			return false;
		
		this.dynamics.put(name, new Dynamic(this));
		this.dynamics.get(name).name = name;
		
		return true;
		
	}
	
	public boolean removeDynamic(String name)
	{
		if (!this.dynamics.containsKey(name))
			return false;
		
		this.dynamics.remove(name);
		
		return true;
		
	}
	
	public boolean renameDynamic(String name, String newName)
	{
		if (!this.dynamics.containsKey(name))
			return false; // Error?
			
		if (this.dynamics.containsKey(newName))
			return false;
		
		this.dynamics.put(newName, this.dynamics.get(name));
		this.dynamics.remove(name);
		this.dynamics.get(newName).name = newName;
		
		for (Condition condition : this.conditions.values())
		{
			condition.replaceDynamicName(name, newName);
		}
		
		return true;
		
	}
	
	public SugarList getList(String name)
	{
		return this.lists.get(name);
		
	}
	
	public boolean addList(String name)
	{
		if (this.lists.containsKey(name))
			return false;
		
		this.lists.put(name, new SugarList());
		this.lists.get(name).name = name;
		
		return true;
		
	}
	
	public boolean removeList(String name)
	{
		if (!this.lists.containsKey(name))
			return false;
		
		this.lists.remove(name);
		
		return true;
		
	}
	
	public boolean renameList(String name, String newName)
	{
		if (!this.lists.containsKey(name))
			return false; // Error?
			
		if (this.lists.containsKey(newName))
			return false;
		
		this.lists.put(newName, this.lists.get(name));
		this.lists.remove(name);
		this.lists.get(newName).name = newName;
		
		for (Condition condition : this.conditions.values())
		{
			condition.replaceListName(name, newName);
		}
		
		return true;
		
	}
	
	public Condition getCondition(String name)
	{
		return this.conditions.get(name);
		
	}
	
	public boolean addCondition(String name)
	{
		if (this.conditions.containsKey(name))
			return false;
		
		this.conditions.put(name, new Condition(this));
		this.conditions.get(name).name = name;
		
		return true;
		
	}
	
	public boolean renameCondition(String name, String newName)
	{
		if (!this.conditions.containsKey(name))
			return false;
		
		if (this.conditions.containsKey(newName))
			return false;
		
		this.conditions.put(newName, this.conditions.get(name));
		this.conditions.remove(name);
		this.conditions.get(newName).name = newName;
		
		for (ConditionSet cset : this.sets.values())
		{
			cset.replaceConditionName(name, newName);
		}
		
		return true;
		
	}
	
	public boolean removeCondition(String name)
	{
		if (!this.conditions.containsKey(name))
			return false;
		
		this.conditions.remove(name);
		
		return true;
		
	}
	
	public ConditionSet getConditionSet(String name)
	{
		return this.sets.get(name);
	}
	
	public boolean addConditionSet(String name)
	{
		if (this.sets.containsKey(name))
			return false;
		
		this.sets.put(name, new ConditionSet(this));
		this.sets.get(name).name = name;
		
		return true;
		
	}
	
	public boolean renameConditionSet(String name, String newName)
	{
		if (!this.sets.containsKey(name))
			return false;
		
		if (this.sets.containsKey(newName))
			return false;
		
		this.sets.put(newName, this.sets.get(name));
		this.sets.remove(name);
		this.sets.get(newName).name = newName;
		
		for (Machine machine : this.machines.values())
		{
			machine.replaceSetName(name, newName);
		}
		
		return true;
		
	}
	
	public boolean removeConditionSet(String name)
	{
		if (!this.sets.containsKey(name))
			return false;
		
		this.sets.remove(name);
		
		return true;
		
	}
	
	void applyMachineNeedsTesting()
	{
		// Do nothing
	}
	
	public Machine getMachine(String name)
	{
		return this.machines.get(name);
	}
	
	public boolean addMachine(String name)
	{
		if (this.machines.containsKey(name))
			return false;
		
		this.machines.put(name, new Machine(this));
		this.machines.get(name).name = name;
		
		applyMachineNeedsTesting();
		
		return true;
		
	}
	
	public boolean removeMachine(String name)
	{
		if (!this.machines.containsKey(name))
			return false;
		
		this.machines.remove(name);
		
		applyMachineNeedsTesting();
		
		return true;
		
	}
	
	public boolean renameMachine(String name, String newName)
	{
		if (!this.machines.containsKey(name))
			return false;
		
		if (this.machines.containsKey(newName))
			return false;
		
		this.machines.put(newName, this.machines.get(name));
		this.machines.remove(name);
		this.machines.get(newName).name = newName;
		
		return true;
		
	}
	
	public void soundRoutine()
	{
		if (!this.isRunning)
			return;
		
		this.soundRelay.routine();
		for (Iterator<Machine> iter = this.machines.values().iterator(); iter.hasNext();)
		{
			iter.next().routine();
			
		}
		
	}
	
	public void dataRoutine()
	{
		if (!this.isRunning)
			return;
		
		if (this.dataLastVersion != this.data.getVersion())
		{
			evaluate();
			this.dataLastVersion = this.data.getVersion();
			
		}
		
	}
	
	void evaluate()
	{
		if (!this.isRunning)
			return;
		
		for (Dynamic dynamic : this.dynamics.values())
		{
			dynamic.evaluate();
			
		}
		// Lists don't have to be tied with the knowledge
		for (Condition condition : this.conditions.values())
		{
			condition.evaluate();
			
		}
		for (ConditionSet cset : this.sets.values())
		{
			cset.evaluate();
			
		}
		for (Machine machine : this.machines.values())
		{
			machine.evaluate();
			
		}
	}
}
