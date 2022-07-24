package pink.zak.discord.music.command.music.history;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import pink.zak.discord.music.model.HistoricTrack;
import pink.zak.discord.music.repository.HistoricTrackRepository;
import pink.zak.discord.utils.listener.ButtonRegistry;
import pink.zak.discord.utils.message.PageableButtonEmbedMenu;

import java.awt.*;
import java.util.concurrent.ScheduledExecutorService;

public class HistoryMenu extends PageableButtonEmbedMenu {
    private final HistoricTrackRepository repository;
    private final User user;
    private final Guild guild;

    public HistoryMenu(ButtonRegistry buttonRegistry, ScheduledExecutorService scheduler, HistoricTrackRepository repository, SlashCommandInteractionEvent event, boolean isUser) {
        super(scheduler, buttonRegistry);

        this.repository = repository;
        this.user = isUser ? event.getUser() : null;
        this.guild = isUser ? null : event.getGuild();

        this.sendInitialMessage(event, false);
    }

    @Override
    public MessageEmbed createPage(int page) {
        Page<HistoricTrack> trackPage;

        PageRequest pageRequest = PageRequest.of(page - 1, 15, Sort.by(Sort.Direction.DESC, "playTime"));
        if (this.user != null)
            trackPage = this.repository
                    .findByMusicUserId(user.getIdLong(), pageRequest);
        else
            trackPage = this.repository
                    .findByServerId(this.guild.getIdLong(), pageRequest);

        this.maxPage = trackPage.getTotalPages();

        StringBuilder descriptionBuilder = new StringBuilder();
        for (HistoricTrack track : trackPage) {
            descriptionBuilder.append("<t:%s:R>".formatted(track.getPlayTime().getEpochSecond()))
                    .append(" | ")
                    .append(track.getTitle())
                    .append("\n");
        }
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setColor(Color.MAGENTA)
                .setDescription(descriptionBuilder.toString())
                .setFooter("Page %s/%s (%s items)".formatted(page, this.maxPage, trackPage.getTotalElements()));

        if (this.user != null)
            embedBuilder.setAuthor(user.getName() + "#" + user.getDiscriminator(), null, user.getAvatarUrl());
        else
            embedBuilder.setAuthor(guild.getName() + " History", null, guild.getIconUrl());

        return embedBuilder.build();
    }
}
