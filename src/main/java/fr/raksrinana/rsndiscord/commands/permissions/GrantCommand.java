package fr.raksrinana.rsndiscord.commands.permissions;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.permission.Permission;
import fr.raksrinana.rsndiscord.utils.permission.SimplePermission;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.util.LinkedList;
import java.util.List;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

public class GrantCommand extends BasicCommand{
	public GrantCommand(Command parent){
		super(parent);
	}
	
	@Override
	public void addHelp(@NonNull Guild guild, @NonNull EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("permission", translate(guild, "command.permissions.grant.help.permission"), false);
		builder.addField("entity", translate(guild, "command.permissions.grant.help.entity"), false);
	}
	
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		super.execute(event, args);
		if(args.isEmpty() || args.size() == event.getMessage().getMentions().size()){
			return CommandResult.BAD_ARGUMENTS;
		}
		var permissionsConfiguration = Settings.get(event.getGuild()).getPermissionsConfiguration();
		String permissionId = args.poll();
		event.getMessage().getMentionedUsers().forEach(user -> permissionsConfiguration.grant(user, permissionId));
		event.getMessage().getMentionedRoles().forEach(role -> permissionsConfiguration.grant(role, permissionId));
		Actions.reply(event, translate(event.getGuild(), "permissions.granted", permissionId), null);
		return CommandResult.SUCCESS;
	}
	
	@Override
	public @NonNull String getCommandUsage(){
		return super.getCommandUsage() + " <permission> <@entity...>";
	}
	
	@Override
	public @NonNull Permission getPermission(){
		return new SimplePermission("command.permissions.grant", false);
	}
	
	@NonNull
	@Override
	public String getName(@NonNull Guild guild){
		return translate(guild, "command.permissions.grant.name");
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("grant", "g");
	}
	
	@NonNull
	@Override
	public String getDescription(@NonNull Guild guild){
		return translate(guild, "command.permissions.grant.description");
	}
}
