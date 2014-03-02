package eu.ha3.matmos.engine.core.interfaces;

/*
--filenotes-placeholder
*/

public interface SheetCommander
{
	/**
	 * Tells if the sheet index exists
	 * 
	 * @param sheetIndex
	 * @return
	 */
	public boolean exists(SheetIndex sheetIndex);
	
	/**
	 * Gets the version of the sheet index
	 * 
	 * @param sheetIndex
	 * @return
	 */
	public int version(SheetIndex sheetIndex);
	
	/**
	 * Returns the value of the sheet index
	 * 
	 * @param sheetIndex
	 * @return
	 */
	public Object get(SheetIndex sheetIndex);
	
	/**
	 * Tells if a list has a certain value.
	 * 
	 * @param constantX
	 * @param value
	 * @return
	 */
	public boolean listHas(String constantX, String value);
}
