CREATE TABLE `user`
(
    `discord_id`         BIGINT UNSIGNED NOT NULL,
    `creation_timestamp` DATETIME(0)     NOT NULL,
    PRIMARY KEY (`discord_id`)
);

CREATE TABLE `track`
(
    `youtube_id` CHAR(11)     NOT NULL,
    `title`      VARCHAR(100) NOT NULL,
    PRIMARY KEY (`youtube_id`)
);

CREATE TABLE `user_favourite_tracks`
(
    `user_discord_id`  BIGINT UNSIGNED NOT NULL,
    `track_youtube_id` CHAR(11)        NOT NULL,
    FOREIGN KEY (`user_discord_id`) REFERENCES user (`discord_id`),
    FOREIGN KEY (`track_youtube_id`) REFERENCES track (`youtube_id`)
);

CREATE TABLE `server`
(
    `discord_id` BIGINT UNSIGNED NOT NULL,
    `volume`     INT(3) UNSIGNED NOT NULL,
    PRIMARY KEY (`discord_id`)
);

CREATE TABLE `historic_track`
(
    `id`                VARCHAR(255)                    NOT NULL,
    `author`            VARCHAR(255) CHARACTER SET utf8 NULL,
    `source_id`         VARCHAR(100)                    NOT NULL,
    `length`            BIGINT                          NULL, # Can be null if it's a stream. TODO investigate datatype and precision
    `play_time`         DATETIME(0)                     NOT NULL,
    `stream`            BOOLEAN                         NOT NULL,
    `title`             VARCHAR(255) CHARACTER SET utf8 NOT NULL,
    `url`               VARCHAR(255)                    NOT NULL,
    `user_discord_id`   BIGINT UNSIGNED                 NOT NULL,
    `server_discord_id` BIGINT UNSIGNED                 NOT NULL,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`user_discord_id`) REFERENCES user (`discord_id`),
    FOREIGN KEY (`server_discord_id`) REFERENCES server (`discord_id`)
);

CREATE TABLE `slash_commands`
(
    `discord_id` BIGINT UNSIGNED NOT NULL,
    `name`       VARCHAR(32)     NOT NULL,
    PRIMARY KEY (`discord_id`)
);