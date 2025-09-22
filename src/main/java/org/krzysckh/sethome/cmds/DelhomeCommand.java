/* -*- mode: java; c-basic-offset: 2; indent-tabs-mode: nil -*- */

package org.krzysckh.sethome.cmds;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentTypeString;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import com.mojang.brigadier.builder.ArgumentBuilderRequired;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.util.phys.*;

import org.krzysckh.sethome.SetHome;

public class DelhomeCommand implements CommandManager.CommandRegistry {
  public DelhomeCommand() {
  }

  public void register(CommandDispatcher<CommandSource> dispatcher) {
    CommandNode<CommandSource> command = dispatcher
        .register((ArgumentBuilderLiteral<CommandSource>) (Object) ArgumentBuilderLiteral.literal("delhome")
            .then(ArgumentBuilderRequired.argument("name", ArgumentTypeString.word())
                .executes((c) -> {
                  CommandSource source = (CommandSource) c.getSource();
                  Player player = source.getSender();
                  String homeName = c.getArgument("name", String.class);

                  if (SetHome.getHome(player, homeName).isEmpty())
                    player.sendTranslatedChatMessage("no such home");
                  else {
                    SetHome.deleteHome(player, homeName);
                    player.sendTranslatedChatMessage(String.format("removed %s", homeName));
                  }

                  return Command.SINGLE_SUCCESS;
                })));
    dispatcher.register((ArgumentBuilderLiteral<CommandSource>) (Object) ArgumentBuilderLiteral.literal("sh")
        .redirect((CommandNode<Object>) (Object) command));
  }
}
