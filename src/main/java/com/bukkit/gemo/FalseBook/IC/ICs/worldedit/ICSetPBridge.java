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

public class ICSetPBridge extends BaseIC {

    public ICSetPBridge() {
        this.ICName = "SET P-BRIDGE";
        this.ICNumber = "ic.pbridge";
        setICGroup(ICGroup.WORLDEDIT);
        this.chipState = new BaseChip(true, false, false, "Clock", "", "");
        this.chipState.setOutputs("Output = Input", "", "");
        this.chipState.setLines("BlockIDOn[:SubID][-BlockIDOff[:SubID]] (Examples: 'wool:15-stone' or 'grass' or 'dirt-44:2')", "xOffset,yOffset[,zOffset]:width,depth (0 being the IC block). Example: -1,2,2:3,5");
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
                String[] bridgeSplit = event.getLine(3).split(":");
                if ((bridgeSplit[0].charAt(0) == 'l') || (bridgeSplit[0].charAt(0) == 'L')) {
                    bridgeSplit[0] = bridgeSplit[0].substring(1);
                }

                String[] bridgePosition = bridgeSplit[0].split(",");
                Integer.parseInt(bridgePosition[0]);
                if (bridgePosition.length == 2) {
                    Integer.parseInt(bridgePosition[1]);
                }
                if (bridgePosition.length == 3) {
                    Integer.parseInt(bridgePosition[1]);
                    Integer.parseInt(bridgePosition[2]);
                }

                if (bridgeSplit.length == 2) {
                    String[] doorSize = bridgeSplit[1].split(",");
                    if (Integer.parseInt(doorSize[0]) < 1) {
                        SignUtils.cancelSignCreation(event, "Bridge width must be more then 0.");
                        return;
                    }
                    if ((doorSize.length != 2)
                            || (Integer.parseInt(doorSize[1]) >= 1)) {
                        return;
                    }
                    SignUtils.cancelSignCreation(event, "Bridge length must be more then 0.");
                    return;
                }

                SignUtils.cancelSignCreation(event, "Bridge size required.");
                return;
            } catch (Exception e) {
                SignUtils.cancelSignCreation(event, "Line 4 is not valid.");
                return;
            }
        } else {
            SignUtils.cancelSignCreation(event, "Bridge position and size required.");
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
        Vector bridgePosition = null;
        Point bridgeSize = null;
        Boolean light = Boolean.valueOf(false);

        if (signBlock.getLine(3) != null) {
            String[] bridgeSplit = signBlock.getLine(3).split(":");
            if ((bridgeSplit[0].charAt(0) == 'l') || (bridgeSplit[0].charAt(0) == 'L')) {
                light = Boolean.valueOf(true);
                bridgeSplit[0] = bridgeSplit[0].substring(1);
            }
            bridgePosition = FetchVector(bridgeSplit[0], 0);
            if (bridgeSplit.length == 2) {
                bridgeSize = FetchPoint(bridgeSplit[1], 1);
            } else {
                return;
            }

        }

        if ((bridgePosition == null) || (bridgeSize == null)) {
            return;
        }

        if (light.booleanValue()) {
            bridgePosition.setX(bridgePosition.getBlockX() - 1);
            bridgeSize.x += 2;
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
        Location[] newBlockPositions = new Location[bridgeSize.x * bridgeSize.y];

        int direction = SignUtils.getDirection(signBlock);
        int hFix = 1;
        int vFix = 1;
        byte ruler = 0;
        if ((direction == 1) || (direction == 3)) {
            ruler = 0;
            if (direction == 3) {
                hFix = -1;
            } else {
                vFix = -1;
            }
            for (int wX = 0; wX < bridgeSize.x; wX++) {
                for (int fY = 0; fY < bridgeSize.y; fY++) {
                    newBlockPositions[(wX * bridgeSize.y + fY)] =
                            new Location(basePosition.getWorld(), basePosition.getX() + (wX + bridgePosition.getBlockX()) * hFix,
                            basePosition.getY() + bridgePosition.getBlockY(), basePosition.getZ() + (fY + bridgePosition.getBlockZ()) * vFix);
                }
            }
        } else if ((direction == 2) || (direction == 4)) {
            ruler = 1;
            if (direction == 2) {
                hFix = -1;
                vFix = -1;
            }
            for (int wX = 0; wX < bridgeSize.x; wX++) {
                for (int fY = 0; fY < bridgeSize.y; fY++) {
                    newBlockPositions[(wX * bridgeSize.y + fY)] = new Location(basePosition.getWorld(), basePosition.getX() + (fY + bridgePosition.getBlockZ()) * vFix, basePosition.getY() + bridgePosition.getBlockY(), basePosition.getZ() + (wX + bridgePosition.getBlockX()) * hFix);
                }
            }
        }

        if ((currentInputs.isInputOneHigh()) && (previousInputs.isInputOneLow())) {
            for (Location pos : newBlockPositions) {
                Block b = pos.getBlock();
                if (light.booleanValue()) {
                    if (ruler == 0) {
                        int x = b.getLocation().getBlockX();
                        if ((x == basePosition.getX() + bridgePosition.getBlockX() * hFix) || (x == basePosition.getX() + (bridgePosition.getBlockX() + bridgeSize.x) * hFix - hFix)) {
                            b.setTypeIdAndData(89, (byte) 0, true);
                            continue;
                        }
                    } else if (ruler == 1) {
                        int z = b.getLocation().getBlockZ();
                        if ((z == basePosition.getZ() + bridgePosition.getBlockX() * hFix) || (z == basePosition.getZ() + (bridgePosition.getBlockX() + bridgeSize.x) * hFix - hFix)) {
                            b.setTypeIdAndData(89, (byte) 0, true);
                            continue;
                        }
                    }
                }
                b.setTypeIdAndData(itemList.get(0).getItemID(), itemList.get(0).getItemDataAsByte(), true);
            }
            switchLever(Lever.BACK, signBlock, true);
        } else if ((currentInputs.isInputOneLow()) && (previousInputs.isInputOneHigh())) {
            for (Location pos : newBlockPositions) {
                Block b = pos.getBlock();
                if (light.booleanValue()) {
                    if (ruler == 0) {
                        int x = b.getLocation().getBlockX();
                        if ((x == basePosition.getX() + bridgePosition.getBlockX() * hFix - 1.0D) || (x == basePosition.getX() + bridgePosition.getBlockX() * hFix + bridgeSize.x)) {
                            b.setTypeIdAndData(0, (byte) 0, true);
                            continue;
                        }
                    } else if (ruler == 1) {
                        int z = b.getLocation().getBlockZ();
                        if ((z == basePosition.getZ() + bridgePosition.getBlockX() * hFix - 1.0D) || (z == basePosition.getZ() + bridgePosition.getBlockX() * hFix + bridgeSize.x)) {
                            b.setTypeIdAndData(0, (byte) 0, true);
                            continue;
                        }
                    }
                }
                b.setTypeIdAndData(itemList.get(1).getItemID(), itemList.get(1).getItemDataAsByte(), true);
            }
            switchLever(Lever.BACK, signBlock, false);
        }
    }
}