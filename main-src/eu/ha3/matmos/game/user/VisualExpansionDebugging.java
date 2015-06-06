package eu.ha3.matmos.game.user;

import eu.ha3.matmos.engine.core.implem.Junction;
import eu.ha3.matmos.engine.core.implem.Machine;
import eu.ha3.matmos.engine.core.implem.ProviderCollection;
import eu.ha3.matmos.engine.core.implem.abstractions.Provider;
import eu.ha3.matmos.engine.core.interfaces.Dependable;
import eu.ha3.matmos.engine.core.visualize.Visualized;
import eu.ha3.matmos.game.system.IDontKnowHowToCode;
import eu.ha3.matmos.game.system.MAtMod;
import eu.ha3.mc.haddon.supporting.SupportsFrameEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.GL11;

import java.util.*;

/*
--filenotes-placeholder
*/

public class VisualExpansionDebugging implements SupportsFrameEvents
{
	private final MAtMod mod;
	private final String ex;
	
	private int GAP = 10;
	
	public VisualExpansionDebugging(MAtMod mod, String ex)
	{
		this.mod = mod;
		this.ex = ex;
	}
	
	@Override
	public void onFrame(float semi)
	{
		Minecraft mc = Minecraft.getMinecraft();
		int fac = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight).getScaleFactor();
		
		float scale = 1f / fac;
		GL11.glPushMatrix();
		GL11.glScalef(scale, scale, 1.0F);
		
		if (!this.mod.getExpansionList().containsKey(this.ex))
		{
			IDontKnowHowToCode.warnOnce("Problem getting expansion " + this.ex + " to debug");
			return;
		}
		
		try
		{
			ProviderCollection providers = this.mod.getExpansionList().get(this.ex).obtainProvidersForDebugging();
			Distances condition = distances(providers.getCondition());
			Distances junction = distances(providers.getJunction());
			Distances machine = distances(providers.getMachine());
			
			int yyBase = 30;
			
			scrub(condition, 20, yyBase);
			scrub(junction, 400, yyBase);
			scrub(machine, 600, yyBase);
			
			//link(condition, 0, 0, junction, 40, 0);
			//link(junction, 40, 0, machine, 80, 0);
		}
		catch (Exception e)
		{
			IDontKnowHowToCode.whoops__printExceptionToChat(this.mod.getChatter(), e, this);
		}
		
		GL11.glPopMatrix();
	}
	
	@SuppressWarnings("unused")
	private void link(Distances reliables, int xR, int yR, Distances dependables, int xD, int yD)
	{
		for (String name : dependables.keySet())
		{
			int yDapplied = yD + this.GAP * dependables.get(name);
			Dependable dependable = dependables.dependable(name);
			
			if (dependable instanceof Junction)
			{
				link(reliables, xR, yR, ((Junction) dependable).getSpecialDependencies("yes"), xD, yDapplied, true);
				link(reliables, xR, yR, ((Junction) dependable).getSpecialDependencies("no"), xD, yDapplied, false);
			}
			else if (dependable instanceof Machine)
			{
				link(reliables, xR, yR, ((Junction) dependable).getSpecialDependencies("allow"), xD, yDapplied, true);
				link(
					reliables, xR, yR, ((Junction) dependable).getSpecialDependencies("restrict"), xD, yDapplied, false);
			}
		}
	}
	
	@SuppressWarnings("unused")
	private void link(
		Distances reliables, int xR, int yR, Collection<String> dependencies, int xD, int yDapplied, boolean right)
	{
		for (String dependency : dependencies)
		{
			int yRapplied = yR + this.GAP * reliables.get(dependency);
			
			// Equivalent of isOn = isActive; if (!right) isOn = !isOn;
			boolean isOn = reliables.visualize(dependency).isActive() == right;
			
		}
	}
	
	private void scrub(Distances subject, int x, int y)
	{
		for (String name : subject.keySet())
		{
			Visualized vis = subject.visualize(name);
			
			paint(x, y + subject.get(name) * this.GAP, vis);
		}
	}
	
	private void paint(int x, int y, Visualized vis)
	{
		String name = vis.getName();
		String feed = vis.getFeed();
		boolean isActive = vis.isActive();
		
		Minecraft mc = Minecraft.getMinecraft();
		FontRenderer fontRenderer = mc.fontRendererObj;
		
		fontRenderer.drawStringWithShadow(name + "(" + feed + ")", x, y, isActive ? 0x0099FF : 0xFF0000);
		
		// PAINT
	}
	
	public Distances distances(Provider<? extends Visualized> provider)
	{
		Distances map = new Distances(provider);
		
		List<String> list = new ArrayList<String>(provider.keySet());
		Collections.sort(list);
		
		int i = 0;
		for (String name : list)
		{
			map.put(name, i);
			i = i + 1;
		}
		
		return map;
	}
	
	@SuppressWarnings("serial")
	private class Distances extends TreeMap<String, Integer>
	{
		private Provider<? extends Visualized> provider;
		
		public Distances(Provider<? extends Visualized> provider)
		{
			super();
			this.provider = provider;
		}
		
		public Visualized visualize(String name)
		{
			return this.provider.get(name);
		}
		
		public Dependable dependable(String name)
		{
			Visualized vis = this.provider.get(name);
			if (vis instanceof Dependable)
				return (Dependable) vis;
			
			return new DependableNullObject();
		}
		
		/**
		 * A shortcut so that we don't waste time checking for nulls in case a
		 * dependable doesn't exist
		 * 
		 * @author Hurry
		 */
		public class DependableNullObject implements Dependable
		{
			private Set<String> dep = new HashSet<String>();
			
			@Override
			public Collection<String> getDependencies()
			{
				return this.dep;
			}
		}
	}
	
}
