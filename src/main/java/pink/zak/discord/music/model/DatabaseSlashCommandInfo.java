package pink.zak.discord.music.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pink.zak.discord.utils.discord.command.data.stored.SlashCommandInfo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "slash_commands")

@NoArgsConstructor
@AllArgsConstructor

@Getter
public class DatabaseSlashCommandInfo implements SlashCommandInfo {

    @Id
    private long id;

    @Column(nullable = false)
    private String name;
}
