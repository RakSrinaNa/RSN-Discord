package fr.mrcraftcod.gunterdiscord.utils.player;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.core.entities.Guild;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import static fr.mrcraftcod.gunterdiscord.utils.log.Log.getLogger;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-06-16
 */
class TrackScheduler extends AudioEventAdapter{
	private final AudioPlayer player;
	private final BlockingQueue<AudioTrack> queue;
	private final Guild guild;
	private final Set<StatusTrackSchedulerListener> listeners;
	
	/**
	 * @param guild  The guild the scheduler is for.
	 * @param player The audio player this scheduler uses
	 */
	TrackScheduler(final Guild guild, final AudioPlayer player){
		this.guild = guild;
		this.player = player;
		this.queue = new LinkedBlockingQueue<>();
		this.listeners = new LinkedHashSet<>();
	}
	
	/**
	 * Add the next track to queue or play right away if nothing is in the queue.
	 *
	 * @param track The track to play or add to queue.
	 */
	public void queue(final AudioTrack track){
		if(!player.startTrack(track, true)){
			queue.offer(track);
		}
	}
	
	@Override
	public void onTrackStart(final AudioPlayer player, final AudioTrack track){
		super.onTrackStart(player, track);
		listeners.forEach(l -> l.onTrackStart(track));
	}
	
	@Override
	public void onTrackEnd(final AudioPlayer player, final AudioTrack track, final AudioTrackEndReason endReason){
		super.onTrackEnd(player, track, endReason);
		listeners.forEach(l -> l.onTrackEnd(track));
		getLogger(getGuild()).info("Track ended: {}", track.getIdentifier());
		if(endReason.mayStartNext){
			if(!nextTrack()){
				getLogger(getGuild()).info("Playlist ended, listeners: {}", listeners.size());
				listeners.forEach(StatusTrackSchedulerListener::onTrackSchedulerEmpty);
			}
		}
	}
	
	/**
	 * Get the guild the scheduler is associated to.
	 *
	 * @return The guild.
	 */
	private Guild getGuild(){
		return guild;
	}
	
	/**
	 * Tell if a track is available next.
	 *
	 * @return True if a track is available, false else.
	 */
	boolean nextTrack(){
		getLogger(getGuild()).info("Playing next track");
		return player.startTrack(queue.poll(), false);
	}
	
	/**
	 * Add a StatusTrackListener.
	 *
	 * @param statusTrackSchedulerListener The listener to add.
	 */
	void addStatusTrackSchedulerListener(final StatusTrackSchedulerListener statusTrackSchedulerListener){
		listeners.add(statusTrackSchedulerListener);
	}
	
	void foundNothing(){
		getLogger(getGuild()).info("Scheduler nothing found (track: {}, queue: {})", player.getPlayingTrack(), queue.size());
		if(player.getPlayingTrack() == null && queue.isEmpty()){
			listeners.forEach(StatusTrackSchedulerListener::onTrackSchedulerEmpty);
		}
	}
}
