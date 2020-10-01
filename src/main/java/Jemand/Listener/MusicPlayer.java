package Jemand.Listener;

import Jemand.func;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import org.javacord.api.DiscordApi;
import org.javacord.api.audio.AudioConnection;
import org.javacord.api.audio.AudioSource;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.util.logging.ExceptionLogger;
import org.javacord.lavaplayerwrapper.LavaplayerAudioSource;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class MusicPlayer {
    private static final Map<Long, MusicPlayer> players = Collections.synchronizedMap(new HashMap<>());

    private final LinkedList<String> videos = new LinkedList<>();
    private final DiscordApi api;
    private long voiceChannelId;
    private final long serverId;
    private final long textChannelId;

    private AudioConnection audioConnection;
    private final AudioPlayerManager playerManager;
    private final AudioPlayer player;
    private AudioSource currentSource = null;
    private String currentSong = "null";


    private MusicPlayer(ServerVoiceChannel channel, ServerTextChannel textChannel) {
        serverId = channel.getServer().getId();
        textChannelId = textChannel.getId();
        api = channel.getApi();
        voiceChannelId = channel.getId();
        audioConnection = channel.connect().exceptionally(ExceptionLogger.get()).join();

        playerManager = new DefaultAudioPlayerManager();
        player = playerManager.createPlayer();
        playerManager.registerSourceManager(new YoutubeAudioSourceManager());

        api.addAudioSourceFinishedListener(event -> {
            if (event.getServer().getId() == serverId) {
                playNext();
            }
        });

        api.addServerVoiceChannelMemberLeaveListener(event -> {
            if (event.getChannel().getId() == voiceChannelId && event.getChannel().getConnectedUserIds().size() == 1) {
                api.getThreadPool().getScheduler().schedule(this::deleteThis, 5, TimeUnit.MINUTES);
            }
        });
    }

    private void deleteThis() {
        if (api.getServerVoiceChannelById(voiceChannelId).map(vc -> vc.getConnectedUserIds().size() == 1).orElse(true)) {
            players.remove(serverId);
            audioConnection.close();
            videos.clear();
        }
    }


    private boolean play(String videoLink) {
        currentSource = new LavaplayerAudioSource(api, player);
        audioConnection.setAudioSource(currentSource);
        playerManager.loadItem(videoLink, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                player.playTrack(track);
                currentSong = videoLink;
                api.getServerTextChannelById(textChannelId).ifPresent(channel -> {
                    channel.sendMessage(getNowPlaying());
                });
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                for (AudioTrack track : playlist.getTracks()) {
                    player.playTrack(track);
                }
            }

            @Override
            public void noMatches() {
                playNext();
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                playNext();
            }
        });
        return true;
    }

    private boolean playNext() {
        if (videos.size() > 0) {
            if (play(videos.get(0))) {
                return true;
            }
        }
        return false;
    }

    private boolean isPlaying() {
        return !(currentSource == null || currentSource.hasFinished());
    }

    private MusicPlayer submitTrack (String arg) {
        videos.addAll(Arrays.asList(func.WHITE_SPACE.split(arg)));
        if (!isPlaying()) {
            playNext();
        }
        return this;
    }

    private void switchVoiceChannel(ServerVoiceChannel channel) {
        //TODO s
        if (channel.getServer().getId() == serverId) {
            voiceChannelId = channel.getId();
            audioConnection = channel.connect().exceptionally(ExceptionLogger.get()).join();
        }
    }

    private boolean userIsInVoice(long userId) {
        return api.getServerById(serverId).flatMap(server -> server.getConnectedVoiceChannel(userId)).map(vc -> vc.getId() == voiceChannelId).orElse(false);
    }

    private String getNowPlaying() {
        return isPlaying() ? "Now playing " + currentSong + "!" : "Playing nothing";
    }

    public static boolean play(Server server, User user, ServerTextChannel textChannel, String arg) {
        ServerVoiceChannel channel = server.getConnectedVoiceChannel(user).orElse(null);

        if (channel == null) return false;

        if (players.containsKey(server.getId())) {
            MusicPlayer player = players.get(server.getId());
            if (!player.userIsInVoice(user.getId())) {
                return false;
            }
            player.submitTrack(arg);
            return true;
        }

        players.put(server.getId(), new MusicPlayer(channel, textChannel).submitTrack(arg));

        return true;
    }

    public static boolean skip (Server server, User user) {
        if (!players.containsKey(server.getId()) || server.getConnectedVoiceChannel(user).isEmpty()) {
            return false;
        }
        MusicPlayer player = players.get(server.getId());

        if (player.videos.size() == 0 || server.getId() != player.serverId || server.getConnectedVoiceChannel(user).map(channel -> channel.getId() == player.voiceChannelId).orElse(false)) {
            return false;
        }
        return player.playNext();
    }
}
