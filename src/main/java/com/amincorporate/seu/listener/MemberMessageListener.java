package com.amincorporate.seu.listener;

import com.amincorporate.seu.dto.InfoDTO;
import com.amincorporate.seu.dto.JoinDTO;
import com.amincorporate.seu.exception.MemberExistsException;
import com.amincorporate.seu.exception.MemberNoExistsException;
import com.amincorporate.seu.pallet.NoticePallet;
import com.amincorporate.seu.service.MemberService;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.emoji.RichCustomEmoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MemberMessageListener extends ListenerAdapter {

    private final MemberService memberService;

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        if (event.getAuthor().isBot()) return;//리퀄시브 막기

        String rawMessage = event.getMessage().getContentDisplay();//완전한 한줄
        String[] messageWords = rawMessage.split(" ");
        String userCalledWay = "";//사용자가 스우를 뭐라고 불렀는지 알아내기
        boolean isInServer = event.isFromGuild();//서버에서 온 메세지인가

        if(isInServer){
            if(rawMessage.startsWith("스우")){//서버에서 왔고 시작이 스우면
                userCalledWay = rawMessage.split(" ")[0].replaceAll("스우", "");
                messageWords = Arrays.copyOfRange(messageWords, 1, messageWords.length);
            }
            else{//서버에서 왔지만 스우를 부른게 아니면
                return;
            }
        }
        if(messageWords.length == 0){
            event.getChannel().sendMessage("왜 "+userCalledWay).queue();
            return;
        }

        //커맨드 구분
        String command = messageWords[0];//타이핑 편하게 함
        //가입(스우(야) 가입)
        switch (command) {
            case "가입" -> {
                try {
                    JoinDTO joinDTO = new JoinDTO();
                    joinDTO.setId(event.getAuthor().getId());
                    joinDTO.setName(event.getAuthor().getName());
                    OffsetDateTime userCreatedTime = event.getAuthor().getTimeCreated();
                    joinDTO.setDiscordJoinDate(Date.from(userCreatedTime.toInstant()));
                    memberService.join(joinDTO);
                    sendSuccessMessage("가입 성공!",
                            joinDTO.getName() + "님, 스우 가입을 환영합니다!",
                            event);
                } catch (MemberExistsException e) {
                    sendErrorMessage("가입 실패",
                            event.getAuthor().getName() + "님은 이미 가입되어 있습니다.",
                            event);
                } catch (Exception e) {
                    sendErrorMessage("원인을 모르는 가입 실패",
                            "원인을 모르는 문제가 다음의 쪽지만 남겨놓고 갔습니다.\n" + e.getMessage(),
                            event);
                }
            }
            //탈퇴(스우(야) 탈퇴)
            case "탈퇴" -> {
                try {
                    memberService.leave(event.getAuthor().getId());
                    sendSuccessMessage("탈퇴 완료",
                            "다음에 다시만나요.",
                            event);
                } catch (MemberNoExistsException e) {
                    sendErrorMessage("탈퇴 실패",
                            event.getAuthor().getName() + "님은 애초에 가입되어 있지 않았습니다!",
                            event);
                } catch (Exception e) {
                    sendErrorMessage("원인을 모르는 탈퇴 실패",
                            "원인을 모르는 문제가 다음의 쪽지만 남겨놓고 갔습니다.\n" + e.getMessage(),
                            event);
                }
            }
            //내정보(스우(야) 내정보)
            case "내정보" -> {
                try {
                    InfoDTO infoDTO = memberService.getInfo(event.getAuthor().getId());
                    sendSuccessMessage("내정보 조회 성공",
                            "이름: " + infoDTO.getName() + "\n" +
                                    "디스코드 가입날: " + infoDTO.getDiscordJoinDate() + "\n" +
                                    "스우 가입날: " + infoDTO.getSeuJoinDate(),
                            event);

                } catch (MemberNoExistsException e) {
                    sendErrorMessage("내정보 조회 실패",
                            event.getAuthor().getName() + "님은 스우프로젝트에 가입되어 있지 않아 내정보를 조회할 수 없습니다.\n" +
                                    "가입 후 다시 시도해주세요.(가입하기: \"스우야 가입\")",
                            event);
                } catch (Exception e) {
                    sendErrorMessage("원인을 모르는 내정보 조회 실패",
                            "원인을 모르는 문제가 다음의 쪽지만 남겨놓고 갔습니다.\n" + e.getMessage(),
                            event);
                }
            }
            default -> {
                sendErrorMessage("그게 뭐누",
                        "뭔지 모르는 커맨드임"
                        ,event);
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



    private void sendSuccessMessage(String title, String description, MessageReceivedEvent event){
        event.getChannel().sendMessageEmbeds(new EmbedBuilder()
                .setTitle(":smile: **"+title+"**")
                .setColor(NoticePallet.goodGreen)
                .setDescription(description)
                .build()).queue();
    }
    private void sendErrorMessage(String title, String description, MessageReceivedEvent event){
        event.getChannel().sendMessageEmbeds(new EmbedBuilder()
                .setTitle(":frowning: **"+title+"**")
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


