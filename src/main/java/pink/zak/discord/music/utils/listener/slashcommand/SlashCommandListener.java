package pink.zak.discord.music.utils.listener.slashcommand;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public interface SlashCommandListener {

    void onSlashCommand(SlashCommandInteractionEvent event);
}
