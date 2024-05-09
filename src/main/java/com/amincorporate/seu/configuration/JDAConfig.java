package com.amincorporate.seu.configuration;

import com.amincorporate.seu.listener.MemberMessageListener;
import com.amincorporate.seu.listener.WalletMessageListener;
import com.amincorporate.seu.repository.MemberRepository;
import com.amincorporate.seu.repository.WalletRepository;
import com.amincorporate.seu.service.MemberService;
import com.amincorporate.seu.service.MemberServiceImpl;
import com.amincorporate.seu.service.WalletServiceImpl;
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

    private final MemberRepository memberRepository;
    private final WalletRepository walletRepository;

    @Value("${discord.bot.token}")
    private String token;

    @Bean
    public JDA jda() {
        return JDABuilder.createDefault(token)
                .setActivity(Activity.playing("Developing"))
                .enableIntents(GatewayIntent.MESSAGE_CONTENT, GatewayIntent.DIRECT_MESSAGES)
                .addEventListeners(new MemberMessageListener(new MemberServiceImpl(memberRepository)))
                .addEventListeners(new WalletMessageListener(new WalletServiceImpl(memberRepository, walletRepository)))
                .build();
    }

}
