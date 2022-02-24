package pink.zak.discord.music.command.music;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import pink.zak.discord.music.model.HistoricTrack;
import pink.zak.discord.music.repository.HistoricTrackRepository;
import pink.zak.discord.music.utils.command.discord.command.BotCommand;
import pink.zak.discord.music.utils.time.Time;

import java.awt.*;
import java.time.Duration;
import java.time.Instant;

@Component
public class HistoryCommand extends BotCommand {
    private final @NotNull HistoricTrackRepository historicTrackRepository;

    protected HistoryCommand(@NotNull HistoricTrackRepository historicTrackRepository) {
        super("history", false);
        this.historicTrackRepository = historicTrackRepository;
    }

    @Override
    public void onExecute(Member sender, SlashCommandInteractionEvent event) {
        OptionMapping option = event.getOption("target");
        boolean isUser = option == null || option.getAsString().equals("user");
        Guild guild = event.getGuild();

        Pageable pageable = PageRequest.of(0, 15, Sort.by(Sort.Direction.DESC, "playTime"));
        Page<HistoricTrack> trackPage;
        if (isUser)
            trackPage = this.historicTrackRepository.findByMusicUserId(sender.getIdLong(), pageable);
        else
            trackPage = this.historicTrackRepository.findByServerId(guild.getIdLong(), pageable);

        StringBuilder descriptionBuilder = new StringBuilder();
        for (HistoricTrack track : trackPage) {
            descriptionBuilder.append(Time.formatShort(Duration.between(track.getPlayTime(), Instant.now())))
                .append(" | ")
                .append(track.getTitle())
                .append("\n");
        }
        EmbedBuilder embedBuilder = new EmbedBuilder()
            .setColor(Color.MAGENTA)
            .setDescription(descriptionBuilder.toString());

        User user = sender.getUser();
        if (isUser) {
            event.replyEmbeds(
                embedBuilder
                    .setAuthor(user.getName() + "#" + user.getDiscriminator(), null, user.getAvatarUrl())
                    .build()
            ).queue();
        } else {
            event.replyEmbeds(
                embedBuilder
                    .setAuthor(guild.getName() + " History", null, guild.getIconUrl())
                    .build()
            ).queue();
        }
    }

    @Override
    protected CommandData createCommandData() {
        return Commands.slash("history", "View track play history (defaults to user history)")
            .addOptions(
                new OptionData(OptionType.STRING, "target", "Get the history for a user or a server")
                    .addChoice("user", "user")
                    .addChoice("server", "server")
                );
    }
}
