package com.amincorporate.seu.listener;

import com.amincorporate.seu.dto.CreateWalletDTO;
import com.amincorporate.seu.entity.wallet.WalletType;
import com.amincorporate.seu.pallet.NoticePallet;
import com.amincorporate.seu.service.MemberService;
import com.amincorporate.seu.service.MemberServiceImpl;
import com.amincorporate.seu.service.WalletServiceImpl;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.emoji.RichCustomEmoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class WalletMessageListener extends ListenerAdapter {

    private final WalletServiceImpl walletService;

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;//리퀄시브 막기

        String rawMessage = event.getMessage().getContentDisplay();//완전한 한줄
        String[] messageWords = rawMessage.split(" ");
        boolean isInServer = event.isFromGuild();//서버에서 온 메세지인가

        if (isInServer) {
            if (rawMessage.startsWith("스우")) {//서버에서 왔고 시작이 스우면
                messageWords = Arrays.copyOfRange(messageWords, 1, messageWords.length);
            }
            else {//서버에서 왔지만 스우를 부른게 아니면
                return;
            }
        }

        //커맨드 구분
        String command = messageWords[0];//타이핑 편하게 함
        switch(command){
            case "지갑생성" -> {
                RichCustomEmoji copperIconRaw = event.getGuild().getEmojisByName("copper", true).getFirst();
                RichCustomEmoji bauxiteIconRaw = event.getGuild().getEmojisByName("bauxite", true).getFirst();
                RichCustomEmoji uraniumIconRaw = event.getGuild().getEmojisByName("uranium", true).getFirst();
                String copperIcon = copperIconRaw.getFormatted();
                String bauxiteIcon = bauxiteIconRaw.getFormatted();
                String uraniumIcon = uraniumIconRaw.getFormatted();
                event.getChannel().sendMessageEmbeds(new EmbedBuilder()
                        .setTitle("**debug test**")
                        .setColor(NoticePallet.warningYellow)
                        .setDescription("copperIcon: "+copperIcon+"\n" +
                                "bauxiteIcon: "+bauxiteIcon+"\n" +
                                "uraniumIcon: "+uraniumIcon)
                        .build()).queue(message -> {
                    message.addReaction(copperIconRaw).queue();
                    message.addReaction(bauxiteIconRaw).queue();
                    message.addReaction(uraniumIconRaw).queue();
                });
            }
        }

    }

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        super.onMessageReactionAdd(event);
        if(event.getUser().isBot()) return;

        if (true) { // YOUR_MESSAGE_ID에는 이벤트를 적용하고 싶은 메시지의 ID를 넣어주세요
//            if (event.getMessageId().equals("YOUR_MESSAGE_ID")) { // YOUR_MESSAGE_ID에는 이벤트를 적용하고 싶은 메시지의 ID를 넣어주세요
            RichCustomEmoji copperIconRaw = event.getGuild().getEmojisByName("copper", true).getFirst();
            RichCustomEmoji bauxiteIconRaw = event.getGuild().getEmojisByName("bauxite", true).getFirst();
            RichCustomEmoji uraniumIconRaw = event.getGuild().getEmojisByName("uranium", true).getFirst();
            String copperIcon = copperIconRaw.getFormatted();
            String bauxiteIcon = bauxiteIconRaw.getFormatted();
            String uraniumIcon = uraniumIconRaw.getFormatted();
            if (event.getEmoji().getName().equals(copperIcon)) {
                event.getChannel().sendMessage(event.getUser().getAsMention() + " 님이 copper 반응을 추가했습니다.").queue();
            }
            else if (event.getEmoji().getName().equals(bauxiteIcon)) {
                event.getChannel().sendMessage(event.getUser().getAsMention() + " 님이 bauxite 반응을 추가했습니다.").queue();
            }
            else if (event.getEmoji().getName().equals(uraniumIcon)) {
                event.getChannel().sendMessage(event.getUser().getAsMention() + " 님이 uranium 반응을 추가했습니다.").queue();
            }
            else{
                event.getChannel().sendMessage(event.getUser().getAsMention() + " 님이 none 반응을 추가했습니다.").queue();
            }
        }
    }
}