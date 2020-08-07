package fr.raksrinana.rsndiscord.utils.trakt.model.users.history;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.rsndiscord.utils.json.ISO8601ZonedDateTimeDeserializer;
import fr.raksrinana.rsndiscord.utils.json.URLDeserializer;
import fr.raksrinana.rsndiscord.utils.themoviedb.model.TVDetails;
import fr.raksrinana.rsndiscord.utils.trakt.TraktObject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import java.net.URL;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@Getter
public class Show implements TraktObject{
	@JsonProperty("title")
	private String title;
	@JsonProperty("year")
	private int year;
	@JsonProperty("ids")
	private MediaIds ids;
	@JsonProperty("overview")
	private String overview;
	@JsonProperty("first_aired")
	@JsonDeserialize(using = ISO8601ZonedDateTimeDeserializer.class)
	private ZonedDateTime firstAired;
	@JsonProperty("airs")
	private Airing airs;
	@JsonProperty("runtime")
	private int runtime;
	@JsonProperty("country")
	private String country;
	@JsonProperty("trailer")
	@JsonDeserialize(using = URLDeserializer.class)
	private URL trailer;
	@JsonProperty("homepage")
	@JsonDeserialize(using = URLDeserializer.class)
	private URL homepage;
	@JsonProperty("status")
	private String status;
	@JsonProperty("rating")
	private double rating;
	@JsonProperty("votes")
	private int votes;
	@JsonProperty("comment_count")
	private int commentCount;
	@JsonProperty("network")
	private String network;
	@JsonProperty("updated_at")
	@JsonDeserialize(using = ISO8601ZonedDateTimeDeserializer.class)
	private ZonedDateTime updatedAt;
	@JsonProperty("language")
	private String language;
	@JsonProperty("genres")
	private Set<String> genres;
	@JsonProperty("aired_episodes")
	private int airedEpisodes;
	
	@Override
	public void fillEmbed(@NonNull Guild guild, @NonNull EmbedBuilder builder){
		fillEmbed(guild, builder, null);
	}
	
	public void fillEmbed(@NonNull Guild guild, @NonNull EmbedBuilder builder, TVDetails tvDetails){
		builder.addField(translate(guild, "trakt.title"), this.getTitle(), true);
		builder.addField(translate(guild, "trakt.year"), Integer.toString(this.getYear()), true);
		Optional.ofNullable(tvDetails).map(TVDetails::getNumberOfSeasons).ifPresent(numberOfSeasons -> builder.addField(translate(guild, "trakt.seasons"), Integer.toString(numberOfSeasons), true));
		builder.addField(translate(guild, "trakt.episodes"), Integer.toString(this.getAiredEpisodes()), true);
		builder.addField(translate(guild, "trakt.status"), this.getStatus(), true);
		builder.addField(translate(guild, "trakt.genres"), String.join(", ", this.getGenres()), true);
		builder.addField(translate(guild, "trakt.overview"), this.getOverview(), false);
	}
	
	@Override
	public URL getUrl(){
		return this.getTrailer();
	}
	
	@Override
	public int compareTo(@NonNull TraktObject o){
		if(o instanceof Show){
			return getTitle().compareTo(((Show) o).getTitle());
		}
		return 0;
	}
	
	@Override
	public int hashCode(){
		return Objects.hash(getIds());
	}
	
	@Override
	public boolean equals(Object o){
		if(this == o){
			return true;
		}
		if(o == null || getClass() != o.getClass()){
			return false;
		}
		Show show = (Show) o;
		return Objects.equals(getIds(), show.getIds());
	}
}
