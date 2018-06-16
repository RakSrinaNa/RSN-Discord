package fr.mrcraftcod.gunterdiscord.commands.config;

import fr.mrcraftcod.gunterdiscord.commands.generic.BasicCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.Command;
import fr.mrcraftcod.gunterdiscord.commands.generic.CommandResult;
import fr.mrcraftcod.gunterdiscord.settings.Configuration;
import fr.mrcraftcod.gunterdiscord.settings.Settings;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.Log;
import fr.mrcraftcod.gunterdiscord.utils.Utilities;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import java.awt.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-05-27
 */
public class ConfigurationCommand extends BasicCommand
{
	private final ChangeConfigType type;
	private final List<String> commands;
	
	/**
	 * Actions available.
	 */
	public enum ChangeConfigType
	{
		ADD, REMOVE, SET, SHOW, ERROR;
		
		/**
		 * Get an action by its name.
		 *
		 * @param action The action to find.
		 *
		 * @return The action or ERROR if not found.
		 */
		public static ChangeConfigType get(String action)
		{
			for(ChangeConfigType type : ChangeConfigType.values())
				if(type.name().equalsIgnoreCase(action))
					return type;
			return ERROR;
		}
	}
	
	/**
	 * The result of a setting action.
	 */
	public enum ActionResult
	{
		OK, NONE, ERROR
	}
	
	/**
	 * Constructor.
	 *
	 * @param parent   The parent command.
	 * @param type     The operation that will be done on the configuration.
	 * @param commands The commands to call this command.
	 */
	ConfigurationCommand(Command parent, ChangeConfigType type, List<String> commands)
	{
		super(parent);
		this.type = type;
		this.commands = commands;
	}
	
	/**
	 * Process the configuration.
	 *
	 * @param event         The event that triggered this change.
	 * @param configuration The configuration to change.
	 * @param args          The args to pass.
	 *
	 * @return The result that happened.
	 */
	private ActionResult processWithValue(MessageReceivedEvent event, Configuration configuration, LinkedList<String> args)
	{
		if(configuration.isActionAllowed(getType()))
			try
			{
				Log.info("Handling change " + getType().name() + " on config " + configuration + " with parameters " + args);
				return configuration.handleChange(event, getType(), args);
			}
			catch(Exception e)
			{
				EmbedBuilder builder = new EmbedBuilder();
				builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
				builder.setColor(Color.RED);
				builder.setTitle("Erreur durant l'opération");
				builder.setDescription("Commande: " + getName());
				builder.addField("Raison", e.getMessage(), false);
				builder.addField("Configuration", configuration.getName(), false);
				Actions.reply(event, builder.build());
				Log.error(e, "Error handling configuration change");
			}
		else
			Actions.reply(event, Utilities.buildEmbed(event.getAuthor(), Color.ORANGE, "Opération impossible").addField("Raison", "Opération " + getType().name() + " impossible sur cette configuration", false).addField("Configuration", configuration.getName(), false).build());
		return ActionResult.ERROR;
	}
	
	@Override
	public String getCommandUsage()
	{
		return super.getCommandUsage() + " <configuration> [valeur]";
	}
	
	@Override
	public CommandResult execute(MessageReceivedEvent event, LinkedList<String> args) throws Exception
	{
		super.execute(event, args);
		if(args.size() > 0)
		{
			Configuration configuration = Settings.getSettings(args.pop());
			if(configuration != null)
			{
				if(processWithValue(event, configuration, args) == ActionResult.ERROR)
				{
					EmbedBuilder builder = new EmbedBuilder();
					builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
					builder.setColor(Color.RED);
					builder.setTitle("Erreur durant l'opération");
					builder.setDescription("Commande: " + getName());
					builder.addField("Raison", "C'est compliqué", false);
					builder.addField("Configuration", configuration.getName(), false);
					Actions.reply(event, builder.build());
					Log.error("Error handling configuration change");
				}
				else
				{
					EmbedBuilder builder = new EmbedBuilder();
					builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
					builder.setColor(Color.GREEN);
					builder.setTitle("Valeur changée");
					builder.setDescription("Commande: " + getName());
					builder.addField("Configuration", configuration.getName(), false);
					Actions.reply(event, builder.build());
					Log.info("Config value " + configuration.getName() + " changed");
				}
			}
			else
			{
				EmbedBuilder builder = new EmbedBuilder();
				builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
				builder.setColor(Color.ORANGE);
				builder.setTitle("Configuration non trouvée");
				builder.addField("Configurations disponibles", Arrays.stream(Settings.SETTINGS).map(Configuration::getName).collect(Collectors.joining(", ")), false);
				Actions.reply(event, builder.build());
			}
		}
		else
		{
			EmbedBuilder builder = new EmbedBuilder();
			builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
			builder.setColor(Color.ORANGE);
			builder.setTitle("Merci de renseigner le nom de la configuration à changer");
			Actions.reply(event, builder.build());
		}
		return CommandResult.SUCCESS;
	}
	
	@Override
	public void addHelp(Guild guild, EmbedBuilder builder)
	{
		super.addHelp(guild, builder);
		builder.addField("Configuration", "Le nom de la configuration", false);
		builder.addField("Optionnel: Valeur", "Paramètres de la sous commande", false);
	}
	
	@Override
	public int getScope()
	{
		return ChannelType.TEXT.getId();
	}
	
	@Override
	public String getName()
	{
		return "Opération " + getType().name();
	}
	
	@Override
	public List<String> getCommand()
	{
		return commands;
	}
	
	@Override
	public String getDescription()
	{
		return "Effectue une opération " + getType().name() + " sur cette configuration";
	}
	
	@Override
	public AccessLevel getAccessLevel()
	{
		return AccessLevel.ADMIN;
	}
	
	/**
	 * Get the type of operation done.
	 *
	 * @return The type.
	 */
	private ChangeConfigType getType()
	{
		return type;
	}
}
