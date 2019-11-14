package fr.raksrinana.rsndiscord.utils.overwatch.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum OverwatchStatus{
	@JsonEnumDefaultValue UNKNOWN, CONCLUDED, PENDING, IN_PROGRESS;
	private static final Logger LOGGER = LoggerFactory.getLogger(OverwatchStatus.class);
	
	@JsonCreator
	@NonNull
	public OverwatchStatus getFromString(@NonNull final String value){
		try{
			return OverwatchStatus.valueOf(value);
		}
		catch(final IllegalArgumentException e){
			LOGGER.warn("Unknown status {}", value);
		}
		return UNKNOWN;
	}
}
