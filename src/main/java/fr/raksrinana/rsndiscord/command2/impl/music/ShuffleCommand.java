package fr.raksrinana.rsndiscord.command2.impl.music;

import fr.raksrinana.rsndiscord.command.CommandResult;
import fr.raksrinana.rsndiscord.command2.base.group.SubCommand;
import fr.raksrinana.rsndiscord.command2.permission.IPermission;
import fr.raksrinana.rsndiscord.music.RSNAudioManager;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.jetbrains.annotations.NotNull;
import static fr.raksrinana.rsndiscord.command2.permission.SimplePermission.FALSE_BY_DEFAULT;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

public class ShuffleCommand extends SubCommand{
	@Override
	@NotNull
	public String getId(){
		return "shuffle";
	}
	
	@Override
	@NotNull
	public String getShortDescription(){
		return "Shuffle the queue";
	}
	
	@Override
	public boolean replyEphemeral(){
		return false;
	}
	
	@Override
	@NotNull
	public IPermission getPermission(){
		return FALSE_BY_DEFAULT;
	}
	
	@Override
	@NotNull
	public CommandResult execute(@NotNull SlashCommandEvent event){
		var guild = event.getGuild();
		
		RSNAudioManager.shuffle(guild);
		JDAWrappers.edit(event, translate(guild, "music.queue.shuffled", event.getUser().getAsMention())).submit();
		return CommandResult.HANDLED;
	}
}
