package info.tregmine.commands;

import info.tregmine.Tregmine; import info.tregmine.api.GenericPlayer;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SuicideCommand extends AbstractCommand {
    Tregmine t;

    public SuicideCommand(Tregmine inst) {
        super(inst, "suicide");
        t = inst;
    }

    @Override
    public boolean handlePlayer(GenericPlayer player, String[] args) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.HARM, 1, 10000));
        player.setDeathCause("suicide");
        return true;
    }
}
