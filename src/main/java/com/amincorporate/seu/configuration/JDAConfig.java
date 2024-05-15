package com.amincorporate.seu.configuration;

import com.amincorporate.seu.listener.MemberMessageListener;
import com.amincorporate.seu.listener.TradeMessageListener;
import com.amincorporate.seu.listener.WalletMessageListener;
import com.amincorporate.seu.work.MemberMessageWork;
import com.amincorporate.seu.work.WalletMessageWork;
import com.amincorporate.seu.work.TradeMessageWork;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class JDAConfig {

    private final MemberMessageWork memberMessageWork;
    private final WalletMessageWork walletMessageWork;
    private final TradeMessageWork tradeMessageWork;

    @Value("${discord.bot.token}")
    private String token;

    @Bean
    public JDA jda() {
        JDA jda = JDABuilder.createDefault(token)
                .setActivity(Activity.playing("Developing"))
                .enableIntents(GatewayIntent.MESSAGE_CONTENT, GatewayIntent.DIRECT_MESSAGES)
                .build();

        jda.addEventListener(new MemberMessageListener(memberMessageWork, walletMessageWork, tradeMessageWork));
        jda.addEventListener(new WalletMessageListener(walletMessageWork, jda));
        jda.addEventListener(new TradeMessageListener(tradeMessageWork, jda));

        return jda;
    }


}
