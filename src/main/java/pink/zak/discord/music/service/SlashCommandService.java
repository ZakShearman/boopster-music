package pink.zak.discord.music.service;

import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;
import pink.zak.discord.music.model.DatabaseSlashCommandInfo;
import pink.zak.discord.music.repository.SlashCommandRepository;
import pink.zak.discord.utils.discord.SlashCommandDetailsService;
import pink.zak.discord.utils.discord.command.data.stored.SlashCommandInfo;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class SlashCommandService implements SlashCommandDetailsService {
    private final SlashCommandRepository slashCommandRepository;

    @Override
    public @Nullable Set<? extends SlashCommandInfo> loadCommands() {
        List<DatabaseSlashCommandInfo> foundCommands = this.slashCommandRepository.findAll();

        if (foundCommands.isEmpty()) return null;
        return new HashSet<>(foundCommands);
    }

    @Override
    public void saveCommands(@NotNull Set<SlashCommandInfo> slashCommandInfos) {
        this.slashCommandRepository.saveAll(
                slashCommandInfos.stream()
                        .map(info -> new DatabaseSlashCommandInfo(info.getId(), info.getName()))
                        .collect(Collectors.toUnmodifiableSet())
        );
    }
}
