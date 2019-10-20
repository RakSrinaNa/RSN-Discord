package fr.raksrinana.rsndiscord.utils.anilist.media;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 2018-10-10.
 *
 * @author Thomas Couchoud
 * @since 2018-10-10
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public enum AniListMediaFormat{TV("TV"), TV_SHORT("TV Short"), MOVIE("Movie"), SPECIAL("Special"), OVA, ONA, MUSIC("Music"), MANGA("Manga"), NOVEL("Novel"), ONE_SHOT("One shot");
	
	private final String display;
	
	AniListMediaFormat(){
		this(null);
	}
	
	AniListMediaFormat(@Nullable final String display){
		this.display = display;
	}
	
	@JsonCreator
	@Nonnull
	public static AniListMediaFormat getFromString(@Nonnull final String value){
		return AniListMediaFormat.valueOf(value);
	}
	
	@Override
	public String toString(){
		return Objects.isNull(this.display) ? this.name() : this.display;
	}}