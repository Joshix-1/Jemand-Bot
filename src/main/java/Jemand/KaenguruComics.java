package Jemand;

import com.goebl.david.Response;
import com.goebl.david.Webb;
import org.javacord.api.DiscordApi;
import org.javacord.api.util.logging.ExceptionLogger;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class KaenguruComics {
    static final private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    static final private String fileName = "last_date.txt";

    private final DiscordApi api;

    public KaenguruComics(DiscordApi api) {
        this.api = api;
    }

    public void start() {
        api.getThreadPool().getScheduler().scheduleAtFixedRate(this::check, 2, 29, TimeUnit.MINUTES);
    }

    private void check() {
        Calendar calendar = lastDeliveredDate();
        calendar.add(Calendar.DAY_OF_MONTH, 1);

        if (test(calendar)) {
            sendComic(calendar);
            func.writetexttofile(dateFormat.format(calendar), fileName);
        } else {
            calendar.add(Calendar.DAY_OF_MONTH, 1);

            if (test(calendar)) {
                sendComic(calendar);
                func.writetexttofile(dateFormat.format(calendar), fileName);
            }
        }
    }

    private void sendComic(Calendar date) {
        api.getServerTextChannelById(784760963575447563L).ifPresent(channel -> {
            channel.sendMessage(", <@&796416266696785920>:\n" +
                    getComicUrl(date)).thenAccept(message -> {
                        message.crossPost().exceptionally(ExceptionLogger.get());
                        message.addReactions("wit:577887998310613032", "zig:577888095735906305").exceptionally(ExceptionLogger.get());
            });
        });
    }

    private String getComicUrl(Calendar date) {
        return String.format("https://img.zeit.de/administratives/kaenguru-comics/%04d-%02d/%02d/original"
                , date.get(Calendar.YEAR), date.get(Calendar.MONTH) + 1, date.get(Calendar.DAY_OF_MONTH));
    }

    private boolean test(Calendar date) {
        String url = getComicUrl(date);
        Webb webb = Webb.create();
        Response<String> response = webb.get(url).asString();
        return response.isSuccess();
    }

    private Calendar lastDeliveredDate() {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dateFormat.parse(func.readtextoffile(fileName)));
            return calendar;
        } catch (ParseException e) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            return calendar;
        }
    }
}
