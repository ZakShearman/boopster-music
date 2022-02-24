package pink.zak.discord.music.command.music;

import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.springframework.stereotype.Component;
import pink.zak.discord.music.utils.command.discord.command.BotCommand;

@Component
public class LeaveCommand extends BotCommand {

    protected LeaveCommand() {
        super("leave", false);
    }

    @Override
    public void onExecute(Member sender, SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        GuildVoiceState voiceState = guild.getSelfMember().getVoiceState();

        if (voiceState == null || !voiceState.inAudioChannel()) {
            event.reply("The bot is not in an audio channel.").setEphemeral(true).queue();
        } else {
            AudioChannel voiceChannel = voiceState.getChannel();
            guild.getAudioManager().closeAudioConnection();
            event.reply("Left voice channel " + voiceChannel.getAsMention()).queue();
        }
    }

    @Override
    protected CommandData createCommandData() {
        return Commands.slash("leave", "Leave a voice channel");
    }
}
