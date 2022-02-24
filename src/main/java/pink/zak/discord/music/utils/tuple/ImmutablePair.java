package pink.zak.discord.music.utils.tuple;

public record ImmutablePair<K, V>(K key, V value) {

    public static <S, U> ImmutablePair<S, U> of(S key, U value) {
        return new ImmutablePair<>(key, value);
    }
}
