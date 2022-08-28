package pink.zak.discord.music.command.music.history;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import pink.zak.discord.music.repository.HistoricTrackRepository;
import pink.zak.discord.utils.discord.annotations.BotCommandComponent;
import pink.zak.discord.utils.discord.command.BotCommand;
import pink.zak.discord.utils.listener.ButtonRegistry;

import java.util.concurrent.ScheduledExecutorService;

@RequiredArgsConstructor
@BotCommandComponent(name = "history", admin = false)
public class HistoryCommand implements BotCommand {
    private final @NotNull HistoricTrackRepository historicTrackRepository;
    private final ButtonRegistry buttonRegistry;
    private final ScheduledExecutorService scheduler;

    @Override
    public void onExecute(Member sender, SlashCommandInteractionEvent event) {
        OptionMapping option = event.getOption("target");
        boolean isUser = option == null || option.getAsString().equals("user");

        new HistoryMenu(this.buttonRegistry, this.scheduler, this.historicTrackRepository, event, isUser);
    }

    @Override
    public CommandData createCommandData() {
        return Commands.slash("history", "View track play history (defaults to user history)")
                .addOptions(
                        new OptionData(OptionType.STRING, "target", "Get the history for a user or a server")
                                .addChoice("user", "user")
                                .addChoice("server", "server")
                )
                .setGuildOnly(true);
    }
}
