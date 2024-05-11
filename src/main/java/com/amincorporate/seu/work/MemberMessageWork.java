package com.amincorporate.seu.work;

import com.amincorporate.seu.dto.MemberInfoDTO;
import com.amincorporate.seu.dto.MemberJoinDTO;
import com.amincorporate.seu.exception.MemberExistsException;
import com.amincorporate.seu.exception.MemberNoExistsException;
import com.amincorporate.seu.pallet.NoticePallet;
import com.amincorporate.seu.service.MemberService;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class MemberMessageWork {

    private final MemberService memberService;

    private static String[] joinCommands = {"join", "가입"};
    private static String[] leaveCommands = {"leave","탈퇴"};
    private static String[] getInfoCommands = {"get info", "내정보"};

    public boolean isMemberMessageCommand(String command){
        for(String joinCommand : joinCommands){
            if(command.equals(joinCommand)){
                return true;
            }
        }
        for(String leaveCommand : leaveCommands){
            if (command.equals(leaveCommand)){
                return true;
            }
        }
        for(String getInfoCommand : getInfoCommands){
            if (command.equals(getInfoCommand)){
                return true;
            }
        }
        return false;
    }

    public void join(MessageReceivedEvent event){
        try {
            MemberJoinDTO memberJoinDTO = new MemberJoinDTO();
            memberJoinDTO.setId(event.getAuthor().getId());
            memberJoinDTO.setName(event.getAuthor().getName());
            OffsetDateTime userCreatedTime = event.getAuthor().getTimeCreated();
            memberJoinDTO.setDiscordJoinDate(Date.from(userCreatedTime.toInstant()));
            memberService.join(memberJoinDTO);
            sendSuccessMessage("가입 성공!",
                    memberJoinDTO.getName() + "님, 스우 가입을 환영합니다!",
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

    public void leave(MessageReceivedEvent event){
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

    public void getInfo(MessageReceivedEvent event){
        try {
            MemberInfoDTO memberInfoDTO = memberService.getInfo(event.getAuthor().getId());
            sendSuccessMessage("내정보 조회 성공",
                    "이름: " + memberInfoDTO.getName() + "\n" +
                            "디스코드 가입날: " + memberInfoDTO.getDiscordJoinDate() + "\n" +
                            "스우 가입날: " + memberInfoDTO.getSeuJoinDate(),
                    event);

        } catch (MemberNoExistsException e) {
            sendErrorMessage("내정보 조회 실패",
                    event.getAuthor().getName() + "님은 스우에 가입되어 있지 않아 정보를 조회할 수 없습니다.\n\n" +
                            "가입 후 다시 시도해주세요. (가입하기: \"스우야 가입\")",
                    event);
        } catch (Exception e) {
            sendErrorMessage("원인을 모르는 내정보 조회 실패",
                    "원인을 모르는 문제가 다음의 쪽지만 남겨놓고 갔습니다.\n" + e.getMessage(),
                    event);
        }
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

}