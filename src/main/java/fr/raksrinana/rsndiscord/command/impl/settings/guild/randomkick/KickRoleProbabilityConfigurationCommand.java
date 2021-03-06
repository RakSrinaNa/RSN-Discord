package fr.raksrinana.rsndiscord.command.impl.settings.guild.randomkick;

import fr.raksrinana.rsndiscord.command.Command;
import fr.raksrinana.rsndiscord.command.impl.settings.helpers.ValueConfigurationCommand;
import fr.raksrinana.rsndiscord.permission.IPermission;
import fr.raksrinana.rsndiscord.settings.Settings;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import static fr.raksrinana.rsndiscord.permission.PermissionUtils.ALLOW;

public class KickRoleProbabilityConfigurationCommand extends ValueConfigurationCommand<Double> {
    public KickRoleProbabilityConfigurationCommand(Command parent) {
        super(parent);
    }

    @Override
    public @NotNull IPermission getPermission() {
        return ALLOW;
    }

    @Override
    protected Double extractValue(@NotNull GuildMessageReceivedEvent event, @NotNull LinkedList<String> args) {
        if (args.isEmpty()) {
            throw new IllegalArgumentException("Please mention the probability");
        }
        try {
            return Double.parseDouble(args.pop());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Please mention the probability");
        }
    }

    @Override
    protected void setConfig(@NotNull Guild guild, @NotNull Double value) {
        Settings.get(guild).getRandomKick().setKickRoleProbability(Math.min(1, Math.max(0, value)));
    }

    @Override
    protected void removeConfig(@NotNull Guild guild) {
    }

    @Override
    protected @NotNull String getValueName() {
        return "Kick role probability";
    }

    @NotNull
    @Override
    protected Optional<Double> getConfig(Guild guild) {
        return Optional.of(Settings.get(guild).getRandomKick().getKickRoleProbability());
    }

    @NotNull
    @Override
    public String getName(@NotNull Guild guild) {
        return "Kick role probability";
    }

    @NotNull
    @Override
    public List<String> getCommandStrings() {
        return List.of("kickRoleProbability");
    }
}
