package com.bukkit.gemo.FalseBook.IC.ICs.worldedit;

import com.bukkit.gemo.FalseBook.IC.ICs.BaseChip;
import com.bukkit.gemo.FalseBook.IC.ICs.BaseIC;
import com.bukkit.gemo.FalseBook.IC.ICs.ICGroup;
import com.bukkit.gemo.FalseBook.IC.ICs.InputState;
import com.bukkit.gemo.utils.ICUtils;
import com.bukkit.gemo.utils.SignUtils;
import net.minecraft.server.EntityAnimal;
import net.minecraft.server.EntityArrow;
import net.minecraft.server.EntityPig;
import net.minecraft.server.World;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.util.Vector;

public class ICEntityCannon extends BaseIC {
    public ICEntityCannon() {
        this.ICName = "ARROW SHOOTER";
        this.ICNumber = "ic.entitycannon";
        setICGroup(ICGroup.WORLDEDIT);
        this.chipState = new BaseChip(true, false, false, "Clock", "", "");
        this.chipState.setOutputs("", "", "");
        this.chipState.setLines("", "");
        this.ICDescription = "";
    }

    public void checkCreation(SignChangeEvent event) {
        String type = event.getLine(1);
        type = type.toUpperCase();
        String speed = event.getLine(2);
        try {
            Float speedValue = Float.parseFloat(speed);
            if(speedValue < 1.0f) {
                speedValue = 1.0f;
            }
            if(speedValue > 32.0f) {
                speedValue = 32.0f;
            }
            event.setLine(1, speedValue.toString());
        } catch (NumberFormatException e) {
            SignUtils.cancelSignCreation(event, "Type is 2nd line. Speed is 3rd line.");
            return;
        }
        
        try {
            EntityType et = EntityType.fromName(type);
            if(et == null) {
                String types = "";
                for(EntityType t : EntityType.values())
                {
                    types = types + t.toString() + " ";
                }

                SignUtils.cancelSignCreation(event, "Valid types are: " + types);
                return;
            }
            event.setLine(1, et.toString());
        } catch (NumberFormatException e) {
            String types = "";
            for(EntityType t : EntityType.values())
            {
                types = types + t.toString() + " ";
            }
                
            SignUtils.cancelSignCreation(event, "Valid types are: " + types);
            return;
        }
    }

    public void Execute(Sign signBlock, InputState currentInputs, InputState previousInputs) {
        if ((currentInputs.isInputOneHigh()) && (previousInputs.isInputOneLow())) {
            int dir = SignUtils.getDirection(signBlock);
            
            Location location = ICUtils.getLeverPos(signBlock).clone();
            location.setX(location.getX() + 0.5d);
            location.setY(location.getY() + 0.5d);
            location.setZ(location.getZ() + 0.5d);
            
            String speed = signBlock.getLine(2);
            Float speedValue = Float.parseFloat(speed);
            
            Vector velocity = new Vector(0.0f, speedValue, 0.0f);
            if (dir == 1) {
                velocity = new Vector(0.0f, 0.0f, -speedValue);
            } else if (dir == 2) {
                velocity = new Vector(-speedValue, 0.0f, 0.0f);
            } else if (dir == 3) {
                velocity = new Vector(0.0f, 0.0f, speedValue);
            } else if (dir == 4) {
                velocity = new Vector(speedValue, 0.0f, 0.0f);
            }

            CraftWorld world = (CraftWorld)location.getWorld();
            
            String type = signBlock.getLine(1);
            
            LivingEntity ent = world.spawnCreature(location, EntityType.fromName(type));
            ent.setVelocity(velocity);
        }
    }
}
