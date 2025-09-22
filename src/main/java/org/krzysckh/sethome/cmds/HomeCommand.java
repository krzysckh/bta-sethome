/* -*- mode: java; c-basic-offset: 2; indent-tabs-mode: nil -*- */

package org.krzysckh.sethome.cmds;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentTypeString;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import com.mojang.brigadier.builder.ArgumentBuilderRequired;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.nbt.tags.CompoundTag;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.entity.player.Player;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.WorldServer;
import net.minecraft.server.entity.player.PlayerServer;
import net.minecraft.core.entity.*;

import java.util.Optional;

import org.krzysckh.sethome.Home;
import org.krzysckh.sethome.SetHome;

public class HomeCommand implements CommandManager.CommandRegistry {
  public HomeCommand() {
  }

  public void register(CommandDispatcher<CommandSource> dispatcher) {
    CommandNode<CommandSource> command = dispatcher
        .register((ArgumentBuilderLiteral<CommandSource>) (Object) ArgumentBuilderLiteral.literal("home")
            .then(ArgumentBuilderRequired.argument("name", ArgumentTypeString.word())
                .executes((c) -> {
                  CommandSource source = (CommandSource) c.getSource();
                  Player player = source.getSender();
                  String homeName = c.getArgument("name", String.class);

                  Optional<Home> h = SetHome.getHome(player, homeName);
                  if (h.isEmpty()) {
                    player.sendTranslatedChatMessage(String.format("no such home: %s", homeName));
                  } else {
                    Home home = h.get();
                    double x = home.place.x;
                    double y = home.place.y;
                    double z = home.place.z;
                    boolean joinedp = home.dimension != player.dimension;

                    MinecraftServer ms = MinecraftServer.getInstance();
                    WorldServer ws = ms.getDimensionWorld(home.dimension);

                    if (ws != null) {
                      if (joinedp)
                        ms.playerList.sendPlayerToOtherDimension(PlayerServer.class.cast(player), home.dimension, null, false);
                      ws.entityJoinedWorld(player);
                    }
                    // player.setPos(x, y, z);
                    player.moveTo(x+0.5, y+0.5, z+0.5, player.yRot, player.xRot);
                  }

                  return Command.SINGLE_SUCCESS;
                })));
    dispatcher.register((ArgumentBuilderLiteral<CommandSource>) (Object) ArgumentBuilderLiteral.literal("sh")
        .redirect((CommandNode<Object>) (Object) command));
  }
}
