package Jemand;

import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class Levelroles {
    private Server server;
    private JSONObject jsonObject;
    private Map<Long, Long> map;
    private String filename = "roles" + func.getFileSeparator();
    private String levels_filename = "xp/user_xp_";

    Levelroles(long server_id) throws NoSuchElementException {
        server = func.getApi().getServerById(server_id).orElse(null);
        if (server == null) throw new NoSuchElementException("Server-Object doesn't exist.");
        create();
    }

    Levelroles(Server server1) {
        server = server1;
        create();
    }

    private void create() {
        filename += server.getId() + ".json";
        levels_filename += server.getId() + ".json";
        loadJson();
    }

    public Map<Long, Long> getMap() {
        return map;
    }

    private Map<Long, Long>  getAsMap() {
        Map<Long, Long> map = new HashMap<>(jsonObject.size());
        jsonObject.forEach((key, value) ->
                map.put(Long.parseLong(key.toString()), Long.parseLong(value.toString()))
        );
        return map;
    }

    private void loadJson() {
        jsonObject = func.JsonFromFile(filename);
        map = getAsMap();
    }

    private void saveJson() {
        func.JsonToFile(jsonObject, filename);
        loadJson();
    }

    boolean addRole(long level, Role role) {
        if(role == null) return false;
        return addRole(level, role.getId());
    }

    boolean addRole(long level, long role_id) {
        if(level < 0 || role_id < 1) return false;
        loadJson();
        if(map.size() >= 10) return false;
        AtomicBoolean value = new AtomicBoolean(true);
        jsonObject.forEach((key, val)->{
            if(val.equals(role_id)) {
                value.set(false);
            }
        });
        if(value.get()) {
            jsonObject.put(Long.toString(level), role_id);
            saveJson();
        }
        checkAllUsersRoles();
        return value.get();
    }

    boolean removeRole(long level) {
        if(level < 0) return false;
        loadJson();
        if(jsonObject.containsKey(Long.toString(level))) {
            jsonObject.remove(Long.toString(level));
            saveJson();
            return true;
        } else return false;
    }

    void checkAllUsersRoles() {
        server.getMembers().forEach(this::checkUserRoles);
    }

    void checkUserRoles(User user) {
        try {
            if (!user.isBot()) {
                JSONObject levels = func.JsonFromFile(levels_filename);
                if (levels.containsKey(user.getIdAsString())) {
                    final long level = Long.parseLong(levels.get(user.getIdAsString()).toString());
                    AtomicLong key = new AtomicLong();
                    map.forEach((k, val) -> {
                        key.set(func.getMinPoints(k));
                        if (level >= key.get()) {
                            Role r = user.getApi().getRoleById(val).orElse(null);
                            if(r != null && !server.getRoles(user).contains(r)) {
                                String reason;
                                if(level == key.get()) reason = "User reached level " + level;
                                else reason = "User is above level " + level;
                                server.addRoleToUser(user, r, reason).join();
                            }
                        }
                    });
                }
            }
        } catch(Exception ignored){}
    }
}
