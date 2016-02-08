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

package co.marcin.novaguilds.command.admin.guild;

import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.enums.Command;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.enums.VarKey;
import co.marcin.novaguilds.interfaces.Executor;
import co.marcin.novaguilds.manager.MessageManager;
import co.marcin.novaguilds.util.NumberUtils;
import co.marcin.novaguilds.util.StringUtils;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;

public class CommandAdminGuildList implements Executor {
	private final Command command = Command.ADMIN_GUILD_LIST;

	public CommandAdminGuildList() {
		plugin.getCommandManager().registerExecutor(command, this);
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(plugin.getGuildManager().getGuilds().isEmpty()) {
			Message.CHAT_GUILD_NOGUILDS.send(sender);
			return;
		}

		int page = 1;
		if(args.length == 1) {
			if(NumberUtils.isNumeric(args[0])) {
				page = Integer.parseInt(args[0]);
			}
		}

		if(page < 1) {
			page = 1;
		}

		int perpage = 10;
		int size = plugin.getGuildManager().getGuilds().size();
		int pages_number = size / perpage;
		if(size % perpage > 0) {
			pages_number++;
		}

		Message.CHAT_ADMIN_GUILD_LIST_HEADER.send(sender);
		String rowformat = Message.CHAT_ADMIN_GUILD_LIST_ITEM.get();

		int i = 0;
		boolean display = false;

		if(size > perpage) {
			Map<VarKey, String> vars = new HashMap<>();
			vars.put(VarKey.PAGE, String.valueOf(page));
			vars.put(VarKey.NEXT, String.valueOf(page + 1));
			vars.put(VarKey.PAGES, String.valueOf(pages_number));

			if(pages_number > page) {
				Message.CHAT_ADMIN_GUILD_LIST_PAGE_HASNEXT.vars(vars).send(sender);
			}
			else {
				Message.CHAT_ADMIN_GUILD_LIST_PAGE_NONEXT.vars(vars).send(sender);
			}
		}

		for(NovaGuild guild : plugin.getGuildManager().getGuilds()) {
			if((i + 1 > (page - 1) * perpage || page == 1) && !display) {
				display = true;
				i = 0;
			}

			if(display) {
				String inactiveString = StringUtils.secondsToString(NumberUtils.systemSeconds() - guild.getInactiveTime());

				Map<VarKey, String> vars = new HashMap<>();
				vars.put(VarKey.GUILDNAME, guild.getName());
				vars.put(VarKey.PLAYERNAME, guild.getLeader().getName());
				vars.put(VarKey.TAG, guild.getTag());
				vars.put(VarKey.PLAYERSCOUNT, String.valueOf(guild.getPlayers().size()));
				vars.put(VarKey.INACTIVE, inactiveString);

				String rowMessage = MessageManager.replaceVarKeyMap(rowformat, vars);
				MessageManager.sendMessage(sender, rowMessage);

				if(i + 1 >= perpage) {
					break;
				}
			}

			i++;
		}
	}

	@Override
	public Command getCommand() {
		return command;
	}
}
