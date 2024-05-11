package com.amincorporate.seu.configuration;

import com.amincorporate.seu.listener.MemberMessageListener;
import com.amincorporate.seu.listener.WalletMessageListener;
import com.amincorporate.seu.repository.MemberRepository;
import com.amincorporate.seu.repository.WalletRepository;
import com.amincorporate.seu.service.MemberService;
import com.amincorporate.seu.service.MemberServiceImpl;
import com.amincorporate.seu.service.WalletServiceImpl;
import com.amincorporate.seu.work.MemberMessageWork;
import com.amincorporate.seu.work.WalletMessageWork;
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

    private final MemberMessageWork memberMessageWork;
    private final WalletMessageWork walletMessageWork;

    @Value("${discord.bot.token}")
    private String token;

    @Bean
    public JDA jda() {
        JDA jda = JDABuilder.createDefault(token)
                .setActivity(Activity.playing("Developing"))
                .enableIntents(GatewayIntent.MESSAGE_CONTENT, GatewayIntent.DIRECT_MESSAGES)
                .build();

        jda.addEventListener(new MemberMessageListener(new MemberServiceImpl(memberRepository), memberMessageWork, walletMessageWork));
        jda.addEventListener(new WalletMessageListener(walletMessageWork, jda));

        return jda;
    }


}
