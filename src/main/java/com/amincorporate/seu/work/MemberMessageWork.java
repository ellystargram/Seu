package com.amincorporate.seu.work;

import com.amincorporate.seu.dto.MemberInfoDTO;
import com.amincorporate.seu.dto.MemberJoinDTO;
import com.amincorporate.seu.exception.MemberExistsException;
import com.amincorporate.seu.exception.MemberNoExistsException;
import com.amincorporate.seu.pallet.NoticePallet;
import com.amincorporate.seu.service.MemberService;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class MemberMessageWork {

    private final MemberService memberService;

    private static String[] joinCommands = {"join", "가입"};
    private static String[] leaveCommands = {"leave", "탈퇴"};
    private static String[] getInfoCommands = {"get info", "내정보"};

    public boolean isMemberMessageCommand(String command) {
        if (isJoinCommand(command)) return true;
        if (isLeaveCommand(command)) return true;
        if (isGetInfoCommand(command)) return true;
        return false;
    }

    public boolean isJoinCommand(String command) {
        for (String joinCommand : joinCommands) {
            if (command.equals(joinCommand)) return true;
        }
        return false;
    }

    public boolean isLeaveCommand(String command) {
        for (String leaveCommand : leaveCommands) {
            if (command.equals(leaveCommand)) return true;
        }
        return false;
    }

    public boolean isGetInfoCommand(String command) {
        for (String getInfoCommand : getInfoCommands) {
            if (command.equals(getInfoCommand)) return true;
        }
        return false;
    }

    public void join(MessageReceivedEvent event) {
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
                    event.getAuthor().getName() + "님은 이미 가입되어 있어요!",
                    event);
        } catch (Exception e) {
            sendErrorMessage("원인을 모르는 가입 실패",
                    "원인을 모르는 문제가 다음의 쪽지만 남겨놓고 갔습니다.\n" + e.getMessage(),
                    event);
        }
    }

    public void leave(MessageReceivedEvent event) {

        if (!memberService.isMemberExists(event.getAuthor().getId())) {
            sendErrorMessage("탈퇴 실패",
                    event.getAuthor().getName() + "님은 Seu에 가입되어 있지 않아요.\n\n\"스우 가입\" 으로 가입 해주세요.",
                    event);
            return;
        }

        String userID = event.getAuthor().getId();

        event.getChannel().sendMessageEmbeds(new EmbedBuilder()
                .setTitle(":face_with_raised_eyebrow: **ㄹㅇ?**")
                .setColor(NoticePallet.warningYellow)
                .setDescription("정말 탈퇴하시겠어요? 탈퇴하시면 정보가 모두 날라가고, 다시는 복구할 수 없어요.")
                .build()).queue(message -> {

            message.addReaction(Emoji.fromUnicode("U+1F198")).queue();
            message.addReaction(Emoji.fromUnicode("U+1F494")).queue();


            event.getJDA().addEventListener(new ListenerAdapter() {
                @Override
                public void onMessageReactionAdd(MessageReactionAddEvent addEvent) {
                    if (!userID.equals(addEvent.getUserId()) || !message.getId().equals(addEvent.getMessageId())) return;

                    if (addEvent.getEmoji().getAsReactionCode().split(":")[0].equals(Emoji.fromUnicode("U+1F494").getAsReactionCode())) {
                        //삭제
                        try {
                            memberService.leave(event.getAuthor().getId());
                            editSuccessMessage("탈퇴 완료",
                                    "다음에 다시만나요.",
                                    message);
                        } catch (Exception e) {
                            editErrorMessage("원인을 모르는 탈퇴 실패",
                                    "원인을 모르는 문제가 다음의 쪽지만 남겨놓고 갔습니다.\n" + e.getMessage(),
                                    message);
                        }

                    } else {
                        //삭제 취소
                        editErrorMessage("탈퇴 취소됨",
                                "사용자가 취소를 취소했습니다(?)",
                                message);
                    }
                    message.clearReactions().queue();
                }
            });
        });


    }

    public void getInfo(MessageReceivedEvent event) {
        try {
            MemberInfoDTO memberInfoDTO = memberService.getInfo(event.getAuthor().getId());
            sendSuccessMessage("내정보 조회 성공",
                    "이름: " + memberInfoDTO.getName() + "\n" +
                            "디스코드 가입날: " + memberInfoDTO.getDiscordJoinDate() + "\n" +
                            "스우 가입날: " + memberInfoDTO.getSeuJoinDate(),
                    event);

        } catch (MemberNoExistsException e) {
            sendErrorMessage("내정보 조회 실패",
                    event.getAuthor().getName() + "님은 Seu에 가입되어 있지 않아요.\n\n\"스우 가입\" 으로 가입 해주세요.",
                    event);
        } catch (Exception e) {
            sendErrorMessage("원인을 모르는 내정보 조회 실패",
                    "원인을 모르는 문제가 다음의 쪽지만 남겨놓고 갔습니다.\n" + e.getMessage(),
                    event);
        }
    }

    private void sendSuccessMessage(String title, String description, MessageReceivedEvent event) {
        event.getChannel().sendMessageEmbeds(new EmbedBuilder()
                .setTitle(":smile: **" + title + "**")
                .setColor(NoticePallet.goodGreen)
                .setDescription(description)
                .build()).queue();
    }

    private String sendWarningMessage(String title, String description, MessageReceivedEvent event) {
        final String[] messageID = new String[1];
        event.getChannel().sendMessageEmbeds(new EmbedBuilder()
                .setTitle(":face_with_raised_eyebrow: **" + title + "**")
                .setColor(NoticePallet.warningYellow)
                .setDescription(description)
                .build()).queue(message -> messageID[0] = message.getId());
        return messageID[0];
    }

    private void sendErrorMessage(String title, String description, MessageReceivedEvent event) {
        event.getChannel().sendMessageEmbeds(new EmbedBuilder()
                .setTitle(":frowning: **" + title + "**")
                .setColor(NoticePallet.badRed)
                .setDescription(description)
                .build()).queue();
    }

    private void editSuccessMessage(String title, String description, Message message) {
        message.editMessageEmbeds(new EmbedBuilder()
                .setTitle(":smile: **" + title + "**")
                .setDescription(description)
                .setColor(NoticePallet.goodGreen)
                .build()).queue();
    }

    private void editErrorMessage(String title, String description, Message message) {
        message.editMessageEmbeds(new EmbedBuilder()
                .setTitle(":frowning: **" + title + "**")
                .setDescription(description)
                .setColor(NoticePallet.badRed)
                .build()).queue();
    }

    private void editWarningMessage(String title, String description, MessageChannel channel, String messageID) {
        channel.editMessageEmbedsById(messageID, new EmbedBuilder()
                .setTitle(":face_with_raised_eyebrow: **" + title + "**")
                .setDescription(description)
                .setColor(NoticePallet.warningYellow)
                .build()).queue();
    }

}