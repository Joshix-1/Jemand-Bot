package Jemand;

import com.vdurmont.emoji.EmojiParser;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.reaction.ReactionAddEvent;

import java.util.Arrays;

public class VierGewinnt {
    private static final String[] zahl = {":one:", ":two:", ":three:", ":four:", ":five:", ":six:", ":seven:"};
    private static final String[] emojis = {func.parseLetterToEmote("x"), ":o2:"};
    private static final String blank_row = "<:01:703321359744499813> <:02:703322946650898443> <:03:703322960420667392> <:04:703322984101838881> <:05:703323000560418927> <:06:703323015030636545> <:07:703323031732486405>".replace(' ', '\u200B');
    private static final String[] leer = blank_row.split("\u200B");
    private static String vierX = emojis[0] + "\u200B" + emojis[0] + "\u200B" + emojis[0] + "\u200B" + emojis[0];
    private static String vierO = emojis[1] + "\u200B" + emojis[1] + "\u200B" + emojis[1] + "\u200B" + emojis[1];
    private static String dreiX = emojis[0] + "\u200B" + emojis[0] + "\u200B" + emojis[0];
    private static String dreiO = emojis[1] + "\u200B" + emojis[1] + "\u200B" + emojis[1];
    private long[] users;
    private String[] rows = new String[6];
    private int rounds;
    private Message m;
    private boolean[] ai = new boolean[2];
    private int state; //0 = game is running; 1 = user1 won; 2 = user2 won; 3 = draw
    private Texte texte;
    private Befehl befehl;
    private boolean vier;

    VierGewinnt(Befehl b, User user1, long user2, boolean vier) {
        long jemand_id = func.getApi().getYourself().getId();
        this.vier = vier;
        state = 0;
        befehl = b;
        texte = b.getTexte();
        user2 = b.getServer().getMemberById(user2).map(User::getId).orElse(jemand_id);
        if(func.getRandom(0, 1) == 0)
            users = new long[]{user1.getId(), user2};
        else users = new long[]{user2, user1.getId()};
        for (int i = 0; i < 2; i++)
            ai[i] = users[i] == jemand_id;
        rounds = 0;

        Arrays.fill(rows, blank_row);
    }

    private String getField() {
        StringBuilder sb = new StringBuilder();
        for (int i = rows.length; i-- > 0;) {
            sb.append(rows[i]).append("\n");
        }
        return sb.toString();
    }

    private String getFieldButReplaced(int index, String replacement) {
        StringBuilder sb = new StringBuilder();
        for (int i = rows.length; i-- > 0;) {
            if(i == index) sb.append(replacement);
            else sb.append(rows[i]);
            sb.append("\n");
        }
        return sb.toString();
    }

    String getDisplayField() {
        return getField() + ":one:\u200B:two:\u200B:three:\u200B:four:\u200B:five:\u200B:six:\u200B:seven:";
    }

    String setMessage(Message message) {
        m = message;
        if(ai[0] && rounds == 0) doAiRound();
        return getDisplayField();
    }

    public boolean noAi() {
        return !ai[0] && !ai[1];
    }

    public String getPlayers() {
        return ":regional_indicator_x: -> <@" + users[0] + ">\n:o2: -> <@" + users[1] + ">";
    }
    public boolean isEnded() {
        return state != 0;
    }

    VierGewinnt doMove(ReactionAddEvent event2) {
        if(state == 0) {
            if (m == null  && event2.getMessage().isPresent()) m = event2.getMessage().orElse(m);
            if (event2.getUser().getId() == users[rounds % 2]) {
                for (int i = 0; i < zahl.length; i++) {
                    if (event2.getEmoji().equalsEmoji(EmojiParser.parseToUnicode(zahl[i]))) {
                        for (int j = 0; j < rows.length; j++) {
                            if(rows[j].contains(leer[i])) {
                                rows[j] = rows[j].replace(leer[i], emojis[rounds % 2]);
                                rounds++;
                                break;
                            }
                        }
                        break;
                    }
                }
            }
            event2.removeReaction();
            check();
        }
        return this;
    }

    private void doAiRound() {
        if(!isEnded()) {
            int[] points = new int[7];
            Arrays.fill(points, 0);
            int s;
            int index = rounds % 2;
            int geg_index = (index * -1) + 1;
            String str;

            for (int i = 0; i < 7; i++) {
                for (int j = 0; j < rows.length; j++) {
                    if (rows[j].contains(leer[i])) {
                        if(!vier && j == 0) points[i] += 2 - func.differenceOf(i, 3);
                        points[i] -= (j + func.differenceOf(i, 3))/2;


                        str = getFieldButReplaced(j, rows[j].replace(leer[i], emojis[index]));
                        s = checkState(str, vier);
                        if (s != 0) points[i] += 100000;

                        //if(s != 0) func.sendOwner(str + "\n\n" + s, null);
                        if(vier) {
                            s = checkState(str, false);
                            if (s != 0) points[i] += 20;
                        }

                        str = getFieldButReplaced(j, rows[j].replace(leer[i], emojis[geg_index]));
                        s = checkState(str, vier);
                        if (s != 0) points[i] += 10000;

                        //if(s != 0) func.sendOwner(str + "\n\n" + s, null);
                        if(vier) {
                            s = checkState(str, false);
                            if (s == geg_index + 1) points[i] += 50;
                        }


                        if (j + 1 < rows.length) {
                            str = getFieldButReplaced(j + 1, rows[j + 1].replace(leer[i], emojis[index]));
                            s = checkState(str, vier);
                            if (s != 0) points[i] -= 50;

                            if(vier) {
                                s = checkState(str, false);
                                if (s != 0) points[i] += 20;
                            }

                            str = getFieldButReplaced(j + 1, rows[j + 1].replace(leer[i], emojis[geg_index]));
                            s = checkState(str, vier);
                            if (s == geg_index + 1) points[i] -= 300;

                            if(vier) {
                                s = checkState(str, false);
                                if (s == geg_index + 1) points[i] -= 20;
                            }
                        }
                        break;
                    }
                    if (j == rows.length - 1) {
                        points[i] = Integer.MIN_VALUE;
                    }
                }
            }
            int max_index = 0;
            for (int i = 1; i < points.length; i++) {
                if (points[max_index] < points[i]) max_index = i;
                else if (points[max_index] == points[i]) {
                    if (func.differenceOf(max_index, 3) > func.differenceOf(i, 3) || func.getRandom(0, 3) == 0) {
                        max_index = i;
                    }
                }
            }

            boolean b1 = true;
            do {
                for (int j = 0; j < rows.length; j++) {
                    if (rows[j].contains(leer[max_index])) {
                        rows[j] = rows[j].replace(leer[max_index], emojis[rounds % 2]);
                        b1 = false;
                        break;
                    }
                }
                max_index = func.getRandom(0, 6);
            } while (b1);

            rounds++;

            check();
        }
    }

    void check() {
        if(!getField().contains("0")) state = 3;
        if(state == 0) {
            state = checkState(getField(), vier);
        }
        if(state != 0) {
            if(state == 3) {
                m.edit(m.getEmbeds().get(0).toBuilder().setDescription(getDisplayField()).addField("\u200B", texte.getString("4GUnentschieden").toString()));
                if(noAi() && vier) func.addGame0("4g", users[0], users[1]);
            } else {
                long[] wl = new long[2];
                if(state == 1) wl = users;
                else {
                    wl[0] = users[1];
                    wl[1] = users[0];
                }
                m.edit(m.getEmbeds().get(0).toBuilder().setDescription(getDisplayField()).addField("\u200B", "<@" + wl[0] + "> (" + emojis[state -1] + ") " + texte.getString("4GGewonnen").toString()));
                if(noAi()  && vier) func.addGame("4g", wl[0], wl[1]);
            }
            m.removeAllReactions().join();
            befehl.addRerun(m);
        } else if(ai[rounds %2]) {
            doAiRound();
        }
    }

   private int checkState(String field, boolean vier) {
        //horizontal:
       int s = getStateOf(field, vier);
       if(s != 0) return s;
       //vertikal
       String[][] str = new String[6][7];
       String[] row = func.reverseArr(field.split("\n"));
       for (int i = 0; i < str.length; i++) {
           str[i] = row[i].split("\u200B");
       }

       //vertikal:
       StringBuilder vertikal = new StringBuilder();
       for (int i = 0; i < str[0].length; i++) {
           for (String[] strings : str) {
               vertikal.append(strings[i]).append("\u200B");
           }
           vertikal.append("\n");
       }
       s = getStateOf(vertikal.toString(), vier);
       if(s != 0) return s;

       //diagonal /
       StringBuilder diagonal = new StringBuilder();
       for (int i = 0; i < 3; i++) {
           for (int j = i; j < 6; j++) {
               diagonal.append(str[j][j-i]).append("\u200B");
           }
           diagonal.append("\n");
       }
       for (int i = 1; i < 4; i++) {
           for (int j = 0; j < 7-i; j++) {
                   diagonal.append(str[j][j + i]).append("\u200B");
           }
           diagonal.append("\n");
       }
       s = getStateOf(diagonal.toString(), vier);
       if(s != 0) return s;

       //diagonal \
       diagonal = new StringBuilder();
       for (int i = 0; i < 3; i++) {
           for (int j = 0; j < 6-i; j++) {
               diagonal.append(str[j][(6 - i - 1)-j]).append("\u200B");
           }
       }
       for (int i = 0; i < 3; i++) {
           for (int j = i; j < 6; j++) {
               diagonal.append(str[j][6 - (j - i)]).append("\u200B");
           }
       }
       return getStateOf(diagonal.toString(), vier);
   }

   private int getStateOf(String str, boolean vier) {
        if(vier) {
            if (str.contains(vierX)) return 1;
            else if (str.contains(vierO)) return 2;
        } else {
            if (str.contains(dreiX)) return 1;
            else if (str.contains(dreiO)) return 2;
        }
        return 0;
    }
}
