package fr.raksrinana.rsndiscord.command.impl.permission;

import fr.raksrinana.rsndiscord.command.BotCommand;
import fr.raksrinana.rsndiscord.command.CommandComposite;
import fr.raksrinana.rsndiscord.permission.IPermission;
import fr.raksrinana.rsndiscord.permission.SimplePermission;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import java.util.List;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

@BotCommand
public class PermissionsCommandComposite extends CommandComposite{
	/**
	 * Constructor.
	 */
	public PermissionsCommandComposite(){
		this.addSubCommand(new GrantCommand(this));
		this.addSubCommand(new DenyCommand(this));
		this.addSubCommand(new ResetCommand(this));
		this.addSubCommand(new ListCommand(this));
	}
	
	@Override
	public @NonNull IPermission getPermission(){
		return new SimplePermission("command.permissions", false);
	}
	
	@NonNull
	@Override
	public String getName(@NonNull Guild guild){
		return translate(guild, "command.permissions.name");
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("permissions", "perm");
	}
	
	@NonNull
	@Override
	public String getDescription(@NonNull Guild guild){
		return translate(guild, "command.permissions.description");
	}
}