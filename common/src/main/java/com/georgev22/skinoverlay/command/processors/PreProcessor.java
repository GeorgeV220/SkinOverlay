package com.georgev22.skinoverlay.command.processors;

import com.georgev22.skinoverlay.command.BaseCommand;
import com.georgev22.skinoverlay.command.CommandContext;
import com.georgev22.skinoverlay.command.CommandIssuer;

import java.util.function.Supplier;

@FunctionalInterface
public interface PreProcessor {

    <T> T process(Supplier<BaseCommand> supplier, CommandIssuer commandIssuer, CommandContext context);

}
