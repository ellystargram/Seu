package com.amincorporate.seu.listener;

import com.amincorporate.seu.work.TradeMessageWork;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class TradeMessageListener extends ListenerAdapter {

    private final TradeMessageWork tradeMessageWork;
    private final Double easterEggUSD = 1371.10;
    private final JDA jda;

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        if (event.getAuthor().isBot()) return;

        String rawMessage = event.getMessage().getContentDisplay().strip(); // 완전한 한줄
        String[] messageWords = rawMessage.split(" ");

        if (event.isFromGuild()) {
            if (rawMessage.startsWith("스우")) { // 서버에서 왔고 시작이 스우면
                messageWords = Arrays.copyOfRange(messageWords, 1, messageWords.length);
            } else { // 서버에서 왔지만 스우를 부른게 아니면
                return;
            }
        }

        String command = messageWords[0];

        if (tradeMessageWork.isCoinMarketSearchCommand(command)){
            tradeMessageWork.coinMarketList(event);
        } else if (tradeMessageWork.isCoinTransactionCommand(command)){
            tradeMessageWork.transaction(event, messageWords, jda);
        } else if (tradeMessageWork.isCoinTransferCommand(command)) {

        }
    }

}
