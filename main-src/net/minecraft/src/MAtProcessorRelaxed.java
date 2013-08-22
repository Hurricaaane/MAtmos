package net.minecraft.src;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.naming.directory.Attributes;
import javax.naming.directory.InitialDirContext;

import eu.ha3.matmos.engine.implem.IntegerData;
import eu.ha3.mc.haddon.PrivateAccessException;

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

public class MAtProcessorRelaxed extends MAtProcessorModel
{
	private Map<String, Integer> deprecatedBiomeHash;
	private Random random;
	
	private Map<String, Integer> serverAddresses;
	private Map<String, Integer> serverPorts;
	
	public MAtProcessorRelaxed(MAtMod modIn, IntegerData dataIn, String normalNameIn, String deltaNameIn)
	{
		super(modIn, dataIn, normalNameIn, deltaNameIn);
		
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
		Minecraft mc = mod().manager().getMinecraft();
		World w = mc.theWorld;
		
		Set<Integer> required = getRequired();
		
		// Dear Princess Celestia
		// i am so alone
		if (required.contains(75)
			|| required.contains(76) || required.contains(77) || required.contains(78) || required.contains(79)
			|| required.contains(80))
		{
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
				
				setValue(75, 1);
				setValue(76, serverData.serverIP.toLowerCase(Locale.ENGLISH).hashCode());
				setValue(77, MOTDsec.hashCode());
				setValue(78, NAMEsec.hashCode());
				setValue(79, this.serverAddresses.get(playerIp));
				setValue(80, this.serverPorts.get(playerIp));
				
			}
			else
			{
				setValue(75, 0);
				setValue(76, 0);
				setValue(77, 0);
				setValue(78, 0);
				setValue(79, 0);
				setValue(80, 0);
			}
		}
		
		for (Integer index : required)
		{
			switch (index)
			{
			case 5:
				setValue(5, w.provider.dimensionId);
				break;
			
			case 12:
				setValue(12, w.isRemote ? 1 : 0);
				break;
			
			case 13:
				setValue(13, 1 + this.random.nextInt(100)); // DICE A
				break;
			
			case 14:
				setValue(14, 1 + this.random.nextInt(100)); // DICE B
				break;
			
			case 15:
				setValue(15, 1 + this.random.nextInt(100)); // DICE C
				break;
			
			case 16:
				setValue(16, 1 + this.random.nextInt(100)); // DICE D
				break;
			
			case 17:
				setValue(17, 1 + this.random.nextInt(100)); // DICE E
				break;
			
			case 18:
				setValue(18, 1 + this.random.nextInt(100)); // DICE F
				break;
			
			case 29:
			{
				int biomei = mod().getConfig().getInteger("useroptions.biome.override");
				if (biomei <= -1)
				{
					Integer biomeInt = this.deprecatedBiomeHash.get(calculateBiome().biomeName);
					if (biomeInt == null)
					{
						biomeInt = -1;
					}
					setValue(29, biomeInt);
				}
				else
				{
					setValue(29, biomei);
				}
				break;
			}
			
			case 30:
				setValue(30, (int) (w.getSeed() >> 32));
				break;
			
			case 31:
				setValue(31, (int) (w.getSeed() & 0xFFFFFFFF));
				break;
			
			case 88:
				setValue(88, w.getMoonPhase());
				break;
			
			case 93:
			{
				int biomej = mod().getConfig().getInteger("useroptions.biome.override");
				if (biomej <= -1)
				{
					setValue(93, calculateBiome().biomeID);
				}
				else
				{
					setValue(93, biomej);
				}
			}
				break;
			
			default:
				break;
			}
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
		
		String wellIp = "<could not determine>";
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
		Minecraft mc = mod().manager().getMinecraft();
		EntityPlayerSP player = mc.thePlayer;
		int x = MathHelper.floor_double(player.posX);
		int z = MathHelper.floor_double(player.posZ);
		
		Chunk chunk = mc.theWorld.getChunkFromBlockCoords(x, z);
		return chunk.getBiomeGenForWorldCoords(x & 15, z & 15, mc.theWorld.getWorldChunkManager());
	}
	
}
