package com.strongholdofsecurity;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class StrongholdOfSecurityPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(StrongholdOfSecurityPlugin.class);
		RuneLite.main(args);
	}
}