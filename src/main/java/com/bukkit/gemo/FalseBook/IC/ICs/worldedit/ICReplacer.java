package com.bukkit.gemo.FalseBook.IC.ICs.worldedit;

import com.bukkit.gemo.FalseBook.IC.FalseBookICCore;
import com.bukkit.gemo.FalseBook.IC.ICs.BaseChip;
import com.bukkit.gemo.FalseBook.IC.ICs.BaseIC;
import com.bukkit.gemo.FalseBook.IC.ICs.ICGroup;
import com.bukkit.gemo.FalseBook.IC.ICs.InputState;
import com.bukkit.gemo.FalseBook.IC.ICs.Lever;
import com.bukkit.gemo.utils.BlockUtils;
import com.bukkit.gemo.utils.FBItemType;
import com.bukkit.gemo.utils.SignUtils;
import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.util.Vector;

public class ICReplacer extends BaseIC {

    public HashMap<String, SchedulerClass> TaskList = new HashMap<String, SchedulerClass>();

    public ICReplacer() {
        this.core = FalseBookICCore.getInstance();
        this.ICName = "BLOCK-REPLACER";
        this.ICNumber = "ic.replacer";
        setICGroup(ICGroup.WORLDEDIT);
        this.chipState = new BaseChip(true, false, false, "Clock", "", "");
        this.chipState.setOutputs("Output = Input", "", "");
        this.chipState.setLines("BlockIDOn[:SubID][-BlockIDOff[:SubID]] (Examples: 'wool:15-stone' or 'grass' or 'dirt-44:2')", "ticksBetweenReplacement:SearchMode(1 OR 3), Examples: '0' (immediate replace) or '3:1' (wait 3 ticks between replacment and use mode 1=CIRCLE) or '5:3' (wait 5 ticks between replacment and use mode 3=ALONG A PATH)");
        this.ICDescription = "The MC1220 is a selfsearching blockreplacer. It starts to search and replace blocks (connected to the ICblock, even diagonal) whenever the input (the \"clock\") changes from low to high or vice versa.";
    }

    public void checkCreation(SignChangeEvent event) {
        if (event.getLine(3).length() > 0) {
            if (event.getLine(3).split(":").length == 1) {
                try {
                    if (Integer.valueOf(event.getLine(3)).intValue() >= 0) {
                    } else {
                        SignUtils.cancelSignCreation(event, "Ticks must be >= 0");
                        return;
                    }
                } catch (Exception e) {
                    SignUtils.cancelSignCreation(event, "Wrong syntax in Line 4.");
                    return;
                }
            } else {
                String[] split = event.getLine(3).split(":");
                try {
                    if (Integer.valueOf(split[0]).intValue() < 0) {
                        SignUtils.cancelSignCreation(event, "Ticks must be >= 0");
                        return;
                    }
                    if ((Integer.valueOf(split[1]).intValue() >= 1) || (Integer.valueOf(split[1]).intValue() <= 3)) {
                    } else {
                        SignUtils.cancelSignCreation(event, "Sorttype not found.");
                        return;
                    }
                } catch (Exception e) {
                    return;
                }
            }
        } else {
            event.setLine(3, "1:1");
        }

        ArrayList<FBItemType> itemList = SignUtils.parseLineToItemListWithSize(event.getLine(2), "-", false, 2, 2);
        if (itemList == null) {
            SignUtils.cancelSignCreation(event, "Line 3 is not valid. Usage: BlockIDOn[:SubID]-BlockIDOff[:SubID]");
            return;
        }

        for (FBItemType item : itemList) {
            if (!BlockUtils.isValidBlock(item.getItemID())) {
                SignUtils.cancelSignCreation(event, "'" + Material.getMaterial(item.getItemID()).name() + "' is not a block.");
                return;
            }
        }
    }

    public void Execute(Sign signBlock, InputState currentInputs, InputState previousInputs) {
        for (SchedulerClass sched : this.TaskList.values()) {
            if (sched.equalsLoc(signBlock.getBlock().getLocation())) {
                return;
            }

        }

        SchedulerClass newSched = new SchedulerClass(this, signBlock.getBlock().getLocation(), signBlock);
        if (signBlock.getLine(3).length() > 0) {
            if (signBlock.getLine(3).split(":").length == 1) {
                try {
                    if ((newSched.waitTicks = Integer.valueOf(signBlock.getLine(3)).intValue()) >= 0) {
                    } else {
                        return;
                    }
                } catch (Exception e) {
                    return;
                }
            } else {
                String[] split = signBlock.getLine(3).split(":");
                try {
                    if ((newSched.waitTicks = Integer.valueOf(split[0]).intValue()) < 0) {
                        return;
                    }
                    if (((newSched.sortType = Integer.valueOf(split[1]).intValue()) >= 1) || ((newSched.sortType = Integer.valueOf(split[1]).intValue()) <= 3)) {
                    } else {
                        return;
                    }
                } catch (Exception e) {
                    return;
                }
            }
        } else {
            newSched.waitTicks = 1;
            newSched.sortType = 1;
            signBlock.setLine(3, "1:1");
        }

        ArrayList<FBItemType> itemList = SignUtils.parseLineToItemListWithSize(signBlock.getLine(2), "-", false, 2, 2);
        if (itemList == null) {
            return;
        }

        for (FBItemType item : itemList) {
            if (!BlockUtils.isValidBlock(item.getItemID())) {
                return;
            }
        }

        if (newSched.waitTicks < 0) {
            newSched.waitTicks = 1;
        }
        if ((currentInputs.isInputOneHigh()) && (previousInputs.isInputOneLow())) {
            newSched.replace = (newSched.waitTicks > 0);
            newSched.TaskID = -1;
            newSched.AllBlocks = new ArrayList<Block>();
            newSched.QueuedBlocks0Ticks = new ArrayList<Block>();
            newSched.QueuedBlocks1Ticks = new ArrayList<Block>();
            newSched.oldBlockID = itemList.get(0).getItemID();
            newSched.oldBlockData = itemList.get(0).getItemDataAsByte();
            newSched.newBlockID = itemList.get(1).getItemID();
            newSched.newBlockData = itemList.get(1).getItemDataAsByte();
            newSched.QueuedBlocks0Ticks.add(getICBlock(signBlock).getBlock());

            if (newSched.sortType == 3) {
                newSched.TaskID = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this.core, newSched, 1L, newSched.waitTicks);
            } else if (newSched.sortType == 1) {
                newSched.TaskID = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this.core, newSched, 1L, 1L);
            }
            this.TaskList.put(signBlock.getBlock().getLocation().toString(), newSched);
            switchLever(Lever.BACK, signBlock, true);
        } else if ((currentInputs.isInputOneLow()) && (previousInputs.isInputOneHigh())) {
            newSched.replace = (newSched.waitTicks > 0);
            newSched.TaskID = -1;
            newSched.AllBlocks = new ArrayList<Block>();
            newSched.QueuedBlocks0Ticks = new ArrayList<Block>();
            newSched.QueuedBlocks1Ticks = new ArrayList<Block>();
            newSched.oldBlockID = itemList.get(1).getItemID();
            newSched.oldBlockData = itemList.get(1).getItemDataAsByte();
            newSched.newBlockID = itemList.get(0).getItemID();
            newSched.newBlockData = itemList.get(0).getItemDataAsByte();
            newSched.QueuedBlocks0Ticks.add(getICBlock(signBlock).getBlock());

            if (newSched.sortType == 3) {
                newSched.TaskID = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this.core, newSched, 1L, newSched.waitTicks);
            } else if (newSched.sortType == 1) {
                newSched.TaskID = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this.core, newSched, 1L, 1L);
            }
            switchLever(Lever.BACK, signBlock, false);
        }
    }

    public class SchedulerClass
            implements Runnable {

        public int TaskID = -1;
        public ICReplacer father;
        public Location signLoc;
        public Sign signBlock;
        public int nowBlock = 0;
        public int oldBlockID = 0;
        public byte oldBlockData = 0;
        public int newBlockID = 0;
        public byte newBlockData = 0;
        public int waitTicks = 1;
        public int sortType = 1;
        public int thisTick = 0;
        public boolean replace = false;
        public boolean searchEnd = false;
        ArrayList<ArrayList<Block>> sortedList = new ArrayList<ArrayList<Block>>();
        public ArrayList<Block> AllBlocks = new ArrayList<Block>();
        public ArrayList<Block> QueuedBlocks0Ticks = new ArrayList<Block>();
        public ArrayList<Block> QueuedBlocks1Ticks = new ArrayList<Block>();

        public SchedulerClass(ICReplacer father, Location signLoc, Sign signBlock) {
            this.father = father;
            this.signLoc = signLoc;
            this.signBlock = signBlock;
        }

        public int getDistPosition(ArrayList<Integer> distList, int distance) {
            for (int i = 0; i < distList.size(); i++) {
                if (distance == distList.get(i).intValue()) {
                    return i;
                }
            }
            return -1;
        }

        public void sortList1(Block signBlock, ArrayList<Block> list) {
            this.sortedList = new ArrayList<ArrayList<Block>>();
            ArrayList<Integer> distList = new ArrayList<Integer>();

            Vector startV = new Vector(signBlock.getX(), signBlock.getY(), signBlock.getZ());

            for (int i = 0; i < list.size(); i++) {
                Vector nowV = new Vector(list.get(i).getX(), list.get(i).getY(), list.get(i).getZ());
                int distance = (int) Math.abs(Math.round(startV.distance(nowV)));

                if (!distList.contains(Integer.valueOf(distance))) {
                    distList.add(Integer.valueOf(distance));
                    this.sortedList.add(new ArrayList<Block>());
                }
                this.sortedList.get(getDistPosition(distList, distance)).add(list.get(i));

                nowV = null;
                distance = 0;
            }
            distList.clear();
            distList = null;
        }

        private ArrayList<Block> getNeighbourBlocks(Block block, ArrayList<Block> list) {
            list.clear();
            World w = block.getWorld();
            int x = block.getX();
            int z = block.getZ();

            int y = block.getY();
            list.add(w.getBlockAt(x - 1, y, z));
            list.add(w.getBlockAt(x + 1, y, z));
            list.add(w.getBlockAt(x, y, z - 1));
            list.add(w.getBlockAt(x, y, z + 1));

            list.add(w.getBlockAt(x, y + 1, z));
            list.add(w.getBlockAt(x, y - 1, z));

            list.add(w.getBlockAt(x - 1, y, z - 1));
            list.add(w.getBlockAt(x + 1, y, z + 1));
            list.add(w.getBlockAt(x - 1, y, z + 1));
            list.add(w.getBlockAt(x + 1, y, z - 1));

            list.add(w.getBlockAt(x - 1, y + 1, z));
            list.add(w.getBlockAt(x + 1, y + 1, z));
            list.add(w.getBlockAt(x, y + 1, z - 1));
            list.add(w.getBlockAt(x, y + 1, z + 1));

            list.add(w.getBlockAt(x - 1, y - 1, z));
            list.add(w.getBlockAt(x + 1, y - 1, z));
            list.add(w.getBlockAt(x, y - 1, z - 1));
            list.add(w.getBlockAt(x, y - 1, z + 1));

            list.add(w.getBlockAt(x - 1, y + 1, z - 1));
            list.add(w.getBlockAt(x + 1, y + 1, z + 1));
            list.add(w.getBlockAt(x - 1, y + 1, z + 1));
            list.add(w.getBlockAt(x + 1, y + 1, z - 1));

            list.add(w.getBlockAt(x - 1, y - 1, z - 1));
            list.add(w.getBlockAt(x + 1, y - 1, z + 1));
            list.add(w.getBlockAt(x - 1, y - 1, z + 1));
            list.add(w.getBlockAt(x + 1, y - 1, z - 1));

            x = 0;
            y = 0;
            z = 0;
            w = null;

            return list;
        }

        public void run() {
            try {
                if (!this.replace) {
                    if (this.AllBlocks == null) {
                        this.AllBlocks = new ArrayList<Block>();
                    }
                    if (this.QueuedBlocks0Ticks == null) {
                        this.QueuedBlocks0Ticks = new ArrayList<Block>();
                    }
                    if (this.QueuedBlocks1Ticks == null) {
                        this.QueuedBlocks1Ticks = new ArrayList<Block>();
                    }

                    ArrayList<Block> neighbours = new ArrayList<Block>();

                    for (int i = 0; i < this.QueuedBlocks1Ticks.size(); i++) {
                        this.QueuedBlocks0Ticks.add(this.QueuedBlocks1Ticks.get(i));
                    }
                    this.QueuedBlocks1Ticks.clear();

                    for (int i = 0; i < this.QueuedBlocks0Ticks.size(); i++) {
                        if ((this.QueuedBlocks0Ticks.get(i).getTypeId() == this.oldBlockID) && (this.QueuedBlocks0Ticks.get(i).getData() == this.oldBlockData) && (this.nowBlock < ICReplacer.this.core.getMaxReplaceBlocks())) {
                            this.nowBlock += 1;
                        } else if (this.nowBlock >= ICReplacer.this.core.getMaxReplaceBlocks()) {
                            for (int j = 0; j < this.AllBlocks.size(); j++) {
                                this.AllBlocks.get(j).setTypeIdAndData(this.newBlockID, this.newBlockData, false);
                            }
                            this.father.TaskList.remove(this.signLoc.getBlock().getLocation().toString());
                            Bukkit.getServer().getScheduler().cancelTask(this.TaskID);
                            this.TaskID = -1;
                            this.AllBlocks.clear();
                            this.QueuedBlocks0Ticks.clear();
                            this.QueuedBlocks1Ticks.clear();
                            return;
                        }

                        neighbours = getNeighbourBlocks(this.QueuedBlocks0Ticks.get(i), neighbours);
                        for (int j = 0; j < neighbours.size(); j++) {
                            if ((neighbours.get(j).getTypeId() != this.oldBlockID) || (neighbours.get(j).getData() != this.oldBlockData)
                                    || (this.QueuedBlocks1Ticks.contains(neighbours.get(j))) || (this.AllBlocks.contains(neighbours.get(j)))) {
                                continue;
                            }
                            this.AllBlocks.add(neighbours.get(j));
                            this.QueuedBlocks1Ticks.add(neighbours.get(j));
                        }

                    }

                    this.QueuedBlocks0Ticks.clear();
                    if ((this.QueuedBlocks1Ticks.size() == 0) || (this.nowBlock >= ICReplacer.this.core.getMaxReplaceBlocks())) {
                        this.father.TaskList.remove(this.signLoc.getBlock().getLocation().toString());
                        Bukkit.getServer().getScheduler().cancelTask(this.TaskID);
                        this.TaskID = -1;
                        for (int i = 0; i < this.AllBlocks.size(); i++) {
                            this.AllBlocks.get(i).setTypeIdAndData(this.newBlockID, this.newBlockData, false);
                        }

                        neighbours.clear();
                        this.AllBlocks.clear();
                        this.QueuedBlocks0Ticks.clear();
                        this.QueuedBlocks1Ticks.clear();
                        neighbours = null;
                        this.AllBlocks = null;
                        this.QueuedBlocks0Ticks = null;
                        this.QueuedBlocks1Ticks = null;
                        return;
                    }
                } else if ((this.sortType == 3) && (this.replace)) {
                    if (this.AllBlocks == null) {
                        this.AllBlocks = new ArrayList<Block>();
                    }
                    if (this.QueuedBlocks0Ticks == null) {
                        this.QueuedBlocks0Ticks = new ArrayList<Block>();
                    }
                    if (this.QueuedBlocks1Ticks == null) {
                        this.QueuedBlocks1Ticks = new ArrayList<Block>();
                    }

                    ArrayList<Block> neighbours = new ArrayList<Block>();

                    for (int i = 0; i < this.QueuedBlocks1Ticks.size(); i++) {
                        this.QueuedBlocks0Ticks.add(this.QueuedBlocks1Ticks.get(i));
                    }
                    this.QueuedBlocks1Ticks.clear();

                    for (int i = 0; i < this.QueuedBlocks0Ticks.size(); i++) {
                        if ((this.QueuedBlocks0Ticks.get(i).getTypeId() == this.oldBlockID) && (this.QueuedBlocks0Ticks.get(i).getData() == this.oldBlockData) && (this.nowBlock < ICReplacer.this.core.getMaxReplaceBlocks())) {
                            this.nowBlock += 1;
                            this.QueuedBlocks0Ticks.get(i).setTypeIdAndData(this.newBlockID, this.newBlockData, false);
                        } else if (this.nowBlock >= ICReplacer.this.core.getMaxReplaceBlocks()) {
                            this.father.TaskList.remove(this.signLoc.getBlock().getLocation().toString());
                            Bukkit.getServer().getScheduler().cancelTask(this.TaskID);
                            this.AllBlocks.clear();
                            this.QueuedBlocks0Ticks.clear();
                            this.QueuedBlocks1Ticks.clear();
                            return;
                        }

                        neighbours = getNeighbourBlocks(this.QueuedBlocks0Ticks.get(i), neighbours);
                        for (int j = 0; j < neighbours.size(); j++) {
                            if ((neighbours.get(j).getTypeId() != this.oldBlockID) || (neighbours.get(j).getData() != this.oldBlockData)
                                    || (this.QueuedBlocks1Ticks.contains(neighbours.get(j))) || (this.AllBlocks.contains(neighbours.get(j)))) {
                                continue;
                            }
                            this.AllBlocks.add(neighbours.get(j));
                            this.QueuedBlocks1Ticks.add(neighbours.get(j));
                        }

                    }

                    this.QueuedBlocks0Ticks.clear();
                    if ((this.QueuedBlocks1Ticks.size() == 0) || (this.nowBlock >= ICReplacer.this.core.getMaxReplaceBlocks())) {
                        for (int i = 0; (i < this.QueuedBlocks1Ticks.size()) && (this.nowBlock < ICReplacer.this.core.getMaxReplaceBlocks()); i++) {
                            this.QueuedBlocks1Ticks.get(i).setTypeIdAndData(this.newBlockID, this.newBlockData, false);
                            this.nowBlock += 1;
                        }
                        this.father.TaskList.remove(this.signLoc.getBlock().getLocation().toString());
                        Bukkit.getServer().getScheduler().cancelTask(this.TaskID);
                        this.TaskID = -1;
                        neighbours.clear();
                        this.AllBlocks.clear();
                        this.QueuedBlocks0Ticks.clear();
                        this.QueuedBlocks1Ticks.clear();
                        neighbours = null;
                        this.AllBlocks = null;
                        this.QueuedBlocks0Ticks = null;
                        this.QueuedBlocks1Ticks = null;
                        return;
                    }

                } else if ((this.sortType == 1) && (this.replace)) {
                    if (!this.searchEnd) {
                        ArrayList<Block> neighbours = new ArrayList<Block>();

                        if (this.AllBlocks == null) {
                            this.AllBlocks = new ArrayList<Block>();
                        }
                        if (this.QueuedBlocks0Ticks == null) {
                            this.QueuedBlocks0Ticks = new ArrayList<Block>();
                        }
                        if (this.QueuedBlocks1Ticks == null) {
                            this.QueuedBlocks1Ticks = new ArrayList<Block>();
                        }

                        for (int i = 0; i < this.QueuedBlocks1Ticks.size(); i++) {
                            this.QueuedBlocks0Ticks.add(this.QueuedBlocks1Ticks.get(i));
                        }
                        this.QueuedBlocks1Ticks.clear();

                        for (int i = 0; i < this.QueuedBlocks0Ticks.size(); i++) {
                            if ((this.QueuedBlocks0Ticks.get(i).getTypeId() == this.oldBlockID) && (this.QueuedBlocks0Ticks.get(i).getData() == this.oldBlockData) && (this.AllBlocks.size() < ICReplacer.this.core.getMaxReplaceBlocks())) {
                                if (!this.AllBlocks.contains(this.QueuedBlocks0Ticks.get(i))) {
                                    this.AllBlocks.add(this.QueuedBlocks0Ticks.get(i));
                                }
                            } else if (this.AllBlocks.size() >= ICReplacer.this.core.getMaxReplaceBlocks()) {
                                sortList1(this.signBlock.getBlock(), this.AllBlocks);
                                this.thisTick = 0;
                                this.searchEnd = true;
                                neighbours.clear();
                                this.AllBlocks.clear();
                                this.QueuedBlocks0Ticks.clear();
                                this.QueuedBlocks1Ticks.clear();
                                neighbours = null;
                                this.AllBlocks = null;
                                this.QueuedBlocks0Ticks = null;
                                this.QueuedBlocks1Ticks = null;
                                this.father.TaskList.remove(this.signLoc.getBlock().getLocation().toString());
                                Bukkit.getServer().getScheduler().cancelTask(this.TaskID);
                                this.TaskID = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(ICReplacer.this.core, this, 1L, this.waitTicks);
                                return;
                            }

                            neighbours = getNeighbourBlocks(this.QueuedBlocks0Ticks.get(i), neighbours);
                            for (int j = 0; j < neighbours.size(); j++) {
                                if ((neighbours.get(j).getTypeId() != this.oldBlockID) || (neighbours.get(j).getData() != this.oldBlockData)
                                        || (this.QueuedBlocks1Ticks.contains(neighbours.get(j))) || (this.AllBlocks.contains(neighbours.get(j)))) {
                                    continue;
                                }
                                this.QueuedBlocks1Ticks.add(neighbours.get(j));
                            }

                        }

                        this.QueuedBlocks0Ticks.clear();
                        if ((this.QueuedBlocks1Ticks.size() == 0) || (this.nowBlock >= ICReplacer.this.core.getMaxReplaceBlocks())) {
                            sortList1(this.signBlock.getBlock(), this.AllBlocks);
                            this.thisTick = 0;
                            this.searchEnd = true;
                            neighbours.clear();
                            this.AllBlocks.clear();
                            this.QueuedBlocks0Ticks.clear();
                            this.QueuedBlocks1Ticks.clear();
                            neighbours = null;
                            this.AllBlocks = null;
                            this.QueuedBlocks0Ticks = null;
                            this.QueuedBlocks1Ticks = null;
                            this.father.TaskList.remove(this.signLoc.getBlock().getLocation().toString());
                            Bukkit.getServer().getScheduler().cancelTask(this.TaskID);
                            this.TaskID = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(ICReplacer.this.core, this, 1L, this.waitTicks);
                            return;
                        }
                    } else if (this.searchEnd) {
                        if (this.thisTick < this.sortedList.size()) {
                            for (int i = 0; i < this.sortedList.get(this.thisTick).size(); i++) {
                                this.sortedList.get(this.thisTick).get(i).setTypeIdAndData(this.newBlockID, this.newBlockData, false);
                            }
                            this.thisTick += 1;
                        } else {
                            this.father.TaskList.remove(this.signLoc.getBlock().getLocation().toString());
                            Bukkit.getServer().getScheduler().cancelTask(this.TaskID);
                            this.searchEnd = false;
                            this.thisTick = 0;
                            this.TaskID = -1;
                            this.sortedList.clear();
                            this.sortedList = null;
                            return;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                this.father.TaskList.remove(this.signLoc.getBlock().getLocation().toString());
                Bukkit.getServer().getScheduler().cancelTask(this.TaskID);
            }
        }

        public boolean equalsLoc(Location loc) {
            return this.signLoc.equals(loc);
        }
    }
}