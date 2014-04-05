package zotmc.etudeoregen;

import static zotmc.etudeoregen.Etude.MODID;
import static zotmc.etudeoregen.Etude.NAME;
import net.minecraftforge.common.config.Configuration;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

/**
 * This mod refactors the ore generation being used in OnlySilver into an API.
 * Only this class, the OreGenAPI class and the tester classes located in
 * the testers package are new. The OreGenerator class would be the same
 * as the one in OnlySilver.
 * 
 * I would suggest you to install and run this mod once first (including the testers
 * contained in the testers sub-package), and have a look at the config file
 * to see how the generated config look like.
 * 
 * @author zot
 */
@Mod(modid = MODID, name = NAME, version = "0.0.0.0-1.7.2")
public class Etude {
	public static final String MODID = "etudeoregen", NAME = "Ore Generation API Étude";
	
	@EventHandler public void preInit(FMLPreInitializationEvent event) {
		/*
		 * Initialize the API. Pass a config file here to be used to store all
		 * the ore generation settings. The default config file is used here,
		 * you may change it to a dedicated one.
		 */
		OreGenAPI.init(new Configuration(event.getSuggestedConfigurationFile()));
		
		/*
		 * Register this mod's own settings immediately after the initialization.
		 * By doing this, this mod's own settings would appear at top of other
		 * settings.
		 */
		OreGenAPI.instance().register(MODID,
				"stone of ALL -> ALL = lapis_block x 50 x 50",
				new String[] {
					"stone       = minecraft:stone",
					"lapis_block = minecraft:lapis_block"
				});
		
	}
	
	@EventHandler public void postInit(FMLPostInitializationEvent event) {
		/*
		 * Generate OreGenerator instances and register them using
		 * GameRegistry.registerWorldGenerator. No OreGenerator instances
		 * would be created before this step.
		 */
		OreGenAPI.instance().registerOreGenerators();
		
	}
	
}
