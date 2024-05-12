package com.amincorporate.seu.work;

import com.amincorporate.seu.dto.CoinBuyableDTO;
import com.amincorporate.seu.exception.CoinNoExistsException;
import com.amincorporate.seu.pallet.NoticePallet;
import com.amincorporate.seu.service.MemberService;
import com.amincorporate.seu.service.TradeService;
import com.amincorporate.seu.service.WalletService;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TradeMessageWork {

    private final MemberService memberService;
    private final WalletService walletService;
    private final TradeService tradeService;

    private String[] coinMarketSearchCommands = {"market", "시장", "거래시장", "거래소"};
    private String[] coinTransactionCommands = {"transaction", "거래"};
    private String[] coinTransferCommands = {"transfer", "이체"};

    public boolean isCoinTradeCommand(String command) {
        if (isCoinMarketSearchCommand(command)) return true;
        if (isCoinTransactionCommand(command)) return true;
        if (isCoinTransferCommand(command)) return true;
        return false;
    }

    public boolean isCoinMarketSearchCommand(String command) {
        for (String coinMarketSearchCommand : coinMarketSearchCommands) {
            if (command.equals(coinMarketSearchCommand)) return true;
        }
        return false;
    }

    public boolean isCoinTransactionCommand(String command) {
        for (String coinTransactionCommand : coinTransactionCommands) {
            if (command.equals(coinTransactionCommand)) return true;
        }
        return false;
    }

    public boolean isCoinTransferCommand(String command) {
        for (String coinTransferCommand : coinTransferCommands) {
            if (command.equals(coinTransferCommand)) return true;
        }
        return false;
    }

    public void coinMarketList(MessageReceivedEvent event) {
        //스우야 거래소
        //todo late
        sendSuccessMessage("공사중이다 새끼야",
                "나중에 다시 찾아와라",
                event);
    }

    public void transaction(MessageReceivedEvent event, String[] commands) {
        // 스우야 거래 살코인 몇개를살지 구매지갑
        //         0   1    2          3
        String buyCoinID = "";
        String howManyInString = "";
        Double howManyInDouble = 0.0;
        String walletAddress = "";

        try {
            buyCoinID = commands[1];
            howManyInString = commands[2];
            howManyInDouble = Double.valueOf(howManyInString);
            walletAddress = commands[3];

        } catch (IndexOutOfBoundsException e) {
            sendErrorMessage("거래 실패",
                    "입력법 \"스우야 거래 (사고싶은 코인 ID) 거래량 거래지갑주소\"",
                    event);
            return;
        } catch (Exception e) {
            sendErrorMessage("원인을 모르는 거래 실패",
                    "다음의 메시지만이 있어요\n" + e.getMessage(),
                    event);
            return;
        }

        try {
            Double needUSD = tradeService.getCoin(buyCoinID).getPrice() * howManyInDouble;
            List<CoinBuyableDTO> coinBuyableDTOS = tradeService.getBuyable(walletAddress, buyCoinID, howManyInDouble);
            String listYouCanBuy = buyCoinID + "구매하는 방법\n";
            if (coinBuyableDTOS.isEmpty()) {
                sendErrorMessage("거래 실패",
                        "지불 가능한 코인이 없습니다.",
                        event);
                return;
            }

            String[] icons = {":one:"};

            for (int i=0; i<coinBuyableDTOS.size();i++){
                CoinBuyableDTO coinBuyableDTO = coinBuyableDTOS.get(i);
                listYouCanBuy += "> " + icons[i] + " " + coinBuyableDTO.getCoinEntity().getName() + ": " + (needUSD / coinBuyableDTO.getCoinEntity().getPrice()) + " " + coinBuyableDTO.getCoinEntity().getSymbol() + "\n\n";
            }

            event.getChannel().sendMessageEmbeds(new EmbedBuilder()
                    .setTitle(":face_with_raised_eyebrow: **코인거래**")
                    .setColor(NoticePallet.warningYellow)
                    .setDescription(listYouCanBuy)
                    .build()).queue(message -> {

            });

        } catch (CoinNoExistsException e) {
            // 존재하지 않는 코인
            sendErrorMessage("거래 실패",
                    "그런 코인은 세상에 존재하지 않아요" + event.getAuthor().getName() + "어린이\n",
                    event);
        } catch (Exception e) {
            sendErrorMessage("원인을 모르는 거래 실패",
                    "다음의 메시지만이 있어요\n" + e.getMessage(),
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

    private void sendErrorMessage(String title, String description, MessageReceivedEvent event) {
        event.getChannel().sendMessageEmbeds(new EmbedBuilder()
                .setTitle(":frowning: **" + title + "**")
                .setColor(NoticePallet.badRed)
                .setDescription(description)
                .build()).queue();
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
