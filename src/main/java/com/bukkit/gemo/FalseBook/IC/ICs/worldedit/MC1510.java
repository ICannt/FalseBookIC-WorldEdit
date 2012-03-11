package com.bukkit.gemo.FalseBook.IC.ICs.worldedit;

import com.bukkit.gemo.FalseBook.IC.ICs.BaseChip;
import com.bukkit.gemo.FalseBook.IC.ICs.BaseIC;
import com.bukkit.gemo.FalseBook.IC.ICs.ICGroup;
import com.bukkit.gemo.FalseBook.IC.ICs.InputState;
import com.bukkit.gemo.FalseBook.IC.ICs.Lever;
import com.bukkit.gemo.utils.ChatUtils;
import com.bukkit.gemo.utils.SignUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;

public class MC1510 extends BaseIC
{
  public MC1510()
  {
    this.ICName = "MESSAGESENDER";
    this.ICNumber = "[MC1510]";
    setICGroup(ICGroup.WORLDEDIT);
    this.chipState = new BaseChip(true, false, false, "Clock", "", "");
    this.chipState.setOutputs("Output = Input", "", "");
    this.chipState.setLines("Player name OR leave blank to message all players", "The message");
    this.ICDescription = "The MC1510 displays a configurable message when the input (the \"clock\") goes from low to high.";
  }

  public void checkCreation(SignChangeEvent event)
  {
    if (event.getLine(3).length() < 1) {
      SignUtils.cancelSignCreation(event, "Enter a message in line 4!");
      return;
    }
  }

  public void Execute(Sign signBlock, InputState currentInputs, InputState previousInputs)
  {
    if ((currentInputs.isInputOneHigh()) && (previousInputs.isInputOneLow())) {
      String toName = signBlock.getLine(2);
      String message = signBlock.getLine(3);
      if (message.length() < 1) {
        return;
      }
      if (toName.length() < 1)
      {
        Bukkit.getServer().broadcastMessage(ChatColor.LIGHT_PURPLE + "[MC1510 -> all] " + ChatColor.WHITE + message);
      }
      else {
        Player[] pList = Bukkit.getServer().getOnlinePlayers();
        for (Player player : pList) {
          if (player.getName().toLowerCase().contains(toName.toLowerCase())) {
            ChatUtils.printLine(player, ChatColor.LIGHT_PURPLE, "[MC1510 -> " + player.getName() + "] " + ChatColor.WHITE + message);
          }
        }
      }
      switchLever(Lever.BACK, signBlock, true);
    } else {
      switchLever(Lever.BACK, signBlock, false);
    }
  }
}