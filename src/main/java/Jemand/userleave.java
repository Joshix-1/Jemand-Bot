package Jemand;

import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.entity.user.UserStatus;
import org.javacord.api.event.server.member.ServerMemberJoinEvent;
import org.javacord.api.event.server.member.ServerMemberLeaveEvent;
import org.json.simple.JSONObject;

import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class userleave {
    private static final Pattern USER = Pattern.compile("(?i)<user>");
    private static final Pattern MEMBERS = Pattern.compile("(?i)<member_count>");
    private static final Pattern USES = Pattern.compile("(?i)<uses>");
    private static final Pattern SERVER = Pattern.compile("(?i)<guild>");
    private func f = new func();
    private String filename = "listeners" + func.getFileSeparator() + "userleave.json";
    private Server server;
    private User user;
    private Boolean activated = false;
    private String message;
    private ServerTextChannel channel;
    private JSONObject js;
    private boolean join;
    private String standardmessage;
    private long uses;


    public userleave(ServerMemberLeaveEvent event, Boolean join) {
        this.join = join;
        this.server = event.getServer();
        this.user = event.getUser();
        getStuff();
    }

    public userleave(ServerMemberJoinEvent event, Boolean join) {
        this.join = join;
        this.server = event.getServer();
        this.user = event.getUser();
        getStuff();
    }

    public userleave(Server server, User user, Boolean join) {
        this.join = join;
        this.server = server;
        this.user = user;
        getStuff();
    }

    public void setChannel(ServerTextChannel channel) {
        if(channel != null) this.channel = channel;
        updateJs();
    }

    private void getStuff() {
        channel = server.getTextChannels().get(0);
        if(join) {
            filename = filename.replace("leave", "join");
            standardmessage = new Texte(user).get("JoinMessage");
        } else {
            filename = filename.replace("join", "leave");
            standardmessage = new Texte(user).get("LeaveMessage");
        }

        js  = func.JsonFromFile(filename);

        if(!js.containsKey(server.getIdAsString())) {
            updateJs(false, standardmessage, 0);
        }
        activated = js.get(server.getIdAsString()).toString().startsWith("t");
        if(activated) {
            message = js.get(server.getIdAsString()).toString().split("\u200B")[1];
            channel = func.getApi().getServerTextChannelById(js.get(server.getIdAsString()).toString().substring(1).split("\u200B")[0]).orElse(channel);
            uses = func.IntFromString(js.get(server.getIdAsString()).toString().substring(1).split("\u200B")[2], 0);
        }
    }

    public void sendMessage() {
        if(activated) {
            addUse();
            String s = SERVER.matcher(USES.matcher(MEMBERS.matcher(message).replaceAll(Integer.toString(server.getMemberCount()))).replaceAll(js.get(server.getIdAsString()).toString().split("\u200B")[2])).replaceAll(server.getName());
            if(join) s = USER.matcher(s).replaceAll(user.getMentionTag());
            else s=USER.matcher(s).replaceAll(Matcher.quoteReplacement(user.getDiscriminatedName()));
            AtomicReference<Integer> online = new AtomicReference<>(server.getMemberCount());
            AtomicReference<Integer> bots = new AtomicReference<>(0);

            server.getMembers().forEach(user1 ->{
                if(user1.getStatus().equals(UserStatus.OFFLINE)) online.set(online.get() - 1); //set online as var with count only, by removing offline.
                if(user1.isBot()) bots.set(bots.get() + 1);
            });
            s = s.replaceAll("(?i)<online_count>", Integer.toString(online.get())).replaceAll("(?i)<offline_count>", Integer.toString(server.getMemberCount() - online.get())).replaceAll("(?i)<bot_count>", Integer.toString(bots.get())).replaceAll("(?i)<user_count>", Integer.toString(server.getMemberCount() - bots.get()));
            channel.sendMessage(s);
            // <online_count> <offline_count> <bot_count> <user_count>
        }
    }

    public void updateJs() {
        js.put(server.getIdAsString(), parse());
        f.JsonToFile(js, filename);
    }

    public void updateJs(Boolean activated, String message, long uses) {
        js.put(server.getIdAsString(), parse(activated, message, uses));
        f.JsonToFile(js, filename);
    }

    public void addUse() {
        uses++;
        updateJs();
    }

    public void setUses(int uses) {
        this.uses = uses;
        updateJs();
    }

    public String parse() {
        return parse(activated, message, uses);
    }

    public String parse(Boolean activated, String message, long uses) {
        String s;
        if(activated) s = "t"; else s = "f"; //wegen true und false
        return s + channel.getId() + "\u200B" + message + "\u200B" + uses;
    }

    public userleave setMessage(String message) {
        this.message = message;
        updateJs();
        return this;
    }

    public String getMessage() {
        return message;
    }

    public Boolean getActivated() {
        activated = js.get(server.getIdAsString()).toString().startsWith("t");
        return activated;
    }
    public userleave setActivated(Boolean activated) {
        if(this.activated == activated) return this;

        if(activated) js.put(server.getIdAsString(), "t" + js.get(server.getIdAsString()).toString().substring(1));
        else js.put(server.getIdAsString(), "t" + js.get(server.getIdAsString()).toString().substring(1));

        this.activated = activated;
        updateJs();
        return this;
    }
}
