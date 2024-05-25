package com.amincorporate.seu.work;

import com.amincorporate.seu.dto.CoinBuyableDTO;
import com.amincorporate.seu.dto.CoinListDTO;
import com.amincorporate.seu.dto.CoinTradeDTO;
import com.amincorporate.seu.entity.CoinEntity;
import com.amincorporate.seu.exception.CoinNoExistsException;
import com.amincorporate.seu.exception.MemberNoExistsException;
import com.amincorporate.seu.exception.MoneyNotEnoughException;
import com.amincorporate.seu.exception.WalletNoExistsException;
import com.amincorporate.seu.pallet.NoticePallet;
import com.amincorporate.seu.service.TradeService;
import com.amincorporate.seu.service.WalletService;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.emoji.RichCustomEmoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class TradeMessageWork {

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

        try {
            RichCustomEmoji BTCIcon = event.getGuild().getEmojisByName("BTC", true).getFirst();
            RichCustomEmoji ETHIcon = event.getGuild().getEmojisByName("ETH", true).getFirst();
            RichCustomEmoji SOLIcon = event.getGuild().getEmojisByName("SOL", true).getFirst();
            RichCustomEmoji ADAIcon = event.getGuild().getEmojisByName("ADA", true).getFirst();
            RichCustomEmoji DOGEIcon = event.getGuild().getEmojisByName("DOGE", true).getFirst();

            Map<String, RichCustomEmoji> coinIcons = new HashMap<>() {{
                put("BTC", BTCIcon);
                put("ETH", ETHIcon);
                put("SOL", SOLIcon);
                put("ADA", ADAIcon);
                put("DOGE", DOGEIcon);
            }};

            String marketResearch = "";
            List<CoinListDTO> coinListDTOS = tradeService.getCoinList();

            for (CoinListDTO coinListDTO : coinListDTOS) {
                BigDecimal minimalTradeAmout = new BigDecimal(10);
                minimalTradeAmout = minimalTradeAmout.pow(coinListDTO.getMaxDecimal());
                minimalTradeAmout = BigDecimal.ONE.divide(minimalTradeAmout);
                marketResearch += "> " + coinIcons.get(coinListDTO.getId()).getFormatted() + " **" + coinListDTO.getName() + " (" + coinListDTO.getId() + ")**" + "\n>\t1.0 단위당 USD 가치: " +
                        coinListDTO.getPrice() + " $\n>\t최소 거래량: " +
                        minimalTradeAmout.toPlainString() + " " + coinListDTO.getSymbol() + "\n\n";
            }
            sendSuccessMessage("현재 시장 상황",
                    marketResearch,
                    event);
        } catch (Exception e) {
            sendErrorMessage("원인을 모르는 시장 조사 실패",
                    "다음의 메시지만이 있어요\n" + e.getMessage(),
                    event);
            e.printStackTrace();
        }
    }

    public void transaction(MessageReceivedEvent event, String[] commands, JDA jda) {
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
            if (howManyInDouble <= 0) {
                sendErrorMessage("거래 실패",
                        "잘못된 거래량입니다.\n" + howManyInDouble + "\n" + howManyInString,
                        event);
                return;
            }
        } catch (IndexOutOfBoundsException e) {
            sendErrorMessage("거래 실패",
                    "입력법 \"스우야 거래 (사고싶은 코인 ID) 거래량 거래지갑주소\"",
                    event);
            return;
        } catch (Exception e) {
            sendErrorMessage("원인을 모르는 거래 실패",
                    "다음의 메시지만이 있어요\n" + e.getMessage(),
                    event);
            e.printStackTrace();
            return;
        }

        if (walletService.isWalletExists(event.getAuthor().getId(), walletAddress)) {
            sendErrorMessage("거래 실패",
                    walletAddress + "이란 주소를 가진 지갑은 존재하지 않거나, " + event.getAuthor().getName() + "님이 소유하고 있지 않습니다.",
                    event);
            return;
        }

        try {
            CoinEntity coin = tradeService.getCoin(buyCoinID);
            Double needUSD = coin.getPrice() * Double.valueOf(decimalCeil(howManyInDouble, coin.getMaxDecimal()));
            List<CoinBuyableDTO> coinBuyableDTOS = tradeService.getBuyable(walletAddress, buyCoinID, Double.valueOf(decimalCeil(howManyInDouble, coin.getMaxDecimal())));
            String listYouCanBuy = "**" + coin.getName() + " " + decimalCeil(howManyInDouble, coin.getMaxDecimal()) + coin.getSymbol() + "을 이 가격으로 매수 할 수 있어요.**\n\n";
            if (coinBuyableDTOS.isEmpty()) {
                sendErrorMessage("거래 실패",
                        "지불 가능한 코인이 없습니다.",
                        event);
                return;
            }


            Map<String, RichCustomEmoji> coinIcons = new HashMap<>();

            RichCustomEmoji BTCIcon = event.getGuild().getEmojisByName("BTC", true).getFirst();
            RichCustomEmoji ETHIcon = event.getGuild().getEmojisByName("ETH", true).getFirst();
            RichCustomEmoji SOLIcon = event.getGuild().getEmojisByName("SOL", true).getFirst();
            RichCustomEmoji ADAIcon = event.getGuild().getEmojisByName("ADA", true).getFirst();
            RichCustomEmoji DOGEIcon = event.getGuild().getEmojisByName("DOGE", true).getFirst();

            // Map 에 Icon 저장
            coinIcons.put("BTC", BTCIcon);
            coinIcons.put("ETH", ETHIcon);
            coinIcons.put("SOL", SOLIcon);
            coinIcons.put("ADA", ADAIcon);
            coinIcons.put("DOGE", DOGEIcon);

            for (int i = 0; i < coinBuyableDTOS.size(); i++) {
                CoinBuyableDTO coinBuyableDTO = coinBuyableDTOS.get(i);
                listYouCanBuy += "> " + coinIcons.get(coinBuyableDTO.getCoinEntity().getId()).getFormatted() + " " + coinBuyableDTO.getCoinEntity().getName() + ": " + decimalCeil(needUSD / coinBuyableDTO.getCoinEntity().getPrice(), coinBuyableDTO.getCoinEntity().getMaxDecimal()) + " **" + coinBuyableDTO.getCoinEntity().getSymbol() + "**\n\n";
            }

            listYouCanBuy += coin.getName() + " 을(를) 사기 위해 사용할 코인을 골라주세요.";

            String finalWalletAddress = walletAddress;
            String finalBuyCoinID = buyCoinID;
            Double finalHowManyInDouble = howManyInDouble;

            event.getChannel().sendMessageEmbeds(new EmbedBuilder()
                    .setTitle(":face_with_raised_eyebrow: **코인거래**")
                    .setColor(NoticePallet.warningYellow)
                    .setDescription(listYouCanBuy)
                    .build()).queue(message -> {

                for (int i = 0; i < coinBuyableDTOS.size(); i++) {
                    CoinBuyableDTO coinBuyableDTO = coinBuyableDTOS.get(i);
                    message.addReaction(coinIcons.get(coinBuyableDTO.getCoinEntity().getId())).queue();
                }

                jda.addEventListener(new ListenerAdapter() {
                    @Override
                    public void onMessageReactionAdd(MessageReactionAddEvent reactionEvent) {

                        if (!message.getId().equals(reactionEvent.getMessageId()) || !reactionEvent.getUserId().equals(event.getAuthor().getId())) {
                            return;
                        }

                        String selectEmoji = reactionEvent.getEmoji().getAsReactionCode().split(":")[0];

                        try {

                            CoinTradeDTO coinTradeDTO = tradeService.trade(finalWalletAddress, finalBuyCoinID, Double.valueOf(decimalCeil(finalHowManyInDouble, coin.getMaxDecimal())), selectEmoji);

                            editSuccessMessage("거래 성공",
                                    "> " + coinIcons.get(coinTradeDTO.getInCoin().getId()).getFormatted() + " " + coinTradeDTO.getInCoin().getName() + " " + decimalCeil(finalHowManyInDouble, coinTradeDTO.getInCoin().getMaxDecimal()) + " **" + coinTradeDTO.getInCoin().getSymbol() + "**(을)를 \n> " +
                                            coinIcons.get(coinTradeDTO.getOutCoin().getId()).getFormatted() + " " + coinTradeDTO.getOutCoin().getName() + " " + decimalCeil(needUSD / coinTradeDTO.getOutCoin().getPrice(), coinTradeDTO.getOutCoin().getMaxDecimal()) + " " + coinTradeDTO.getOutCoin().getSymbol() + "로 매수 했어요.",
                                    message);

                            message.clearReactions().queue();

                        } catch (MoneyNotEnoughException e) {
                            editErrorMessage("거래 실패",
                                    finalWalletAddress + "지갑에 필요한 코인이 **\"아까까지만 해도\" 존재했지만**, 지금은 없어서 거래를 할 수 없네요",
                                    message);
                            message.clearReactions().queue();
                        } catch (MemberNoExistsException e) {
                            editErrorMessage("거래 실패",
                                    event.getAuthor().getName() + "님은 Seu에 가입되어 있지 않아요.\n\n\"스우 가입\" 으로 가입 해주세요.",
                                    message);
                            message.clearReactions().queue();
                        } catch (WalletNoExistsException e) {
                            editErrorMessage("거래 실패",
                                    finalWalletAddress + " 이라는 지갑이 존재하지 않아요.\n\n\"스우 지갑생성\" 으로 지갑을 만들어주세요.",
                                    message);
                            message.clearReactions().queue();
                        } catch (Exception e) {
                            editErrorMessage("원인을 모르는 거래 실패",
                                    "원인을 모르는 문제가 다음의 쪽지만 남겨놓고 갔습니다.\n" + e.getMessage(),
                                    message);
                            e.printStackTrace();
                            message.clearReactions().queue();
                        }

                    }
                });

            });

        } catch (MemberNoExistsException e) {
            sendErrorMessage("거래 실패",
                    event.getAuthor().getName() + "님은 Seu에 가입되어 있지 않아요.\n\n\"스우 가입\" 으로 가입 해주세요.",
                    event);
        } catch (WalletNoExistsException e) {
            sendErrorMessage("거래 실패",
                    event.getAuthor().getName() + "님은 지갑이 존재하지 않아요.\n\n\"스우 지갑 생성\" 으로 지갑을 만들어주세요.",
                    event);
        } catch (CoinNoExistsException e) {
            // 존재하지 않는 코인
            sendErrorMessage("거래 실패",
                    "그런 코인은 세상에 존재하지 않아요" + event.getAuthor().getName() + "어린이\n",
                    event);
        } catch (MoneyNotEnoughException e) {
            sendErrorMessage("거래 실패",
                    "지불 가능한 코인이 없습니다.",
                    event);
        } catch (Exception e) {
            sendErrorMessage("원인을 모르는 거래 실패",
                    "다음의 메시지만이 있어요\n" + e.getMessage(),
                    event);
            e.printStackTrace();
        }

    }

    private String decimalRounder(Double value, int cutDecimal) {
        String decimal = String.valueOf(value).split("\\.")[1];
        int decimalLen = decimal.length();
        if (decimalLen > cutDecimal) decimalLen = cutDecimal;
        return String.format("%." + decimalLen + "f", value);
    }

    private String decimalCeil(Double value, int cutDecimal) {
        String decimal = String.valueOf(value).split("\\.")[1];
        int decimalLen = decimal.length();
        if (decimalLen > cutDecimal) decimalLen = cutDecimal;
        return String.format("%." + decimalLen + "f", Math.ceil(value * Math.pow(10, cutDecimal)) / Math.pow(10, cutDecimal));
    }

    private String decimalFloor(Double value, int cutDecimal) {
        String decimal = String.valueOf(value).split("\\.")[1];
        int decimalLen = decimal.length();
        if (decimalLen > cutDecimal) decimalLen = cutDecimal;
        return String.format("%." + decimalLen + "f", Math.floor(value * Math.pow(10, cutDecimal)) / Math.pow(10, cutDecimal));
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
