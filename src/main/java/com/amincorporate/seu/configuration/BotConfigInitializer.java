package com.amincorporate.seu.configuration;

import com.amincorporate.seu.entity.CoinEntity;
import com.amincorporate.seu.repository.CoinRepository;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class BotConfigInitializer extends ListenerAdapter {

    private final CoinRepository coinRepository;

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        super.onGuildJoin(event);
        Guild guild = event.getGuild();

        List<CoinEntity> coinEntities = coinRepository.findAll();

        File coinADA = new File("src/main/resources/static/coin/ADA.png");
        File coinBTC = new File("src/main/resources/static/coin/BTC.png");
        File coinDOGE = new File("src/main/resources/static/coin/DOGE.png");
        File coinETH = new File("src/main/resources/static/coin/ETH.png");
        File coinSOL = new File("src/main/resources/static/coin/SOL.png");

        Map<String, File> coinIconMap = new HashMap<>() {{
            put("ADA", coinADA);
            put("BTC", coinBTC);
            put("DOGE", coinDOGE);
            put("ETH", coinETH);
            put("SOL", coinSOL);
        }};

        File walletCopper = new File("src/main/resources/static/wallet/Copper.png");
        File walletBauxite = new File("src/main/resources/static/wallet/Bauxite.png");
        File walletUranium = new File("src/main/resources/static/wallet/Uranium.png");


        try {
            if (guild.getEmojisByName("copper", true).isEmpty())
                guild.createEmoji("copper", Icon.from(walletCopper)).queue();
            if (guild.getEmojisByName("bauxite", true).isEmpty())
                guild.createEmoji("bauxite", Icon.from(walletBauxite)).queue();
            if (guild.getEmojisByName("uranium", true).isEmpty())
                guild.createEmoji("uranium", Icon.from(walletUranium)).queue();

            for (CoinEntity coinEntity : coinEntities) {
                if (!guild.getEmojisByName(coinEntity.getId(), true).isEmpty()) continue;
                guild.createEmoji(coinEntity.getId(), Icon.from(coinIconMap.get(coinEntity.getId()))).queue();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
