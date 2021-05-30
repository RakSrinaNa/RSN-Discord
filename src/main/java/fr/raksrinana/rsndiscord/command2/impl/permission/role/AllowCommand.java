package fr.raksrinana.rsndiscord.command2.impl.permission.role;

import fr.raksrinana.rsndiscord.command.CommandResult;
import fr.raksrinana.rsndiscord.command2.SlashCommandService;
import fr.raksrinana.rsndiscord.command2.base.group.SubCommand;
import fr.raksrinana.rsndiscord.utils.Utilities;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;
import org.jetbrains.annotations.NotNull;
import java.util.Collection;
import java.util.Set;
import static fr.raksrinana.rsndiscord.command.CommandResult.NOT_ALLOWED;
import static fr.raksrinana.rsndiscord.command.CommandResult.SUCCESS;
import static net.dv8tion.jda.api.interactions.commands.OptionType.ROLE;
import static net.dv8tion.jda.api.interactions.commands.OptionType.STRING;

public class AllowCommand extends SubCommand{
	public static final String ROLE_OPTION_ID = "role";
	public static final String NAME_OPTION_ID = "name";
	
	@Override
	@NotNull
	public String getId(){
		return "allow";
	}
	
	@Override
	@NotNull
	public String getShortDescription(){
		return "Give permission to a role";
	}
	
	@Override
	protected @NotNull Collection<? extends OptionData> getOptions(){
		return Set.of(
				new OptionData(ROLE, ROLE_OPTION_ID, "Role").setRequired(true),
				new OptionData(STRING, NAME_OPTION_ID, "Permission name").setRequired(true)
		);
	}
	
	@Override
	@NotNull
	public CommandResult execute(@NotNull SlashCommandEvent event){
		if(!isAllowed(event.getMember())){
			return NOT_ALLOWED;
		}
		
		var role = event.getOption(ROLE_OPTION_ID).getAsRole();
		var name = event.getOption(NAME_OPTION_ID).getAsString();
		var privilege = CommandPrivilege.enable(role);
		
		SlashCommandService.getRegistrableCommand(name).ifPresentOrElse(
				command -> command.updateCommandPrivileges(event.getGuild(), privileges -> {
					privileges.remove(privilege);
					privileges.add(privilege);
					return privileges;
				}).thenAccept(empty -> JDAWrappers.replyCommand(event, "Permission allowed").submit()),
				() -> JDAWrappers.replyCommand(event, "Permission not found").submit());
		
		return SUCCESS;
	}
	
	private boolean isAllowed(Member member){
		return member.isOwner() || Utilities.isCreator(member);
	}
}
