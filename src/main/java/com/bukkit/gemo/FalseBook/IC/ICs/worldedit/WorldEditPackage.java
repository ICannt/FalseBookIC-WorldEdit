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
        
        ICUpgrade.addUpgrader("[MC1200]", new ICUpgraderMC("ic.mobspawner"));
        ICUpgrade.addUpgrader("[MC1201]", new ICUpgraderMC("ic.dispenser"));
        ICUpgrade.addUpgrader("[MC1203]", new ICUpgraderMC("ic.lightning"));
        ICUpgrade.addUpgrader("[MC1205]", new ICUpgraderMC("ic.setblockup"));
        ICUpgrade.addUpgrader("[MC1206]", new ICUpgraderMC("ic.setblockdown"));
        ICUpgrade.addUpgrader("[MC1207]", new ICUpgraderMC("ic.setblock"));
        ICUpgrade.addUpgrader("[MC1210]", new ICUpgraderMC("ic.pdoor"));
        ICUpgrade.addUpgrader("[MC1211]", new ICUpgraderMC("ic.pbridge"));
        ICUpgrade.addUpgrader("[MC1220]", new ICUpgraderMC("ic.replacer"));
        ICUpgrade.addUpgrader("[MC1231]", new ICUpgraderMC("ic.setday"));
        ICUpgrade.addUpgrader("[MC1232]", new ICUpgraderMC("ic.settime"));
        ICUpgrade.addUpgrader("[MC1240]", new ICUpgraderMC("ic.arrowshooter"));
        ICUpgrade.addUpgrader("[MC1241]", new ICUpgraderMC("ic.arrowbarrage"));
        ICUpgrade.addUpgrader("[MC1265]", new ICUpgraderMC("ic.vanisher"));
        ICUpgrade.addUpgrader("[MC1285]", new ICUpgraderMC("ic.setweather"));
        ICUpgrade.addUpgrader("[MC1510]", new ICUpgraderMC("ic.msgsender"));
        ICUpgrade.addUpgrader("[MC1511]", new ICUpgraderMC("ic.cmdsender"));
        ICUpgrade.addUpgrader("[MC1700]", new ICUpgraderMC("ic.teleporter"));
        ICUpgrade.addUpgrader("[MC3231]", new ICUpgraderMC("ic.timecontrol"));
    }
}