package pink.zak.discord.music.command;

import lombok.SneakyThrows;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.springframework.stereotype.Component;
import pink.zak.discord.music.utils.command.discord.command.BotCommand;

@Component
public class EvalCommand extends BotCommand {
    private static final Context SCRIPT_ENGINE = Context.newBuilder().allowExperimentalOptions(true).allowAllAccess(true).option("js.nashorn-compat", "true").build();
    private final Value BINDINGS = SCRIPT_ENGINE.getBindings("js");

//    static {
//        SCRIPT_ENGINE.eval("js", """
//            var imports = new JavaImporter(
//            java.io,
//            java.lang,
//            java.util,
//            Packages.net.dv8tion.jda.api,
//            Packages.net.dv8tion.jda.api.entities,
//            Packages.net.dv8tion.jda.api.entities.impl,
//            Packages.net.dv8tion.jda.api.managers,
//            Packages.net.dv8tion.jda.api.managers.impl,
//            Packages.net.dv8tion.jda.api.utils
//            );
//            """);
//    }

    protected EvalCommand(JDA jda) {
        super("eval", true);
        BINDINGS.putMember("jda", jda);
    }

    @SneakyThrows
    @Override
    public void onExecute(Member sender, SlashCommandInteractionEvent event) {
        String code = event.getOption("code").getAsString();
        try {
            event.reply(SCRIPT_ENGINE.eval("js",code).toString()).queue();
        } catch (Exception ex) {
            if (!event.isAcknowledged())
                event.reply(":x: Exception: " + ex.getMessage()).queue();
        }
    }

    @Override
    protected CommandData createCommandData() {
        return Commands.slash("eval", "Admin debugging tool")
            .addOption(OptionType.STRING, "code", "code to run", true);
    }
}
