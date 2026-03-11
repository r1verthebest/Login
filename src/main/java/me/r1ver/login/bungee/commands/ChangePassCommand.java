package me.r1ver.login.bungee.commands;

import me.r1ver.login.bungee.BungeeMain;
import me.r1ver.login.bungee.player.BungeePlayer;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class ChangePassCommand extends Command {

	private static final String PREFIX = "§b§lLOGIN §f";
	private static final int MIN_PASS_LENGTH = 4;
	private static final int MAX_PASS_LENGTH = 16;

	public ChangePassCommand() {
		super("mudarsenha", "", "changepass", "trocarsenha");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (!(sender instanceof ProxiedPlayer)) {
			sender.sendMessage(new TextComponent("§cEste comando é exclusivo para jogadores."));
			return;
		}

		ProxiedPlayer player = (ProxiedPlayer) sender;
		BungeePlayer profile = BungeePlayer.get(player.getUniqueId());

		if (isInvalidState(player, profile, args))
			return;

		String oldPassInput = args[0];
		String newPassInput = args[1];

		if (!profile.verifyPassword(oldPassInput)) {
			player.sendMessage(new TextComponent(PREFIX + "A senha atual informada está incorreta."));
			return;
		}

		if (newPassInput.length() < MIN_PASS_LENGTH || newPassInput.length() > MAX_PASS_LENGTH) {
			player.sendMessage(new TextComponent(PREFIX + "A nova senha deve ter entre " + MIN_PASS_LENGTH + " e "
					+ MAX_PASS_LENGTH + " caracteres."));
			return;
		}

		BungeeMain.getInstance().getProxy().getScheduler().runAsync(BungeeMain.getInstance(), () -> {
			try {
				profile.changePassword(newPassInput);

				player.disconnect(new TextComponent(
						PREFIX + "\n\n§fSua senha foi alterada com sucesso!\n§fPor favor, conecte-se novamente."));
			} catch (Exception e) {
				player.sendMessage(new TextComponent("§cOcorreu um erro ao salvar sua nova senha. Tente novamente."));
				e.printStackTrace();
			}
		});
	}

	private boolean isInvalidState(ProxiedPlayer p, BungeePlayer bp, String[] args) {
		if (bp.getType() == BungeePlayer.PlayerType.ORIGINAL) {
			p.sendMessage(new TextComponent(PREFIX + "Jogadores §b§lPREMIUM §fnão utilizam senha local."));
			return true;
		}

		if (!bp.hasPasswordDefined()) {
			p.sendMessage(new TextComponent(PREFIX + "Você ainda não possui uma senha definida."));
			return true;
		}

		if (args.length != 2) {
			p.sendMessage(new TextComponent(PREFIX + "Use: /changepass <senha-atual> <nova-senha>"));
			return true;
		}
		return false;
	}
}