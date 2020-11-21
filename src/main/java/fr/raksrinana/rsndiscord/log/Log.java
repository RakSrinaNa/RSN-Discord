package fr.raksrinana.rsndiscord.log;

import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class Log{
	private static final Map<Guild, Logger> LOGGERS = new ConcurrentHashMap<>();
	private static final Logger NO_GUILD = LoggerFactory.getLogger("No Guild");
	
	@NonNull
	public static Logger getLogger(final Guild guild){
		if(Objects.isNull(guild)){
			return NO_GUILD;
		}
		return LOGGERS.computeIfAbsent(guild, key -> LoggerFactory.getLogger(key.getName()));
	}
}