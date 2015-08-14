package co.marcin.novaguilds.util;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.enums.Config;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class TagUtils {
	private final NovaGuilds plugin;

	public TagUtils(NovaGuilds novaGuilds) {
		plugin = novaGuilds;
	}

	public String getTag(Player namedplayer) { //TODO deleted second arg Player player
		String tag = "";
		String guildTag;
		String rank = "";
		NovaPlayer nPlayer = plugin.getPlayerManager().getPlayer(namedplayer);

		if(nPlayer.hasGuild()) {
			tag = plugin.getConfig().getString("guild.tag");
			guildTag = nPlayer.getGuild().getTag();

			if(!Config.TAGAPI_COLORTAGS.getBoolean()) {
				guildTag = StringUtils.removeColors(guildTag);
			}

			tag = StringUtils.replace(tag, "{TAG}", guildTag);

			if(plugin.getConfig().getBoolean("tabapi.rankprefix")) {
				if(nPlayer.getGuild().getLeader().getName().equalsIgnoreCase(namedplayer.getName())) {
					rank = plugin.getMessageManager().getMessages().getString("chat.guildinfo.leaderprefix");
				}
			}

			tag = StringUtils.replace(tag, "{RANK}", rank);

			//TODO: ally/war colors
//			NovaPlayer nPlayerReceiver = plugin.getPlayerManager().getPlayer(player);
//			if(nPlayerReceiver.hasGuild()) {
//				if(nPlayerReceiver.getGuild().isAlly(nPlayer.getGuild())) {
//					if(plugin.getConfig().getBoolean("tagapi.allycolor.enabled")) {
//						tabName = plugin.getConfig().getString("tagapi.allycolor.color") + tabName;
//					}
//				}
//				else if(plugin.getPlayerManager().isGuildMate(player,namedplayer)) {
//					if(plugin.getConfig().getBoolean("tagapi.guildcolor.enabled")) {
//						tabName = plugin.getConfig().getString("tagapi.guildcolor.color") + tabName;
//					}
//				}
//				else if(nPlayer.getGuild().isWarWith(nPlayerReceiver.getGuild())) {
//					if(plugin.getConfig().getBoolean("tagapi.warcolor.enabled")) {
//						tabName = plugin.getConfig().getString("tagapi.warcolor.color") + tabName;
//					}
//				}
//			}

			//TODO: using chat permissions
			if(namedplayer.hasPermission("novaguilds.chat.notag")) {
				tag = "";
			}
		}

		return StringUtils.fixColors(tag);
	}

	@SuppressWarnings("deprecation")
	private static void setPrefix(OfflinePlayer player, String tag, Player p) {
		Scoreboard board = p.getScoreboard();
		Team team;
		if(board.getPlayerTeam(player) == null) {
			String tName = "ng_"+player.getName();
			if(tName.length() > 16) {
				tName = tName.substring(0, 16);
			}

			team = board.registerNewTeam(tName);
			team.addPlayer(player);
		}
		else {
			team = board.getPlayerTeam(player);
		}
		team.setPrefix(StringUtils.fixColors(tag));
	}

	public void updatePrefix(Player p) {
		for(Player of : Bukkit.getOnlinePlayers()) {
			setPrefix(of, getTag(of), p);
		}
	}

	public void refreshAll() {
		for(Player player : Bukkit.getOnlinePlayers()) {
			updatePrefix(player);
		}
	}

	public void refreshGuild(NovaGuild guild) {
		if(guild != null) {
			for(Player player : guild.getOnlinePlayers()) {
				updatePrefix(player);
			}
		}
	}
}
