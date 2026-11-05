package com.sxxxp.ALPGMC.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.sxxxp.ALPGMC.common.playerclass.ClassProgression;
import com.sxxxp.ALPGMC.common.playerclass.PlayerBranch;
import com.sxxxp.ALPGMC.common.playerclass.PlayerClass;
import com.sxxxp.ALPGMC.common.playerclass.PlayerClassData;
import com.sxxxp.ALPGMC.common.playerclass.PlayerGold;
import com.sxxxp.ALPGMC.common.playerclass.PlayerLevelData;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class ClassCommand {

    // 일반 플레이어는 직업을 바꿀 수 없으므로, 변경 명령어는 관리자용 /class set 하나만 둡니다.
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        registerGold(dispatcher);

        RequiredArgumentBuilder<CommandSourceStack, ?> target = Commands.argument("player", EntityArgument.player());

        for (PlayerClass playerClass : PlayerClass.values()) {
            target.then(Commands.literal(playerClass.getId())
                    .executes(context -> setClass(context.getSource(), EntityArgument.getPlayer(context, "player"), playerClass)));
        }

        RequiredArgumentBuilder<CommandSourceStack, ?> branchTarget = Commands.argument("player", EntityArgument.player());

        for (PlayerBranch branch : PlayerBranch.values()) {
            branchTarget.then(Commands.literal(branch.getId())
                    .executes(context -> setBranch(context.getSource(), EntityArgument.getPlayer(context, "player"), branch)));
        }

        dispatcher.register(Commands.literal("class")
                .then(Commands.literal("set")
                        .requires(source -> source.hasPermission(2))
                        .then(target))
                .then(Commands.literal("branch")
                        .requires(source -> source.hasPermission(2))
                        .then(branchTarget))
                .then(Commands.literal("addxp")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.argument("player", EntityArgument.player())
                                .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                        .executes(context -> addXp(context.getSource(),
                                                EntityArgument.getPlayer(context, "player"),
                                                IntegerArgumentType.getInteger(context, "amount")))))));
    }

    private static int setClass(CommandSourceStack source, ServerPlayer player, PlayerClass playerClass) throws CommandSyntaxException {
        PlayerClassData.set(player, playerClass);
        source.sendSuccess(() -> Component.translatable(
                "command.alpgmc.class.set",
                player.getDisplayName(),
                Component.translatable("class.alpgmc." + playerClass.getId())
        ), true);
        return 1;
    }

    // 골드는 아이템이 아니어서 건네줄 수 없으므로, 플레이어끼리 거래할 수 있도록 송금 명령어를 둡니다.
    private static void registerGold(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("gold")
                .then(Commands.literal("pay")
                        .then(Commands.argument("player", EntityArgument.player())
                                .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                        .executes(context -> payGold(context.getSource(),
                                                EntityArgument.getPlayer(context, "player"),
                                                IntegerArgumentType.getInteger(context, "amount"))))))
                .then(Commands.literal("add")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.argument("player", EntityArgument.player())
                                .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                        .executes(context -> addGold(context.getSource(),
                                                EntityArgument.getPlayer(context, "player"),
                                                IntegerArgumentType.getInteger(context, "amount")))))));
    }

    private static int payGold(CommandSourceStack source, ServerPlayer target, int amount) throws CommandSyntaxException {
        ServerPlayer sender = source.getPlayerOrException();
        if (sender == target) {
            source.sendFailure(Component.translatable("command.alpgmc.gold.pay_self"));
            return 0;
        }
        if (!PlayerGold.spend(sender, amount)) {
            source.sendFailure(Component.translatable("command.alpgmc.gold.pay_no_gold", PlayerGold.get(sender)));
            return 0;
        }

        PlayerGold.add(target, amount);
        source.sendSuccess(() -> Component.translatable("command.alpgmc.gold.pay_sent", amount, target.getDisplayName()), false);
        target.sendSystemMessage(Component.translatable("command.alpgmc.gold.pay_received", sender.getDisplayName(), amount));
        return 1;
    }

    private static int addGold(CommandSourceStack source, ServerPlayer player, int amount) {
        PlayerGold.add(player, amount);
        source.sendSuccess(() -> Component.translatable("command.alpgmc.gold.add", amount, player.getDisplayName()), true);
        return 1;
    }

    // 비용과 레벨은 따지지 않지만, 다른 직업의 갈래는 줄 수 없습니다.
    private static int setBranch(CommandSourceStack source, ServerPlayer player, PlayerBranch branch) {
        if (branch.getPlayerClass() != PlayerClassData.get(player)) {
            source.sendFailure(Component.translatable("command.alpgmc.class.branch_wrong_class"));
            return 0;
        }
        ClassProgression.set(player, branch);
        source.sendSuccess(() -> Component.translatable(
                "command.alpgmc.class.branch",
                player.getDisplayName(),
                Component.translatable("branch.alpgmc." + branch.getId())
        ), true);
        return 1;
    }

    private static int addXp(CommandSourceStack source, ServerPlayer player, int amount) {
        PlayerLevelData.addXp(player, amount);
        source.sendSuccess(() -> Component.translatable("command.alpgmc.class.addxp", amount, player.getDisplayName()), true);
        return 1;
    }
}
