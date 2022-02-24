package pink.zak.discord.music.utils;

import net.dv8tion.jda.api.entities.Emoji;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class BotConstants {
    private static final Logger LOGGER = LoggerFactory.getLogger(BotConstants.class);

    public static final String VERSION = BotConstants.class.getPackage().getImplementationVersion();
    public static final String DEVICE_NAME = getInitDeviceName();

    public static final String BACK_ARROW = "\u2B05";
    public static final String FORWARD_ARROW = "\u27A1";

    public static final Emoji BACK_EMOJI = Emoji.fromUnicode("\u2B05");
    public static final Emoji FORWARD_EMOJI = Emoji.fromUnicode("\u27A1");

    private static String getInitDeviceName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException ex) {
            LOGGER.error("Could not get local hostname", ex);
            return "UNKNOWN";
        }
    }
}
