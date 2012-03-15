package com.bukkit.gemo.FalseBook.IC.ICs.worldedit;

import com.bukkit.gemo.FalseBook.IC.ICs.BaseChip;
import com.bukkit.gemo.FalseBook.IC.ICs.BaseIC;
import com.bukkit.gemo.FalseBook.IC.ICs.ICGroup;
import com.bukkit.gemo.FalseBook.IC.ICs.InputState;
import com.bukkit.gemo.FalseBook.IC.ICs.Lever;
import com.bukkit.gemo.utils.BlockUtils;
import com.bukkit.gemo.utils.FBItemType;
import com.bukkit.gemo.utils.Parser;
import com.bukkit.gemo.utils.SignUtils;
import java.util.ArrayList;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.event.block.SignChangeEvent;

public class ICSetBlock extends BaseIC {

    public ICSetBlock() {
        this.ICName = "SET BLOCK";
        this.ICNumber = "ic.setblock";
        setICGroup(ICGroup.WORLDEDIT);
        this.chipState = new BaseChip(true, false, false, "Clock", "", "");
        this.chipState.setOutputs("Output = Input", "", "");
        this.chipState.setLines("BlockIDOn[:SubID][-BlockIDOff[:SubID]] (Examples: 'wool:15-stone' or 'grass' or 'dirt-44:2')", "Y offset, with 0 being the IC block.");
        this.ICDescription = "The MC1207 sets the specified block to the specified blocktype whenever the input (the \"clock\") changes.";
    }

    public void checkCreation(SignChangeEvent event) {
        ArrayList<FBItemType> itemList = SignUtils.parseLineToItemListWithSize(event.getLine(1), "-", true, 1, 2);
        if (itemList == null) {
            SignUtils.cancelSignCreation(event, "Line 3 is not valid. Usage: BlockIDOn[:SubID][-BlockIDOff[:SubID]]");
            return;
        }

        for (FBItemType item : itemList) {
            if (!BlockUtils.isValidBlock(item.getItemID())) {
                SignUtils.cancelSignCreation(event, "'" + Material.getMaterial(item.getItemID()).name() + "' is not a block.");
                return;
            }

        }

        if (!Parser.isInteger(event.getLine(2))) {
            SignUtils.cancelSignCreation(event, "Line 4 must be a number.");
            return;
        }
    }

    public void Execute(Sign signBlock, InputState currentInputs, InputState previousInputs) {
        ArrayList<FBItemType> itemList = SignUtils.parseLineToItemListWithSize(signBlock.getLine(1), "-", true, 1, 2);
        if (itemList == null) {
            return;
        }

        for (FBItemType item : itemList) {
            if (!BlockUtils.isValidBlock(item.getItemID())) {
                return;
            }

        }

        if (itemList.size() == 1) {
            itemList.add(new FBItemType(0));
        }

        if (!Parser.isInteger(signBlock.getLine(2))) {
            return;
        }

        Location newBlockLoc = getICBlock(signBlock).getBlock().getRelative(0, Parser.getInteger(signBlock.getLine(2), 1), 0).getLocation();
        if ((currentInputs.isInputOneHigh()) && (previousInputs.isInputOneLow())) {
            newBlockLoc.getBlock().setTypeIdAndData(itemList.get(0).getItemID(), itemList.get(0).getItemDataAsByte(), true);
            switchLever(Lever.BACK, signBlock, true);
        } else {
            newBlockLoc.getBlock().setTypeIdAndData(itemList.get(1).getItemID(), itemList.get(1).getItemDataAsByte(), true);
            switchLever(Lever.BACK, signBlock, false);
        }

        itemList.clear();
        itemList = null;
    }
}