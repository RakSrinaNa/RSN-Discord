package fr.raksrinana.rsndiscord.modules.settings.command.guild;

import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.commands.generic.CommandComposite;
import fr.raksrinana.rsndiscord.modules.permission.IPermission;
import fr.raksrinana.rsndiscord.modules.settings.command.guild.anilist.MediaChangeChannelConfigurationCommand;
import fr.raksrinana.rsndiscord.modules.settings.command.guild.anilist.NotificationChannelConfigurationCommand;
import fr.raksrinana.rsndiscord.modules.settings.command.guild.anilist.ThaChannelConfigurationCommand;
import fr.raksrinana.rsndiscord.modules.settings.command.guild.anilist.ThaUserConfigurationCommand;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import java.util.List;
import static fr.raksrinana.rsndiscord.modules.permission.PermissionUtils.ALLOW;

public class AniListConfigurationCommandComposite extends CommandComposite{
	public AniListConfigurationCommandComposite(final Command parent){
		super(parent);
		this.addSubCommand(new NotificationChannelConfigurationCommand(this));
		this.addSubCommand(new ThaChannelConfigurationCommand(this));
		this.addSubCommand(new ThaUserConfigurationCommand(this));
		this.addSubCommand(new MediaChangeChannelConfigurationCommand(this));
	}
	
	@Override
	public @NonNull IPermission getPermission(){
		return ALLOW;
	}
	
	@NonNull
	@Override
	public String getName(@NonNull Guild guild){
		return "AniList";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("al", "anilist");
	}
	
	@NonNull
	@Override
	public String getDescription(@NonNull Guild guild){
		return "AniList configurations";
	}
}