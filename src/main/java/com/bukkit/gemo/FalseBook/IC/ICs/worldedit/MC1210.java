package com.bukkit.gemo.FalseBook.IC.ICs.worldedit;

import com.bukkit.gemo.FalseBook.IC.ICs.BaseChip;
import com.bukkit.gemo.FalseBook.IC.ICs.BaseIC;
import com.bukkit.gemo.FalseBook.IC.ICs.ICGroup;
import com.bukkit.gemo.FalseBook.IC.ICs.InputState;
import com.bukkit.gemo.FalseBook.IC.ICs.Lever;
import com.bukkit.gemo.utils.BlockUtils;
import com.bukkit.gemo.utils.FBItemType;
import com.bukkit.gemo.utils.SignUtils;
import java.awt.Point;
import java.util.ArrayList;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.util.Vector;

public class MC1210 extends BaseIC {

    public MC1210() {
        this.ICName = "SET P-DOOR";
        this.ICNumber = "[MC1210]";
        setICGroup(ICGroup.WORLDEDIT);
        this.chipState = new BaseChip(true, false, false, "Clock", "", "");
        this.chipState.setOutputs("Output = Input", "", "");
        this.chipState.setLines("BlockIDOn[:SubID][-BlockIDOff[:SubID]] (Examples: 'wool:15-stone' or 'grass' or 'dirt-44:2')", "xOffset,yOffset[,zOffset]:width,height (0 being the IC block). Example: -1,2,2:3,5");
    }

    public void checkCreation(SignChangeEvent event) {
        ArrayList<FBItemType> itemList = SignUtils.parseLineToItemListWithSize(event.getLine(2), "-", true, 1, 2);
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

        if (event.getLine(3).length() > 0) {
            try {
                String[] doorSplit = event.getLine(3).split(":");

                String[] doorPosition = doorSplit[0].split(",");
                Integer.parseInt(doorPosition[0]);
                if (doorPosition.length == 2) {
                    Integer.parseInt(doorPosition[1]);
                }
                if (doorPosition.length == 3) {
                    Integer.parseInt(doorPosition[1]);
                    Integer.parseInt(doorPosition[2]);
                }

                if (doorSplit.length == 2) {
                    String[] doorSize = doorSplit[1].split(",");
                    if (Integer.parseInt(doorSize[0]) < 1) {
                        SignUtils.cancelSignCreation(event, "Door width must be more then 0.");
                        return;
                    }
                    if ((doorSize.length != 2)
                            || (Integer.parseInt(doorSize[1]) >= 1)) {
                        return;
                    }
                    SignUtils.cancelSignCreation(event, "Door height must be more then 0.");
                    return;
                }

                SignUtils.cancelSignCreation(event, "Door size required.");
                return;
            } catch (Exception e) {
                SignUtils.cancelSignCreation(event, "Line 4 is not valid.");
                return;
            }
        } else {
            SignUtils.cancelSignCreation(event, "Door position and size required.");
            return;
        }
    }

    private static Point FetchPoint(String data, int a) {
        String[] pointData = data.split(",");
        Point point = new Point(a, a);
        try {
            point.x = Integer.parseInt(pointData[0]);
            if (pointData.length == 2) {
                point.y = Integer.parseInt(pointData[1]);
            }
        } catch (Exception ex) {
            return null;
        }
        return point;
    }

    private static Vector FetchVector(String data, int a) {
        String[] pointData = data.split(",");
        Vector point = new Vector(a, a, a);
        try {
            point.setX(Integer.parseInt(pointData[0]));
            if (pointData.length == 2) {
                point.setY(Integer.parseInt(pointData[1]));
                point.setZ(Integer.parseInt("0"));
            }
            if (pointData.length == 3) {
                point.setY(Integer.parseInt(pointData[1]));
                point.setZ(Integer.parseInt(pointData[2]));
            }
        } catch (Exception ex) {
            return null;
        }
        return point;
    }

    public void Execute(Sign signBlock, InputState currentInputs, InputState previousInputs) {
        Vector doorPosition = null;
        Point doorSize = null;

        if (signBlock.getLine(3) != null) {
            String[] doorSplit = signBlock.getLine(3).split(":");
            doorPosition = FetchVector(doorSplit[0], 0);
            if (doorSplit.length == 2) {
                doorSize = FetchPoint(doorSplit[1], 1);
            }

        }

        if ((doorPosition == null) || (doorSize == null)) {
            return;
        }

        ArrayList<FBItemType> itemList = SignUtils.parseLineToItemListWithSize(signBlock.getLine(2), "-", true, 1, 2);
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
        Location basePosition = getICBlock(signBlock);
        Location[] newBlockPositions = new Location[doorSize.x * doorSize.y];

        int direction = SignUtils.getDirection(signBlock);
        int hFix = 1;
        int zFix = -1;
        if ((direction == 1) || (direction == 3)) {
            if (direction == 3) {
                hFix = -1;
                zFix = 1;
            }
            for (int x = 0; x < doorSize.x; x++) {
                for (int y = 0; y < doorSize.y; y++) {
                    newBlockPositions[(x * doorSize.y + y)] = new Location(basePosition.getWorld(), basePosition.getX() + (x + doorPosition.getBlockX()) * hFix, basePosition.getY() + y + doorPosition.getBlockY(), basePosition.getZ() + doorPosition.getBlockZ() * zFix);
                }
            }
        } else if ((direction == 2) || (direction == 4)) {
            zFix = 1;
            if (direction == 2) {
                hFix = -1;
                zFix = -1;
            }
            for (int x = 0; x < doorSize.x; x++) {
                for (int y = 0; y < doorSize.y; y++) {
                    newBlockPositions[(x * doorSize.y + y)] = new Location(basePosition.getWorld(), basePosition.getX() + doorPosition.getBlockZ() * zFix, basePosition.getY() + y + doorPosition.getBlockY(), basePosition.getZ() + (x + doorPosition.getBlockX()) * hFix);
                }
            }
        }

        if ((currentInputs.isInputOneHigh()) && (previousInputs.isInputOneLow())) {
            for (Location pos : newBlockPositions) {
                Block b = pos.getBlock();
                if (!BlockUtils.LocationEquals(b.getLocation(), signBlock.getBlock().getLocation())) {
                    b.setTypeIdAndData(itemList.get(0).getItemID(), itemList.get(0).getItemDataAsByte(), true);
                }
            }
            switchLever(Lever.BACK, signBlock, true);
        } else if ((currentInputs.isInputOneLow()) && (previousInputs.isInputOneHigh())) {
            for (Location pos : newBlockPositions) {
                Block b = pos.getBlock();
                if (!BlockUtils.LocationEquals(b.getLocation(), signBlock.getBlock().getLocation())) {
                    b.setTypeIdAndData(itemList.get(1).getItemID(), itemList.get(1).getItemDataAsByte(), true);
                }
            }
            switchLever(Lever.BACK, signBlock, false);
        }
    }
}