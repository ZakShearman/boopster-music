package pink.zak.discord.music.model.trackdata;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;

public record TrackDataUser(@NotNull String username, @NotNull String discriminator,
                            String avatarUrl, long id) {

    public static @NotNull TrackDataUser fromMember(@NotNull Member member) {
        return fromUser(member.getUser());
    }

    public static @NotNull TrackDataUser fromUser(@NotNull User user) {
        return new TrackDataUser(user.getName(), user.getDiscriminator(), user.getAvatarUrl(), user.getIdLong());
    }
}
