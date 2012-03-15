package com.bukkit.gemo.FalseBook.IC.ICs.worldedit;

import com.bukkit.gemo.FalseBook.IC.ICs.BaseChip;
import com.bukkit.gemo.FalseBook.IC.ICs.BaseIC;
import com.bukkit.gemo.FalseBook.IC.ICs.ICGroup;
import com.bukkit.gemo.FalseBook.IC.ICs.InputState;
import com.bukkit.gemo.FalseBook.IC.ICs.Lever;
import com.bukkit.gemo.utils.BlockUtils;
import com.bukkit.gemo.utils.FBBlockType;
import com.bukkit.gemo.utils.ICUtils;
import com.bukkit.gemo.utils.Parser;
import com.bukkit.gemo.utils.SignUtils;
import org.bukkit.block.Sign;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.ItemStack;

public class ICDispenser extends BaseIC {

    public ICDispenser() {
        this.ICName = "DISPENSER";
        this.ICNumber = "ic.dispenser";
        setICGroup(ICGroup.WORLDEDIT);
        this.chipState = new BaseChip(true, false, false, "Clock", "", "");
        this.chipState.setOutputs("Output = Input", "", "");
        this.chipState.setLines("ItemID[:SubID]", "Blank to drop one item or a number to drop more items (in a stack)");
        this.ICDescription = "The MC1201 spawns an item in the first free space above the block behind the IC sign when the input (the \"clock\") goes from low to high.";
    }

    public void checkCreation(SignChangeEvent event) {
        if (!Parser.isBlock(event.getLine(1))) {
            SignUtils.cancelSignCreation(event, "Item not found.");
            return;
        }

        if (!Parser.isIntegerOrEmpty(event.getLine(2))) {
            SignUtils.cancelSignCreation(event, "Line 4 must be a number.");
            return;
        }
    }

    public void Execute(Sign signBlock, InputState currentInputs, InputState previousInputs) {
        if ((currentInputs.isInputOneHigh()) && (previousInputs.isInputOneLow())) {
            if (!Parser.isBlock(signBlock.getLine(1))) {
                return;
            }
            if (!Parser.isIntegerOrEmpty(signBlock.getLine(2))) {
                return;
            }

            FBBlockType item = BlockUtils.getItemFromString(signBlock.getLine(1));
            int count = Parser.getInteger(signBlock.getLine(2), 1);
            if ((count < 1) || (count > 64)) {
                count = 64;
            }
            if (item == null) {
                return;
            }
            if (item.getItemID() < 1) {
                return;
            }

            ItemStack itemstack = new ItemStack(item.getItemID(), count);
            itemstack.setDurability(item.getItemData());

            ICUtils.dropItemOnNextFreeBlockAbove(getICBlock(signBlock), itemstack, 10);
            switchLever(Lever.BACK, signBlock, true);
            item = null;
        } else {
            switchLever(Lever.BACK, signBlock, false);
        }
    }
}