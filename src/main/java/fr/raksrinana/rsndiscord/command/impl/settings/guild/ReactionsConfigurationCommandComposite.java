package fr.raksrinana.rsndiscord.command.impl.settings.guild;

import fr.raksrinana.rsndiscord.command.Command;
import fr.raksrinana.rsndiscord.command.CommandComposite;
import fr.raksrinana.rsndiscord.command.impl.settings.guild.reactions.AutoTodoChannelsConfigurationCommand;
import fr.raksrinana.rsndiscord.command.impl.settings.guild.reactions.SavedForwardingChannelsConfigurationCommand;
import fr.raksrinana.rsndiscord.permission.IPermission;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import static fr.raksrinana.rsndiscord.permission.PermissionUtils.ALLOW;

public class ReactionsConfigurationCommandComposite extends CommandComposite{
	public ReactionsConfigurationCommandComposite(Command parent){
		super(parent);
		addSubCommand(new AutoTodoChannelsConfigurationCommand(this));
		addSubCommand(new SavedForwardingChannelsConfigurationCommand(this));
	}
	
	@Override
	public @NotNull IPermission getPermission(){
		return ALLOW;
	}
	
	@NotNull
	@Override
	public String getName(@NotNull Guild guild){
		return "Reactions";
	}
	
	@NotNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("reactions");
	}
	
	@NotNull
	@Override
	public String getDescription(@NotNull Guild guild){
		return "Reactions configurations";
	}
}
