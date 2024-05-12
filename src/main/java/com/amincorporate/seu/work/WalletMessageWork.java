package com.amincorporate.seu.work;

import com.amincorporate.seu.dto.ExchangeInfoDTO;
import com.amincorporate.seu.dto.WalletCreateDTO;
import com.amincorporate.seu.dto.WalletInfoDTO;
import com.amincorporate.seu.entity.wallet.WalletType;
import com.amincorporate.seu.exception.MemberNoExistsException;
import com.amincorporate.seu.exception.WalletNoExistsException;
import com.amincorporate.seu.pallet.NoticePallet;
import com.amincorporate.seu.service.MemberService;
import com.amincorporate.seu.service.MemberServiceImpl;
import com.amincorporate.seu.service.WalletService;
import com.amincorporate.seu.service.WalletServiceImpl;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.emoji.RichCustomEmoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class WalletMessageWork {

    private final WalletService walletService;
    private final MemberService memberService;

    private static String[] createWalletCommands = {"createWallet", "지갑생성", "지갑만들기"};
    private static String[] deleteWalletCommands = {"deleteWallet", "지갑삭제"};
    private static String[] walletInfoCommands = {"walletInfo", "지갑정보", "지갑조회", "내지갑"};

    public boolean isWalletMessageCommand(String command) {
        if (isCreateWalletCommand(command)) return true;
        if (isDeleteWalletCommand(command)) return true;
        if (isWalletInfoCommand(command)) return true;
        return false;
    }

    public boolean isCreateWalletCommand(String command) {
        for (String createWalletCommand : createWalletCommands) {
            if (command.equals(createWalletCommand)) return true;
        }
        return false;
    }

    public boolean isDeleteWalletCommand(String command) {
        for (String deleteWalletCommand : deleteWalletCommands) {
            if (command.equals(deleteWalletCommand)) return true;
        }
        return false;
    }

    public boolean isWalletInfoCommand(String command) {
        for (String walletInfoCommand : walletInfoCommands) {
            if (command.equals(walletInfoCommand)) return true;
        }
        return false;
    }

    public void createWallet(MessageReceivedEvent event, JDA jda) {
        if (!memberService.isMemberExists(event.getAuthor().getId())) {
            sendErrorMessage("지갑 생성 실패",
                    event.getAuthor().getName() + "님은 가입되지 않았습니다! 먼저 \"스우 가입\" 명령어를 통해 가입해 주세요.",
                    event);
            return;
        }

        RichCustomEmoji copperIconRaw = event.getGuild().getEmojisByName("copper", true).getFirst();
        RichCustomEmoji bauxiteIconRaw = event.getGuild().getEmojisByName("bauxite", true).getFirst();
        RichCustomEmoji uraniumIconRaw = event.getGuild().getEmojisByName("uranium", true).getFirst();

        String copperIcon = event.getGuild().getEmojisByName("copper", true).getFirst().getFormatted();
        String bauxiteIcon = event.getGuild().getEmojisByName("bauxite", true).getFirst().getFormatted();
        String uraniumIcon = event.getGuild().getEmojisByName("uranium", true).getFirst().getFormatted();

        event.getChannel().sendMessageEmbeds(new EmbedBuilder()
                .setTitle(":moneybag: 지갑을 골라주세요")
                .setColor(NoticePallet.warningYellow)
                .setDescription("**" + WalletType.COPPER.toString() + " (**" + copperIcon + "**) : 1배**\n" +
                        "**" + WalletType.BAUXITE.toString() + " (**" + bauxiteIcon + "**) : 2배**\n" +
                        "**" + WalletType.URANIUM.toString() + " (**" + uraniumIcon + "**) : 3배**")
                .build()).queue(message -> {
            message.addReaction(copperIconRaw).queue();
            message.addReaction(bauxiteIconRaw).queue();
            message.addReaction(uraniumIconRaw).queue();

            jda.addEventListener(new ListenerAdapter() {
                @Override
                public void onMessageReactionAdd(MessageReactionAddEvent reactionEvent) {

                    if (!message.getId().equals(reactionEvent.getMessageId()) || !reactionEvent.getUserId().equals(event.getAuthor().getId())) {
                        return;
                    }

                    String selectEmoji = reactionEvent.getEmoji().getAsReactionCode().split(":")[0];

                    try {

                        WalletCreateDTO walletCreateDTO = walletService.create(event.getAuthor().getId(), WalletType.valueOf(selectEmoji.toUpperCase()));

                        String icon = "";
                        if (walletCreateDTO.getWalletType() == WalletType.COPPER) icon = copperIcon;
                        else if (walletCreateDTO.getWalletType() == WalletType.BAUXITE) icon = bauxiteIcon;
                        else if (walletCreateDTO.getWalletType() == WalletType.URANIUM) icon = uraniumIcon;

                        editSuccessMessage("지갑 생성 성공!",
                                "> 지갑주소: **" + walletCreateDTO.getWalletId() + "**\n> 지갑종류: **" + walletCreateDTO.getWalletType().toString() + " (**" + icon + "**)**",
                                message);

                        message.clearReactions().queue();

                    } catch (Exception e) {
                        sendErrorMessage("원인을 모르는 지갑 생성 실패",
                                "원인을 모르는 문제가 다음의 쪽지만 남겨놓고 갔습니다.\n" + e.getMessage(),
                                event);
                    }
                }
            });
        });

    }

    public void walletInfo(MessageReceivedEvent event) {
        String[] userInput = event.getMessage().getContentDisplay().strip().split(" ");
        String accountListString = "";
        userInput = Arrays.copyOfRange(userInput, 1, userInput.length);

        if (userInput.length == 1) { // 지갑정보만 입력함
            String copperIcon = event.getGuild().getEmojisByName("copper", true).getFirst().getFormatted();
            String bauxiteIcon = event.getGuild().getEmojisByName("bauxite", true).getFirst().getFormatted();
            String uraniumIcon = event.getGuild().getEmojisByName("uranium", true).getFirst().getFormatted();
            try {
                List<WalletInfoDTO> walletInfoDTOS = walletService.getInfo(event.getAuthor().getId());

                for (WalletInfoDTO walletInfoDTO : walletInfoDTOS) {

                    String icon = "";
                    if (walletInfoDTO.getWalletType() == WalletType.COPPER) icon = copperIcon;
                    else if (walletInfoDTO.getWalletType() == WalletType.BAUXITE) icon = bauxiteIcon;
                    else if (walletInfoDTO.getWalletType() == WalletType.URANIUM) icon = uraniumIcon;

                    accountListString += "> 지갑주소: **" + walletInfoDTO.getId() + "**\n> 지갑종류: **" + walletInfoDTO.getWalletType().toString() + " (**" + icon + "**)**\n\n";
                }

                sendSuccessMessage(event.getAuthor().getName() + "님의 지갑 목록",
                        accountListString,
                        event);
            } catch (MemberNoExistsException e) {
                sendErrorMessage("지갑 조회 실패",
                        event.getAuthor().getName() + "님은 가입되어 있지 않습니다. \"스우 가입\" 명령어를 통해 가입 먼저 해주세요.",
                        event);
            } catch (WalletNoExistsException e) {
                sendErrorMessage("지갑 조회 실패",
                        event.getAuthor().getName() + "님은 지갑을 가지고 계시지 않습니다. \"스우 지갑생성\" 명령어를 통해 지갑을 먼저 만들어주세요.",
                        event);
            } catch (Exception e) {
                sendErrorMessage("원인을 모르는 지갑 조회 실패",
                        "원인을 모르는 문제가 다음의 쪽지만 남겨놓고 갔습니다.\n" + e.getMessage(),
                        event);
            }
            return;
        } else {
            userInput = Arrays.copyOfRange(userInput, 1, userInput.length);
        }
        try {
            List<ExchangeInfoDTO> exchangeInfoDTOS = walletService.getInfoDetail(event.getAuthor().getId(), userInput[0]);
            String walletDetail = userInput[0] + " 지갑의 목록들\n";
            for (ExchangeInfoDTO exchangeInfoDTO : exchangeInfoDTOS) {
                String coinName = exchangeInfoDTO.getName();
                String coinCode;
                Double coinQT = exchangeInfoDTO.getQuantity();
                String coinSymbol = exchangeInfoDTO.getSymbol();
                Double coinPrice = exchangeInfoDTO.getPrice();

                walletDetail += "> **COIN: " + coinName + "**\n> **AMOUNT: " + String.valueOf(coinQT) + " " + coinSymbol + "**\n> **BUY PRICE: " + String.valueOf(coinPrice) + " $**\n\n";
            }
            sendSuccessMessage(userInput[0] + "지갑의 내용물들",
                    walletDetail,
                    event);

        } catch (WalletNoExistsException e) {
            sendErrorMessage("지갑 상세조회 실패",
                    userInput[0] + "이란 주소를 가진 지갑은 존재하지 않거나, " + event.getAuthor().getName() + "님이 소유하고 있지 않습니다.",
                    event);
        } catch (Exception e) {
            sendErrorMessage("원인을 모르는 지갑 상세조회 실패",
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

    private String sendErrorMessage(String title, String description, MessageReceivedEvent event) {
        final String[] messageID = new String[1];
        event.getChannel().sendMessageEmbeds(new EmbedBuilder()
                .setTitle(":frowning: **" + title + "**")
                .setColor(NoticePallet.badRed)
                .setDescription(description)
                .build()).queue(message -> messageID[0] = message.getId());
        return messageID[0];
    }

    private void sendWarningMessage(String title, String description, MessageReceivedEvent event) {
        event.getChannel().sendMessageEmbeds(new EmbedBuilder()
                .setTitle(":face_with_raised_eyebrow: **" + title + "**")
                .setColor(NoticePallet.warningYellow)
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
