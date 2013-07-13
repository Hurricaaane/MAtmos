package eu.ha3.matmos.engine.implem;

import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;

import eu.ha3.matmos.conv.MAtmosConvLogger;
import eu.ha3.matmos.engine.interfaces.Sheet;

/*
            DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE 
                    Version 2, December 2004 

 Copyright (C) 2004 Sam Hocevar <sam@hocevar.net> 

 Everyone is permitted to copy and distribute verbatim or modified 
 copies of this license document, and changing it is allowed as long 
 as the name is changed. 

            DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE 
   TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION 

  0. You just DO WHAT THE FUCK YOU WANT TO. 
*/

public class Condition extends Switchable
{
	private String sheet = "";
	private int key = 0;
	private String dynamicKey = "";
	private int conditionType = 0;
	private int constant = 0;
	private String list = "";
	
	private int version = -1;
	
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
	
	public void setKey(int keyIn)
	{
		this.key = keyIn;
		flagNeedsTesting();
		
	}
	
	public void setDynamic(String dynamicKeyIn)
	{
		this.key = -1;
		this.dynamicKey = dynamicKeyIn;
		this.sheet = "";
		flagNeedsTesting();
		
	}
	
	public void setSymbol(String symbol)
	{
		this.conditionType = -1;
		
		if (symbol.equals("!="))
		{
			this.conditionType = 0;
		}
		else if (symbol.equals("=="))
		{
			this.conditionType = 1;
		}
		else if (symbol.equals(">"))
		{
			this.conditionType = 2;
		}
		else if (symbol.equals(">="))
		{
			this.conditionType = 3;
		}
		else if (symbol.equals("<"))
		{
			this.conditionType = 4;
		}
		else if (symbol.equals("<="))
		{
			this.conditionType = 5;
		}
		else if (symbol.equals("in"))
		{
			this.conditionType = 6;
		}
		else if (symbol.equals("!in"))
		{
			this.conditionType = 7;
		}
		
		flagNeedsTesting();
		
	}
	
	public void setConstant(int constantIn)
	{
		this.constant = constantIn;
		flagNeedsTesting(); // Not required.
		
	}
	
	public void setList(String listIn)
	{
		this.list = listIn;
		flagNeedsTesting(); // Required.
		
	}
	
	public boolean isDynamic()
	{
		return this.key == -1;
		
	}
	
	public String getSheet()
	{
		return this.sheet;
		
	}
	
	public int getKey()
	{
		return this.key;
		
	}
	
	public String getDynamic()
	{
		return this.dynamicKey;
		
	}
	
	public String getList()
	{
		return this.list;
		
	}
	
	public int getConditionType()
	{
		return this.conditionType;
		
	}
	
	public int getConstant()
	{
		return this.constant;
		
	}
	
	@Override
	protected boolean testIfValid()
	{
		if (this.conditionType == -1)
			return false;
		
		boolean valid = false;
		if (!isDynamic())
		{
			if (this.knowledge.getData().getSheet(this.sheet) != null)
			{
				if (this.key >= 0 && this.key < this.knowledge.getData().getSheet(this.sheet).getSize())
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
		if (valid && (this.conditionType == 6 || this.conditionType == 7))
		{
			valid = this.knowledge.getListsKeySet().contains(this.list);
			
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
				.append(this.nickname).append(this.isTrueEvaluated ? " now On." : " now Off.").toString());
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
		
		int gotValue;
		
		if (!isDynamic())
		{
			Sheet<Integer> sheet = this.knowledge.getData().getSheet(this.sheet);
			int newVersion = sheet.getVersionOf(this.key);
			
			if (sheet.getVersionOf(this.key) == this.version)
				return this.isTrueEvaluated;
			
			this.version = newVersion;
			
			gotValue = sheet.get(this.key);
		}
		else
		{
			gotValue = this.knowledge.getDynamic(this.dynamicKey).value;
		}
		
		if (this.conditionType == 0)
			return gotValue != this.constant;
		
		else if (this.conditionType == 1)
			return gotValue == this.constant;
		
		else if (this.conditionType == 2)
			return gotValue > this.constant;
		
		else if (this.conditionType == 3)
			return gotValue >= this.constant;
		
		else if (this.conditionType == 4)
			return gotValue < this.constant;
		
		else if (this.conditionType == 5)
			return gotValue <= this.constant;
		
		else if (this.conditionType == 6)
			return this.knowledge.getList(this.list).contains(gotValue);
		
		else if (this.conditionType == 7)
			return !this.knowledge.getList(this.list).contains(gotValue);
		
		else
			return false;
		
	}
	
	@Override
	public String serialize(XMLEventWriter eventWriter) throws XMLStreamException
	{
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
	
	public void replaceListName(String name, String newName)
	{
		if (this.list.equals(name))
		{
			this.list = newName;
		}
		
		flagNeedsTesting();
		
	}
	
}
