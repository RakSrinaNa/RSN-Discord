package fr.mrcraftcod.gunterdiscord.commands.warn;

import fr.mrcraftcod.gunterdiscord.settings.configs.DoubleWarnRoleConfig;
import fr.mrcraftcod.gunterdiscord.settings.configs.DoubleWarnTimeConfig;
import fr.mrcraftcod.gunterdiscord.settings.configurations.SingleRoleConfiguration;
import fr.mrcraftcod.gunterdiscord.settings.configurations.ValueConfiguration;
import java.util.List;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 12/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-12
 */
public class DoubleWarnCommand extends WarnCommand{
	@Override
	public String getName(){
		return "Double warn";
	}
	
	@Override
	public List<String> getCommand(){
		return List.of("doublewarn", "dwarn");
	}
	
	@Override
	protected SingleRoleConfiguration getRoleConfig(){
		return new DoubleWarnRoleConfig();
	}
	
	@Override
	protected ValueConfiguration getTimeConfig(){
		return new DoubleWarnTimeConfig();
	}
}