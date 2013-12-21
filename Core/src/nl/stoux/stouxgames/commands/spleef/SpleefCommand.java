package nl.stoux.stouxgames.commands.spleef;

import nl.stoux.stouxgames.commands.AbstractCommand;
import nl.stoux.stouxgames.commands.exception.CommandException;
import nl.stoux.stouxgames.commands.exception.UsageException;
import nl.stoux.stouxgames.commands.main.StouxGamesCommand;
import nl.stoux.stouxgames.games.GameMode;
import nl.stoux.stouxgames.games.GameState;
import nl.stoux.stouxgames.games.spleef.Spleef;
import nl.stoux.stouxgames.util._;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

public class SpleefCommand extends AbstractCommand {

	public SpleefCommand(CommandSender sender, String[] args) {
		super(sender, args);
	}

	@Override
	public boolean handle() throws CommandException {
		if (args.length == 0) { //Redirect to standard StouxGames command
			return new StouxGamesCommand(sender, new String[]{}).handle();
		}
		
		Spleef spleef = (Spleef) _.getGameController().getGame(GameMode.Spleef);
		isGameRunning(spleef);
		
		switch (args[0].toLowerCase()) {
		case "forcestart": case "forces": case "start":
			checkPermission("spleef.start");
			if (spleef.getGameState() != GameState.lobbyJoining && spleef.getGameState() != GameState.lobby) throw new CommandException("The game cannot be forced start from this GameState.");
			spleef.startGame();
			break;
		
		case "forcematerial": case "forcem": case "forcefloor": case "forcef": //Force a floor type
			checkPermission("spleef.forcefloor");
			Player p = getPlayer();
			ItemStack hand = p.getItemInHand();
			if (hand.getType() == Material.AIR) throw new CommandException("You not nothing in your hand! Keep a block of the floor type in your hand!");
			if (!hand.getType().isBlock()) throw new CommandException("The item in your hand is not a block!");
			spleef.forceFloorType(hand.getType());
			_.msg(sender, spleef.getGamemode(), "The floor type of the next round has been set to: " + hand.getType().toString());
			break;
		
		case "forcepotion": case "forcep": case "forcepotioneffect": //Force a potion in the following round
			checkPermission("spleef.forcepotion");
			String usage = "/spleef forcepotion <Blindness|Confusion|Speed|Slow>";
			if (args.length != 2) throw new UsageException(usage);
			PotionEffectType type = null;
			switch (args[1].toLowerCase()) {
			case "blindness": case "blind": case "darkness":
				type = PotionEffectType.BLINDNESS;
				break;
			case "confusion": case "nausea": case "confus":
				type = PotionEffectType.CONFUSION;
				break;
			case "speed": case "speedness": case "fast":
				type = PotionEffectType.SPEED;
				break;
			case "slowness": case "slow": case "slowpoke":
				type = PotionEffectType.SLOW;
				break;
			default:
				throw new UsageException(usage);
			}
			
			spleef.forcePotionEffect(type);
			_.msg(sender, spleef.getGamemode(), "The potion effect in the following round has been set to: " + type.getName());
			break;
		
		default:
			throw new UsageException("/spleef");
		}		
		return true;
	}

	
	
}
