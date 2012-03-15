package com.bukkit.gemo.FalseBook.IC.ICs.worldedit;

import com.bukkit.gemo.FalseBook.IC.ICs.BaseChip;
import com.bukkit.gemo.FalseBook.IC.ICs.BaseIC;
import com.bukkit.gemo.FalseBook.IC.ICs.ICGroup;
import com.bukkit.gemo.FalseBook.IC.ICs.InputState;
import com.bukkit.gemo.FalseBook.IC.ICs.Lever;
import com.bukkit.gemo.utils.Parser;
import com.bukkit.gemo.utils.SignUtils;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.event.block.SignChangeEvent;

public class ICSetWeather extends BaseIC {

    public ICSetWeather() {
        this.ICName = "SET WEATHER";
        this.ICNumber = "ic.setweather";
        setICGroup(ICGroup.WORLDEDIT);
        this.chipState = new BaseChip(true, false, false, "Clock", "", "");
        this.chipState.setOutputs("Output = Input", "", "");
        this.chipState.setLines("SUN or RAIN or STORM", "");
        this.ICDescription = "The MC1285 sets the weather to the specified weather, whenever the input (the \"clock\") goes from low to high.";
    }

    public void checkCreation(SignChangeEvent event) {
        event.setLine(2, "");
        event.setLine(3, "");

        if ((!Parser.isString(event.getLine(1), "sun")) && (!Parser.isString(event.getLine(1), "rain")) && (!Parser.isString(event.getLine(1), "storm"))) {
            SignUtils.cancelSignCreation(event, ChatColor.RED + "Line 3 must be sun, rain or storm");
            return;
        }
        event.setLine(1, event.getLine(1).toUpperCase());
    }

    public void Execute(Sign signBlock, InputState currentInputs, InputState previousInputs) {
        if ((!Parser.isString(signBlock.getLine(1), "sun")) && (!Parser.isString(signBlock.getLine(1), "rain")) && (!Parser.isString(signBlock.getLine(1), "storm"))) {
            return;
        }

        if ((currentInputs.isInputOneHigh()) && (previousInputs.isInputOneLow())) {
            if (signBlock.getLine(1).equalsIgnoreCase("sun")) {
                signBlock.getWorld().setStorm(false);
                signBlock.getWorld().setThundering(false);
            } else if (signBlock.getLine(1).equalsIgnoreCase("rain")) {
                signBlock.getWorld().setStorm(true);
                signBlock.getWorld().setThundering(false);
            } else if (signBlock.getLine(1).equalsIgnoreCase("storm")) {
                signBlock.getWorld().setStorm(true);
                signBlock.getWorld().setThundering(true);
            }
            switchLever(Lever.BACK, signBlock, true);
        } else {
            switchLever(Lever.BACK, signBlock, false);
        }
    }
}