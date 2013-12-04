package eu.ha3.matmos.engine.implem;

/* x-placeholder */

public abstract class Switchable extends Descriptible
{
	Knowledge knowledge;
	boolean needsTesting;
	boolean isValid;
	
	Switchable(Knowledge knowledgeIn)
	{
		this.knowledge = knowledgeIn;
		this.isValid = false;
		this.needsTesting = true;
		
	}
	
	public abstract boolean isActive();
	
	/**
	 * Flags the fact that this Switchable might start/stop working due to an
	 * update on the internal data.
	 * 
	 */
	public void flagNeedsTesting()
	{
		//System.out.println("TESTING");
		this.needsTesting = true;
	}
	
	/**
	 * Changes the Knowledge this Switchable belongs to.
	 */
	public void setKnowledge(Knowledge knowledgeIn)
	{
		this.knowledge = knowledgeIn;
		flagNeedsTesting();
		
	}
	
	/**
	 * Tests if this Switchable is ought to work. A Switchable that references
	 * existing elements that are not valid doesn't mean this Switchable won't
	 * be valid. An non-existing reference usually causes the Switchable to stop
	 * being valid.
	 */
	public boolean isValid()
	{
		validateUsability();
		return this.isValid;
		
	}
	
	/**
	 * Rests if tjis Switchable is actually useable. Called by isValid. Don't
	 * call this.
	 */
	private void validateUsability()
	{
		if (!this.needsTesting)
			return;
		
		this.isValid = testIfValid();
		this.needsTesting = false;
		
	}
	
	/**
	 * Returns if the Switchable is valid. Usually, if some references lead to
	 * non-existing elements, this Switchable should be marked as invalid.
	 */
	protected abstract boolean testIfValid();
	
}
