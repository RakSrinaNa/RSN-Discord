package fr.raksrinana.rsndiscord.schedule;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public enum ScheduleTag{
	ANILIST_AIRING_SCHEDULE,
	DELETE_CHANNEL,
	DELETE_MESSAGE,
	NONE,
	REMOVE_ROLE,
	UNBAN_USER;
	
	@JsonCreator
	@NotNull
	public static ScheduleTag getFromString(@NotNull String value){
		return ScheduleTag.valueOf(value);
	}
}
