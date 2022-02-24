package pink.zak.discord.music.utils.command.discord.command;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class BotCommand extends GenericBotCommand {
    private final String name;
    private final CommandData commandData;
    private Map<String, BotSubCommand> subCommands = new HashMap<>();

    private net.dv8tion.jda.api.interactions.commands.Command command;

    protected BotCommand(String name, boolean admin) {
        super(admin);
        this.name = name;
        this.commandData = this.createCommandData();
    }

    public String getName() {
        return this.name;
    }

    public Map<String, BotSubCommand> getSubCommands() {
        return this.subCommands;
    }

    public void setSubCommands(Set<BotSubCommand> subCommands) {
        Map<String, BotSubCommand> subCommandMap = new HashMap<>();
        for (BotSubCommand subCommand : subCommands)
            if (subCommand.getSubCommandGroupId() != null)
                subCommandMap.put(subCommand.getSubCommandGroupId() + "/" + subCommand.getSubCommandId(), subCommand);
            else
                subCommandMap.put(subCommand.getSubCommandId(), subCommand);
        this.subCommands = subCommandMap;
    }

    @Override
    public abstract void onExecute(Member sender, SlashCommandInteractionEvent event);

    public void setSubCommands(BotSubCommand... subCommands) {
        this.setSubCommands(Set.of(subCommands));
    }

    public CommandData getCommandData() {
        return this.commandData;
    }

    public net.dv8tion.jda.api.interactions.commands.Command getCommand() {
        return this.command;
    }

    public void setCommand(net.dv8tion.jda.api.interactions.commands.Command command) {
        this.command = command;
    }

    protected abstract CommandData createCommandData();
}