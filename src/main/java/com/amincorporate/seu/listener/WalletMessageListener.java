package com.amincorporate.seu.listener;

import com.amincorporate.seu.configuration.JDAConfig;
import com.amincorporate.seu.pallet.NoticePallet;
import com.amincorporate.seu.service.MemberService;
import com.amincorporate.seu.service.MemberServiceImpl;
import com.amincorporate.seu.service.WalletServiceImpl;
import com.amincorporate.seu.work.MemberMessageWork;
import com.amincorporate.seu.work.WalletMessageWork;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class WalletMessageListener extends ListenerAdapter {

    private final WalletMessageWork walletMessageWork;
    private final JDA jda;

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return; // 리퀄시브 막기

        String rawMessage = event.getMessage().getContentDisplay().strip(); // 완전한 한줄
        String[] messageWords = rawMessage.split(" ");

        if (event.isFromGuild()) {
            if (rawMessage.startsWith("스우")) { // 서버에서 왔고 시작이 스우면
                messageWords = Arrays.copyOfRange(messageWords, 1, messageWords.length);
                if(!rawMessage.contains(" ")) return;
            } else { // 서버에서 왔지만 스우를 부른게 아니면
                return;
            }
        }

        //커맨드 구분
        String command = messageWords[0]; // 타이핑 편하게 함

        if (walletMessageWork.isCreateWalletCommand(command)){//지갑생성류 커맨드면
            walletMessageWork.createWallet(event, jda);
        }
        else if(walletMessageWork.isWalletInfoCommand(command)){//지갑정보류 커맨드면
            walletMessageWork.walletInfo(event);
        }

    }
}