package fr.raksrinana.rsndiscord.utils.anilist.media;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeName("ANIME")
@Getter
public class AnimeMedia extends Media{
	@JsonProperty("episodes")
	private Integer episodes;
	
	public AnimeMedia(){
		super(MediaType.ANIME);
	}
	
	@Override
	@NonNull
	public String getProgressType(final boolean contains){
		return "watched episode";
	}
	
	@Override
	public void fillEmbed(@NonNull final EmbedBuilder builder){
		super.fillEmbed(builder);
		Optional.ofNullable(this.getEpisodes()).map(Object::toString).ifPresent(val -> builder.addField("Episodes", val, true));
		Optional.ofNullable(this.getSeason()).map(Enum::toString).ifPresent(val -> builder.addField("Season", val, true));
	}
	
	@Override
	public String toString(){
		return ToStringBuilder.reflectionToString(this);
	}
	
	@Override
	public Integer getItemCount(){
		return this.getEpisodes();
	}
}