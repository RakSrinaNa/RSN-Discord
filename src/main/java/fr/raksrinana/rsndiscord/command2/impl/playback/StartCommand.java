package fr.raksrinana.rsndiscord.command2.impl.playback;

import fr.raksrinana.rsndiscord.command.CommandResult;
import fr.raksrinana.rsndiscord.command2.base.group.SubCommand;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.jetbrains.annotations.NotNull;
import static fr.raksrinana.rsndiscord.command.CommandResult.HANDLED;
import static fr.raksrinana.rsndiscord.command.CommandResult.HANDLED_NO_MESSAGE;

@Log4j2
public class StartCommand extends SubCommand{
	@Override
	@NotNull
	public String getId(){
		return "start";
	}
	
	@Override
	@NotNull
	public String getShortDescription(){
		return "Start playback";
	}
	
	@Override
	@NotNull
	public CommandResult execute(@NotNull SlashCommandEvent event){
		var member = event.getMember();
		var voiceState = member.getVoiceState();
		
		if(!voiceState.inVoiceChannel()){
			JDAWrappers.edit(event, "You're not in a voice channel...").submit();
			return HANDLED;
		}
		
		var audioManager = event.getGuild().getAudioManager();
		var echoHandler = new EchoHandler();
		
		audioManager.setSendingHandler(echoHandler);
		audioManager.setReceivingHandler(echoHandler);
		audioManager.openAudioConnection(voiceState.getChannel());
		
		return HANDLED_NO_MESSAGE;
	}
}
