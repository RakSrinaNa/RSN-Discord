package fr.raksrinana.rsndiscord.commands.config.guild.warns;

import fr.raksrinana.rsndiscord.commands.config.helpers.WarnConfigurationCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.guild.warns.WarnConfiguration;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

public class MegaWarnConfigurationCommand extends WarnConfigurationCommand{
	public MegaWarnConfigurationCommand(final Command parent){
		super(parent);
	}
	
	@Nonnull
	@Override
	protected Optional<WarnConfiguration> getConfig(final Guild guild){
		return Settings.getConfiguration(guild).getWarnsConfiguration().getMegaWarn();
	}
	
	@Override
	protected void removeConfig(@Nonnull final Guild guild){
		Settings.getConfiguration(guild).getWarnsConfiguration().setMegaWarn(null);
	}
	
	@Override
	protected void createConfig(@Nonnull final Guild guild, @Nonnull final Role role, final long delay){
		Settings.getConfiguration(guild).getWarnsConfiguration().setMegaWarn(new WarnConfiguration(role, delay));
	}
	
	@Nonnull
	@Override
	public String getName(){
		return "Mega warn";
	}
	
	@Nonnull
	@Override
	public List<String> getCommandStrings(){
		return List.of("megaWarn");
	}
}
