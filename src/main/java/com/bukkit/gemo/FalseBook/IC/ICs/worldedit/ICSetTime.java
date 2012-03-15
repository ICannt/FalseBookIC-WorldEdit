package com.bukkit.gemo.FalseBook.IC.ICs.worldedit;

import com.bukkit.gemo.FalseBook.IC.ICs.BaseChip;
import com.bukkit.gemo.FalseBook.IC.ICs.BaseIC;
import com.bukkit.gemo.FalseBook.IC.ICs.ICGroup;
import com.bukkit.gemo.FalseBook.IC.ICs.InputState;
import com.bukkit.gemo.FalseBook.IC.ICs.Lever;
import com.bukkit.gemo.utils.SignUtils;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.event.block.SignChangeEvent;

public class ICSetTime extends BaseIC {

    public ICSetTime() {
        this.ICName = "SET-TIME";
        this.ICNumber = "ic.settime";
        setICGroup(ICGroup.WORLDEDIT);
        this.chipState = new BaseChip(true, false, false, "Clock", "", "");
        this.chipState.setOutputs("Output = Input", "", "");
        this.chipState.setLines("worldtime in ticks", "");
        this.ICDescription = "The MC1232 sets the time to the specified time whenever the input (the \"clock\") goes from low to high.";
    }

    public void checkCreation(SignChangeEvent event) {
        try {
            Integer.valueOf(event.getLine(2));
        } catch (Exception e) {
            SignUtils.cancelSignCreation(event, ChatColor.RED + "Line 3 must be a number.");
            return;
        }

        if (Integer.valueOf(event.getLine(2)).intValue() < 0) {
            event.setLine(2, "0");
        }
        if (Integer.valueOf(event.getLine(2)).intValue() >= 24000) {
            event.setLine(2, "24000");
        }
        event.setLine(3, "");
    }

    public void Execute(Sign signBlock, InputState currentInputs, InputState previousInputs) {
        int zeit = 0;
        try {
            zeit = Integer.valueOf(signBlock.getLine(2)).intValue();
            if ((zeit < 0) || (zeit >= 24000)) {
                zeit = 0;
            }
        } catch (Exception e) {
            return;
        }

        if ((currentInputs.isInputOneHigh()) && (previousInputs.isInputOneLow())) {
            signBlock.getWorld().setTime(zeit);
            switchLever(Lever.BACK, signBlock, true);
        } else {
            switchLever(Lever.BACK, signBlock, false);
        }
    }
}