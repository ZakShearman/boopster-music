package pink.zak.discord.music.utils.command.discord.command;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public abstract class BotSubCommand extends GenericBotCommand {
    private final String subCommandId;
    private final String subCommandGroupId;

    protected BotSubCommand(String subCommandId, boolean admin) {
        super(admin);
        this.subCommandId = subCommandId;
        this.subCommandGroupId = null;
    }

    protected BotSubCommand(String subCommandGroupId, String subCommandId, boolean admin) {
        super(admin);
        this.subCommandId = subCommandId;
        this.subCommandGroupId = subCommandGroupId;
    }

    public String getSubCommandId() {
        return this.subCommandId;
    }

    public String getSubCommandGroupId() {
        return this.subCommandGroupId;
    }

    @Override
    public abstract void onExecute(Member sender, SlashCommandInteractionEvent event);
}