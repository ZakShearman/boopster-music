package pink.zak.discord.music.utils.command.discord;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.interactions.command.CommandImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import pink.zak.discord.music.config.BoopsterConfig;
import pink.zak.discord.music.utils.command.discord.command.BotCommand;
import pink.zak.discord.music.utils.command.discord.command.BotSubCommand;
import pink.zak.discord.music.utils.command.discord.command.GenericBotCommand;
import pink.zak.discord.music.utils.listener.slashcommand.SlashCommandListener;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DiscordCommandBackend implements SlashCommandListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(DiscordCommandBackend.class);

    private final JDA jda;
    private final ExecutorService executor = ForkJoinPool.commonPool();
    private final BoopsterConfig boopsterConfig;

    private final Map<String, BotCommand> commands = new HashMap<>();
    private final Map<Long, BotCommand> slashCommands = new HashMap<>();

    public void registerCommand(BotCommand command) {
        this.commands.put(command.getName(), command);
    }

    public void init() {
        // load existing commands so we don't have to update every time
        Set<SlashCommandFileHandler.SlashCommandInfo> loadedCommands = SlashCommandFileHandler.loadSlashCommands();

        if (loadedCommands == null) {
            List<net.dv8tion.jda.api.interactions.commands.Command> createdCommands = this.createNewCommands();

            createdCommands.forEach(command -> {
                BotCommand matchedCommand = this.commands.get(command.getName());
                matchedCommand.setCommand(command);
                this.slashCommands.put(command.getIdLong(), matchedCommand);
                LOGGER.info("Bound created command {} to ID {}", matchedCommand.getName(), command.getIdLong());
            });

            SlashCommandFileHandler.saveSlashCommands(createdCommands);
        } else {
            LOGGER.info("Loaded Commands {}", loadedCommands);
            for (SlashCommandFileHandler.SlashCommandInfo commandInfo : loadedCommands) {
                BotCommand command = this.commands.get(commandInfo.name());
                this.slashCommands.put(commandInfo.id(), command);
                command.setCommand(new CommandImpl((JDAImpl) this.jda, null, commandInfo.dataObject()));
                LOGGER.debug("Bound loaded command {} to ID {}", commandInfo.name(), commandInfo.id());
            }
        }

        // permissions - should be completely redone. this is just a placeholder and doesnt even work rofl
        Map<String, Collection<? extends CommandPrivilege>> privileges = new HashMap<>();
        for (BotCommand command : this.commands.values()) {
            if (!command.isAdmin()) {
                for (BotSubCommand genericCommand : command.getSubCommands().values()) {
                    if (genericCommand.isAdmin()) {
                        privileges.put(command.getCommand().getId(), Set.of(CommandPrivilege.enableUser(240721111174610945L)));
                    }
                }
            } else {
                privileges.put(command.getCommand().getId(), Set.of(CommandPrivilege.enableUser(240721111174610945L)));
            }
        }
//        if (!privileges.isEmpty())
//            this.jda.getGuildById(931501134177251338L).updateCommandPrivileges(privileges).queue(success -> {
//                LOGGER.info("Set restricted permissions for " + privileges.size() + " commands");
//            });
    }

    private List<net.dv8tion.jda.api.interactions.commands.Command> createNewCommands() {
        Set<CommandData> createdData = this.commands.values()
            .stream()
            .map(BotCommand::getCommandData)
            .collect(Collectors.toSet());
        LOGGER.info("Created data {}", createdData);

        //Use this commented line for testing new slash commands on a guild

        List<net.dv8tion.jda.api.interactions.commands.Command> createdCommands;
        if (this.boopsterConfig.guildId() == null) {
            createdCommands = this.jda.updateCommands().addCommands(createdData).complete();
        } else {
            createdCommands = this.jda.getGuildById(this.boopsterConfig.guildId()).updateCommands().addCommands(createdData).complete();
        }
        LOGGER.info("Created Commands {}", createdCommands);

        return createdCommands;
    }

    @Override
    public void onSlashCommand(SlashCommandInteractionEvent event) {
        Member sender = event.getMember();
        CompletableFuture.runAsync(() -> {
            long commandId = event.getCommandIdLong();
            BotCommand command = this.slashCommands.get(commandId);
            if (command == null) {
                LOGGER.error("Command not found with ID {} and path {}", commandId, event.getCommandPath());
                return;
            }
            if (!this.memberHasAccess(command, event, sender)) {
                return;
            }
            String subCommandName = event.getSubcommandName();
            String subCommandGroup = event.getSubcommandGroup();
            if (subCommandName == null) {
                this.executeCommand(command, sender, event);
                return;
            }

            if (subCommandGroup != null)
                subCommandName = subCommandGroup + "/" + subCommandName;

            BotSubCommand subCommand = command.getSubCommands().get(subCommandName);
            if (subCommand == null) {
                LOGGER.error("SubCommand not found with ID {} and path {}", subCommandName, event.getCommandPath());
                return;
            }
            if (this.memberHasAccess(subCommand, event, sender)) {
                this.executeCommand(subCommand, sender, event);
            }
        }, this.executor).exceptionally(ex -> {
            LOGGER.error("Error from CommandBase input \"{}\"", event.getCommandString(), ex);
            return null;
        });
    }

    private void executeCommand(GenericBotCommand command, Member sender, SlashCommandInteractionEvent event) {
        command.middleMan(sender, event);
    }

    private boolean memberHasAccess(GenericBotCommand simpleCommand, SlashCommandInteractionEvent event, Member member) {
        if (simpleCommand.isAdmin() && member.getRoles().stream().anyMatch(role -> role.hasPermission(Permission.ADMINISTRATOR))) { // has admin if command requires
            event.reply("no permission dud :|").queue();
            return false;
        }
        return true;
    }

    public Map<String, BotCommand> getCommands() {
        return this.commands;
    }
}