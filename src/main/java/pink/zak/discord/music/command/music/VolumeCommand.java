package pink.zak.discord.music.command.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import pink.zak.discord.music.service.AudioService;
import pink.zak.discord.music.utils.command.discord.command.BotCommand;

@Component
public class VolumeCommand extends BotCommand {
    private final @NotNull AudioService audioService;

    protected VolumeCommand(@NotNull AudioService audioService) {
        super("volume", true);
        this.audioService = audioService;
    }

    @Override
    public void onExecute(Member sender, SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();

        OptionMapping volumeMapping = event.getOption("volume");
        AudioPlayer audioPlayer = this.audioService.getLiveServer(guild).getAudioPlayer();
        if (volumeMapping == null) {
            event.reply("The volume is currently set to " + audioPlayer.getVolume() + "%").queue();
        } else {
            int volume = (int) volumeMapping.getAsLong();
            audioPlayer.setVolume(volume);

            event.reply("Set bot volume to " + volume + ".").queue();
        }
    }

    @Override
    protected CommandData createCommandData() {
        return Commands.slash("volume", "Set the bot's volume")
            .addOptions(
                new OptionData(OptionType.INTEGER, "volume", "The volume level (1-200)", false)
                    .setMinValue(1)
                    .setMaxValue(200)
            );
    }
}
