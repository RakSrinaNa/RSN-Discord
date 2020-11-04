package fr.raksrinana.rsndiscord.modules.settings.command.guild;

import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.commands.generic.CommandComposite;
import fr.raksrinana.rsndiscord.modules.permission.IPermission;
import fr.raksrinana.rsndiscord.modules.settings.command.guild.randomkick.KickRoleProbabilityConfigurationCommand;
import fr.raksrinana.rsndiscord.modules.settings.command.guild.randomkick.KickableRolesConfigurationCommand;
import fr.raksrinana.rsndiscord.modules.settings.command.guild.randomkick.KickedRoleConfigurationCommand;
import fr.raksrinana.rsndiscord.modules.settings.command.guild.randomkick.RandomKickRolesPingRolesConfigurationCommand;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;

import java.util.List;

import static fr.raksrinana.rsndiscord.modules.permission.PermissionUtils.ALLOW;

public class RandomKickConfigurationCommandComposite extends CommandComposite{
	public RandomKickConfigurationCommandComposite(final Command parent) {
		super(parent);
		this.addSubCommand(new RandomKickRolesPingRolesConfigurationCommand(this));
		this.addSubCommand(new KickedRoleConfigurationCommand(this));
		this.addSubCommand(new KickableRolesConfigurationCommand(this));
		this.addSubCommand(new KickRoleProbabilityConfigurationCommand(this));
	}
	
	@Override
	public @NonNull IPermission getPermission(){
		return ALLOW;
	}
	
	@NonNull
	@Override
	public String getName(@NonNull Guild guild){
		return "RandomKick";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("randomKick");
	}
	
	@NonNull
	@Override
	public String getDescription(@NonNull Guild guild){
		return "Random kick configuration";
	}
}
