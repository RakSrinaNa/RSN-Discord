package fr.raksrinana.rsndiscord.utils.anilist.notifications;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.raksrinana.rsndiscord.utils.anilist.AniListUtils;
import fr.raksrinana.rsndiscord.utils.anilist.media.Media;
import lombok.Getter;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import java.awt.Color;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeName("AIRING")
@Getter
public class AiringNotification extends Notification{
	private static final String QUERY = "AiringNotification {\n" + "id\n" + "type\n" + "episode\n" + "createdAt\n" + Media.getQUERY() + "\n}";
	@JsonProperty("episode")
	private int episode;
	@JsonProperty("media")
	private Media media;
	
	public AiringNotification(){
		super(NotificationType.AIRING);
	}
	
	@Override
	public void fillEmbed(@NonNull final EmbedBuilder builder){
		builder.setTimestamp(this.getDate());
		builder.setColor(Color.GREEN);
		builder.setTitle("New release", this.getMedia().getUrl().toString());
		builder.addField("Episode", String.valueOf(this.getEpisode()), true);
		builder.addBlankField(false);
		builder.addField("Media:", "", false);
		this.getMedia().fillEmbed(builder);
	}
	
	@Override
	@NonNull
	public URL getUrl(){
		return Optional.of(this.getMedia()).map(Media::getUrl).orElse(AniListUtils.FALLBACK_URL);
	}
	
	@Override
	public int hashCode(){
		return this.getEpisode();
	}
	
	@Override
	public boolean equals(final Object obj){
		if(!(obj instanceof AiringNotification)){
			return false;
		}
		final var notification = (AiringNotification) obj;
		return Objects.equals(notification.getEpisode(), this.getEpisode()) && Objects.equals(notification.getMedia(), this.getMedia());
	}
	
	@Override
	public String toString(){
		return ToStringBuilder.reflectionToString(this);
	}
	
	public static String getQuery(){
		return QUERY;
	}
}