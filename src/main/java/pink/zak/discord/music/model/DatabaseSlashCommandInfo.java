package pink.zak.discord.music.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pink.zak.discord.utils.discord.command.data.stored.SlashCommandInfo;

@Entity
@Table(name = "slash_commands")

@NoArgsConstructor
@AllArgsConstructor

@Getter
public class DatabaseSlashCommandInfo implements SlashCommandInfo {

    @Id
    @Column(name = "discord_id")
    private long id;

    @Column(nullable = false)
    private String name;
}
