package pink.zak.discord.music.utils.types;

import lombok.experimental.UtilityClass;

import java.time.Duration;

@UtilityClass
public class DurationUtils {
    private static final long HOUR_LENGTH = Duration.ofHours(1).getSeconds();

    public static String formatSeconds(long durationSeconds) {
        return format(Duration.ofSeconds(durationSeconds));
    }

    public static String format(Duration duration) {
        String result = longAsString(duration.toMinutesPart()) + ":" + longAsString(duration.toSecondsPart());

        if (duration.getSeconds() >= HOUR_LENGTH)
            result = longAsString(duration.toHours()) + ":" + result;

        return result;
    }

    private static String longAsString(long value) {
        return value < 10 ? "0" + value : String.valueOf(value);
    }
}
