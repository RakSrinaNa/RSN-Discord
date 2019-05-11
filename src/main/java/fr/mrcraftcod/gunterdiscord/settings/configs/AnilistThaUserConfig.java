package fr.mrcraftcod.gunterdiscord.settings.configs;

import fr.mrcraftcod.gunterdiscord.settings.configurations.SingleUserConfiguration;
import net.dv8tion.jda.api.entities.Guild;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-04-15
 */
public class AnilistThaUserConfig extends SingleUserConfiguration{
	/**
	 * Constructor.
	 *
	 * @param guild The guild for this config.
	 */
	public AnilistThaUserConfig(final Guild guild){
		super(guild);
	}
	
	@Override
	public String getName(){
		return "anilistThaUser";
	}
}