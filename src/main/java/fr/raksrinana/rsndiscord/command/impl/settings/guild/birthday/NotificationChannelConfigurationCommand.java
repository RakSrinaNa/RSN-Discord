package fr.raksrinana.rsndiscord.command.impl.settings.guild.birthday;

import fr.raksrinana.rsndiscord.command.Command;
import fr.raksrinana.rsndiscord.command.impl.settings.helpers.ChannelConfigurationCommand;
import fr.raksrinana.rsndiscord.permission.IPermission;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import java.util.Optional;
import static fr.raksrinana.rsndiscord.permission.PermissionUtils.ALLOW;

public class NotificationChannelConfigurationCommand extends ChannelConfigurationCommand{
	public NotificationChannelConfigurationCommand(Command parent){
		super(parent);
	}
	
	@Override
	public @NotNull IPermission getPermission(){
		return ALLOW;
	}
	
	@Override
	protected void setConfig(@NotNull Guild guild, @NotNull ChannelConfiguration value){
		Settings.get(guild).getBirthdays().setNotificationChannel(value);
	}
	
	@Override
	protected void removeConfig(@NotNull Guild guild){
		Settings.get(guild).getBirthdays().setNotificationChannel(null);
	}
	
	@NotNull
	@Override
	protected Optional<ChannelConfiguration> getConfig(@NotNull Guild guild){
		return Settings.get(guild).getBirthdays().getNotificationChannel();
	}
	
	@NotNull
	@Override
	public String getName(@NotNull Guild guild){
		return "Notification channel";
	}
	
	@NotNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("notificationChannel");
	}
}
