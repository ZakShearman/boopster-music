package pink.zak.discord.music.command;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import pink.zak.discord.utils.discord.annotations.BotCommandComponent;
import pink.zak.discord.utils.discord.command.BotCommand;

@BotCommandComponent(name = "ping", admin = false)
public class PingCommand implements BotCommand {

    @Override
    public void onExecute(Member sender, SlashCommandInteractionEvent event) {
        event.getJDA().getRestPing()
                .flatMap(result -> event.reply(":robot: *bap* " + result + "ms ping.").setEphemeral(true))
                .queue();
    }

    @Override
    public CommandData createCommandData() {
        return Commands.slash("ping", "Get the ping of the bot to Discord");
    }
}
