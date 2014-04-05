package zotmc.etudeoregen.testers;

import static zotmc.etudeoregen.testers.EtudeTester1.DEPENDENCIES;
import static zotmc.etudeoregen.testers.EtudeTester1.MODID;
import static zotmc.etudeoregen.testers.EtudeTester1.NAME;
import zotmc.etudeoregen.Etude;
import zotmc.etudeoregen.OreGenAPI;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;

/**
 * An ore generation tester.
 * 
 * @author zot
 */
@Mod(modid = MODID, name = NAME, version = "0.0.0.0-1.7.2", dependencies = DEPENDENCIES)
public class EtudeTester1 {
	public static final String
	MODID = "etudeoregen_tester1",
	NAME = "Ore Generation API Étude Tester 1",
	DEPENDENCIES = "required-after:" + Etude.MODID;
	
	@EventHandler public void init(FMLInitializationEvent event) {
		OreGenAPI.instance().register(MODID,
				"stone of ALL -> ALL = glowstone x 50 x 50",
				new String[] {
					"stone       = minecraft:stone",
					"glowstone   = minecraft:glowstone"
				});
		
	}

}
