package me.r1ver.login.bukkit.commands;

import me.r1ver.login.BukkitMain;
import me.r1ver.login.bukkit.SHA1Encrypt;
import me.r1ver.login.bukkit.player.BukkitPlayer;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RegisterCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("§cApenas jogadores podem se registrar.");
			return true;
		}

		Player p = (Player) sender;
		BukkitPlayer bp = BukkitPlayer.get(p.getUniqueId());
		if (bp.isAuthenticated() || bp.getStatus() == BukkitPlayer.LoginStatus.LOGIN) {
			p.sendMessage("§b§lREGISTRO §fVocê já possui uma conta ou já está logado.");
			p.playSound(p.getLocation(), Sound.VILLAGER_NO, 1.0F, 1.0F);
			return true;
		}

		if (args.length < 2) {
			p.sendMessage("§b§lREGISTRO §fUtilize: §7/register <senha> <confirmarSenha>");
			return true;
		}

		String senha = args[0];
		String confirmar = args[1];
		if (!senha.equals(confirmar)) {
			p.sendMessage("§cAs senhas informadas não coincidem!");
			return true;
		}

		if (senha.length() < 4 || senha.length() > 16) {
			p.sendMessage("§cUA senha deve ter entre 4 e 16 caracteres.");
			return true;
		}

		String hash = SHA1Encrypt.encrypt(senha);
		if (hash == null) {
			p.sendMessage("§cErro interno ao processar sua senha. Contate um administrador.");
			return true;
		}

		p.sendMessage("§6§lPROCESSANDO... §fAguarde enquanto protegemos sua conta.");
		BukkitMain.getInstance().getMysql().update(p.getUniqueId(), "password", hash);

		bp.setPassword(hash);
		bp.setStatus(BukkitPlayer.LoginStatus.AUTHENTICATED);

		p.sendMessage("§b§lREGISTRO §fSua senha foi protegida com sucesso!");
		p.playSound(p.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);

		BukkitMain.getInstance().enter(p);
		return true;
	}
}