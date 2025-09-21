/* -*- mode: java; c-basic-offset: 2; indent-tabs-mode: nil -*- */

package org.krzysckh.sethome;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.minecraft.core.net.command.CommandManager;

import net.minecraft.core.entity.player.Player;
import net.minecraft.core.util.phys.*;

import java.util.Optional;
import java.sql.*;
import java.util.ArrayList;

import org.krzysckh.sethome.cmds.*;
import org.krzysckh.sethome.Home;

public class SetHome implements ModInitializer {
  public static final String MOD_ID = "sethome";
  public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

  public static Connection DB_CON = null;

  private static String dburl = "jdbc:sqlite:sethome.db";

  private void initializeDB() {
    try {
      DB_CON = DriverManager.getConnection(dburl);

      PreparedStatement s = DB_CON
          .prepareStatement("CREATE TABLE IF NOT EXISTS homes (player text, name text, x double, y double, z double, removedp boolean);");
      s.executeUpdate();
    } catch (Exception e) {
      LOGGER.error(String.format("Couldn't initialize database: %s", e.getMessage()));
      e.printStackTrace();
    }
  }

  public static void saveHome(Player p, String name) {
    String pname = p.getDisplayName();
    Vec3 pos = p.getPosition(0, false);

    try {
      PreparedStatement s = DB_CON.prepareStatement("INSERT INTO homes (player, name, x, y, z, removedp) VALUES (?, ?, ?, ?, ?, 0)");
      s.setString(1, pname);
      s.setString(2, name);
      s.setDouble(3, pos.x);
      s.setDouble(4, pos.y);
      s.setDouble(5, pos.z);
      s.executeUpdate();
    } catch (Exception e) {
      p.sendTranslatedChatMessage(String.format("Oops!: %s", e.getMessage()));
      LOGGER.warn(e.getMessage());
    }
  }

  public static ArrayList<Home> getHomes(Player p) {
    ArrayList<Home> a = new ArrayList<Home>();

    String pn = p.getDisplayName();

    try {
      PreparedStatement s = DB_CON
          .prepareStatement("SELECT name, x, y, z FROM homes WHERE player = ? and removedp = 0");
      s.setString(1, pn);
      ResultSet rs = s.executeQuery();

      while (rs.next())
        a.add(new Home(pn, rs.getString("name"), rs.getDouble("x"), rs.getDouble("y"), rs.getDouble("z")));

    } catch (Exception e) {
      p.sendTranslatedChatMessage(String.format("Oops!: %s", e.getMessage()));
      LOGGER.warn(e.getMessage());
    }

    return a;
  }

  public static Optional<Home> getHome(Player p, String name) {
    String pn = p.getDisplayName();

    try {
      PreparedStatement s = DB_CON
          .prepareStatement("SELECT name, x, y, z FROM homes WHERE player = ? and removedp = 0 and name = ?");
      s.setString(1, pn);
      s.setString(2, name);
      ResultSet rs = s.executeQuery();

      while (rs.next())
        return Optional.of(new Home(pn, rs.getString("name"), rs.getDouble("x"), rs.getDouble("y"), rs.getDouble("z")));
    } catch (Exception e) {
      p.sendTranslatedChatMessage(String.format("Oops!: %s", e.getMessage()));
      LOGGER.warn(e.getMessage());
    }

    return Optional.empty();
  }

  public static void deleteHome(Player p, String name) {
    String pname = p.getDisplayName();
    Vec3 pos = p.getPosition(0, false);

    try {
      PreparedStatement s = DB_CON.prepareStatement("UPDATE homes SET removedp = 1 WHERE player = ? AND name = ?");
      s.setString(1, pname);
      s.setString(2, name);
      s.executeUpdate();
    } catch (Exception e) {
      p.sendTranslatedChatMessage(String.format("Oops!: %s", e.getMessage()));
      LOGGER.warn(e.getMessage());
    }
  }


  @Override
  public void onInitialize() {
    this.initializeDB();

    CommandManager.registerServerCommand(new SethomeCommand());
    CommandManager.registerServerCommand(new ListhomesCommand());
    CommandManager.registerServerCommand(new DelhomeCommand());
    CommandManager.registerServerCommand(new HomeCommand());
    LOGGER.info("sethome initialized.");
  }

  public void onRecipesReady() {

  }

  public void initNamespaces() {

  }

  public void beforeGameStart() {

  }

  public void afterGameStart() {

  }
}
