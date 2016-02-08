/*
 *     NovaGuilds - Bukkit plugin
 *     Copyright (C) 2015 Marcin (CTRL) Wieczorek
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package co.marcin.novaguilds.util;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.enums.VarKey;
import co.marcin.novaguilds.interfaces.TabList;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public final class TabUtils {
	private static final NovaGuilds plugin = NovaGuilds.getInstance();

	public static void refresh(NovaPlayer nPlayer) {
		if(!Config.TABLIST_ENABLED.getBoolean()) {
			return;
		}

		nPlayer.getTabList().send();
	}

	public static void refresh(Player player) {
		refresh(NovaPlayer.get(player));
	}

	public static void refresh() {
		refresh(new ArrayList<>(plugin.getPlayerManager().getOnlinePlayers()));
	}

	public static void refresh(NovaGuild guild) {
		refresh(guild.getPlayers());
	}

	public static void refresh(List<NovaPlayer> list) {
		for(NovaPlayer nPlayer : list) {
			refresh(nPlayer);
		}
	}

	@SuppressWarnings("deprecation")
	public static void fillVars(TabList tabList) {
		NovaPlayer nPlayer = tabList.getPlayer();
		Map<VarKey, String> vars = tabList.getVars();
		tabList.clear();

		//Server vars
		vars.put(VarKey.SERVER_ONLINE, String.valueOf(Bukkit.getOnlinePlayers().size()));
		vars.put(VarKey.SERVER_MAX, String.valueOf(Bukkit.getMaxPlayers()));

		//Time
		Date date = Calendar.getInstance().getTime();
		vars.put(VarKey.DATE_YEAR, String.valueOf(1900 + date.getYear()));
		vars.put(VarKey.DATE_MONTH, String.valueOf((date.getMonth()<10?"0":"") + date.getMonth()));
		vars.put(VarKey.DATE_DAY, String.valueOf((date.getDay()<10?"0":"") + date.getDay()));
		vars.put(VarKey.DATE_HOURS, String.valueOf((date.getHours()<10?"0":"") + date.getHours()));
		vars.put(VarKey.DATE_MINUTES, String.valueOf((date.getMinutes()<10?"0":"") + date.getMinutes()));
		vars.put(VarKey.DATE_SECONDS, String.valueOf((date.getSeconds()<10?"0":"") + date.getSeconds()));

		//World vars
		if(nPlayer.isOnline()) {
			World world = Bukkit.getWorlds().get(0);
			vars.put(VarKey.WORLD_NAME, world.getName());
			vars.put(VarKey.WORLD_SPAWN, Message.getCoords3D(world.getSpawnLocation()).get());
		}

		//Player vars
		vars.put(VarKey.PLAYER_NAME, nPlayer.getName());
		vars.put(VarKey.PLAYER_BALANCE, String.valueOf(nPlayer.getMoney()));
		vars.put(VarKey.PLAYER_KILLS, String.valueOf(nPlayer.getKills()));
		vars.put(VarKey.PLAYER_DEATHS, String.valueOf(nPlayer.getDeaths()));
		vars.put(VarKey.PLAYER_KDR, String.valueOf(nPlayer.getKillDeathRate()));
		vars.put(VarKey.PLAYER_CHATMODE, Message.getChatModeName(nPlayer.getChatMode()).get());
		vars.put(VarKey.PLAYER_SPYMODE, Message.getOnOff(nPlayer.getSpyMode()));
		vars.put(VarKey.PLAYER_BYPASS, Message.getOnOff(nPlayer.getBypass()));

		//Guild vars
		NovaGuild guild = nPlayer.getGuild();
		String guildName, guildTag, guildPlayersOnline, guildPlayersMax, guildLives, guildRegenTime, guildRaidProgress, guildPvp, guildMoney, guildPoints, guildSlots = "";
		String guildTimeRest, guildTimeCreated, guildHomeCoords, guildOpenInvitation = "";
		guildName = guildTag = guildPlayersOnline = guildPlayersMax = guildLives = guildRegenTime = guildRaidProgress = guildPvp = guildMoney = guildPoints = guildSlots;
		guildTimeRest = guildTimeCreated = guildHomeCoords = guildOpenInvitation;

		if(nPlayer.hasGuild()) {
			long liveRegenerationTime = Config.LIVEREGENERATION_REGENTIME.getSeconds() - (NumberUtils.systemSeconds() - guild.getLostLiveTime());
			long createdTime = Config.LIVEREGENERATION_REGENTIME.getSeconds() - (NumberUtils.systemSeconds() - guild.getTimeCreated());
			long restTime = Config.LIVEREGENERATION_REGENTIME.getSeconds() - (NumberUtils.systemSeconds() - guild.getTimeRest());

			guildName = guild.getName();
			guildTag = guild.getTag();
			guildPlayersOnline = String.valueOf(guild.getOnlinePlayers().size());
			guildPlayersMax = String.valueOf(guild.getPlayers().size());
			guildLives = String.valueOf(guild.getLives());
			guildRegenTime = StringUtils.secondsToString(liveRegenerationTime, TimeUnit.HOURS);
			guildRaidProgress = guild.isRaid() ? String.valueOf(guild.getRaid().getProgress()) : "";
			guildPvp = Message.getOnOff(guild.getFriendlyPvp());
			guildMoney = String.valueOf(guild.getMoney());
			guildPoints = String.valueOf(guild.getPoints());
			guildSlots = String.valueOf(guild.getSlots());
			guildTimeRest = StringUtils.secondsToString(restTime, TimeUnit.HOURS);
			guildTimeCreated = StringUtils.secondsToString(createdTime, TimeUnit.HOURS);
			guildHomeCoords = Message.getCoords3D(guild.getSpawnPoint()).get();
			guildOpenInvitation = Message.getOnOff(guild.isOpenInvitation());
		}

		vars.put(VarKey.GUILD_NAME, guildName);
		vars.put(VarKey.GUILD_TAG, guildTag);
		vars.put(VarKey.GUILD_PLAYERS_ONLINE, guildPlayersOnline);
		vars.put(VarKey.GUILD_PLAYERS_MAX, guildPlayersMax);
		vars.put(VarKey.GUILD_LIVES, guildLives);
		vars.put(VarKey.GUILD_RAIDPROGRESS, guildRaidProgress);
		vars.put(VarKey.GUILD_PVP, guildPvp);
		vars.put(VarKey.GUILD_MONEY, guildMoney);
		vars.put(VarKey.GUILD_POINTS, guildPoints);
		vars.put(VarKey.GUILD_SLOTS, guildSlots);
		vars.put(VarKey.GUILD_TIME_REGEN, guildRegenTime);
		vars.put(VarKey.GUILD_TIME_REST, guildTimeRest);
		vars.put(VarKey.GUILD_TIME_CREATED, guildTimeCreated);
		vars.put(VarKey.GUILD_HOME, guildHomeCoords);
		vars.put(VarKey.GUILD_OPENINVITATION, guildOpenInvitation);

		//Guild TOP
		List<NovaGuild> topGuildsList = plugin.getGuildManager().getTopGuildsByPoints(20);
		for(int i=1; i<=20; i++) {
			if(i <= topGuildsList.size()) {
				NovaGuild guildTop = topGuildsList.get(i - 1);
				String row = Config.TABLIST_TOPROW_GUILDS.getString();
				Map<VarKey, String> rowVars = new HashMap<>();
				rowVars.put(VarKey.N, String.valueOf(i));
				rowVars.put(VarKey.GUILDNAME, guildTop.getName());
				rowVars.put(VarKey.GUILDTAG, guildTop.getTag());
				rowVars.put(VarKey.POINTS, String.valueOf(guildTop.getPoints()));
				row = StringUtils.replaceVarKeyMap(row, rowVars);

				vars.put(VarKey.valueOf("GUILD_TOP_N" + i), row);
			}
			else {
				vars.put(VarKey.valueOf("GUILD_TOP_N" + i), "");
			}
		}

		//Players TOP
		List<ListDisplay> listDisplays = new ArrayList<>();
		listDisplays.add(new ListDisplay(Config.TABLIST_TOPROW_PLAYERS_POINTS, "POINTS", plugin.getPlayerManager().getTopPlayersByPoints(20)));
		listDisplays.add(new ListDisplay(Config.TABLIST_TOPROW_PLAYERS_KDR, "KDR", plugin.getPlayerManager().getTopPlayersByKDR(20)));

		for(ListDisplay listDisplay : listDisplays) {
			List<NovaPlayer> topPlayersList = listDisplay.getList();

			for(int i = 1; i <= 20; i++) {
				if(i <= topPlayersList.size()) {
					NovaPlayer nPlayerTop = topPlayersList.get(i - 1);
					String row = listDisplay.getRowPattern().getString();
					Map<VarKey, String> rowVars = new HashMap<>();
					rowVars.put(VarKey.N, String.valueOf(i));
					rowVars.put(VarKey.PLAYERNAME, nPlayerTop.getName());
					rowVars.put(VarKey.KILLS, String.valueOf(nPlayerTop.getKills()));
					rowVars.put(VarKey.DEATHS, String.valueOf(nPlayerTop.getDeaths()));
					rowVars.put(VarKey.KDR, String.valueOf(nPlayerTop.getKillDeathRate()));
					rowVars.put(VarKey.POINTS, String.valueOf(nPlayerTop.getPoints()));
					row = StringUtils.replaceVarKeyMap(row, rowVars);

					vars.put(VarKey.valueOf("PLAYER_TOP_" + listDisplay.getVarKey() + "_N" + i), row);
				}
				else {
					vars.put(VarKey.valueOf("PLAYER_TOP_" + listDisplay.getVarKey() + "_N" + i), "");
				}
			}
		}
	}

	private static class ListDisplay {
		private Config rowPattern;
		private String varKey;
		private List<NovaPlayer> list;

		ListDisplay(Config rowPattern, String varKey, List<NovaPlayer> list) {
			this.rowPattern = rowPattern;
			this.varKey = varKey;
			this.list = list;
		}

		public Config getRowPattern() {
			return rowPattern;
		}

		public String getVarKey() {
			return varKey;
		}

		public List<NovaPlayer> getList() {
			return list;
		}
	}
}
