package fr.raksrinana.rsndiscord.modules.trombinoscope.command;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.log.Log;
import fr.raksrinana.rsndiscord.modules.permission.IPermission;
import fr.raksrinana.rsndiscord.modules.permission.SimplePermission;
import fr.raksrinana.rsndiscord.modules.settings.Settings;
import fr.raksrinana.rsndiscord.modules.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.modules.trombinoscope.config.Picture;
import fr.raksrinana.rsndiscord.utils.Actions;
import lombok.NonNull;
import net.coobird.thumbnailator.Thumbnails;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.apache.commons.lang3.tuple.Pair;
import org.imgscalr.Scalr;
import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

class GlobalCommand extends BasicCommand{
	private static final int PICTURE_PIXELS = 500;
	
	/**
	 * Constructor.
	 *
	 * @param parent The parent command.
	 */
	GlobalCommand(final Command parent){
		super(parent);
	}
	
	@Override
	public void addHelp(@NonNull Guild guild, @NonNull EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("mode", translate(guild, "command.trombinoscope.global.help.mode"), false);
	}
	
	@Override
	public @NonNull IPermission getPermission(){
		return new SimplePermission("command.trombinoscope.global", true);
	}
	
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		super.execute(event, args);
		var mode = Optional.ofNullable(args.poll()).flatMap(PictureMode::fromString).orElse(PictureMode.STRETCH);
		var trombinoscopeConfiguration = Settings.get(event.getGuild()).getTrombinoscope();
		var userCount = trombinoscopeConfiguration.getUserCount();
		var dimensions = getDims(userCount);
		
		var bufferedImage = new BufferedImage(PICTURE_PIXELS * dimensions.getLeft(), PICTURE_PIXELS * dimensions.getRight(), BufferedImage.TYPE_INT_RGB);
		var g2d = bufferedImage.createGraphics();
		var currentIndex = new AtomicInteger(0);
		trombinoscopeConfiguration.getPictures().entrySet().stream().parallel().forEach(entry -> {
			var userIndex = currentIndex.getAndIncrement();
			entry.getValue().stream().skip(ThreadLocalRandom.current().nextInt(entry.getValue().size())).findFirst().ifPresent(picture -> {
				int x = userIndex % dimensions.getLeft();
				int y = userIndex / dimensions.getLeft();
				drawImage(g2d, picture, PICTURE_PIXELS * x, PICTURE_PIXELS * y, PICTURE_PIXELS, mode);
			});
		});
		g2d.dispose();
		ByteArrayOutputStream imgOutputStream = new ByteArrayOutputStream();
		try{
			if(ImageIO.write(bufferedImage, "jpeg", imgOutputStream)){
				try(var imgInputStream = new ByteArrayInputStream(imgOutputStream.toByteArray())){
					trombinoscopeConfiguration.getPicturesChannel().flatMap(ChannelConfiguration::getChannel).ifPresent(channel -> Actions.sendMessage(channel, translate(event.getGuild(), "trombinoscope.global", userCount, event.getAuthor().getAsMention()), null, false, action -> action.addFile(imgInputStream, System.currentTimeMillis() + ".jpg")));
				}
			}
		}
		catch(IOException e){
			Actions.reply(event, translate(event.getGuild(), "trombinoscope.error.create-fail"), null);
		}
		return CommandResult.SUCCESS;
	}
	
	private static Pair<Integer, Integer> getDims(int count){
		var divisors = IntStream.rangeClosed(1, (int) Math.floor(Math.sqrt(count)))
				.filter(i -> count % i == 0)
				.mapToObj(i -> Pair.of(i, count / i))
				.collect(Collectors.toSet());
		return divisors.stream()
				.min(Comparator.comparingInt(GlobalCommand::getDimsDiff))
				.orElseGet(() -> {
					var side = (int) Math.ceil(Math.sqrt(count));
					return Pair.of(side, side);
				});
	}
	
	private static int getDimsDiff(Pair<Integer, Integer> dimensions){
		return Math.abs(dimensions.getLeft() - dimensions.getRight());
	}
	
	@Override
	public @NonNull String getCommandUsage(){
		return super.getCommandUsage() + " [mode]";
	}
	
	private void drawImage(Graphics2D g2d, Picture picture, int x, int y, int dim, PictureMode mode){
		try{
			var image = Thumbnails.of(picture.getPath().toFile())
					.size(dim, dim)
					.keepAspectRatio(mode == PictureMode.KEEP_ASPECT_RATIO)
					.useExifOrientation(true)
					.asBufferedImage();
			image = Scalr.resize(image, dim);
			g2d.drawImage(image, x, y, null);
		}
		catch(IOException e){
			Log.getLogger(null).error("Failed to read trombinoscope picture {}", picture.getPath(), e);
		}
	}
	
	@NonNull
	@Override
	public String getName(@NonNull Guild guild){
		return translate(guild, "command.trombinoscope.global.name");
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("global", "gl");
	}
	
	@NonNull
	@Override
	public String getDescription(@NonNull Guild guild){
		return translate(guild, "command.trombinoscope.global.description");
	}
}