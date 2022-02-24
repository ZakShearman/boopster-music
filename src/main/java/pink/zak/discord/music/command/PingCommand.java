package pink.zak.discord.music.command;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.springframework.stereotype.Component;
import pink.zak.discord.music.utils.command.discord.command.BotCommand;

@Component
public class PingCommand extends BotCommand {

    protected PingCommand() {
        super("ping", false);
    }

    @Override
    public void onExecute(Member sender, SlashCommandInteractionEvent event) {
        event.getJDA().getRestPing().queue(result -> event.reply(":robot: *bap* " + result + "ms ping.")
            .setEphemeral(true)
            .queue());
    }

    @Override
    protected CommandData createCommandData() {
        return Commands.slash("ping", "Get the ping of the bot to Discord");
    }
}
