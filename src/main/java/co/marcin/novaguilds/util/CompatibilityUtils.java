/*
 *     NovaGuilds - Bukkit plugin
 *     Copyright (C) 2017 Marcin (CTRL) Wieczorek
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

import co.marcin.novaguilds.manager.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

public class CompatibilityUtils {
	private static Method getOnlinePlayersMethod;

	static {
		try {
			getOnlinePlayersMethod = Server.class.getMethod("getOnlinePlayers");
		}
		catch(NoSuchMethodException e) {
			LoggerUtils.exception(e);
		}
	}

	/**
	 * Gets online players
	 *
	 * @return Collection of online players
	 */
	@SuppressWarnings("unchecked")
	public static Collection<Player> getOnlinePlayers() {
		Collection<Player> collection = new HashSet<>();

		try {
			if(getOnlinePlayersMethod.getReturnType().equals(Collection.class)) {
				collection = ((Collection) getOnlinePlayersMethod.invoke(Bukkit.getServer()));
			}
			else {
				Player[] array = ((Player[]) getOnlinePlayersMethod.invoke(Bukkit.getServer()));
				Collections.addAll(collection, array);
			}
		}
		catch(Exception e) {
			LoggerUtils.exception(e);
		}

		return collection;
	}

	/**
	 * Gets item in player's hand
	 * Fixes issues with 2 hands introduced in 1.9
	 *
	 * @param player player
	 * @return boolean
	 */
	@SuppressWarnings("deprecation")
	public static ItemStack getItemInMainHand(Player player) {
		if(ConfigManager.getServerVersion().isOlderThan(ConfigManager.ServerVersion.MINECRAFT_1_9_R1)) {
			return player.getItemInHand();
		}
		else {
			return player.getInventory().getItemInMainHand();
		}
	}

	/**
	 * Gets clicked inventory
	 * For API older than 1.8
	 *
	 * @param event inventory click event
	 * @return inventory
	 */
	public static Inventory getClickedInventory(InventoryClickEvent event) {
		int slot = event.getRawSlot();
		InventoryView view = event.getView();

		if(slot < 0) {
			return null;
		}
		else if(view.getTopInventory() != null && slot < view.getTopInventory().getSize()) {
			return view.getTopInventory();
		}
		else {
			return view.getBottomInventory();
		}
	}
}
