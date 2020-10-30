package fr.raksrinana.rsndiscord.modules.settings.command.guild;

import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.modules.permission.IPermission;
import fr.raksrinana.rsndiscord.modules.settings.Settings;
import fr.raksrinana.rsndiscord.modules.settings.command.helpers.SetConfigurationCommand;
import fr.raksrinana.rsndiscord.modules.settings.types.RoleConfiguration;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.util.*;
import static fr.raksrinana.rsndiscord.modules.permission.PermissionUtils.ALLOW;

public class AutoRolesConfigurationCommand extends SetConfigurationCommand<RoleConfiguration>{
	public AutoRolesConfigurationCommand(final Command parent){
		super(parent);
	}
	
	@Override
	public @NonNull IPermission getPermission(){
		return ALLOW;
	}
	
	@NonNull
	@Override
	protected Optional<Set<RoleConfiguration>> getConfig(@NonNull final Guild guild){
		return Optional.of(Settings.get(guild).getAutoRoles());
	}
	
	@NonNull
	@Override
	protected RoleConfiguration extractValue(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args) throws IllegalArgumentException{
		if(event.getMessage().getMentionedRoles().isEmpty()){
			throw new IllegalArgumentException("Please mention a role");
		}
		return new RoleConfiguration(event.getMessage().getMentionedRoles().get(0));
	}
	
	@Override
	protected void removeConfig(@NonNull final Guild guild, @NonNull final RoleConfiguration value){
		Settings.get(guild).getAutoRoles().remove(value);
	}
	
	@Override
	protected void createConfig(@NonNull final Guild guild, @NonNull final RoleConfiguration value){
		final var set = new HashSet<RoleConfiguration>();
		set.add(value);
		Settings.get(guild).setAutoRoles(set);
	}
	
	@Override
	public void addHelp(@NonNull final Guild guild, @NonNull final EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("Role", "The role to add or remove", false);
	}
	
	@NonNull
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " [role]";
	}
	
	@NonNull
	@Override
	public String getName(@NonNull Guild guild){
		return "Auto roles";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("autoRoles");
	}
}