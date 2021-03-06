package com.bukkit.gemo.FalseBook.IC.ICs.worldedit;

import com.bukkit.gemo.FalseBook.IC.ICs.BaseChip;
import com.bukkit.gemo.FalseBook.IC.ICs.BaseIC;
import com.bukkit.gemo.FalseBook.IC.ICs.ICGroup;
import com.bukkit.gemo.FalseBook.IC.ICs.InputState;
import com.bukkit.gemo.FalseBook.IC.ICs.Lever;
import org.bukkit.block.Sign;
import org.bukkit.event.block.SignChangeEvent;

public class ICTimeControl extends BaseIC {

    public ICTimeControl() {
        this.ICName = "TIME CONTROL";
        this.ICNumber = "ic.timecontrol";
        setICGroup(ICGroup.WORLDEDIT);
        this.chipState = new BaseChip(true, true, false, "Clock", "Datainput", "");
        this.chipState.setOutputs("Output = Input", "", "");
        this.ICDescription = "The MC3231 changes the time of day when the clock input goes from low to high. The time of day is changed to day if the datainput is high and to night if the datainput is low.";
    }

    public void checkCreation(SignChangeEvent event) {
        event.setLine(1, "");
        event.setLine(2, "");
        event.setLine(3, "");
    }

    public void Execute(Sign signBlock, InputState currentInputs, InputState previousInputs) {
        if ((currentInputs.isInputOneHigh()) && (previousInputs.isInputOneLow())) {
            signBlock.getWorld().setTime(currentInputs.isInputTwoHigh() ? 0 : 13000);
            switchLever(Lever.BACK, signBlock, true);
        } else {
            switchLever(Lever.BACK, signBlock, false);
        }
    }
}