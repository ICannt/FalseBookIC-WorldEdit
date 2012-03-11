package com.bukkit.gemo.FalseBook.IC.ICs.worldedit;

import com.bukkit.gemo.utils.BlockUtils;
import java.util.HashMap;
import java.util.Map.Entry;
import org.bukkit.Location;

public class TeleporterList
{
  private HashMap<String, String> ICList = new HashMap<String, String>();

  public int getSize() {
    return this.ICList.size();
  }

  public HashMap<String, String> getAll() {
    return this.ICList;
  }

  public boolean TeleporterExistsByName(String name) {
    return this.ICList.containsKey(name);
  }

  public boolean removeTeleporterByName(String name) {
    if (name != null)
      return this.ICList.remove(name) != null;
    return false;
  }

  public boolean TeleporterExistsByLocation(String location) {
    for (Entry<String, String> entry : this.ICList.entrySet()) {
      if (((String)entry.getValue()).equalsIgnoreCase(location)) {
        return true;
      }
    }
    return false;
  }

  public boolean removeTeleporterByLocation(String location) {
    String name = null;
    for (Entry<String, String> entry : this.ICList.entrySet()) {
      if (((String)entry.getValue()).equalsIgnoreCase(location)) {
        name = (String)entry.getKey();
        break;
      }
    }
    return removeTeleporterByName(name);
  }

  public Location getLocation(String name) {
    return BlockUtils.LocationFromString(this.ICList.get(name));
  }

  public String getLocationString(String name) {
    return this.ICList.get(name);
  }

  public void addTeleporter(String name, Location location) {
    this.ICList.put(name, BlockUtils.LocationToString(location));
  }
}