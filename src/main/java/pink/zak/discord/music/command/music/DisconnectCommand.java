package pink.zak.discord.music.command.music;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;
import pink.zak.discord.music.model.LiveServer;
import pink.zak.discord.music.repository.keyvalue.LiveServerRepository;
import pink.zak.discord.utils.discord.annotations.BotCommandComponent;
import pink.zak.discord.utils.discord.command.BotCommand;

import java.util.Optional;

@RequiredArgsConstructor
@BotCommandComponent(name = "disconnect")
public class DisconnectCommand implements BotCommand {
    private final LiveServerRepository liveServerRepository;

    @Override
    public void onExecute(@NotNull Member sender, @NotNull SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        GuildVoiceState voiceState = guild.getSelfMember().getVoiceState();
        if (voiceState == null || !voiceState.inAudioChannel()) {
            event.reply("The bot is not connected to a voice channel.").setEphemeral(true).queue();
            return;
        }
        Optional<LiveServer> optionalLiveServer = this.liveServerRepository.findById(guild.getIdLong());

        if (optionalLiveServer.isEmpty()) {
            event.reply("The bot is not connected to a voice channel.").setEphemeral(true).queue();
            return;
        }

        LiveServer liveServer = optionalLiveServer.get();
        liveServer.getQueueController().getQueue().clear();
        liveServer.getQueueController().getRepeatsRemaining().set(0);
        liveServer.getAudioPlayer().destroy();

        guild.getAudioManager().closeAudioConnection();

        event.reply("Disconnected").queue();
    }

    @Override
    public @NotNull CommandData createCommandData() {
        return Commands.slash("disconnect", "Make the bot disconnect from the voice channel.")
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR))
                .setGuildOnly(true);
    }
}
