package eu.ha3.matmos.engine0.core.implem;

import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;

import eu.ha3.matmos.engine0.conv.MAtmosConvLogger;
import eu.ha3.matmos.engine0.core.interfaces.Sheet;

/* x-placeholder */

public class Condition extends Switchable
{
	private boolean isDynamic;
	
	private String sheet = "";
	private String key = "0";
	private String dynamicKey = "";
	private ConditionType conditionType = ConditionType.ALWAYS_FALSE;
	private String constant = "";
	
	private int version = -1;
	
	private Integer constantIntegerForm = 0;
	
	private boolean isTrueEvaluated;
	
	public Condition(Knowledge knowledgeIn)
	{
		super(knowledgeIn);
	}
	
	public void setSheet(String sheetIn)
	{
		this.sheet = sheetIn;
		flagNeedsTesting();
	}
	
	public void setKey(String keyIn)
	{
		this.isDynamic = false;
		
		this.key = keyIn;
		flagNeedsTesting();
	}
	
	public void setDynamic(String dynamicKeyIn)
	{
		this.isDynamic = true;
		
		this.dynamicKey = dynamicKeyIn;
		this.sheet = "";
		flagNeedsTesting();
		
	}
	
	public void setConditionType(ConditionType type)
	{
		this.conditionType = type;
		
		flagNeedsTesting();
		
	}
	
	public void setConstant(String constantIn)
	{
		this.constant = constantIn;
		
		try
		{
			this.constantIntegerForm = Integer.parseInt(constantIn);
		}
		catch (Exception e)
		{
			this.constantIntegerForm = null;
		}
		flagNeedsTesting(); // Not required.
		
	}
	
	public boolean isDynamic()
	{
		return this.isDynamic;
		
	}
	
	public String getSheet()
	{
		return this.sheet;
		
	}
	
	public String getKey()
	{
		return this.key;
		
	}
	
	public String getDynamic()
	{
		return this.dynamicKey;
		
	}
	
	public ConditionType getConditionType()
	{
		return this.conditionType;
		
	}
	
	public String getConstant()
	{
		return this.constant;
	}
	
	@Override
	protected boolean testIfValid()
	{
		if (this.conditionType == ConditionType.ALWAYS_FALSE)
			return false;
		
		boolean valid = false;
		if (!isDynamic())
		{
			if (this.knowledge.getData().getSheet(this.sheet) != null)
			{
				/*if (!this.key.equals("") && this.knowledge.getData().getSheet(this.sheet).containsKey(this.key))
				{
					valid = true;
				}*/
				
				// The entry doesn't need to exist initially for the condition to be valid.
				if (!this.key.equals("") && this.knowledge.getData().getSheet(this.sheet) != null)
				{
					valid = true;
				}
			}
			
		}
		else
		{
			if (this.knowledge.getDynamicsKeySet().contains(this.dynamicKey))
			{
				valid = true;
				
			}
			
		}
		if (valid && (this.conditionType == ConditionType.IN_LIST || this.conditionType == ConditionType.NOT_IN_LIST))
		{
			valid = this.knowledge.getListsKeySet().contains(this.constant);
			
		}
		
		return valid;
		
	}
	
	public boolean evaluate()
	{
		if (!isValid())
			return false;
		
		boolean pre = this.isTrueEvaluated;
		this.isTrueEvaluated = testIfTrue();
		
		if (pre != this.isTrueEvaluated)
		{
			//MAtmosEngine.logger; //TODO Logger
			MAtmosConvLogger.fine(new StringBuilder("C:")
				.append(this.name).append(this.isTrueEvaluated ? " now On." : " now Off.").toString());
		}
		
		return this.isTrueEvaluated;
		
	}
	
	@Override
	public boolean isActive()
	{
		return isTrue();
		
	}
	
	public boolean isTrue()
	{
		return this.isTrueEvaluated;
		
	}
	
	public boolean testIfTrue()
	{
		if (!isValid())
			return false;
		
		String gotValue;
		
		if (!isDynamic())
		{
			Sheet sheet = this.knowledge.getData().getSheet(this.sheet);
			int newVersion = sheet.getVersionOf(this.key);
			
			if (sheet.getVersionOf(this.key) == this.version)
				return this.isTrueEvaluated;
			
			this.version = newVersion;
			
			gotValue = sheet.get(this.key);
		}
		else
		{
			gotValue = Integer.toString(this.knowledge.getDynamic(this.dynamicKey).getValue());
		}
		
		// Always return false if the entry in this sheet does not exist
		if (gotValue == null)
			return false;
		
		Integer gotValueIntegerForm;
		try
		{
			gotValueIntegerForm = Integer.parseInt(gotValue);
		}
		catch (Exception e)
		{
			gotValueIntegerForm = null;
		}
		
		if (this.conditionType == ConditionType.NOT_EQUAL)
			return !gotValue.equals(this.constant);
		
		else if (this.conditionType == ConditionType.EQUAL)
			return gotValue.equals(this.constant);
		
		else if (this.conditionType == ConditionType.IN_LIST)
			return this.knowledge.getList(this.constant).contains(gotValue);
		
		else if (this.conditionType == ConditionType.NOT_IN_LIST)
			return !this.knowledge.getList(this.constant).contains(gotValue);
		
		else if (gotValueIntegerForm != null && this.constantIntegerForm != null)
		{
			// if (both values are integers), then
			
			if (this.conditionType == ConditionType.GREATER)
				return gotValueIntegerForm > this.constantIntegerForm;
			
			else if (this.conditionType == ConditionType.GREATER_OR_EQUAL)
				return gotValueIntegerForm >= this.constantIntegerForm;
			
			else if (this.conditionType == ConditionType.LESSER_)
				return gotValueIntegerForm < this.constantIntegerForm;
			
			else if (this.conditionType == ConditionType.LESSER_OR_EQUAL)
				return gotValueIntegerForm <= this.constantIntegerForm;
			
			else
				return false;
		}
		
		else
			return false;
		
	}
	
	@Override
	public String serialize(XMLEventWriter eventWriter) throws XMLStreamException
	{
		/*
		buildDescriptibleSerialized(eventWriter);
		
		if (!isDynamic())
		{
			createNode(eventWriter, "sheet", this.sheet);
			createNode(eventWriter, "key", "" + this.key);
			
		}
		else
		{
			createNode(eventWriter, "key", "" + this.key);
			createNode(eventWriter, "dynamickey", this.dynamicKey);
			
		}
		
		if (this.conditionType == 0)
		{
			createNode(eventWriter, "symbol", "!=");
		}
		else if (this.conditionType == 1)
		{
			createNode(eventWriter, "symbol", "==");
		}
		else if (this.conditionType == 2)
		{
			createNode(eventWriter, "symbol", ">");
		}
		else if (this.conditionType == 3)
		{
			createNode(eventWriter, "symbol", ">=");
		}
		else if (this.conditionType == 4)
		{
			createNode(eventWriter, "symbol", "<");
		}
		else if (this.conditionType == 5)
		{
			createNode(eventWriter, "symbol", "<=");
		}
		else if (this.conditionType == 6)
		{
			createNode(eventWriter, "symbol", "in");
		}
		else if (this.conditionType == 7)
		{
			createNode(eventWriter, "symbol", "!in");
		}
		else
		{
			createNode(eventWriter, "symbol", "><"); // TODO Exceptions?
		}
		
		createNode(eventWriter, "constant", "" + this.constant);
		createNode(eventWriter, "list", "" + this.list);
		*/
		return "";
	}
	
	public void replaceDynamicName(String name, String newName)
	{
		if (!isDynamic())
			return;
		
		if (this.dynamicKey.equals(name))
		{
			this.dynamicKey = newName;
		}
		
		flagNeedsTesting();
	}
	
	public boolean isListBased()
	{
		return this.conditionType == ConditionType.IN_LIST || this.conditionType == ConditionType.NOT_IN_LIST;
	}
	
	public void replaceListName(String name, String newName)
	{
		if (isListBased())
		{
			if (this.constant.equals(name))
			{
				this.constant = newName;
			}
		}
		
		flagNeedsTesting();
		
	}
	
}
