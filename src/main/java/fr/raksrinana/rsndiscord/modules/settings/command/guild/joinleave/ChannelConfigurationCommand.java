package fr.raksrinana.rsndiscord.modules.settings.command.guild.joinleave;

import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.modules.permission.IPermission;
import fr.raksrinana.rsndiscord.modules.settings.Settings;
import fr.raksrinana.rsndiscord.modules.settings.types.ChannelConfiguration;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import java.util.List;
import java.util.Optional;
import static fr.raksrinana.rsndiscord.modules.permission.PermissionUtils.ALLOW;

public class ChannelConfigurationCommand extends fr.raksrinana.rsndiscord.modules.settings.command.helpers.ChannelConfigurationCommand{
	public ChannelConfigurationCommand(final Command parent){
		super(parent);
	}
	
	@Override
	protected void setConfig(@NonNull final Guild guild, @NonNull final ChannelConfiguration value){
		Settings.get(guild).getJoinLeaveConfiguration().setChannel(value);
	}
	
	@Override
	protected void removeConfig(@NonNull final Guild guild){
		Settings.get(guild).getJoinLeaveConfiguration().setChannel(null);
	}
	
	@NonNull
	@Override
	protected Optional<ChannelConfiguration> getConfig(@NonNull final Guild guild){
		return Settings.get(guild).getJoinLeaveConfiguration().getChannel();
	}
	
	@Override
	public @NonNull IPermission getPermission(){
		return ALLOW;
	}
	
	@NonNull
	@Override
	public String getName(@NonNull Guild guild){
		return "Announce channel";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("channel");
	}
}
