package com.bukkit.gemo.FalseBook.IC.ICs.worldedit;

import com.bukkit.gemo.FalseBook.IC.ICs.ExternalICPackage;

public class WorldEditPackage extends ExternalICPackage {

    public WorldEditPackage() {
        setAPI_VERSION("1.1");

        setShowImportMessages(false);
        addIC(MC1200.class);
        addIC(MC1201.class);
        addIC(MC1203.class);
        addIC(MC1205.class);
        addIC(MC1206.class);
        addIC(MC1207.class);
        addIC(MC1210.class);
        addIC(MC1211.class);
        addIC(MC1220.class);
        addIC(MC1231.class);
        addIC(MC1232.class);
        addIC(MC1240.class);
        addIC(MC1241.class);
        addIC(MC1265.class);
        addIC(MC1285.class);
        addIC(MC1510.class);
        addIC(MC1511.class);
        addIC(MC1700.class);
        addIC(MC3231.class);
    }
}