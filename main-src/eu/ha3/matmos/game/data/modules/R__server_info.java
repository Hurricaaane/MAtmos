package eu.ha3.matmos.game.data.modules;

import eu.ha3.matmos.engine.core.interfaces.Data;
import eu.ha3.matmos.game.data.abstractions.module.Module;
import eu.ha3.matmos.game.data.abstractions.module.ModuleProcessor;
import eu.ha3.matmos.log.MAtLog;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;

import javax.naming.directory.Attributes;
import javax.naming.directory.InitialDirContext;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;

/*
--filenotes-placeholder
*/

public class R__server_info extends ModuleProcessor implements Module
{
	private final Map<String, Integer> serverAddresses;
	private final Map<String, Integer> serverPorts;
	
	public R__server_info(Data data)
	{
		super(data, "server_info", true);
		
		this.serverAddresses = new HashMap<String, Integer>();
		this.serverPorts = new HashMap<String, Integer>();
	}
	
	@Override
	protected void doProcess()
	{
		ServerData serverData = Minecraft.getMinecraft().getCurrentServerData();
		
		if (serverData != null && serverData.serverIP != null)
		{
			String playerDefinedAddress = serverData.serverIP;
			
			queryActualIP_useCache(playerDefinedAddress);
			
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
			
			setValue("has_server_info", true);
			setValue("addressinput_hashcode", serverData.serverIP.toLowerCase(Locale.ENGLISH).hashCode());
			setValue("motd_hashcode", MOTDsec.hashCode());
			setValue("name_hashcode", NAMEsec.hashCode());
			setValue("ip_computed_hashcode", this.serverAddresses.get(playerDefinedAddress));
			setValue("port_computed", this.serverPorts.get(playerDefinedAddress));
		}
		else
		{
			setValue("has_server_info", false);
			setValue("addressinput_hashcode", "");
			setValue("motd_hashcode", "");
			setValue("name_hashcode", "");
			setValue("ip_computed_hashcode", "");
			setValue("port_computed", "");
		}
	}
	
	private void queryActualIP_useCache(String playerDefinedAddress)
	{
		if (this.serverAddresses.containsKey(playerDefinedAddress))
			return;
		
		String[] splitIp = playerDefinedAddress.split(":");
		
		if (playerDefinedAddress.charAt(0) == '[')
		{
			int vDelimiter = playerDefinedAddress.indexOf("]");
			
			if (vDelimiter > 0)
			{
				String ipPart = playerDefinedAddress.substring(1, vDelimiter);
				String portPart = playerDefinedAddress.substring(vDelimiter + 1).trim();
				
				if (portPart.charAt(0) == ':' && portPart.length() > 0)
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
			splitIp = new String[] { playerDefinedAddress };
		}
		
		String ipPotential = splitIp[0];
		int portPotential = splitIp.length > 1 ? parseIntWithDefault(splitIp[1], 25565) : 25565;
		
		if (portPotential == 25565)
		{
			String[] var7 = resolveSRV(ipPotential);
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
			e.printStackTrace();
			
			this.serverAddresses.put(playerDefinedAddress, 0);
			this.serverPorts.put(playerDefinedAddress, conPort);
			
			MAtLog.info("Error while hashing server addess: Defaulted to 0");
			
			return;
		}
		
		this.serverAddresses.put(playerDefinedAddress, wellHashCode);
		this.serverPorts.put(playerDefinedAddress, conPort);
		
		MAtLog.info("Computed server IP and hashed as (" + wellHashCode + ") : " + conPort);
		
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static String[] resolveSRV(String resolve)
	{
		try
		{
			Hashtable hash = new Hashtable();
			hash.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
			hash.put("java.naming.provider.url", "dns:");
			InitialDirContext idc = new InitialDirContext(hash);
			Attributes att = idc.getAttributes("_minecraft._tcp." + resolve, new String[] { "SRV" });
			String[] cts = att.get("srv").get().toString().split(" ", 4);
			return new String[] { cts[3], cts[2] };
		}
		catch (Throwable e)
		{
			return new String[] { resolve, Integer.toString(25565) };
		}
	}
	
	private static int parseIntWithDefault(String integer, int def)
	{
		try
		{
			return Integer.parseInt(integer.trim());
		}
		catch (Exception var3)
		{
			return def;
		}
	}
}
