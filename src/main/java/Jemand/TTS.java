package Jemand;

import marytts.LocalMaryInterface;
import marytts.MaryInterface;
import marytts.exceptions.MaryConfigurationException;
import marytts.exceptions.SynthesisException;
import org.javacord.api.DiscordApi;
import org.javacord.api.audio.AudioConnection;
import org.javacord.api.audio.AudioSource;
import org.javacord.api.audio.AudioTransformer;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.listener.ObjectAttachableListener;
import org.javacord.api.listener.audio.AudioSourceAttachableListener;
import org.javacord.api.listener.audio.AudioSourceFinishedListener;
import org.javacord.api.util.event.ListenerManager;

import javax.sound.sampled.AudioInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TTS {

    public static void play(ServerTextChannel textChannel, User user, String text) {
        play(textChannel, user, text, "de-DE");
    }

    public static void play(ServerTextChannel textChannel, User user, String text, String language) {
        Server server = textChannel.getServer();
        server.getConnectedVoiceChannel(user).ifPresent(vc -> {
            vc.connect().thenAccept(audioConnection -> {
                play(audioConnection, text, language);
            });
        });
    }

    private static MaryInterface maryTts;
    static {
        try {
            maryTts = new LocalMaryInterface();
        } catch (MaryConfigurationException e) {
            e.printStackTrace();
            maryTts = null;
        }
    }

    private static void play(AudioConnection audioConnection, String text, String language) {
        DiscordApi api = audioConnection.getServer().getApi();
        maryTts.setLocale(Locale.forLanguageTag(language));
        try {
            AudioInputStream stream = maryTts.generateAudio(text);
            //AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
            //AudioPlayer player = playerManager.createPlayer();
            //playerManager.registerSourceManager(new LocalAudioSourceManager());
            //audioConnection.setAudioSource(new LavaplayerAudioSource(api, player));

            AudioSource audioSource = new AudioSource() {
                @Override
                public ListenerManager<AudioSourceFinishedListener> addAudioSourceFinishedListener(AudioSourceFinishedListener listener) {
                    return null;
                }

                @Override
                public List<AudioSourceFinishedListener> getAudioSourceFinishedListeners() {
                    return null;
                }

                @Override
                public <T extends AudioSourceAttachableListener & ObjectAttachableListener> Collection<ListenerManager<T>> addAudioSourceAttachableListener(T listener) {
                    return null;
                }

                @Override
                public <T extends AudioSourceAttachableListener & ObjectAttachableListener> void removeAudioSourceAttachableListener(T listener) {

                }

                @Override
                public <T extends AudioSourceAttachableListener & ObjectAttachableListener> Map<T, List<Class<T>>> getAudioSourceAttachableListeners() {
                    return null;
                }

                @Override
                public <T extends AudioSourceAttachableListener & ObjectAttachableListener> void removeListener(Class<T> listenerClass, T listener) {}

                @Override
                public DiscordApi getApi() {
                    return api;
                }

                @Override
                public void addTransformer(AudioTransformer transformer) {}

                @Override
                public boolean removeTransformer(AudioTransformer transformer) {
                    return false;
                }

                @Override
                public List<AudioTransformer> getTransformers() {
                    return null;
                }

                @Override
                public void removeTransformers() {}

                @Override
                public byte[] getNextFrame() {
                    try {
                        return stream.readNBytes((int)stream.getFrameLength());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return new byte[0];
                }

                @Override
                public boolean hasNextFrame() {
                    try {
                        return stream.available() != 0;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return false;
                }

                @Override
                public boolean hasFinished() {
                    return !hasNextFrame();
                }

                @Override
                public void setMuted(boolean muted) {

                }

                @Override
                public boolean isMuted() {
                    return false;
                }

                @Override
                public AudioSource copy() {
                    return null;
                }
            };

            audioConnection.setAudioSource(audioSource);
        } catch (SynthesisException e) {
            e.printStackTrace();
        }
    }
}
