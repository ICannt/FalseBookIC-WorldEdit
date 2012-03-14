package com.bukkit.gemo.FalseBook.IC.ICs.worldedit;

import com.bukkit.gemo.FalseBook.IC.ICs.BaseChip;
import com.bukkit.gemo.FalseBook.IC.ICs.BaseIC;
import com.bukkit.gemo.FalseBook.IC.ICs.ICGroup;
import com.bukkit.gemo.FalseBook.IC.ICs.InputState;
import com.bukkit.gemo.utils.ICUtils;
import com.bukkit.gemo.utils.SignUtils;
import net.minecraft.server.EntityArrow;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.util.Vector;

public class MC1240 extends BaseIC {

    public MC1240() {
        this.ICName = "ARROW SHOOTER";
        this.ICNumber = "[MC1240]";
        setICGroup(ICGroup.WORLDEDIT);
        this.chipState = new BaseChip(true, false, false, "Clock", "", "");
        this.chipState.setOutputs("", "", "");
        this.chipState.setLines("Either speed or both speed and spread. Speed is a value between 0.2 and 2 and the default value is 0.6. Spread is a value between 0 and 50 and the default value is 12. Leave blank to use default. <br /><br />Speed[:Spread]", "Vertical velocity between -1 and 1, with 0 being the default. Leave blank to use default of 0.");
        this.ICDescription = "The MC1240 shoots one arrow when the input (the \"clock\") goes from low to high.";
    }

    public void checkCreation(SignChangeEvent event) {
        String speedSpreadLine = event.getLine(2);
        String vertVelLine = event.getLine(3);
        try {
            if (speedSpreadLine.length() > 0) {
                String[] parts = speedSpreadLine.split(":");

                float speed = Float.parseFloat(parts[0]);
                if ((speed < 0.3D) || (speed > 2.0F)) {
                    SignUtils.cancelSignCreation(event, "Speed must be >= 0.3 and <= 2.");
                    return;
                }

                if (parts.length > 1) {
                    float spread = Float.parseFloat(parts[1]);
                    if ((spread < 0.0F) || (spread > 50.0F)) {
                        SignUtils.cancelSignCreation(event, "Spread must be >= 0 and <= 50.");
                        return;
                    }
                }
            }

            if (vertVelLine.length() > 0) {
                float speed = Float.parseFloat(vertVelLine);
                if ((speed < -1.0F) || (speed > 1.0F)) {
                    SignUtils.cancelSignCreation(event, "Vertical velocity must be between or equal to -1 and 1.");
                    return;
                }
            }
        } catch (NumberFormatException e) {
            SignUtils.cancelSignCreation(event, "Speed is the third line and spread is the fourth line.");
            return;
        }
    }

    public void Execute(Sign signBlock, InputState currentInputs, InputState previousInputs) {
        if ((currentInputs.isInputOneHigh()) && (previousInputs.isInputOneLow())) {
            int dir = SignUtils.getDirection(signBlock);
            float speed = 0.5F;
            float spread = 12.0F;
            float vertVel = 0.0F;

            Location location = ICUtils.getLeverPos(signBlock).clone();
            location.setX(location.getX() + 0.5D);
            location.setY(location.getY() + 0.5D);
            location.setZ(location.getZ() + 0.5D);

            String speedSpreadLine = signBlock.getLine(2);
            String vertVelLine = signBlock.getLine(3);
            try {
                if (speedSpreadLine.length() > 0) {
                    String[] parts = speedSpreadLine.split(":");
                    speed = Float.parseFloat(parts[0]);
                    if (parts.length > 1) {
                        spread = Float.parseFloat(parts[1]);
                    }
                }

                if (vertVelLine.length() > 0) {
                    vertVel = Float.parseFloat(vertVelLine);
                }
            } catch (NumberFormatException e) {
                return;
            }

            Vector velocity = new Vector(0.0F, 1.0F, 0.0F);
            if (dir == 1) {
                velocity = new Vector(0.0F, vertVel, -1.0F);
            } else if (dir == 2) {
                velocity = new Vector(-1.0F, vertVel, 0.0F);
            } else if (dir == 3) {
                velocity = new Vector(0.0F, vertVel, 1.0F);
            } else if (dir == 4) {
                velocity = new Vector(1.0F, vertVel, 0.0F);
            }

            CraftWorld w = (CraftWorld) signBlock.getWorld();
            EntityArrow arrow = new EntityArrow(w.getHandle());
            arrow.setPositionRotation(location.getX(), location.getY(), location.getZ(), 0.0F, 0.0F);
            arrow.shoot(velocity.getX(), velocity.getY(), velocity.getZ(), speed, spread);
            w.getHandle().addEntity(arrow);
        }
    }
}