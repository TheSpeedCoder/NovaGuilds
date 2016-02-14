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

package co.marcin.novaguilds.impl.basic;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.api.basic.NovaGuild;
import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.basic.NovaRaid;
import co.marcin.novaguilds.basic.NovaRank;
import co.marcin.novaguilds.basic.NovaRegion;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.manager.GuildManager;
import co.marcin.novaguilds.manager.RankManager;
import co.marcin.novaguilds.util.LoggerUtils;
import co.marcin.novaguilds.util.NumberUtils;
import co.marcin.novaguilds.util.TabUtils;
import co.marcin.novaguilds.util.TagUtils;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import me.confuser.barapi.BarAPI;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class NovaGuildImpl implements co.marcin.novaguilds.api.basic.NovaGuild {
	private int id;
	private String name;
	private String tag;
	private NovaRegion region;
	private String leaderName;
	private NovaPlayer leader;
	private Location homeLocation;
	private double money = 0;
	private int points;
	private NovaRaid raid;
	private long timeRest;
	private long lostLiveTime;
	private long inactiveTime;
	private long timeCreated;
	private int lives;
	private int slots;
	private boolean openInvitation = false;
	private boolean changed = false;
	private boolean friendlyPvp = false;
	private Location vaultLocation;
	private Hologram vaultHologram;

	private final List<NovaPlayer> players = new ArrayList<>();

	private final List<NovaGuild> allies = new ArrayList<>();
	private final List<String> alliesNames = new ArrayList<>();
	private final List<String> allyInvitationNames = new ArrayList<>();
	private final List<NovaGuild> allyInvitations = new ArrayList<>();

	private final List<NovaGuild> war = new ArrayList<>();
	private final List<String> warNames = new ArrayList<>();

	private final List<String> nowarInvitedNames = new ArrayList<>();
	private final List<NovaGuild> nowarInvited = new ArrayList<>();

	private final List<NovaPlayer> invitedPlayers = new ArrayList<>();
	private final List<NovaRank> ranks = new ArrayList<>();

	//getters
	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getPoints() {
		return points;
	}

	@Override
	public String getTag() {
		return tag;
	}

	@Override
	public NovaRegion getRegion() {
		return region;
	}

	@Override
	public Hologram getVaultHologram() {
		return vaultHologram;
	}

	@Override
	public List<NovaGuild> getAllies() {
		return allies;
	}

	@Override
	public List<NovaGuild> getAllyInvitations() {
		return allyInvitations;
	}

	@Override
	public List<NovaGuild> getWars() {
		return war;
	}

	@Override
	public List<NovaGuild> getNoWarInvitations() {
		return nowarInvited;
	}

	@Override
	public List<NovaPlayer> getPlayers() {
		return players;
	}

	@Override
	public List<NovaPlayer> getOnlineNovaPlayers() {
		List<NovaPlayer> list = new ArrayList<>();

		for(NovaPlayer nPlayer : getPlayers()) {
			if(nPlayer.isOnline() && nPlayer.getPlayer() != null) {
				list.add(nPlayer);
			}
		}

		return list;
	}

	@Override
	public List<Player> getOnlinePlayers() {
		List<Player> list = new ArrayList<>();

		for(NovaPlayer nPlayer : getOnlineNovaPlayers()) {
			list.add(nPlayer.getPlayer());
		}

		return list;
	}

	@Override
	public List<NovaPlayer> getInvitedPlayers() {
		return invitedPlayers;
	}

	@Override
	public List<NovaRank> getRanks() {
		return ranks;
	}

	@Override
	public List<String> getNoWarInvitationNames() {
		return nowarInvitedNames;
	}

	@Override
	public List<String> getAlliesNames() {
		return alliesNames;
	}

	@Override
	public List<String> getAllyInvitationNames() {
		return allyInvitationNames;
	}

	@Override
	public List<String> getWarsNames() {
		return warNames;
	}

	@Override
	public Location getVaultLocation() {
		return vaultLocation;
	}

	@Override
	public NovaPlayer getLeader() {
		return leader;
	}

	@Override
	public String getLeaderName() {
		return leaderName;
	}

	@Override
	public Location getHome() {
		return homeLocation;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public double getMoney() {
		return money;
	}

	@Override
	public NovaRaid getRaid() {
		return raid;
	}

	@Override
	public int getLives() {
		return lives;
	}

	@Override
	public long getTimeRest() {
		return timeRest;
	}

	@Override
	public long getLostLiveTime() {
		return lostLiveTime;
	}

	@Override
	public long getInactiveTime() {
		return inactiveTime;
	}

	@Override
	public long getTimeCreated() {
		return timeCreated;
	}

	@Override
	public boolean getFriendlyPvp() {
		return friendlyPvp;
	}

	@Override
	public int getSlots() {
		return slots;
	}

	@Override
	public NovaRank getDefaultRank() {
		for(NovaRank rank : getRanks()) {
			if(rank.isDefault()) {
				return rank;
			}
		}

		return RankManager.getDefaultRank();
	}

	@Override
	public NovaRank getCloneOfGenericRank(NovaRank genericRank) {
		if(genericRank != null && genericRank.isGeneric()) {
			for(NovaRank rank : getRanks()) {
				if(rank.isClone()) {
					if(genericRank.getName().equals(rank.getName())) {
						return rank;
					}
				}
			}
		}

		return null;
	}

	//setters
	@Override
	public void setVaultHologram(Hologram hologram) {
		vaultHologram = hologram;
	}

	@Override
	public void setUnchanged() {
		changed = false;
	}

	@Override
	public void changed() {
		changed = true;
	}

	@Override
	public void setName(String n) {
		name = n;
		changed();

		//Force changed()
		for(NovaPlayer nPlayer : getPlayers()) {
			nPlayer.setGuild(this);
		}

		if(hasRegion()) {
			getRegion().changed();
		}
	}

	@Override
	public void setTag(String t) {
		tag = t;
		changed();
	}

	@Override
	public void setRegion(NovaRegion region) {
		this.region = region;

		if(region != null) {
			region.setGuild(this);
		}
	}

	@Override
	public void setLeaderName(String name) {
		leaderName = name;
	}

	@Override
	public void setVaultLocation(Location location) {
		vaultLocation = location;
		changed();
	}

	@Override
	public void setLeader(NovaPlayer nPlayer) {
		if(leader != null) {
			leader.setGuildRank(getDefaultRank());
		}

		leader = nPlayer;
		leader.setGuildRank(RankManager.getLeaderRank());
		changed();
	}

	@Override
	public void setSpawnPoint(Location location) {
		homeLocation = location;
		changed();
	}

	@Override
	public void setId(int i) {
		id = i;
		changed();
	}

	@Override
	public void setMoney(double m) {
		money = m;
		changed();
	}

	@Override
	public void setAllies(List<NovaGuild> list) {
		allies.clear();
		allies.addAll(list);

		changed();
	}

	@Override
	public void setAlliesNames(List<String> list) {
		alliesNames.clear();
		alliesNames.addAll(list);

		changed();
	}

	@Override
	public void setAllyInvitationNames(List<String> list) {
		allyInvitationNames.clear();
		allyInvitationNames.addAll(list);

		changed();
	}

	@Override
	public void setWars(List<NovaGuild> list) {
		war.clear();
		war.addAll(list);
	}

	@Override
	public void setWarsNames(List<String> list) {
		warNames.clear();
		warNames.addAll(list);

		changed();
	}

	@Override
	public void setNoWarInvitations(List<String> list) {
		nowarInvitedNames.clear();
		nowarInvitedNames.addAll(list);

		changed();
	}

	@Override
	public void setPoints(int points) {
		this.points = points;
		changed();
	}

	@Override
	public void setOpenInvitation(boolean openInvitation) {
		this.openInvitation = openInvitation;
		changed();
	}

	@Override
	public void updateTimeRest() {
		timeRest = NumberUtils.systemSeconds();
		changed();
	}

	@Override
	public void updateLostLive() {
		lostLiveTime = NumberUtils.systemSeconds();
		changed();
	}

	@Override
	public void updateInactiveTime() {
		inactiveTime = NumberUtils.systemSeconds();
		changed();
	}

	@Override
	public void setLostLiveTime(long time) {
		lostLiveTime = time;
		changed();
	}

	@Override
	public void setInactiveTime(long time) {
		inactiveTime = time;
		changed();
	}

	@Override
	public void resetLostLiveTime() {
		lostLiveTime = 0;
		changed();
	}

	@Override
	public void setLives(int lives) {
		this.lives = lives;
		changed();
	}

	@Override
	public void setTimeRest(long time) {
		this.timeRest = time;
		changed();
	}

	@Override
	public void setFriendlyPvp(boolean pvp) {
		friendlyPvp = pvp;
	}

	@Override
	public void setTimeCreated(long time) {
		this.timeCreated = time;
	}

	@Override
	public void setSlots(int slots) {
		this.slots = slots;
	}

	@Override
	public void setRanks(List<NovaRank> ranks) {
		this.ranks.clear();
		this.ranks.addAll(ranks);
	}

	//check
	@Override
	public boolean isInvitedToAlly(NovaGuild guild) {
		return allyInvitations.contains(guild);
	}

	@Override
	public boolean isWarWith(NovaGuild guild) {
		return war.contains(guild);
	}

	@Override
	public boolean isNoWarInvited(NovaGuild guild) {
		return nowarInvited.contains(guild);
	}

	@Override
	public boolean isLeader(NovaPlayer nPlayer) {
		return nPlayer.equals(leader) || (leaderName != null && nPlayer.getName().equals(leaderName));
	}

	@Override
	public boolean hasRegion() {
		return region != null;
	}

	@Override
	public boolean isAlly(NovaGuild guild) {
		return guild != null && allies.contains(guild);
	}

	@Override
	public boolean isRaid() {
		return !(raid == null) && NovaGuilds.getInstance().guildRaids.contains(this);
	}

	@Override
	public boolean isChanged() {
		return changed;
	}

	@Override
	public boolean isFull() {
		return getPlayers().size() >= slots;
	}

	@Override
	public boolean isOpenInvitation() {
		return openInvitation;
	}

	@Override
	public boolean hasMoney(double money) {
		return this.money >= money;
	}

	//add/remove
	@Override
	public void addAlly(NovaGuild guild) {
		if(!isAlly(guild)) {
			alliesNames.add(guild.getName().toLowerCase());
			allies.add(guild);
			changed();
		}
	}

	@Override
	public void addAllyInvitation(NovaGuild guild) {
		if(!isInvitedToAlly(guild)) {
			allyInvitations.add(guild);
			changed();
		}
	}

	@Override
	public void addWar(NovaGuild guild) {
		if(!isWarWith(guild)) {
			warNames.add(guild.getName().toLowerCase());
			war.add(guild);
			changed();
		}
	}

	@Override
	public void addNoWarInvitation(NovaGuild guild) {
		if(!isNoWarInvited(guild)) {
			nowarInvited.add(guild);
			changed();
		}
	}

	@Override
	public void addPlayer(NovaPlayer nPlayer) {
		if(nPlayer == null) {
			LoggerUtils.info("Tried to add null player to a guild! " + name);
			return;
		}

		if(!players.contains(nPlayer)) {
			players.add(nPlayer);
			nPlayer.setGuild(this);

			if(getLeaderName() != null && getLeaderName().equalsIgnoreCase(nPlayer.getName())) {
				setLeader(nPlayer);
				leaderName = null;
			}

			if(NovaGuilds.getInstance().getRankManager().isLoaded() && !nPlayer.isLeader()) {
				nPlayer.setGuildRank(getDefaultRank());
			}
		}
	}

	@Override
	public void addMoney(double money) {
		this.money += money;
		changed();
	}

	@Override
	public void addPoints(int points) {
		this.points += points;
		changed();
	}

	@Override
	public void addSlot() {
		slots++;
	}

	@Override
	public void addRank(NovaRank rank) {
		if(!ranks.contains(rank)) {
			ranks.add(rank);
			if(rank.getGuild() == null || !rank.getGuild().equals(this)) {
				rank.setGuild(this);
			}
		}
	}

	@Override
	public void removePlayer(NovaPlayer nPlayer) {
		if(players.contains(nPlayer)) {
			players.remove(nPlayer);
			nPlayer.setGuild(null);
			nPlayer.setGuildRank(null);
		}
	}

	@Override
	public void removeAlly(NovaGuild guild) {
		if(allies.contains(guild)) {
			allies.remove(guild);
			alliesNames.remove(guild.getName().toLowerCase());
			changed();
		}
	}

	@Override
	public void removeWar(NovaGuild guild) {
		if(war.contains(guild)) {
			war.remove(guild);
			warNames.remove(guild.getName().toLowerCase());
			changed();
		}
	}

	@Override
	public void removeNoWarInvitation(NovaGuild guild) {
		if(nowarInvited.contains(guild)) {
			nowarInvited.remove(guild);
			changed();
		}
	}

	@Override
	public void removeRank(NovaRank rank) {
		ranks.remove(rank);
		rank.setGuild(null);
	}

	@Override
	public void removeRaid() {
		raid = null;
	}

	@Override
	public void takeMoney(double money) {
		this.money -= money;
		changed();
	}

	@Override
	public void takeLive() {
		lives--;
		changed();
	}

	@Override
	public void addLive() {
		lives++;
		changed();
	}

	@Override
	public void takePoints(int points) {
		this.points -= points;
		changed();
	}

	@Override
	public void removeAllyInvitation(NovaGuild guild) {
		if(isInvitedToAlly(guild)) {
			allyInvitations.remove(guild);
			changed();
		}
	}

	@Override
	public void createRaid(NovaGuild attacker) {
		raid = new NovaRaid(attacker, this);
	}

	@Override
	public boolean isMember(NovaPlayer nPlayer) {
		return players.contains(nPlayer);
	}

	@Override
	public void destroy() {
		final NovaGuilds plugin = NovaGuilds.getInstance();

		//remove players
		for(NovaPlayer nPlayer : getPlayers()) {
			nPlayer.cancelToolProgress();
			nPlayer.setGuild(null);
			nPlayer.setGuildRank(null);

			if(nPlayer.isOnline()) {
				//update tags
				TagUtils.refresh(nPlayer.getPlayer());

				//Close GUI
				if(nPlayer.getGuiInventory() != null) {
					nPlayer.getGuiInventoryHistory().clear();
					nPlayer.getPlayer().closeInventory();
				}
			}
		}

		//remove guild invitations
		for(NovaPlayer nPlayer : plugin.getPlayerManager().getPlayers()) {
			if(nPlayer.isInvitedTo(this)) {
				nPlayer.deleteInvitation(this);
			}
		}

		//remove allies and wars
		for(NovaGuild nGuild : plugin.getGuildManager().getGuilds()) {
			//ally
			if(nGuild.isAlly(this)) {
				nGuild.removeAlly(this);
			}

			//ally invitation
			if(nGuild.isInvitedToAlly(this)) {
				nGuild.removeAllyInvitation(this);
			}

			//war
			if(nGuild.isWarWith(this)) {
				nGuild.removeWar(this);
			}

			//no war invitation
			if(nGuild.isNoWarInvited(this)) {
				nGuild.removeNoWarInvitation(this);
			}
		}

		//remove raid
		//TODO

		//bank and hologram
		if(this.getVaultHologram() != null) {
			this.getVaultHologram().delete();
		}


		GuildManager.checkVaultDestroyed(this);
		if(getVaultLocation() != null) {
			getVaultLocation().getBlock().breakNaturally();
			getVaultLocation().getWorld().playEffect(getVaultLocation(), Effect.SMOKE, 1000);
		}

		//Delete ranks
		plugin.getRankManager().delete(this);

		//Refresh top holograms
		plugin.getHologramManager().refreshTopHolograms();

		//Update tab
		TabUtils.refresh(this);

		//Give all the money to the leader
		getLeader().addMoney(getMoney());
	}

	@Override
	public void showVaultHologram(Player player) {
		if(vaultHologram != null) {
			vaultHologram.getVisibilityManager().showTo(player);
		}
	}

	@Override
	public void hideVaultHologram(Player player) {
		if(vaultHologram != null) {
			vaultHologram.getVisibilityManager().hideTo(player);
		}
	}

	@Override
	public void removeRaidBar() {
		if(Config.BARAPI_ENABLED.getBoolean()) {
			for(Player player : getOnlinePlayers()) {
				BarAPI.removeBar(player);
			}
		}
	}
}
