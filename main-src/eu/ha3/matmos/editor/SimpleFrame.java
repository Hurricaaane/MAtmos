package eu.ha3.matmos.editor;

import javax.swing.*;

/**
 * Utility lib to quickly format frames.
 * 
 * @author Hurry
 */

public class SimpleFrame
{
	/**
	 * Makes the frame generic, minimal.<br>
	 * <br>
	 * Sets:
	 * <ul>
	 * <li>Look and feel of the frame using the system's</li>
	 * <li>EXIT_ON_CLOSE as the default close operation.</li>
	 * </ul>
	 * 
	 * @param frame
	 */
	public static void genericMinimal(JFrame frame)
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		
	}
	
	/**
	 * Makes the frame generic.<br>
	 * <br>
	 * Sets:
	 * <ul>
	 * <li>Look and feel of the frame using the system's</li>
	 * <li>EXIT_ON_CLOSE as the default close operation.</li>
	 * <li>Sets the size of the frame (including system header).</li>
	 * <li>Center the frame.</li>
	 * <li>Sets the title.</li>
	 * </ul>
	 * 
	 * @param frame
	 * @param title
	 * @param width
	 * @param height
	 */
	public static void makeMeGeneric(JFrame frame, String title, int width, int height)
	{
		genericMinimal(frame);
		frame.setSize(width, height);
		frame.setLocationRelativeTo(frame);
		frame.setTitle(title);
		
	}
	
}
