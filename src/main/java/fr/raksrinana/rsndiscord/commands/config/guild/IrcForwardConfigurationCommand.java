package fr.raksrinana.rsndiscord.commands.config.guild;

import fr.raksrinana.rsndiscord.commands.config.helpers.BooleanConfigurationCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.settings.Settings;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import java.util.List;
import java.util.Optional;

public class IrcForwardConfigurationCommand extends BooleanConfigurationCommand{
	public IrcForwardConfigurationCommand(final Command parent){
		super(parent);
	}
	
	@Override
	protected void setConfig(@NonNull final Guild guild, @NonNull final Boolean value){
		Settings.get(guild).setIrcForward(value);
	}
	
	@Override
	protected void removeConfig(@NonNull final Guild guild){
		Settings.get(guild).setIrcForward(false);
	}
	
	@NonNull
	@Override
	protected Optional<Boolean> getConfig(final Guild guild){
		return Optional.of(Settings.get(guild).getIrcForward());
	}
	
	@NonNull
	@Override
	public String getName(){
		return "IRC message forwarding";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("ircForward");
	}
}
