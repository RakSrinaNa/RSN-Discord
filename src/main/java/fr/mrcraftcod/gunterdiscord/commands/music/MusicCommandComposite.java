package fr.mrcraftcod.gunterdiscord.commands.music;

import fr.mrcraftcod.gunterdiscord.commands.generic.CommandComposite;
import fr.mrcraftcod.gunterdiscord.settings.configs.DJRoleConfig;
import fr.mrcraftcod.gunterdiscord.utils.Utilities;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Member;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-08-18
 */
public class MusicCommandComposite extends CommandComposite{
	/**
	 * Constructor.
	 */
	public MusicCommandComposite(){
		addSubCommand(new AddMusicCommand(this));
		addSubCommand(new StopMusicCommand(this));
		addSubCommand(new PauseMusicCommand(this));
		addSubCommand(new ResumeMusicCommand(this));
		addSubCommand(new NowPlayingMusicCommand(this));
		addSubCommand(new SeekMusicCommand(this));
		addSubCommand(new SkipMusicCommand(this));
		addSubCommand(new QueueMusicCommand(this));
		addSubCommand(new ShuffleMusicCommand(this));
		addSubCommand(new MoveMusicCommand(this));
	}
	
	@Override
	public boolean isAllowed(@Nullable final Member member){
		return Objects.nonNull(member) && (Utilities.isTeam(member) || Utilities.hasRole(member, new DJRoleConfig(member.getGuild()).getObject(null)));
	}
	
	@Nonnull
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.ALL;
	}
	
	@Nonnull
	@Override
	public String getName(){
		return "Music";
	}
	
	@Nonnull
	@Override
	public List<String> getCommandStrings(){
		return List.of("music", "m");
	}
	
	@Nonnull
	@Override
	public String getDescription(){
		return "Handles music interactions";
	}
	
	@Override
	public int getScope(){
		return ChannelType.TEXT.getId();
	}
}
