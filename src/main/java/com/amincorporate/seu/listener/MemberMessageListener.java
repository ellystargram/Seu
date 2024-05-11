package com.amincorporate.seu.listener;

import com.amincorporate.seu.pallet.NoticePallet;
import com.amincorporate.seu.service.MemberService;
import com.amincorporate.seu.work.MemberMessageWork;
import com.amincorporate.seu.work.WalletMessageWork;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class MemberMessageListener extends ListenerAdapter {

    //todo 나중에 삭제
    private final MemberService memberService;

    private final MemberMessageWork memberMessageWork;
    private final WalletMessageWork walletMessageWork;

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        if (event.getAuthor().isBot()) return; // 리퀄시브 막기

        String rawMessage = event.getMessage().getContentDisplay().strip(); // 완전한 한줄
        String[] messageWords = rawMessage.split(" ");
        String userCalledWay = ""; // 사용자가 스우를 뭐라고 불렀는지 알아내기

        if (event.isFromGuild()) {
            if (rawMessage.startsWith("스우")) { // 서버에서 왔고 시작이 스우면
                try {
                    userCalledWay = rawMessage.split(" ")[0].replaceAll("스우", "");
                } catch (Exception _) {
                }

                messageWords = Arrays.copyOfRange(messageWords, 1, messageWords.length);
            } else { // 서버에서 왔지만 스우를 부른게 아니면
                return;
            }
        }
        if (messageWords.length == 0) {
            event.getChannel().sendMessage("왜 " + userCalledWay).queue();
            return;
        }

        // 커맨드 구분
        String command = messageWords[0]; // 타이핑 편하게 함
        // 가입(스우(야) 가입)
        switch (command) {
            case "가입" -> {
                memberMessageWork.join(event);
            }
            // 탈퇴(스우(야) 탈퇴)
            case "탈퇴" -> {
                memberMessageWork.leave(event);
            }
            // 내정보(스우(야) 내정보)
            case "내정보" -> {
                memberMessageWork.getInfo(event);
            }
            default -> {
                boolean isAnotherCommand = true;
                if (memberMessageWork.isMemberMessageCommand(command)) {
                    isAnotherCommand = false;
                } else if (walletMessageWork.isWalletMessageCommand(command)) {
                    isAnotherCommand = false;
                }

                //todo late 나중에 이 부분 다른곳으로 옮기기
                if (isAnotherCommand) {
                    sendErrorMessage("그게 뭐누",
                            "뭔지 모르는 커맨드임"
                            , event);
                }
            }
        }


        // 메세지 보내는법
//        event.getChannel().sendMessage("보낼 메세지").queue();

        //임베드 보내는법
//        event.getChannel().sendMessageEmbeds(new EmbedBuilder()
//                .setTitle("제목")
//                .setDescription("설명")
//                .build()).queue();

    }


    private void sendErrorMessage(String title, String description, MessageReceivedEvent event) {
        event.getChannel().sendMessageEmbeds(new EmbedBuilder()
                .setTitle(":frowning: **" + title + "**")
                .setColor(NoticePallet.badRed)
                .setDescription(description)
                .build()).queue();
    }
//    private void sendWarningMessage(String title, String description, MessageReceivedEvent event){
//        event.getChannel().sendMessageEmbeds(new EmbedBuilder()
//                .setTitle(":frowning: "+title)
//                .setColor(NoticePallet.warningYellow)
//                .setDescription(description)
//                .build()).queue(message -> {
//                    message.addReaction()
//        });
//    }

}


