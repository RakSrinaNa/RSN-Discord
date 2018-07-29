package fr.mrcraftcod.gunterdiscord.listeners;

import fr.mrcraftcod.gunterdiscord.settings.configs.BannedRegexConfig;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.Utilities;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.GenericMessageEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.MessageUpdateEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static fr.mrcraftcod.gunterdiscord.utils.log.Log.getLogger;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-04-15
 */
public class BannedRegexeMessageListener extends ListenerAdapter{
	@Override
	public void onMessageReceived(MessageReceivedEvent event){
		super.onMessageReceived(event);
		verify(event, event.getMessage());
	}
	
	@Override
	public void onMessageUpdate(MessageUpdateEvent event){
		super.onMessageUpdate(event);
		verify(event, event.getMessage());
	}
	
	/**
	 * Verify a message.
	 *
	 * @param event   The event.
	 * @param message The message to check.
	 */
	private static void verify(GenericMessageEvent event, Message message){
		try{
			if(!Utilities.isTeam(message.getMember())){
				String word = isBanned(event.getGuild(), message.getContentRaw());
				if(word != null){
					Actions.deleteMessage(message);
					Actions.reply(event, "Attention " + message.getAuthor().getAsMention() + ", l'utilisation de mot prohibés va finir par te bruler le derrière.");
					Actions.replyPrivate(event.getGuild(), message.getAuthor(), "Restes poli s'il te plait :). Le mot " + getCensoredWord(word) + " est prohibé.");
					getLogger(event.getGuild()).info("Banned message from user " + Utilities.getUserToLog(message.getAuthor()) + " for word `" + word + "` : " + message.getContentRaw());
				}
			}
		}
		catch(Exception e){
			getLogger(event.getGuild()).error("", e);
		}
	}
	
	/**
	 * Find if the text contains a banned word.
	 *
	 * @param guild The guild to get the banned words from.
	 * @param text  The text.
	 *
	 * @return The banned word found.
	 */
	private static String isBanned(Guild guild, String text){
		text = text.toLowerCase();
		for(String regex : new BannedRegexConfig().getAsList(guild)){
			Matcher matcher = Pattern.compile(regex).matcher(text);
			if(matcher.matches()){
				return matcher.group(0);
			}
		}
		return null;
	}
	
	/**
	 * Get a word censored.
	 *
	 * @param word The word to censor.
	 *
	 * @return The censored word.
	 */
	private static String getCensoredWord(String word){
		if(word.length() > 2){
			StringBuilder sb = new StringBuilder(word);
			sb.replace(1, word.length() - 1, "********************************************");
			return sb.toString();
		}
		return word;
	}
}
