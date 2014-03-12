package eu.ha3.matmos.editor;

import java.awt.Component;

import eu.ha3.matmos.editor.interfaces.Window;
import eu.ha3.matmos.jsonformat.serializable.expansion.SerialRoot;

/*
--filenotes-placeholder
*/

public class WindowEventQueue implements Window
{
	private Window window;
	
	public WindowEventQueue(Window window)
	{
		this.window = window;
	}
	
	@Override
	public void updateSerial(final SerialRoot root)
	{
		java.awt.EventQueue.invokeLater(new Runnable() {
			@Override
			public void run()
			{
				WindowEventQueue.this.window.updateSerial(root);
			}
		});
	}
	
	@Override
	public void setEditFocus(final String name, final Object item, boolean forceSelect)
	{
		java.awt.EventQueue.invokeLater(new Runnable() {
			@Override
			public void run()
			{
				WindowEventQueue.this.window.setEditFocus(name, item, false);
			}
		});
	}
	
	@Override
	public Component asComponent()
	{
		return this.window.asComponent();
	}
	
	@Override
	public void display()
	{
		java.awt.EventQueue.invokeLater(new Runnable() {
			@Override
			public void run()
			{
				WindowEventQueue.this.window.display();
			}
		});
	}
	
	@Override
	public void refreshFileState()
	{
		java.awt.EventQueue.invokeLater(new Runnable() {
			@Override
			public void run()
			{
				WindowEventQueue.this.window.refreshFileState();
			}
		});
	}
	
	@Override
	public void showErrorPopup(final String error)
	{
		java.awt.EventQueue.invokeLater(new Runnable() {
			@Override
			public void run()
			{
				WindowEventQueue.this.window.showErrorPopup(error);
			}
		});
	}
	
	@Override
	public void disableMinecraftCapabilitites()
	{
		java.awt.EventQueue.invokeLater(new Runnable() {
			@Override
			public void run()
			{
				WindowEventQueue.this.window.disableMinecraftCapabilitites();
			}
		});
	}
	
}
