package Jemand;

import org.javacord.api.entity.user.User;
import org.json.simple.JSONObject;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Coins {
    private static final String FILE_PATH = "games/coins.json";
    private static BigInteger STANDARD_COINS = new BigInteger("100");
    private static BigInteger MAX_COINS = new BigInteger("10000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000"); //1 Gogool
    private String user_id;

    Coins(User user) {
        user_id = user.getIdAsString();
        if(user.isBot()) user_id = "-1";
    }

    Coins(long user_id) {
        this.user_id = Long.toString(user_id);
        if(func.getApi().getCachedUserById(user_id).map(User::isBot).orElse(false)) this.user_id = "-1";
    }

    BigInteger getCoins() {
        if(user_id.equals("-1")) return BigInteger.ZERO;
        JSONObject coins = func.JsonFromFile(FILE_PATH);
        if(!coins.containsKey(user_id)) {
            setCoins(STANDARD_COINS);
            return STANDARD_COINS;
        }
        return new BigInteger(coins.get(user_id).toString());
    }

    private Coins setCoins(BigInteger coins) {
        JSONObject js = func.JsonFromFile(FILE_PATH);
        js.put(user_id, coins.toString());
        func.JsonToFile(js, FILE_PATH);
        return this;
    }

    boolean hasCoins(BigInteger coins) {
        if(user_id.equals("-1")) return false;
        return getCoins().compareTo(coins) >= 0;
    }

    BigInteger addCoins(BigInteger coins) {
        if(user_id.equals("-1")) return BigInteger.ZERO;
        BigInteger old_coins = getCoins();
        BigInteger new_coins = coins.add(old_coins);
        new_coins = new_coins.min(MAX_COINS);
        setCoins(new_coins);
        return new_coins.subtract(old_coins);
    }

    boolean removeCoins(BigInteger coins) {
        if(hasCoins(coins) && !user_id.equals("-1")) {
            addCoins(coins.multiply(BigInteger.valueOf(-1L)));
            return true;
        }
        return false;
    }

    static List<Map.Entry<String, String>> top(){
        JSONObject js = func.JsonFromFile(FILE_PATH);
        Map<String, String> m = new HashMap<>(js.size());
        js.forEach((key, val) ->{
            m.put(key.toString(), val.toString());
        });

        List<Map.Entry<String, String>> list = new LinkedList<>(m.entrySet());
        list.sort((a,b) -> new BigInteger(b.getValue()).compareTo(new BigInteger(a.getValue())));

        return list;
    }
}
