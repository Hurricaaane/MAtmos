package eu.ha3.matmos.engine0.conv;

import net.minecraft.client.resources.IResourcePack;
import net.minecraft.util.ResourceLocation;

/*
--filenotes-placeholder
*/

public final class ExpansionIdentity
{
	private final String uniqueName;
	private final String friendlyName;
	private final IResourcePack pack;
	private final ResourceLocation location;
	
	public ExpansionIdentity(String uniqueName, String friendlyName, IResourcePack pack, ResourceLocation location)
	{
		this.uniqueName = uniqueName;
		this.friendlyName = friendlyName;
		this.pack = pack;
		this.location = location;
	}
	
	public final String getUniqueName()
	{
		return this.uniqueName;
	}
	
	public final String getFriendlyName()
	{
		return this.friendlyName;
	}
	
	public final IResourcePack getPack()
	{
		return this.pack;
	}
	
	public final ResourceLocation getLocation()
	{
		return this.location;
	}
}
