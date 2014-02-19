package eu.ha3.matmos.engine0.game.data;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import javax.naming.directory.Attributes;
import javax.naming.directory.InitialDirContext;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import eu.ha3.matmos.engine0.core.interfaces.Data;
import eu.ha3.matmos.engine0.game.data.abstractions.processor.MAtProcessorModel;
import eu.ha3.matmos.engine0.game.system.MAtMod;
import eu.ha3.mc.haddon.PrivateAccessException;

/* x-placeholder */

@Deprecated
public class MAtProcessorRelaxed extends MAtProcessorModel
{
	private final HashSet<String> serverData;
	private Map<String, Integer> deprecatedBiomeHash;
	private Random random;
	
	private Map<String, Integer> serverAddresses;
	private Map<String, Integer> serverPorts;
	
	public MAtProcessorRelaxed(MAtMod modIn, Data dataIn, String normalNameIn, String deltaNameIn)
	{
		super(modIn, dataIn, normalNameIn, deltaNameIn);
		
		this.serverData = new HashSet<String>(Arrays.asList(new String[] { "75", "76", "77", "78", "79", "80" }));
		
		this.deprecatedBiomeHash = new HashMap<String, Integer>();
		//biomeHash.put("Rainforest", 1);
		this.deprecatedBiomeHash.put("Swampland", 2);
		//biomeHash.put("Seasonal Forest", 3);
		this.deprecatedBiomeHash.put("Forest", 4);
		//biomeHash.put("Savanna", 5);
		//biomeHash.put("Shrubland", 6);
		this.deprecatedBiomeHash.put("Taiga", 7);
		this.deprecatedBiomeHash.put("Desert", 8);
		this.deprecatedBiomeHash.put("Plains", 9);
		//biomeHash.put("Ice Desert", 10);
		//biomeHash.put("Tundra", 11);
		this.deprecatedBiomeHash.put("Hell", 12);
		this.deprecatedBiomeHash.put("Sky", 13);
		this.deprecatedBiomeHash.put("Ocean", 14);
		this.deprecatedBiomeHash.put("Extreme Hills", 15);
		this.deprecatedBiomeHash.put("River", 16);
		this.deprecatedBiomeHash.put("FrozenOcean", 17);
		this.deprecatedBiomeHash.put("FrozenRiver", 18);
		this.deprecatedBiomeHash.put("Ice Plains", 19);
		this.deprecatedBiomeHash.put("Ice Mountains", 20);
		this.deprecatedBiomeHash.put("MushroomIsland", 21);
		this.deprecatedBiomeHash.put("MushroomIslandShore", 22);
		this.deprecatedBiomeHash.put("Beach", 23);
		this.deprecatedBiomeHash.put("DesertHills", 24);
		this.deprecatedBiomeHash.put("ForestHills", 25);
		this.deprecatedBiomeHash.put("TaigaHills", 26);
		this.deprecatedBiomeHash.put("Extreme Hills Edge", 27);
		this.deprecatedBiomeHash.put("Jungle", 28);
		this.deprecatedBiomeHash.put("JungleHills", 29);
		
		this.random = new Random(System.nanoTime());
		
		this.serverAddresses = new HashMap<String, Integer>();
		this.serverPorts = new HashMap<String, Integer>();
		
	}
	
	@Override
	protected void doProcess()
	{
		Minecraft mc = Minecraft.getMinecraft();
		World w = mc.theWorld;
		
		ServerData serverData = null;
		try
		{
			serverData =
				(ServerData) mod().util().getPrivateValueLiteral(Minecraft.class, Minecraft.getMinecraft(), "M", 6);
		}
		catch (PrivateAccessException e)
		{
			e.printStackTrace();
		}
		if (serverData != null && serverData.serverIP != null)
		{
			String playerIp = serverData.serverIP;
			
			computeServerIP(playerIp);
			
			String MOTDsec = serverData.serverMOTD;
			String NAMEsec = serverData.serverName;
			
			if (MOTDsec == null)
			{
				MOTDsec = "";
			}
			if (NAMEsec == null)
			{
				NAMEsec = "";
			}
			
			conversionComplete(75, 1);
			conversionComplete(76, serverData.serverIP.toLowerCase(Locale.ENGLISH).hashCode());
			conversionComplete(77, MOTDsec.hashCode());
			conversionComplete(78, NAMEsec.hashCode());
			conversionComplete(79, this.serverAddresses.get(playerIp));
			conversionComplete(80, this.serverPorts.get(playerIp));
			
		}
		else
		{
			conversionComplete(75, 0);
			conversionComplete(76, 0);
			conversionComplete(77, 0);
			conversionComplete(78, 0);
			conversionComplete(79, 0);
			conversionComplete(80, 0);
		}
		
		setValueLegacyIntIndexes(5, w.provider.dimensionId);
		conversionComplete(12, w.isRemote ? 1 : 0);
		conversionComplete(13, 1 + this.random.nextInt(100)); // DICE A
		conversionComplete(14, 1 + this.random.nextInt(100)); // DICE B
		conversionComplete(15, 1 + this.random.nextInt(100)); // DICE C
		conversionComplete(16, 1 + this.random.nextInt(100)); // DICE D
		conversionComplete(17, 1 + this.random.nextInt(100)); // DICE E
		conversionComplete(18, 1 + this.random.nextInt(100)); // DICE F
		
		int biomei = mod().getConfig().getInteger("useroptions.biome.override");
		if (biomei <= -1)
		{
			Integer biomeInt = this.deprecatedBiomeHash.get(calculateBiome().biomeName);
			if (biomeInt == null)
			{
				biomeInt = -1;
			}
			setValueLegacyIntIndexes(29, biomeInt);
		}
		else
		{
			setValueLegacyIntIndexes(29, biomei);
		}
		
		conversionComplete(30, (int) (w.getSeed() >> 32));
		conversionComplete(31, (int) (w.getSeed() & 0xFFFFFFFF));
		conversionComplete(88, w.getMoonPhase());
		
		int biomej = mod().getConfig().getInteger("useroptions.biome.override");
		if (biomej <= -1)
		{
			conversionComplete(93, calculateBiome().biomeID);
		}
		else
		{
			conversionComplete(93, biomej);
		}
		
	}
	
	private void computeServerIP(String playerIp)
	{
		if (this.serverAddresses.containsKey(playerIp))
			return;
		
		String[] splitIp = playerIp.split(":");
		
		if (playerIp.startsWith("["))
		{
			int vDelimiter = playerIp.indexOf("]");
			
			if (vDelimiter > 0)
			{
				String ipPart = playerIp.substring(1, vDelimiter);
				String portPart = playerIp.substring(vDelimiter + 1).trim();
				
				if (portPart.startsWith(":") && portPart.length() > 0)
				{
					portPart = portPart.substring(1);
					splitIp = new String[] { ipPart, portPart };
				}
				else
				{
					splitIp = new String[] { ipPart };
				}
			}
		}
		
		if (splitIp.length > 2)
		{
			splitIp = new String[] { playerIp };
		}
		
		String ipPotential = splitIp[0];
		int portPotential = splitIp.length > 1 ? parseIntWithDefault(splitIp[1], 25565) : 25565;
		
		if (portPotential == 25565)
		{
			String[] var7 = useDnsC(ipPotential);
			ipPotential = var7[0];
			portPotential = parseIntWithDefault(var7[1], 25565);
		}
		
		String conIp = ipPotential;
		int conPort = portPotential;
		
		String wellIp = "";
		int wellHashCode = 0;
		try
		{
			wellIp = InetAddress.getByName(conIp).getHostAddress();
			wellHashCode = wellIp.hashCode();
		}
		catch (UnknownHostException e)
		{
		}
		
		this.serverAddresses.put(playerIp, wellHashCode);
		this.serverPorts.put(playerIp, conPort);
		
		//System.out.println("Computed server IP for \""
		//	+ playerIp + " as : " + wellIp + " (" + wellHashCode + ") : " + conPort);
		System.out.println("Computed server IP and hashed as (" + wellHashCode + ") : " + conPort);
		
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static String[] useDnsC(String par0Str)
	{
		try
		{
			Hashtable var1 = new Hashtable();
			var1.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
			var1.put("java.naming.provider.url", "dns:");
			InitialDirContext var2 = new InitialDirContext(var1);
			Attributes var3 = var2.getAttributes("_minecraft._tcp." + par0Str, new String[] { "SRV" });
			String[] var4 = var3.get("srv").get().toString().split(" ", 4);
			return new String[] { var4[3], var4[2] };
		}
		catch (Throwable var5)
		{
			return new String[] { par0Str, Integer.toString(25565) };
		}
	}
	
	private static int parseIntWithDefault(String par0Str, int par1)
	{
		try
		{
			return Integer.parseInt(par0Str.trim());
		}
		catch (Exception var3)
		{
			return par1;
		}
	}
	
	private BiomeGenBase calculateBiome()
	{
		Minecraft mc = Minecraft.getMinecraft();
		EntityPlayerSP player = mc.thePlayer;
		int x = MathHelper.floor_double(player.posX);
		int z = MathHelper.floor_double(player.posZ);
		
		Chunk chunk = mc.theWorld.getChunkFromBlockCoords(x, z);
		return chunk.getBiomeGenForWorldCoords(x & 15, z & 15, mc.theWorld.getWorldChunkManager());
	}
	
}
