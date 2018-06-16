package fr.mrcraftcod.gunterdiscord.utils.player;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import fr.mrcraftcod.gunterdiscord.utils.Log;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.managers.AudioManager;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-06-16
 */
public class GunterAudioManager implements StatusTrackSchedulerListener
{
	private static final HashMap<Guild, GunterAudioManager> managers = new HashMap<>();
	private final AudioManager audioManager;
	private final AudioPlayerManager audioPlayerManager;
	private final AudioPlayer audioPlayer;
	private final TrackScheduler trackScheduler;
	private final VoiceChannel channel;
	
	public GunterAudioManager(VoiceChannel channel, AudioManager audioManager, AudioPlayerManager audioPlayerManager, AudioPlayer audioPlayer, TrackScheduler trackScheduler)
	{
		this.channel = channel;
		this.audioManager = audioManager;
		this.audioPlayerManager = audioPlayerManager;
		this.audioPlayer = audioPlayer;
		this.trackScheduler = trackScheduler;
	}
	
	public static void play(VoiceChannel channel, String identifier)
	{
		GunterAudioManager gunterAudioManager = getGunterPlayerManager(channel);
		gunterAudioManager.getAudioPlayerManager().loadItem(identifier, new AudioLoadResultHandler()
		{
			@Override
			public void trackLoaded(AudioTrack track)
			{
				Log.info("Added `%s` to the audio queue on channel %s", identifier, channel.getName());
				gunterAudioManager.getTrackScheduler().queue(track);
			}
			
			@Override
			public void playlistLoaded(AudioPlaylist playlist)
			{
				for(AudioTrack track: playlist.getTracks())
					gunterAudioManager.getTrackScheduler().queue(track);
			}
			
			@Override
			public void noMatches()
			{
				Log.warning("Player found nothing for channel %s", channel.getName());
				gunterAudioManager.getTrackScheduler().foundNothing();
			}
			
			@Override
			public void loadFailed(FriendlyException throwable)
			{
				Log.warning(throwable,"Failed to load audio for channel %s", channel.getName());
				gunterAudioManager.getTrackScheduler().foundNothing();
			}
		});
	}
	
	private static GunterAudioManager getGunterPlayerManager(VoiceChannel channel)
	{
		return managers.computeIfAbsent(channel.getGuild(), g -> {
			AudioManager audioManager = channel.getGuild().getAudioManager();
			audioManager.openAudioConnection(channel);
			AudioPlayerManager audioPlayerManager = new DefaultAudioPlayerManager();
			AudioSourceManagers.registerRemoteSources(audioPlayerManager);
			AudioPlayer audioPlayer = audioPlayerManager.createPlayer();
			audioManager.setSendingHandler(new AudioPlayerSendHandler(audioPlayer));
			TrackScheduler trackScheduler = new TrackScheduler(audioPlayer);
			audioPlayer.addListener(trackScheduler);
			GunterAudioManager gunterAudioManager = new GunterAudioManager(channel, audioManager, audioPlayerManager, audioPlayer, trackScheduler);
			trackScheduler.addStatusTrackSchedulerListener(gunterAudioManager);
			Log.info("Audio manager Created");
			return gunterAudioManager;
		});
	}
	
	public AudioPlayerManager getAudioPlayerManager()
	{
		return audioPlayerManager;
	}
	
	private TrackScheduler getTrackScheduler()
	{
		return trackScheduler;
	}
	
	public static void leave(Guild guild)
	{
		if(managers.containsKey(guild))
			managers.get(guild).onTrackSchedulerEmpty();
	}
	
	@Override
	public void onTrackSchedulerEmpty()
	{
		ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
		executorService.schedule(() -> {
			getAudioPlayerManager().shutdown();
			getAudioManager().setSendingHandler(null);
			getAudioManager().closeAudioConnection();
			managers.put(channel.getGuild(), null);
			Log.info("Audio manager shutdown");
		}, 1, TimeUnit.NANOSECONDS);
		executorService.shutdown();
	}
	
	public AudioManager getAudioManager()
	{
		return audioManager;
	}
	
	public AudioPlayer getAudioPlayer()
	{
		return audioPlayer;
	}
	
	public VoiceChannel getChannel()
	{
		return channel;
	}
}
