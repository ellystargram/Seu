package com.amincorporate.seu.configuration;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.Role;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class IconDataInitializer implements CommandLineRunner {

    private final JDA jda;

    @Override
    public void run(String... args) throws Exception {
        List<Guild> guilds = jda.getGuilds();
        System.out.println("Guilds: ");
        for (Guild guild : guilds) {
            System.out.println(guild.getName());
            addWalletTypeEmoji(guild);
        }
    }

    private void addWalletTypeEmoji(Guild event) {
        boolean isCopperIconNotExists = event.getEmojisByName("copper", true).isEmpty();
        boolean isBauxiteIconNotExists = event.getEmojisByName("bauxite", true).isEmpty();
        boolean isUraniumIconNotExists = event.getEmojisByName("uranium", true).isEmpty();

        if (isCopperIconNotExists) {
            File copperIcon = new File("src/main/resources/WalletIcon/Copper.png");
            Icon icon = null;
            try {
                icon = Icon.from(copperIcon);
            } catch (IOException e) {
                e.printStackTrace();
            }
            event.createEmoji("copper", icon, new Role[0]).queue((emote) -> System.out.println("Emoji created successfully!"),
                    (error) -> System.out.println("Error occurred while creating emoji: " + error.getMessage()));
        }
        if (isBauxiteIconNotExists) {
            File bauxiteIcon = new File("src/main/resources/WalletIcon/Bauxite.png");
            Icon icon = null;
            try {
                icon = Icon.from(bauxiteIcon);
            } catch (IOException e) {
                e.printStackTrace();
            }
            event.createEmoji("bauxite", icon, new Role[0]).queue((emote) -> System.out.println("Emoji created successfully!"),
                    (error) -> System.out.println("Error occurred while creating emoji: " + error.getMessage()));
        }
        if (isUraniumIconNotExists) {
            File uraniumIcon = new File("src/main/resources/WalletIcon/Uranium.png");
            Icon icon = null;
            try {
                icon = Icon.from(uraniumIcon);
            } catch (IOException e) {
                e.printStackTrace();
            }
            event.createEmoji("uranium", icon, new Role[0]).queue((emote) -> System.out.println("Emoji created successfully!"),
                    (error) -> System.out.println("Error occurred while creating emoji: " + error.getMessage()));
        }
    }
}
