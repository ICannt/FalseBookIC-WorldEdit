package com.bukkit.gemo.FalseBook.IC.ICs.worldedit;

import com.bukkit.gemo.FalseBook.IC.ICs.BaseChip;
import com.bukkit.gemo.FalseBook.IC.ICs.BaseIC;
import com.bukkit.gemo.FalseBook.IC.ICs.ICGroup;
import com.bukkit.gemo.FalseBook.IC.ICs.InputState;
import com.bukkit.gemo.FalseBook.IC.ICs.Lever;
import com.bukkit.gemo.utils.BlockUtils;
import com.bukkit.gemo.utils.FBBlockType;
import com.bukkit.gemo.utils.Parser;
import com.bukkit.gemo.utils.SignUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.event.block.SignChangeEvent;

public class ICSetBlockDown extends BaseIC {

    public ICSetBlockDown() {
        this.ICName = "SET BLOCK BELOW";
        this.ICNumber = "ic.setblockdown";
        setICGroup(ICGroup.WORLDEDIT);
        this.chipState = new BaseChip(true, false, false, "Clock", "", "");
        this.chipState.setOutputs("Output = Input", "", "");
        this.chipState.setLines("BlockID[:SubID]", "FORCE to set the block even if there is already a block there.");
        this.ICDescription = "The MC1206 sets a block of a specified type two blocks below the block behind the IC sign. <a href=\"MC1205.html\">MC1205</a> is the version of the IC that sets the block above.";
    }

    public void checkCreation(SignChangeEvent event) {
        if (!Parser.isBlock(event.getLine(1))) {
            SignUtils.cancelSignCreation(event, "Item not found.");
            return;
        }

        FBBlockType item = Parser.getBlock(event.getLine(1));
        if (!BlockUtils.isValidBlock(item.getItemID())) {
            SignUtils.cancelSignCreation(event, "This is not a valid blocktype.");
            return;
        }

        if (!Parser.isStringOrEmpty(event.getLine(2), "force")) {
            SignUtils.cancelSignCreation(event, "Line 4 must be empty or 'FORCE'.");
            return;
        }

        event.setLine(2, event.getLine(2).toUpperCase());
    }

    public void Execute(Sign signBlock, InputState currentInputs, InputState previousInputs) {
        if ((currentInputs.isInputOneHigh()) && (previousInputs.isInputOneLow())) {
            if (!Parser.isBlock(signBlock.getLine(1))) {
                return;
            }

            FBBlockType item = Parser.getBlock(signBlock.getLine(1));
            if (!BlockUtils.isValidBlock(item.getItemID())) {
                return;
            }

            if (!Parser.isStringOrEmpty(signBlock.getLine(2), "force")) {
                return;
            }

            boolean force = Parser.isString(signBlock.getLine(2), "force");

            Location newBlockLoc = getICBlock(signBlock).getBlock().getRelative(0, -2, 0).getLocation();
            if ((newBlockLoc.getBlock().getType().equals(Material.AIR)) || (force)) {
                newBlockLoc.getBlock().setTypeIdAndData(item.getItemID(), item.getItemDataAsByte(), true);
                switchLever(Lever.BACK, signBlock, true);
            }
        } else {
            switchLever(Lever.BACK, signBlock, false);
        }
    }
}