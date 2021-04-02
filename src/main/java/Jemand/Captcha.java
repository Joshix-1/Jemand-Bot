package Jemand;

import org.apache.commons.lang3.StringUtils;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.WebhookMessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.util.DiscordRegexPattern;
import org.javacord.api.util.logging.ExceptionLogger;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class Captcha {
    private static final ConcurrentHashMap<Long, Long> newUsersLastMessage = new ConcurrentHashMap<>();
    private static final long HQ = 561337749245001768L;

    public static int calculate(User user, Server server) {
        int hash = Objects.hash(user.getJoinedAtTimestamp(server).orElse(Instant.EPOCH), user.getIdAsString() + ":why_so_salty#LazyCrypto");
        return (Math.abs(hash) % 9000) + 1000;
    }

    public static String getString(User user, Server server) {
        int captchaNumber = calculate(user, server);
        return user.getNicknameMentionTag() + " schreibe `" + NumberToText.intToText(captchaNumber) + "` mit Ziffern (0-9) in diesen Kanal.\n"
                + "Write `" + NumberToEnglischWords.convertToWord(captchaNumber) + "` as number (e.g. 1234) in this channel.";
    }

    public static void check(MessageCreateEvent event) {
        if(!event.getMessageAuthor().isRegularUser()) return;

        User user = event.getMessageAuthor().asUser().orElse(null);
        Server server = event.getServer().orElse(null);
        if (user != null && server != null && user.getRoles(server).size() == 1 /*@everyone role*/) {
            if (newUsersLastMessage.containsKey(user.getId()) && isMoreThan10minAgo(newUsersLastMessage.get(user.getId()))) {
                newUsersLastMessage.remove(user.getId());
            }
            if (newUsersLastMessage.containsKey(user.getId()) && user.getJoinedAtTimestamp(server).map(ts -> ts.toEpochMilli() > newUsersLastMessage.get(user.getId())).orElse(false)) {
                newUsersLastMessage.remove(user.getId());
            }
            String replacedContent = event.getMessageContent().replaceAll("[,.]", "");
            if (replacedContent.equals(Integer.toString(Captcha.calculate(user, server)))) {
                try {
                    newUsersLastMessage.remove(user.getId());
                    user.addRole(server.getRoleById(559141475812769793L).orElseThrow(() -> new AssertionError("Rolle nicht da"))).join();
                    //user.addRole(server.getRoleById(559444155726823484L).orElseThrow(() -> new AssertionError("Rolle nicht da"))).join();
                    event.getChannel().sendMessage(user.getNicknameMentionTag() + " du kannst dir nun in <#686282295098736820> Rollen geben ;)").exceptionally(ExceptionLogger.get());
                    event.getApi().getThreadPool().getScheduler().schedule(() -> {
                        if (server.getMemberById(user.getId()).isEmpty()) { // user left server:
                            return;
                        }
                        event.getApi().getServerTextChannelById(HQ).flatMap(func::getIncomingWebhook).ifPresent(webhook -> {
                            String name;
                            String url;
                            String message;
                            switch (func.getRandom(1, 4)) {
                                case 1: {
                                    name = "Die Amazonenkönigin";
                                    url = "https://i.postimg.cc/YSSwwvgc/herta.png";
                                    message = "Juten Tach %s!";
                                    break;
                                }
                                case 2: {
                                    name = "Gott";
                                    url = "https://i.postimg.cc/htsqwLXb/gott.png";
                                    message = "Hi %s ;)";
                                    break;
                                }
                                case 3: {
                                    name = "Commandante";
                                    url = "https://i.postimg.cc/LXYKnGNN/k-nguru.png";
                                    message = "Hallo %s!";
                                    if (func.getRandom(0, 4) == 0)
                                        message = "Hallo %s, ich wollte mir Eierkuchen backen und da ist mir aufgefallen dass ich vergessen habe Eier zu kaufen.";
                                    break;
                                }
                                default: {
                                    name = "Der Hauptmann";
                                    url = "https://i.postimg.cc/9fNHXctD/muk.png";
                                    message = "Hi %s o/";
                                    break;
                                }
                            }
                            try {
                                new WebhookMessageBuilder()
                                        .setContent(String.format(message, user.getNicknameMentionTag()))
                                        .setDisplayName(name)
                                        .setDisplayAvatar(new URL(url))
                                        .send(webhook)
                                        .exceptionally(ExceptionLogger.get());
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            }
                        });
                    }, func.getRandom(6, 14), TimeUnit.SECONDS);
                } catch (Exception e) {
                    func.handle(e);
                }
            } else if (newUsersLastMessage.containsKey(user.getId())) { //got captcha
                if (replacedContent.contains(Integer.toString(Captcha.calculate(user, server)))) {
                    event.getChannel().sendMessage(user.getDisplayName(server) + " du musst die Zahl alleine schreiben. :)");
                } else {
                    try {
                        int answer = Captcha.calculate(user, server);
                        int sent = Integer.parseInt(replacedContent);
                        if (sent > answer) {
                            event.getMessage().addReaction("⬇️").exceptionally(ExceptionLogger.get());
                        } else {
                            event.getMessage().addReaction("⬆️").exceptionally(ExceptionLogger.get());
                        }

                        int answerLength = Integer.toString(answer).length();

                        if (answerLength == replacedContent.length()) {

                            String sentStr = NumberToText.intToText(sent);
                            String answerStr = NumberToText.intToText(answer);

                            String difference = StringUtils.difference(sentStr, answerStr);

                            String reverseAnswerStr = StringUtils.reverse(answerStr);
                            if (difference.equals(answerStr)) {
                                String reverseSentStr = StringUtils.reverse(sentStr);

                                String reverseDifference = StringUtils.difference(reverseSentStr, reverseAnswerStr);

                                if (reverseDifference.equals(reverseAnswerStr)) {
                                    new MessageBuilder().setContent(user.getNicknameMentionTag() + " die Zahl lautet: " + answerStr)
                                            .send(event.getChannel()).exceptionally(ExceptionLogger.get());
                                } else {
                                    String reverseDifferenceReversed = StringUtils.reverse(reverseDifference);
                                    new MessageBuilder().setContent(user.getNicknameMentionTag() + " " + answerStr.replaceFirst(reverseDifferenceReversed, "***" + reverseDifferenceReversed.toUpperCase() + "***"))
                                            .send(event.getChannel()).exceptionally(ExceptionLogger.get());
                                }
                            } else {
                                String differenceReversed = StringUtils.reverse(difference);
                                new MessageBuilder().setContent(user.getNicknameMentionTag() + " " + StringUtils.reverse(reverseAnswerStr.replaceFirst(differenceReversed, "***" + differenceReversed.toUpperCase() + "***")))
                                        .send(event.getChannel()).exceptionally(ExceptionLogger.get());
                            }
                        } else { //length differs
                            new MessageBuilder().setContent(user.getNicknameMentionTag() + " die Zahl ist " + answerLength + " Ziffern lang.")
                                    .send(event.getChannel()).exceptionally(ExceptionLogger.get());
                        }
                        return;
                    } catch (NumberFormatException ignored) {}
                }
                event.getMessage().addReaction("❌").exceptionally(ExceptionLogger.get());
            } else if (!DiscordRegexPattern.ROLE_MENTION.matcher(event.getMessageContent()).find()
                    && !event.getMessageContent().contains("@everyone")
                    && !event.getMessageContent().contains("@here")
                    && event.getMessage().getMentionedUsers().size() < 2) {
                sendCaptcha(user, server, event.getChannel());
            }
        }
    }

    public static boolean isMoreThan10minAgo(long timeMs) {
        return (System.currentTimeMillis() - timeMs) > 10 * 60 * 1000;
    }

    private static void sendCaptcha(User user, Server server, TextChannel channel) {
        if (channel == null || user.getRoles(server).size() > 1) return;

        if (!newUsersLastMessage.containsKey(user.getId())
                || isMoreThan10minAgo(newUsersLastMessage.get(user.getId()))) { //is 10 min ago last message

            newUsersLastMessage.put(user.getId(), System.currentTimeMillis());
            EmbedBuilder embed = func.getNormalEmbed(user, null)
                    .setTitle("Captcha")
                    .setDescription(Captcha.getString(user, server));

            channel.sendMessage(embed).exceptionally(ExceptionLogger.get());
        }
    }
}
