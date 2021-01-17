package fr.raksrinana.rsndiscord.command.impl.birthday;

import fr.raksrinana.rsndiscord.command.BasicCommand;
import fr.raksrinana.rsndiscord.command.Command;
import fr.raksrinana.rsndiscord.command.CommandResult;
import fr.raksrinana.rsndiscord.permission.IPermission;
import fr.raksrinana.rsndiscord.permission.SimplePermission;
import fr.raksrinana.rsndiscord.settings.Settings;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import static fr.raksrinana.rsndiscord.command.CommandResult.SUCCESS;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

public class ListCommand extends BasicCommand{
	private static final DateTimeFormatter DF = DateTimeFormatter.ISO_LOCAL_DATE;
	
	public ListCommand(Command parent){
		super(parent);
	}
	
	@Override
	public @NonNull IPermission getPermission(){
		return new SimplePermission("command.birthday.list", false);
	}
	
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		super.execute(event, args);
		
		var guild = event.getGuild();
		
		Settings.get(guild).getBirthdays().getBirthdays()
				.forEach((key, birthday) -> key.getUser()
						.ifPresent(user -> {
							var message = translate(guild, "birthday.birthday",
									user.getAsMention(),
									birthday.getDate().format(DF),
									birthday.getDate().until(LocalDate.now()).normalized().getYears());
							event.getChannel().sendMessage(message).submit();
						}));
		return SUCCESS;
	}
	
	@NonNull
	@Override
	public String getName(@NonNull Guild guild){
		return translate(guild, "command.birthday.list.name");
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("list");
	}
	
	@NonNull
	@Override
	public String getDescription(@NonNull Guild guild){
		return translate(guild, "command.birthday.list.description");
	}
}