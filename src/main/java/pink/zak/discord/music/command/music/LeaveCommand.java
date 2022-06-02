package pink.zak.discord.music.command.music;

import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import pink.zak.discord.utils.discord.annotations.BotCommandComponent;
import pink.zak.discord.utils.discord.command.BotCommand;

@BotCommandComponent(name = "leave", admin = false)
public class LeaveCommand implements BotCommand {

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
    public CommandData createCommandData() {
        return Commands.slash("leave", "Leave a voice channel");
    }
}
