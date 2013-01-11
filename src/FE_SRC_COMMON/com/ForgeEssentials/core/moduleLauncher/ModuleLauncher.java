package com.ForgeEssentials.core.moduleLauncher;

import java.io.File;
import java.util.Collection;
import java.util.Set;
import java.util.TreeMap;

import net.minecraft.command.ICommandSender;

import com.ForgeEssentials.util.OutputHandler;

import cpw.mods.fml.common.discovery.ASMDataTable.ASMData;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;

/**
 * Initialize modules here. Yes. HERE. NOT ForgeEssentials.java! This is the springboard...
 */

public class ModuleLauncher
{
	public ModuleLauncher()
	{
		instance = this;
	}

	public static ModuleLauncher				instance;
	private static TreeMap<String, ModuleContainer>	containerMap = new TreeMap<String, ModuleContainer>();

	public void preLoad(FMLPreInitializationEvent e)
	{
		OutputHandler.SOP("Discovering and loading modules...");
		OutputHandler.SOP("If you would like to disable a module, please look in ForgeEssentials/main.cfg.");

		// started ASM handling for the module loaidng.
		Set<ASMData> data = e.getAsmData().getAll(FEModule.class.getName());

		ModuleContainer temp;
		for (ASMData asm : data)
		{
			temp = new ModuleContainer(asm);
			if (temp.isValid)
			{
				containerMap.put(temp.name, temp);
				temp.createAndPopulate();
				OutputHandler.SOP("Loaded " + temp.name);
			}
		}

		Collection<ModuleContainer> modules = (Collection<ModuleContainer>) containerMap.values();

		// run the preinits.
		for (ModuleContainer module : modules)
		{
			module.runPreInit(e);
		}

		// run the config init methods..
		boolean generate = false;
		for (ModuleContainer module : modules)
		{
			IModuleConfig cfg = module.getConfig();

			if (cfg != null)
			{
				File file = cfg.getFile();

				if (!file.getParentFile().exists())
				{
					generate = true;
					file.getParentFile().mkdirs();
				}

				if (!generate && (!file.exists() || !file.isFile()))
				{
					generate = true;
				}

				cfg.setGenerate(generate);
				cfg.init();
			}
		}
	}

	public void load(FMLInitializationEvent e)
	{
		for (ModuleContainer module : containerMap.values())
		{
			module.runInit(e);
		}
	}

	public void postLoad(FMLPostInitializationEvent e)
	{
		for (ModuleContainer module : containerMap.values())
		{
			module.runPostInit(e);
		}
	}

	public void serverStarting(FMLServerStartingEvent e)
	{
		for (ModuleContainer module : containerMap.values())
		{
			module.runServerInit(e);
		}
	}

	public void serverStarted(FMLServerStartedEvent e)
	{
		for (ModuleContainer module : containerMap.values())
		{
			module.runServerPostInit(e);
		}
	}

	public void serverStopping(FMLServerStoppingEvent e)
	{
		for (ModuleContainer module : containerMap.values())
		{
			module.runServerStop(e);
		}
	}

	public void reloadConfigs(ICommandSender sender)
	{
		IModuleConfig config;
		for (ModuleContainer module : containerMap.values())
		{
			config = module.getConfig();
			if (config != null)
				config.forceLoad(sender);
			module.runReload(sender);
		}
	}
}
