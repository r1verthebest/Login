package me.r1ver.login.bukkit.commands;

import me.r1ver.login.BukkitMain;
import me.r1ver.login.bukkit.SHA1Encrypt;
import me.r1ver.login.bukkit.player.BukkitPlayer;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LoginCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("§cApenas jogadores podem executar este comando.");
			return true;
		}

		Player p = (Player) sender;
		BukkitPlayer bp = BukkitPlayer.get(p.getUniqueId());
		if (bp.isAuthenticated()) {
			p.sendMessage("§aVocê já está autenticado.");
			return true;
		}

		if (bp.getStatus() == BukkitPlayer.LoginStatus.REGISTER) {
			p.sendMessage("§b§lLOGIN §fVocê ainda não possui uma conta. Use §b/registrar <senha> <senha>§f.");
			p.playSound(p.getLocation(), Sound.VILLAGER_NO, 1.0F, 1.0F);
			return true;
		}

		if (args.length != 1) {
			p.sendMessage("§cUse: /login <senha>");
			p.playSound(p.getLocation(), Sound.VILLAGER_NO, 1.0F, 1.0F);
			return true;
		}

		String senhaInformada = args[0];
		String senhaCriptografada = SHA1Encrypt.encrypt(senhaInformada);
		if (senhaCriptografada == null || !senhaCriptografada.equals(bp.getPassword())) {
			p.kickPlayer("§b§lLOGIN \n\n§cSenha incorreta! §fTente novamente.");
			return true;
		}

		completeAuthentication(p, bp);
		return true;
	}

	private void completeAuthentication(Player p, BukkitPlayer bp) {
		bp.setStatus(BukkitPlayer.LoginStatus.AUTHENTICATED);

		p.sendMessage(" ");
		p.sendMessage("§b§lLOGIN §fAutenticado com sucesso! Divirta-se.");
		p.playSound(p.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);

		BukkitMain.getInstance().enter(p);
	}
}