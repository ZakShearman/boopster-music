package pink.zak.discord.music.utils.command.discord.command;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public abstract class GenericBotCommand {
    private final boolean admin;

    protected GenericBotCommand(boolean admin) {
        this.admin = admin;
    }

    public abstract void onExecute(Member sender, SlashCommandInteractionEvent event);

    public void middleMan(Member sender, SlashCommandInteractionEvent event) {
        this.onExecute(sender, event);
    }

    public boolean isAdmin() {
        return this.admin;
    }
}