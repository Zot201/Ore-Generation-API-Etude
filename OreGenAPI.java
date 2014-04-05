package zotmc.etudeoregen;

import static net.minecraftforge.common.config.Property.Type.BOOLEAN;
import static net.minecraftforge.common.config.Property.Type.INTEGER;
import static net.minecraftforge.common.config.Property.Type.STRING;
import static org.apache.logging.log4j.Level.ERROR;

import java.util.Arrays;
import java.util.Map;

import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import com.google.common.base.CharMatcher;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Maps;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;

/**
 * This class contains most of the functionality of the API. User mods can use the various
 * register methods to register their settings for the ore generation.
 * 
 * @author zot
 */
public class OreGenAPI {

	/**
	 * The root category name. Don't use spaces here or the category name would bug with
	 * a lots of quotation marks
	 */
	private static final String
	ORE_GEN_CAT = "ore_generation_settings";
	
	/**
	 * Config entry keys which would be used in every sub-categories.
	 */
	private static final String
	OVERRIDE_DEFAULT = "Override Default Settings",
	ORE_GEN_PROFILE = "Ore Generation Profile",
	BLOCK_DEFS = "Block Definitions",
	PRIORITY = "World Generator Priority";
	
	
	/**
	 * The error message to be displayed whenever an error occurred while try to access
	 * the config file.
	 */
	private static final String
	ERROR_MESSAGE = "[%1$s] An error occurred while trying to access the %1$s config file!";
	
	
	
	/**
	 * The OreGenAPI instance which would be assigned during a call to the init method.
	 */
	private static OreGenAPI instance;
	
	/**
	 * @return the OreGenAPI instance
	 * @throws IllegalStateException if the API have not yet initialized.
	 */
	public static OreGenAPI instance() {
		if (instance == null)
			throw new IllegalStateException();
		
		return instance;
	}
	
	
	
	/**
	 * The config file being passed in the init method.
	 */
	private Configuration config;
	
	/**
	 * A map holds mapping from mod IDs to the mod's ore generation entry.
	 */
	private Map<String, Entry> entries = Maps.newLinkedHashMap();
	
	/**
	 * Initialize the API using the config file given.
	 * 
	 * @param config - the config file used to store all the ore generation settings
	 * @throws IllegalStateException if the API have already initialized.
	 */
	static void init(Configuration config) {
		if (instance != null)
			throw new IllegalStateException();
		
		instance = new OreGenAPI(config);
	}
	
	/**
	 * A private constructor being called by the init method.
	 * 
	 * @param config - the config file used to store all the ore generation settings
	 */
	private OreGenAPI(Configuration config) {
		this.config = config;
	}
	
	
	
	
	/**
	 * A class to represent registration entries to this API.
	 * 
	 * @author zot
	 */
	private static class Entry {
		final String[] profile, blockDefs;
		final int priority;
		
		Entry(String[] profile, String[] blockDefs, int priority) {
			this.profile = profile;
			this.blockDefs = blockDefs;
			this.priority = priority;
		}
		
		public void register() {
			GameRegistry.registerWorldGenerator(
					new OreGenerator(profile, blockDefs),
					priority);
		}
		
	}
	
	
	
	
	// register methods from here
	
	public void register(String modid, String   profile, String   blockDefs) { register(modid, profile, blockDefs, 0); }
	public void register(String modid, String   profile, String[] blockDefs) { register(modid, profile, blockDefs, 0); }
	public void register(String modid, String[] profile, String   blockDefs) { register(modid, profile, blockDefs, 0); }
	public void register(String modid, String[] profile, String[] blockDefs) { register(modid, profile, blockDefs, 0); }

	public void register(String modid, String profile, String blockDefs, int priority) {
		register(modid, split(profile), split(blockDefs), priority);
	}
	public void register(String modid, String profile, String[] blockDefs, int priority) {
		register(modid, split(profile), wrap(blockDefs), priority);
	}
	public void register(String modid, String[] profile, String blockDefs, int priority) {
		register(modid, wrap(profile), split(blockDefs), priority);
	}
	public void register(String modid, String[] profile, String[] blockDefs, int priority) {
		register(modid, wrap(profile), wrap(blockDefs), priority);
	}
	
	private void register(String modid,
			FluentIterable<String> p, FluentIterable<String> b, int priority) {
		
		String[] profile = p.toArray(String.class);
		String[] blockDefs = b.toArray(String.class);
		
		try {
			if (ignoreDefault(modid))
				entries.put(modid, null);
			else {
				entries.put(modid, new Entry(profile, blockDefs, priority));
				
				ConfigCategory cat = getCategory(modid);
				cat.put(ORE_GEN_PROFILE,
						new Property(ORE_GEN_PROFILE, profile, STRING));
				cat.put(BLOCK_DEFS,
						new Property(BLOCK_DEFS, blockDefs, STRING));
				cat.put(PRIORITY,
						new Property(PRIORITY, String.valueOf(priority), INTEGER));
				
			}
		} catch (Exception e) {
			FMLLog.log(ERROR, e, ERROR_MESSAGE, Etude.NAME);
			
		} finally {
			if (config.hasChanged())
				config.save();
			
		}
		
	}
	
	
	
	
	// Some helper methods and fields to organize the ore generation information data.
	
	private static final Splitter LF = Splitter.on('\n');
	private static final Predicate<String> NOT_BLANK = new Predicate<String>() {
		@Override public boolean apply(String input) {
			return !input.equals("");
		}
	};
	
	private static final CharMatcher QUOTE_OR_SPACE = CharMatcher.anyOf("\" ");
	private static final Function<String, String> WRAP = new Function<String, String>() {
		@Override public String apply(String input) {
			return '\"' + QUOTE_OR_SPACE.trimFrom(input) + '\"';
		}
	};
	
	private static FluentIterable<String> split(String s) {
		return FluentIterable.from(LF.split(s)).filter(NOT_BLANK).transform(WRAP);
	}
	
	private static FluentIterable<String> wrap(String[] s) {
		return FluentIterable.from(Arrays.asList(s)).filter(NOT_BLANK).transform(WRAP);
	}
	
	
	
	
	/**
	 * Find the config category to store the ore generation data for the given mod.
	 * 
	 * @param modid - the ID of the mod to store ore generation data
	 * @return the corresponding category to store the mod's ore generation data
	 */
	private ConfigCategory getCategory(String modid) {
		return config.getCategory(ORE_GEN_CAT + "." + modid);
	}
	
	/**
	 * Check if the default settings should be ignored or not.
	 * 
	 * Note that if default settings is used, the corresponding config part
	 * would also be replaced by the default settings. Have a look at the
	 * register method if you are interested in how the config entries are
	 * replaced.
	 * 
	 * @param modid - the ID of the mod to check if the default settings should be used
	 * @return true if the current config should be used or false if default settings should be used
	 */
	private boolean ignoreDefault(String modid) {
		ConfigCategory cat = getCategory(modid);
		
		Property prop = cat.get(OVERRIDE_DEFAULT);
		if (prop != null)
			return prop.getBoolean(false);
		
		cat.put(OVERRIDE_DEFAULT, new Property(OVERRIDE_DEFAULT, String.valueOf(false), BOOLEAN));
		return false;
	}
	
	
	
	
	/**
	 * The final procedure in this API. Create OreGenerator instances
	 * and register them using GameRegistry.registerWorldGenerator.
	 * Further access to this API would produce a NullPointerException.
	 */
	void registerOreGenerators() {
		try {
			for (ConfigCategory cc : config.getCategory(ORE_GEN_CAT).getChildren())
				addOverridedEntry(cc);

		} catch (Exception e) {
			FMLLog.log(ERROR, e, ERROR_MESSAGE, Etude.NAME);
			
		} finally {
			if (config.hasChanged())
				config.save();
			
		}
		
		for (Map.Entry<String, Entry> me : entries.entrySet())
			if (Loader.isModLoaded(me.getKey()) && me.getValue() != null)
				me.getValue().register();
		
		config = null;
		entries = null;
		
	}
	
	/**
	 * Get the short simple sub-category name from the given category.
	 * Used to retrieve the mod ID from the categories generated
	 * during previous procedures.
	 * 
	 * @param cat - the category to find out the mod ID it represent
	 * @return the mod ID represented by this category.
	 */
	private String getModid(ConfigCategory cat) {
		String s = cat.getQualifiedName();
		
		int dot = s.indexOf('.');
		return s.substring(dot + 1, s.length());
	}
	
	/**
	 * Read data from the config. Convert the data into an entry if required.
	 * Initialize the config entries if they have not yet initialized.
	 * 
	 * @param cat - the category representing a specific mod to be read
	 */
	private void addOverridedEntry(ConfigCategory cat) {
		try {
			String modid = getModid(cat);
			
			Property
			profile = cat.get(ORE_GEN_PROFILE),
			blockDefs = cat.get(BLOCK_DEFS),
			priority = cat.get(PRIORITY);
			
			if (profile == null) {
				profile = new Property(ORE_GEN_PROFILE, new String[0], STRING);
				cat.put(ORE_GEN_PROFILE, profile);
			}
			if (blockDefs == null) {
				blockDefs = new Property(BLOCK_DEFS, new String[0], STRING);
				cat.put(BLOCK_DEFS, blockDefs);
			}
			if (priority == null) {
				priority = new Property(PRIORITY, String.valueOf(0), INTEGER);
				cat.put(PRIORITY, priority);
			}
			
			if (ignoreDefault(modid))
				entries.put(modid, new Entry(
						profile.getStringList(),
						blockDefs.getStringList(),
						priority.getInt()));
			
		} catch (Exception e) {
			FMLLog.log(ERROR, e, ERROR_MESSAGE, Etude.NAME);
			
		}
		
	}
	
}
