package Jemand;

import com.vdurmont.emoji.EmojiParser;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.reaction.ReactionAddEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class TTT {
    private static final Pattern O = Pattern.compile(":o2:");
    private static final Pattern X = Pattern.compile("\uD83C\uDDFD");
    private static final Pattern NEW_LINE = Pattern.compile("\n");
    private static final String[] zahl = {":zero:", ":one:", ":two:", ":three:", ":four:", ":five:", ":six:", ":seven:", ":eight:", ":nine:", ":keycap_ten:"};
    private static final String[] leer = "0 <:01:703321359744499813> <:02:703322946650898443> <:03:703322960420667392> <:04:703322984101838881> <:05:703323000560418927> <:06:703323015030636545> <:07:703323031732486405> <:08:703323047209205851> <:09:703323063571185777>".split(" ");
    private long[] users;
    private final String[] emojis = {func.parseLetterToEmote("x"), ":o2:"};
    private String message;
    private int rounds;
    private Message m;
    private boolean[] ai = new boolean[2];
    private int state = 0; //0 = game is running; 1 = user1 won; 2 = user2 won; 3 = draw
    private Texte texte;
    private Befehl befehl;

    TTT(Befehl b, User user1, long user2) {
        long jemand_id = func.getApi().getYourself().getId();
        befehl = b;
        texte = b.getTexte();
        user2 = b.getServer().getMemberById(user2).map(User::getId).orElse(jemand_id);
        if(func.getRandom(0, 1) == 0) {
            users = new long[]{user1.getId(), user2};
        } else {
            users = new long[]{user2, user1.getId()};
        }
        message = leer[1] + " " + leer[2] + " " + leer[3] + "\n"
                + leer[4] + " " + leer[5] + " " + leer[6] + "\n"
                + leer[7] + " " + leer[8] + " " + leer[9];

        for (int i = 0; i < 2; i++) {
            ai[i] = users[i] == jemand_id;
        }
        rounds = 0;
    }

    public String getPlayers() {
        return ":regional_indicator_x: -> <@" + users[0] + ">\n:o2: -> <@" + users[1] + ">";
    }

    public String setMessage(Message message) {
        m = message;
        if(ai[0] && rounds == 0) doAiRound();
        return this.message;
    }

    public String getMessage() {
        return message;
    }

    public boolean isEnded() {
        return state != 0;
    }

    public boolean noAi() {
        return !ai[0] && !ai[1];
    }

    public void doRound(ReactionAddEvent event2) {
        if(state == 0) {
            if (m == null  && event2.getMessage().isPresent()) m = event2.getMessage().orElse(m);
            if (event2.getUserId() == users[rounds % 2]) {
                String ttt1;
                for (int i = 0; i < 10; i++) {
                    if (event2.getEmoji().equalsEmoji(EmojiParser.parseToUnicode(zahl[i]))) {
                        ttt1 = leer[i];
                        String MContent = m.getEmbeds().get(0).getDescription().get();
                        if (MContent.toLowerCase().contains(ttt1.toLowerCase())) {
                            m.removeReactionsByEmoji(EmojiParser.parseToUnicode(zahl[i]));
                            message = MContent.replace(ttt1, emojis[rounds%2]);
                            rounds++;
                        }
                        break;
                    }
                }
            } else {
                if (event2.getUserId() != event2.getApi().getYourself().getId()) {
                    event2.removeReaction();
                }
            }
            check();
        }
    }

    public void doAiRound() {
        String output = "";
        String own_emoji = O.matcher(X.matcher(emojis[rounds%2]).replaceAll("x")).replaceAll("o");
        String geg_emoji = O.matcher(X.matcher(emojis[(rounds +1)%2]).replaceAll("x")).replaceAll("o");
        String[] tttArr = NEW_LINE.matcher(O.matcher(X.matcher(message).replaceAll("x")).replaceAll("o")).replaceAll(" ").split(" ");
        String field_text = //horizontal:
                O.matcher(X.matcher(message).replaceAll("x")).replaceAll("o") + "\n"
                //vertikal:
                + tttArr[0] + " " + tttArr[3] + " " + tttArr[6] + "\n"
                + tttArr[1] + " " + tttArr[4] + " " + tttArr[7] + "\n"
                + tttArr[2] + " " + tttArr[5] + " " + tttArr[8] + "\n"
                //diagonal:
                + tttArr[0] + " " + tttArr[4] + " " + tttArr[8] + "\n"
                + tttArr[2] + " " + tttArr[4] + " " + tttArr[6] + "\n";

        String[] rows = field_text.split("\n");
        String[][] field = new String[rows.length][3];
        for (int i = 0; i < rows.length; i++) {
            field[i] = rows[i].split(" ");
        }
        Map<String, Integer> score = new HashMap<>(9);
        int s;
        for (int j = 0; j < field.length; j++) {
            for (int k = 0; k < field[j].length; k++) {
                if(field[j][k].length() != 1) {
                    if(score.containsKey(field[j][k])) s = score.get((field[j][k]));
                    else s = 0;
                    int count_geg = func.countOfStringInString(rows[j], geg_emoji);
                    int count_own = func.countOfStringInString(rows[j], own_emoji);
                    if(count_geg > 1) s += 100;
                    else if(count_own > 1) s += 10000;

                    if(count_geg == 1 && count_own == 1) {
                        if(j > rows.length - 3
                                && (field[j][1].equals("x") || !field[j][1].equals("o"))
                                && (rows[j].contains(geg_emoji +" <") || rows[j].contains("> " + geg_emoji)))
                            s += 4;
                        else s-= 5;
                    } else {
                        if (count_geg == 1) s += 1;
                        if (count_own == 1) s += 1;
                    }

                    score.put(field[j][k], s);
                }
            }
        }
        Map.Entry<String, Integer> maxEntry = null;
        for (Map.Entry<String, Integer> entry : score.entrySet()) {
            if (maxEntry == null) {
                maxEntry = entry;
            } else {
                int i = entry.getValue().compareTo(maxEntry.getValue());
                if(i > 0) maxEntry = entry;
                else if(i == 0) if(func.getRandom(0, 1) == 0) maxEntry = entry;
            }
        }
        String MContent = message;
        if (maxEntry != null && MContent.toLowerCase().contains(maxEntry.getKey().toLowerCase())) {
            for (int i = 0; i < leer.length; i++) {
                if(leer[i].equals(maxEntry.getKey())) m.removeReactionsByEmoji(EmojiParser.parseToUnicode(zahl[i]));
            }
            message = MContent.replace(maxEntry.getKey(), emojis[rounds%2]);
            rounds++;
            check();
        }
    }

    public void check() {
        String MContent = message;
        if (MContent.contains("\uD83C\uDDFD \uD83C\uDDFD \uD83C\uDDFD")) {
            state = 1;
        } else if (MContent.contains(":o2: :o2: :o2:")) {
            state = 2;
        } else {
            String[] tttArr = NEW_LINE.matcher(O.matcher(X.matcher(MContent).replaceAll("x")).replaceAll("o")).replaceAll(" ").split(" ");
            //0 1 2
            //3 4 5
            //6 7 8
            if ((tttArr[0].equalsIgnoreCase(tttArr[4]) && tttArr[4].equalsIgnoreCase(tttArr[8])) || (tttArr[6].equalsIgnoreCase(tttArr[4]) && tttArr[2].equalsIgnoreCase(tttArr[4]))) {
                if (tttArr[4].equalsIgnoreCase("x"))
                    state = 1;
                else state = 2;
            } else {
                for (int i = 0; i < 3; i++) {
                    if (tttArr[i].equalsIgnoreCase(tttArr[i + 3]) && tttArr[i].equalsIgnoreCase(tttArr[i + 6])) {
                        if (tttArr[i].equalsIgnoreCase("x"))
                            state = 1;
                        else state = 2;
                    }
                }
            }
            if (!MContent.contains("0") && state == 0) {
                state = 3;
            }
        }
        if(state != 0) {
            if(state == 3) {
                m.edit(m.getEmbeds().get(0).toBuilder().setDescription(message).addField("\u200B", texte.getString("4GUnentschieden").toString()));
                if(noAi()) func.addGame0("ttt", users[0], users[1]);
            } else {
                long[] wl = new long[2];
                if(state == 1) wl = users;
                else {
                    wl[0] = users[1];
                    wl[1] = users[0];
                }
                m.edit(m.getEmbeds().get(0).toBuilder().setDescription(message).addField("\u200B", "<@" + wl[0] + "> (" + emojis[state -1] + ") " + texte.getString("4GGewonnen").toString()));
                if(noAi()) func.addGame("ttt", wl[0], wl[1]);
            }
            m.removeAllReactions().join();
            befehl.addRerun(m);
            //add rerun
        } else if(ai[rounds %2]) {
            doAiRound();
        }
    }
}