package Jemand;

import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.json.simple.JSONObject;

import java.util.Map;

// <USER_DISC_NAME>
// <USER_DISP_NAME>
// "J!" (Prefix)
// <VAR1>
// <VAR2>
// <USER_MENTION>

public class Texte {
    private JSONObject sprachliste;
    private String sprache;
    private User user;
    private Server server;
    private String output;
    static final private String standard = "en";

    public Texte (User user, Server server) {
        this.user = user;
        this.server = server;
        reloadSprache();
    }

    public Texte(MessageCreateEvent event) {
        this.user = event.getMessageAuthor().asUser().orElse(null);
        this.server = event.getServer().orElse(null);
    }

    public Texte (User user) {
        this.user = user;
        this.server = null;
        reloadSprache();
    }

    private void reloadSprache() {
        sprachliste = func.JsonFromFile("sprachliste.json");
        if(user != null && sprachliste.containsKey(user.getIdAsString())) {
            sprache = (String) sprachliste.get(user.getIdAsString());
        } else {
            sprache = standard; //"en"
        }
    }

    private void setSprache(String Sprache) {
        sprachliste.put(user.getIdAsString(), Sprache);
        func.JsonToFile(sprachliste, "sprachliste.json");
        sprache = Sprache;
    }

    public Boolean putSprache(String Sprache) {
        if(Sprache.toLowerCase().contains("en")) {
            setSprache("en");
            return true;
        } else if(Sprache.toLowerCase().contains("ger") || Sprache.toLowerCase().contains("de")) {
            setSprache("de");
            return true;
        }
        return false;
    }

    public String getSprache() {
        reloadSprache();
        return sprache;
    }


    public Texte getString(String name) {
        reloadSprache();
        Map<String, Map<String, String>> map = func.JsonFromFile("texte.json");
        try {
            output = map.get(name).getOrDefault(sprache, map.get("FehlerAufgetreten").get(sprache));
        } catch (Exception e) {
            func.handle(e);
            output = map.get("FehlerAufgetreten").get(sprache);
        }

        if(!func.StringBlank(output)) {
            output = func.replaceRandom(output);
            output = output.replace("<RAND_NUM>", Integer.toString(func.getRandom(120, 1200)));
            if (user != null) {
                output = output.replace("<USER_DISC_NAME>", user.getDiscriminatedName()).replace("<USER_MENTION>", user.getMentionTag()).replace("<USER_AVATAR_LINK>", user.getAvatar().getUrl().toString());
                if (server != null) output = output.replace("<USER_DISP_NAME>", user.getDisplayName(server));
                JSONObject prefixjs = func.JsonFromFile("prefix.json");
                if (server != null && prefixjs.containsKey(server.getIdAsString())) {
                    output = output.replace("J!", (String) prefixjs.get(server.getIdAsString()));
                }
            }
        }
        return this;
    }

    Texte getString(String Name, String VAR1) {
        return getString(Name).replace("<VAR1>", VAR1);
    }

    Texte getString(String Name, String VAR1, String VAR2) {
        return getString(Name).replace("<VAR1>", VAR1).replace("<VAR2>", VAR2);
    }

     private Texte getString(String Name, String VAR1, String VAR2, String VAR3) {
        return getString(Name).replace("<VAR1>", VAR1).replace("<VAR2>", VAR2).replace("<VAR3>", VAR3);
    }
    Texte getString(String Name, String VAR1, String VAR2, String VAR3, String VAR4) {
        return getString(Name).replace("<VAR1>", VAR1).replace("<VAR2>", VAR2).replace("<VAR3>", VAR3).replace("<VAR4>", VAR4);
    }


    public String get(String name) {
        return getString(name).toString();
    }

    public String get(String Name, String VAR1) {
        return getString(Name, VAR1).toString();
    }

    public String get(String Name, String VAR1, String VAR2) {
        return getString(Name, VAR1, VAR2).toString();
    }

    public String get(String Name, String VAR1, String VAR2, String VAR3) {
        return getString(Name, VAR1, VAR2, VAR3).toString();
    }
    public String get(String Name, String VAR1, String VAR2, String VAR3, String VAR4) {
        return getString(Name, VAR1, VAR2, VAR3, VAR4).toString();
    }


    public Texte replace(String Target, String replacement) {
        output = output.replace(Target, replacement);
        return this;
    }

    public String toString() {
        if(output == null || output.isEmpty()) output = get("FehlerAufgetreten");
        return output;
    }
}
