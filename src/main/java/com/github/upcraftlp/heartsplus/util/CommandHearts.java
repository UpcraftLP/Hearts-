package com.github.upcraftlp.heartsplus.util;

import com.github.upcraftlp.heartsplus.handler.HeartsHandler;
import com.google.common.collect.Lists;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author UpcraftLP
 */
public class CommandHearts extends CommandBase {

    @Override
    public String getName() {
        return "heartsplus";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "commands.heartsplus.usage";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if(args.length != 0 && args.length != 3) throw new WrongUsageException(getUsage(sender));
        EntityPlayer player = getCommandSenderAsPlayer(sender);
        IExtraHearts hearts = player.getCapability(HeartProvider.HEARTS_CAPABILITY, null);
        if(args.length == 0) {
            sender.sendMessage(new TextComponentString("Red: " + hearts.getRedHearts()).setStyle(new Style().setColor(TextFormatting.RED)));
            sender.sendMessage(new TextComponentString("Black: " + hearts.getBlackHearts()).setStyle(new Style().setColor(TextFormatting.BLUE)));
            sender.sendMessage(new TextComponentString("White: " + (hearts.hasWhiteHeart() ? "0.5" : "0")).setStyle(new Style().setColor(TextFormatting.WHITE)));
        }
        else {
            float amount = (float) parseDouble(args[2]);
            EnumHeartType type = EnumHeartType.valueOf(args[1].toUpperCase(Locale.ROOT));
            if("add".equalsIgnoreCase(args[0])) HeartsHandler.addHearts(player, player.getEntityWorld(), type, amount, false);
            else if("set".equalsIgnoreCase(args[0])) hearts.setHearts(type == EnumHeartType.RED ? amount : hearts.getRedHearts(), type == EnumHeartType.BLACK ? amount : hearts.getBlackHearts(), type == EnumHeartType.WHITE ? (amount > 0) : hearts.hasWhiteHeart());
            else throw new WrongUsageException(getUsage(sender));
        }
        HeartProvider.sync(player);
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        switch (args.length) {
            case 1:
                return getListOfStringsMatchingLastWord(args, Lists.newArrayList("set", "add"));
            case 2:
                String[] values = new String[EnumHeartType.values().length];
                for (int i = 0; i< EnumHeartType.values().length; i++) {
                    values[i] = EnumHeartType.values()[i].name().toLowerCase(Locale.ROOT);
                }
                return getListOfStringsMatchingLastWord(args, values);
            default:
                return Collections.emptyList();
        }
    }
}
