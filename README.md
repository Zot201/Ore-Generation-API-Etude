Ore-Generation-API-Etude
========================

Ore Generation API Étude

This mod refactors the ore generation being used in OnlySilver into an API. Only the Etude class, the OreGenAPI class and the tester classes located in the testers package are new. The OreGenerator class would be the same as the one in OnlySilver.

I would suggest you to install and run this mod once first (including the testers contained in the testers sub-package), and have a look at the config file to see how the generated config look like.

Update:
There is a example of how it look like in a generated config.



# Configuration file

####################
# ore_generation_settings
####################

ore_generation_settings {
    ####################
    # etudeoregen
    ####################

    etudeoregen {
        S:"Block Definitions" <
            "stone       = minecraft:stone"
            "lapis_block = minecraft:lapis_block"
         >
        S:"Ore Generation Profile" <
            "stone of ALL -> ALL = lapis_block x 50 x 50"
         >
        B:"Override Default Settings"=false
        I:"World Generator Priority"=0
    }

    ####################
    # etudeoregen_tester
    ####################

    etudeoregen_tester {
        S:"Block Definitions" <
            "stone       = minecraft:stone"
            "quartz_ore  = minecraft:quartz_ore"
         >
        S:"Ore Generation Profile" <
            "stone of ALL -> ALL = quartz_ore x 50 x 50"
         >
        B:"Override Default Settings"=false
        I:"World Generator Priority"=0
    }

    ####################
    # etudeoregen_tester1
    ####################

    etudeoregen_tester1 {
        S:"Block Definitions" <
            "stone       = minecraft:stone"
            "glowstone   = minecraft:glowstone"
         >
        S:"Ore Generation Profile" <
            "stone of ALL -> ALL = glowstone x 50 x 50"
         >
        B:"Override Default Settings"=false
        I:"World Generator Priority"=0
    }

    ####################
    # etudeoregen_tester2
    ####################

    etudeoregen_tester2 {
        S:"Block Definitions" <
            "stone       = minecraft:stone"
            "lit_pumpkin = minecraft:lit_pumpkin"
         >
        S:"Ore Generation Profile" <
            "stone of ALL -> ALL = lit_pumpkin x 50 x 50"
         >
        B:"Override Default Settings"=false
        I:"World Generator Priority"=0
    }

}


