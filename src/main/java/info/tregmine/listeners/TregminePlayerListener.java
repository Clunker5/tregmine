package info.tregmine.listeners;

import info.tregmine.Tregmine;
import info.tregmine.api.*;
import info.tregmine.api.lore.Created;
import info.tregmine.api.util.ScoreboardClearTask;
import info.tregmine.commands.MentorCommand;
import info.tregmine.database.*;
import info.tregmine.events.PlayerMoveBlockEvent;
import info.tregmine.quadtree.Point;
import info.tregmine.zones.Lot;
import info.tregmine.zones.ZoneWorld;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scoreboard.*;

import java.util.*;

public class TregminePlayerListener implements Listener {
    private Tregmine plugin;
    private Map<Item, GenericPlayer> droppedItems;

    public TregminePlayerListener(Tregmine instance) {
        this.plugin = instance;

        droppedItems = new HashMap<>();
    }

    private void activateGuardians() {
        // Identify all guardians and categorize them based on their current
        // state
        Collection<? extends Player> players = plugin.getServer().getOnlinePlayers();
        Set<GenericPlayer> guardians = new HashSet<GenericPlayer>();
        List<GenericPlayer> activeGuardians = new ArrayList<GenericPlayer>();
        List<GenericPlayer> inactiveGuardians = new ArrayList<GenericPlayer>();
        List<GenericPlayer> queuedGuardians = new ArrayList<GenericPlayer>();
        for (Player srvPlayer : players) {
            GenericPlayer guardian = plugin.getPlayer(srvPlayer.getName());
            if (guardian == null || guardian.getRank() != Rank.GUARDIAN) {
                continue;
            }

            GenericPlayer.GuardianState state = guardian.getGuardianState();
            if (state == null) {
                state = GenericPlayer.GuardianState.QUEUED;
            }

            switch (state) {
                case ACTIVE:
                    activeGuardians.add(guardian);
                    break;
                case INACTIVE:
                    inactiveGuardians.add(guardian);
                    break;
                case QUEUED:
                    queuedGuardians.add(guardian);
                    break;
            }

            guardian.setGuardianState(GenericPlayer.GuardianState.QUEUED);
            guardians.add(guardian);
        }

        Collections.sort(activeGuardians, new RankComparator());
        Collections.sort(inactiveGuardians, new RankComparator(true));
        Collections.sort(queuedGuardians, new RankComparator());

        int idealCount = (int) Math.ceil(Math.sqrt(players.size()) / 2);
        // There are not enough guardians active, we need to activate a few more
        if (activeGuardians.size() <= idealCount) {
            // Make a pool of every "willing" guardian currently online
            List<GenericPlayer> activationList = new ArrayList<GenericPlayer>();
            activationList.addAll(activeGuardians);
            activationList.addAll(queuedGuardians);

            // If the pool isn't large enough to satisfy demand, we add the
            // guardians
            // that have made themselves inactive as well.
            if (activationList.size() < idealCount) {
                int diff = idealCount - activationList.size();
                // If there aren't enough of these to satisfy demand, we add all
                // of them
                if (diff >= inactiveGuardians.size()) {
                    activationList.addAll(inactiveGuardians);
                }
                // Otherwise we just add the lowest ranked of the inactive
                else {
                    activationList.addAll(inactiveGuardians.subList(0, diff));
                }
            }

            // If there are more than necessarry guardians online, only activate
            // the most highly ranked.
            Set<GenericPlayer> activationSet;
            if (activationList.size() > idealCount) {
                Collections.sort(activationList, new RankComparator());
                activationSet = new HashSet<>(activationList.subList(0, idealCount));
            } else {
                activationSet = new HashSet<>(activationList);
            }

            // Perform activation
            StringBuffer globalMessage = new StringBuffer();
            String delim = "";
            for (GenericPlayer guardian : activationSet) {
                guardian.setGuardianState(GenericPlayer.GuardianState.ACTIVE);
                globalMessage.append(delim);
                globalMessage.append(guardian.getName());
                delim = ", ";
            }

            Set<GenericPlayer> oldActiveGuardians = new HashSet<GenericPlayer>(activeGuardians);
            if (!activationSet.containsAll(oldActiveGuardians) || activationSet.size() != oldActiveGuardians.size()) {

                plugin.getServer().broadcastMessage(ChatColor.BLUE + "Active guardians are: " + globalMessage
                        + ". Please contact any of them if you need help.");

                // Notify previously active guardian of their state change
                for (GenericPlayer guardian : activeGuardians) {
                    if (!activationSet.contains(guardian)) {
                        guardian.sendMessage(ChatColor.BLUE
                                + "You are no longer on active duty, and should not respond to help requests, unless asked by an admin or active guardian.");
                    }
                }

                // Notify previously inactive guardians of their state change
                for (GenericPlayer guardian : inactiveGuardians) {
                    if (activationSet.contains(guardian)) {
                        guardian.sendMessage(ChatColor.BLUE
                                + "You have been restored to active duty and should respond to help requests.");
                    }
                }

                // Notify previously queued guardians of their state change
                for (GenericPlayer guardian : queuedGuardians) {
                    if (activationSet.contains(guardian)) {
                        guardian.sendMessage(
                                ChatColor.BLUE + "You are now on active duty and should respond to help requests.");
                    }
                }
            }
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        GenericPlayer player = plugin.getPlayer(event.getEntity());
        player.setLastPos(player.getLocation());
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        Entity e = event.getEntity();
        if (e instanceof Player) {
            if (event.getCause() == DamageCause.HOT_FLOOR) {
                event.setCancelled(true);
            }
        } else {
            return;
        }
    }

    @EventHandler
    public void onPlayerBlockMove(PlayerMoveBlockEvent event) {
        GenericPlayer player = event.getPlayer();

        // To add player.hasBadge for a flight badge when made
        if (player.getRank().canFly() && player.isFlying() && player.isSprinting()) {
            player.setFlySpeed(0.4f); // To be balanced
        } else {
            player.setFlySpeed(0.1f); // 0.1 is default
        }
    }

    @EventHandler
    public void onPlayerClick(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK || event.getHand() != EquipmentSlot.HAND) {
            return;
        }
        Player player = event.getPlayer();
        BlockState block = event.getClickedBlock().getState();
        if (block instanceof Skull) {
            Skull skull = (Skull) block;
            if (!skull.getSkullType().equals(SkullType.PLAYER)) {
                return;
            }
            OfflinePlayer owner = skull.getOwningPlayer();
            GenericPlayer skullowner = plugin.getPlayerOffline(owner);
            if (skullowner != null) {
                ChatColor C = skullowner.getNameColor();
                player.sendMessage(ChatColor.AQUA + "This is " + C + owner + "'s " + ChatColor.AQUA + "head!");
            } else {
                player.sendMessage(ChatColor.AQUA + "This is " + ChatColor.WHITE + owner + ChatColor.AQUA + "'s head!");

            }
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        GenericPlayer player = this.plugin.getPlayer(event.getPlayer());

        if (player.getGameMode() == GameMode.CREATIVE) {
            event.setCancelled(true);
            return;
        }

        if (!player.getRank().arePickupsLogged()) {
            return;
        }

        if (!player.getRank().canPickup()) {
            event.setCancelled(true);
            return;
        }

        Item item = event.getItemDrop();
        droppedItems.put(item, player);
    }

    @EventHandler
    public void onPlayerFlight(PlayerToggleFlightEvent event) {
        GenericPlayer player = plugin.getPlayer(event.getPlayer());
        if (player.getRank().canModifyZones()) {
            return;
        }

        if (!player.getRank().canFly()) {
            event.setCancelled(true);
        }

        if (player.hasFlag(GenericPlayer.Flags.HARDWARNED) || player.hasFlag(GenericPlayer.Flags.SOFTWARNED)) {

            event.setCancelled(true);
        }

        Location loc = player.getLocation();

        ZoneWorld world = plugin.getWorld(loc.getWorld());
        Lot lot = world.findLot(new Point(loc.getBlockX(), loc.getBlockZ()));
        if (lot == null) {
            return;
        }

        if (!lot.hasFlag(Lot.Flags.FLIGHT_ALLOWED)) {
            event.setCancelled(true);
        }

    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Player player = event.getPlayer();
            Block block = event.getClickedBlock();
            Location loc = block.getLocation();

            if (player.getInventory().getItemInMainHand().getType() == Material.BOOK) {

                player.sendMessage(ChatColor.DARK_AQUA + "Type: " + ChatColor.AQUA + block.getType().toString() + " ("
                        + ChatColor.BLUE + block.getType().toString() + ChatColor.DARK_AQUA + ")");
                player.sendMessage(ChatColor.DARK_AQUA + "Data: " + ChatColor.AQUA + (int) block.getData());
                player.sendMessage(ChatColor.RED + "X" + ChatColor.WHITE + ", " + ChatColor.GREEN + "Y"
                        + ChatColor.WHITE + ", " + ChatColor.BLUE + "Z" + ChatColor.WHITE + ": " + ChatColor.RED
                        + loc.getBlockX() + ChatColor.WHITE + ", " + ChatColor.GREEN + loc.getBlockY() + ChatColor.WHITE
                        + ", " + ChatColor.BLUE + loc.getBlockZ());

                try {
                    player.sendMessage(ChatColor.DARK_AQUA + "Biome: " + ChatColor.AQUA + block.getBiome().toString());
                } catch (Exception e) {
                    player.sendMessage(ChatColor.DARK_AQUA + "Biome: " + ChatColor.AQUA + "NULL");
                }

                Tregmine.LOGGER.info("POS: " + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ());
            }
        }
    }

    @EventHandler
    public void onPlayerItemHeld(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE) {
            for (ItemStack item : player.getInventory().getContents()) {
                if (item != null) {
                    if (item.hasItemMeta()) {
                        ItemMeta meta = item.getItemMeta();
                        List<String> oldlore = meta.getLore();
                        String line2 = "";
                        if (oldlore != null) {
                            line2 = oldlore.get(1).replace("Â", "");
                        }
                        List<String> lore = new ArrayList<String>();
                        lore.add(Created.CREATIVE.toColorString());
                        GenericPlayer p = this.plugin.getPlayer(player);
                        if (line2.contains("by: ") && !line2.contains(p.getChatNameNoHover())) {
                            lore.add(line2 + ", " + p.getChatNameNoHover());
                        } else {
                            lore.add(ChatColor.WHITE + "by: " + p.getChatNameNoHover());
                        }
                        lore.add(ChatColor.WHITE + "Value: " + ChatColor.MAGIC + "0000" + ChatColor.RESET
                                + ChatColor.WHITE + " Treg");
                        meta.setLore(lore);
                        item.setItemMeta(meta);
                    }
                }
            }
        }

        GenericPlayer p = plugin.getPlayer(player);
        if (p == null) {
            Tregmine.LOGGER.info(player.getName() + " was not found in player map.");
            return;
        }

        if (p.getCurrentInventory() != null) {
            p.saveInventory(p.getCurrentInventory());
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);
        GenericPlayer player = plugin.getPlayer(event.getPlayer());
        if (player == null) {
            event.getPlayer().kickPlayer("error loading profile!");
            return;
        }
        Rank rank = player.getRank();
        if (player.getIsStaff()) {
            List<StaffNews> news = null;
            try (IContext ctx = this.plugin.createContext()) {
                IStaffNewsDAO newsDAO = ctx.getNewsByUploader();
                news = newsDAO.getStaffNews();

            } catch (DAOException e) {
                throw new RuntimeException(e);
            }
            if (news == null) {
            } else {
                // There's messages :)
                for (StaffNews singleNews : news) {
                    String username = singleNews.getUsername();
                    String text = singleNews.getText();
                    player.sendMessage(
                            ChatColor.GREEN + "There is a message from " + ChatColor.RESET + ChatColor.BLUE + username);
                    player.sendMessage(ChatColor.GOLD + text);
                }
            }
        }
        try (IContext ctx = this.plugin.createContext()) {
            IMailDAO maildao = ctx.getMailDAO();
            int total = maildao.getMailTotal(player.getName());
            if (total != 0) {
                String suffix = "";
                if (total == 1) {
                    suffix = "message";
                } else {
                    suffix = "messages";
                }
                player.sendMessage(
                        ChatColor.AQUA + "You have " + total + " " + suffix + " -- Type /mail read to view them.");
            }
        } catch (DAOException e) {
            throw new RuntimeException(e);
        }

        // Handle invisibility, if set
        List<GenericPlayer> players = plugin.getOnlinePlayers();
        if (player.hasFlag(GenericPlayer.Flags.INVISIBLE)) {
            player.sendMessage(ChatColor.YELLOW + "You are now invisible!");

            // Hide the new player from all existing players
            for (GenericPlayer current : players) {
                if (!current.getRank().canVanish()) {
                    current.hidePlayer(player);
                } else {
                    current.showPlayer(player);
                }
            }
        } else {
            for (GenericPlayer current : players) {
                current.showPlayer(player);
            }
        }

        World cWorld = player.getWorld();
        String[] worldNamePortions = cWorld.getName().split("_");

        if (worldNamePortions[0].equalsIgnoreCase("world")) {
            player.loadInventory("survival", false);
        } else {
            player.loadInventory(worldNamePortions[0], false);
        }

        // Hide currently invisible players from the player that just signed on
        for (GenericPlayer current : players) {
            if (current.hasFlag(GenericPlayer.Flags.INVISIBLE)) {
                player.hidePlayer(current);
            } else {
                player.showPlayer(current);
            }

            if (player.getRank().canVanish()) {
                player.showPlayer(current);
            }
        }

        // Set applicable game mode
        if (rank == Rank.BUILDER) {
            player.setGameMode(GameMode.CREATIVE);
        } else if (!rank.canUseCreative()) {
            player.setGameMode(GameMode.SURVIVAL);
        }

        // Try to find a mentor for new players
        if (rank == Rank.UNVERIFIED) {

            return;
        }

        // Check if the player is allowed to fly
        if (player.hasFlag(GenericPlayer.Flags.HARDWARNED) || player.hasFlag(GenericPlayer.Flags.SOFTWARNED)) {
            player.sendMessage("You are warned and are not allowed to fly.");
            player.setAllowFlight(false);
        } else if (rank.canFly()) {
            if (player.hasFlag(GenericPlayer.Flags.FLY_ENABLED) || player.getGameMode() == GameMode.SPECTATOR) {
                player.sendMessage("Flying: Allowed and Enabled! Toggle flying with /fly");
                player.setAllowFlight(true);
            } else {
                player.sendMessage("Flying: Allowed but Disabled! Toggle flying with /fly");
                player.setAllowFlight(false);
            }
        } else {
            player.sendMessage("You are NOT allowed to fly");
            player.setAllowFlight(false);
        }

        try (IContext ctx = plugin.createContext()) {
            if (player.getPlayTime() > 10 * 3600 && rank == Rank.SETTLER) {
                player.setRank(Rank.RESIDENT);
                rank = Rank.RESIDENT;

                IPlayerDAO playerDAO = ctx.getPlayerDAO();
                playerDAO.updatePlayer(player);
                playerDAO.updatePlayerInfo(player);

                player.sendMessage(
                        ChatColor.DARK_GREEN + "Congratulations! " + "You are now a resident on Tregmine!");
            }

            // Load motd
            IMotdDAO motdDAO = ctx.getMotdDAO();
            String message = motdDAO.getMotd();
            if (message != null) {
                String[] lines = message.split("\n");
                for (String line : lines) {
                    player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + line);
                }
            }
        } catch (DAOException e) {
            throw new RuntimeException(e);
        }

        // Show a score board
        if (player.isOnline()) {
            ScoreboardManager manager = Bukkit.getScoreboardManager();
            Scoreboard board = manager.getNewScoreboard();

            Objective objective = board.registerNewObjective("1", "2");
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
            objective.setDisplayName("" + ChatColor.AQUA + "" + ChatColor.BOLD + "Welcome to Tregmine!");

            // Update staff statistics
            plugin.updateStatistics();

            // Get a fake offline player
            String desc = ChatColor.BLUE + "Online guardians: ";
            Score score = objective.getScore(desc);

            score.setScore(plugin.getOnlineGuardians());

            String juniors = ChatColor.RED + "Online juniors: ";
            Score junior = objective.getScore(juniors);
            junior.setScore(plugin.getOnlineJuniors());

            String seniors = ChatColor.DARK_RED + "Online seniors: ";
            Score senior = objective.getScore(seniors);
            senior.setScore(plugin.getOnlineSeniors());
            try {
                player.setScoreboard(board);

                ScoreboardClearTask.start(plugin, player);
            } catch (IllegalStateException e) {
                // ignore
            }
        }

        // Recalculate guardians
        activateGuardians();

        if (rank == Rank.TOURIST || rank == Rank.UNVERIFIED) {
            // Try to find a mentor for tourists that rejoin
            MentorCommand.findMentor(plugin, player);
        } else if (player.canMentor()) {
            Queue<GenericPlayer> students = plugin.getStudentQueue();
            if (students.size() > 0) {
                player.sendMessage(
                        ChatColor.YELLOW + "Mentors are needed! " + "Type /mentor to offer your services!");
            }
        }

        if (rank == Rank.DONATOR && !player.hasBadge(Badge.PHILANTROPIST)) {
            player.awardBadgeLevel(Badge.PHILANTROPIST, "For being a Tregmine donator!");
        }
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        event.setLeaveMessage(null);
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {

        GenericPlayer player;

        try {
            player = plugin.addPlayer(event.getPlayer(), event.getAddress());
            if (player == null) {
                event.disallow(Result.KICK_OTHER, ChatColor.RED + "Something went wrong");
                return;
            }
        } catch (PlayerBannedException e) {
            event.disallow(Result.KICK_BANNED, e.getMessage());
            return;
        }

        if (player.getRank() == Rank.UNVERIFIED) {
            player.setChatState(GenericPlayer.ChatState.SETUP);
        }

        if (player.getLocation().getWorld().getName().matches("world_the_end")) {
            player.teleportWithHorse(this.plugin.getServer().getWorld("world").getSpawnLocation());
        }
        if (plugin.getLockdown() && !event.getPlayer().isOp()) {

            if (!player.getIsStaff()) {
                event.disallow(Result.KICK_OTHER, ChatColor.GOLD + "Tregmine " + ChatColor.RED
                        + "is on lockdown, only staff can join. Check the forums for more info.");
                return;
            }

        }

        if (player.getKeyword() != null) {
            String url = plugin.getConfig().getString("general.url").replace("http://", "").replace("https://", "")
                    + ":" + Bukkit.getPort();
            String urlnoport = plugin.getConfig().getString("general.url").replace("http://", "").replace("https://",
                    "");

            String keyword = player.getKeyword() + url.toLowerCase();
            Tregmine.LOGGER.warning("host: " + event.getHostname());
            Tregmine.LOGGER.warning("keyword:" + keyword);

            if (keyword.equals(event.getHostname().toLowerCase()) || keyword.matches(urlnoport)) {
                Tregmine.LOGGER.warning(player.getName() + " keyword :: success");
            } else {
                Tregmine.LOGGER.warning(player.getName() + " keyword :: faild");
                event.disallow(Result.KICK_BANNED, ChatColor.RED
                        + "Wrong keyword!\nIf you have forgotten your keyword, contact a senior admin via Discord.\nhttp://tinyurl.com/TregmineDiscord");
            }
        } else {
            Tregmine.LOGGER.warning(player.getName() + " keyword :: notset");
        }

        if (player.getRank() == Rank.GUARDIAN) {
            player.setGuardianState(GenericPlayer.GuardianState.QUEUED);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        GenericPlayer player = this.plugin.getPlayer(event.getPlayer());
        if (player.getFrozen()) {
            event.setCancelled(true);
        }
        if (player.isAfk()) {
            player.setAfk(false);
        }
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        GenericPlayer player = this.plugin.getPlayer(event.getPlayer());

        if (player.getGameMode() == GameMode.CREATIVE) {
            event.setCancelled(true);
            return;
        }

        if (!player.getRank().arePickupsLogged()) {
            return;
        }

        if (!player.getRank().canPickup()) {
            event.setCancelled(true);
            return;
        }

        try (IContext ctx = plugin.createContext()) {
            Item item = event.getItem();
            GenericPlayer droppedBy = droppedItems.get(item);

            if (droppedBy != null && droppedBy.getId() != player.getId()) {
                ItemStack stack = item.getItemStack();

                ILogDAO logDAO = ctx.getLogDAO();
                logDAO.insertGiveLog(droppedBy, player, stack);

                player.sendMessage(new TextComponent(ChatColor.YELLOW + "You got " + stack.getAmount() + " "
                        + stack.getType() + " from " + droppedBy.getName() + "."));

                if (droppedBy.isOnline()) {
                    droppedBy.sendMessage(new TextComponent(ChatColor.YELLOW + "You gave " + stack.getAmount()
                            + " " + stack.getType() + " to " + player.getName() + "."));
                }
            }
            droppedItems.remove(item);
        } catch (DAOException e) {
            throw new RuntimeException(e);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        GenericPlayer player = plugin.getPlayer(event.getPlayer());
        if (player == null) {
            Tregmine.LOGGER.info(event.getPlayer().getName() + " was not found " + "in players map when quitting.");
            return;
        }

        if (player.isCombatLogged()) {
            player.setHealth(0);
            Tregmine.LOGGER.info(event.getPlayer().getName() + " just combat logged... What a fool!");
        }

        player.saveInventory(player.getCurrentInventory());
        event.setQuitMessage(null);

        if (!player.isOp() && player.getQuitCause() != QuitCause.AFK) {
            if (player.getQuitMessage() != null) {
                plugin.broadcast(player.getChatName(),
                        new TextComponent(" quit: " + ChatColor.YELLOW + player.getQuitMessage()));
            } else {
                Random rand = new Random();
                int msgIndex = rand.nextInt(plugin.getQuitMessages().size());
                plugin.broadcast(new TextComponent(ChatColor.GRAY + "Quit: "), player.getChatName(),
                        new TextComponent(ChatColor.GRAY + " " + plugin.getQuitMessages().get(msgIndex)));
            }
        }

        // Look if there are any students being mentored by the exiting player
        if (player.getStudent() != null) {
            GenericPlayer student = player.getStudent();

            try (IContext ctx = plugin.createContext()) {
                IMentorLogDAO mentorLogDAO = ctx.getMentorLogDAO();
                int mentorLogId = mentorLogDAO.getMentorLogId(student, player);
                mentorLogDAO.updateMentorLogEvent(mentorLogId, IMentorLogDAO.MentoringEvent.CANCELLED);
            } catch (DAOException e) {
                throw new RuntimeException(e);
            }

            student.setMentor(null);
            player.setStudent(null);

            student.sendMessage(ChatColor.RED + "Your mentor left. We'll try "
                    + "to find a new one for you as quickly as possible.");

            MentorCommand.findMentor(plugin, student);
        } else if (player.getMentor() != null) {
            GenericPlayer mentor = player.getMentor();

            try (IContext ctx = plugin.createContext()) {
                IMentorLogDAO mentorLogDAO = ctx.getMentorLogDAO();
                int mentorLogId = mentorLogDAO.getMentorLogId(player, mentor);
                mentorLogDAO.updateMentorLogEvent(mentorLogId, IMentorLogDAO.MentoringEvent.CANCELLED);
            } catch (DAOException e) {
                throw new RuntimeException(e);
            }

            mentor.setStudent(null);
            player.setMentor(null);

            mentor.sendMessage(ChatColor.RED + "Your student left. :(");
        }

        plugin.removePlayer(player);
        Tregmine.LOGGER.info("Unloaded settings for " + player.getName() + ".");

        activateGuardians();
    }

    @EventHandler
    public void onPlayerRespawnSave(PlayerRespawnEvent event) {
        GenericPlayer p = plugin.getPlayer(event.getPlayer());
        p.saveInventory(p.getCurrentInventory());
    }

    @EventHandler
    public void onPreCommand(PlayerCommandPreprocessEvent event) {
        // Tregmine.LOGGER.info("COMMAND: " + event.getPlayer().getName() + "::"
        // + event.getMessage());
    }

    private static class RankComparator implements Comparator<GenericPlayer> {
        private int order;

        public RankComparator() {
            this.order = 1;
        }

        public RankComparator(boolean reverseOrder) {
            this.order = reverseOrder ? -1 : 1;
        }

        @Override
        public int compare(GenericPlayer a, GenericPlayer b) {
            return order * (a.getGuardianRank() - b.getGuardianRank());
        }
    }
}
