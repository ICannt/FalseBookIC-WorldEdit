package com.bukkit.gemo.FalseBook.IC.ICs.worldedit;

import com.bukkit.gemo.FalseBook.IC.ICs.ExternalICPackage;
import com.bukkit.gemo.FalseBook.IC.ICs.ICUpgrade;
import com.bukkit.gemo.FalseBook.IC.ICs.ICUpgraderMC;

public class WorldEditPackage extends ExternalICPackage {

    public WorldEditPackage() {
        setAPI_VERSION("1.1");

        setShowImportMessages(false);
        addIC(ICMobSpawner.class);
        addIC(ICDispenser.class);
        addIC(ICLightning.class);
        addIC(ICSetBlockUp.class);
        addIC(ICSetBlockDown.class);
        addIC(ICSetBlock.class);
        addIC(ICSetPDoor.class);
        addIC(ICSetPBridge.class);
        addIC(ICReplacer.class);
        addIC(ICSetDay.class);
        addIC(ICSetTime.class);
        addIC(ICArrowShooter.class);
        addIC(ICArrowBarrage.class);
        addIC(ICVanisher.class);
        addIC(ICSetWeather.class);
        addIC(ICMessageSender.class);
        addIC(ICCommandSender.class);
        addIC(ICTeleporter.class);
        addIC(ICTimeControl.class);
        addIC(ICEntityCannon.class);

        addUpgrader("[MC1200]", new ICUpgraderMC("ic.mobspawner"));
        addUpgrader("[MC1201]", new ICUpgraderMC("ic.dispenser"));
        addUpgrader("[MC1203]", new ICUpgraderMC("ic.lightning"));
        addUpgrader("[MC1205]", new ICUpgraderMC("ic.setblockup"));
        addUpgrader("[MC1206]", new ICUpgraderMC("ic.setblockdown"));
        addUpgrader("[MC1207]", new ICUpgraderMC("ic.setblock"));
        addUpgrader("[MC1210]", new ICUpgraderMC("ic.pdoor"));
        addUpgrader("[MC1211]", new ICUpgraderMC("ic.pbridge"));
        addUpgrader("[MC1220]", new ICUpgraderMC("ic.replacer"));
        addUpgrader("[MC1231]", new ICUpgraderMC("ic.setday"));
        addUpgrader("[MC1232]", new ICUpgraderMC("ic.settime"));
        addUpgrader("[MC1240]", new ICUpgraderMC("ic.arrowshooter"));
        addUpgrader("[MC1241]", new ICUpgraderMC("ic.arrowbarrage"));
        addUpgrader("[MC1265]", new ICUpgraderMC("ic.vanisher"));
        addUpgrader("[MC1285]", new ICUpgraderMC("ic.setweather"));
        addUpgrader("[MC1510]", new ICUpgraderMC("ic.msgsender"));
        addUpgrader("[MC1511]", new ICUpgraderMC("ic.cmdsender"));
        addUpgrader("[MC1700]", new ICUpgraderMC("ic.teleporter"));
        addUpgrader("[MC3231]", new ICUpgraderMC("ic.timecontrol"));
    }
}