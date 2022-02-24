package pink.zak.discord.music;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.map.repository.config.EnableMapRepositories;

@SpringBootApplication
@EnableMapRepositories(basePackages = "pink.zak.discord.music.repository.keyvalue")
@EnableConfigurationProperties
@ConfigurationPropertiesScan
public class BoopsterMusicApplication {

    public static void main(String[] args) {
        SpringApplication.run(BoopsterMusicApplication.class, args);
    }

}
