package info.tregmine.commands;

import info.tregmine.Tregmine;
import info.tregmine.api.GenericPlayer;
import info.tregmine.api.Rank;
import info.tregmine.database.DAOException;
import info.tregmine.database.IContext;
import info.tregmine.database.IMentorLogDAO;
import info.tregmine.database.IPlayerDAO;
import info.tregmine.discord.DiscordDelegate;
import net.dv8tion.jda.core.EmbedBuilder;
import net.md_5.bungee.api.ChatColor;

import java.awt.*;
import java.util.Queue;

import static org.bukkit.ChatColor.*;

public class MentorCommand extends AbstractCommand {

    private static int onlineTeachers = 0;

    public MentorCommand(Tregmine tregmine) {
        super(tregmine, "mentor");
        onlineTeachers = tregmine.getOnlineTeachers();
    }

    public static void findMentor(Tregmine plugin, GenericPlayer student) {
        if (student.getRank() != Rank.UNVERIFIED && student.getRank() != Rank.TOURIST) {
            return;
        }
        Queue<GenericPlayer> mentors = plugin.getMentorQueue();
        GenericPlayer mentor = mentors.poll();
        if (mentor != null) {
            startMentoring(plugin, student, mentor);
        } else {
            if (onlineTeachers < 3) {
                student.sendMessage(RED
                        + "As there are less than three teachers online, you can do /mentor complete to skip the mentoring process automatically. Alternatively, you can wait for a mentor to be assigned to you.");
            } else {
                student.sendMessage(YELLOW + "You will now be assigned "
                        + "a mentor to show you around, as soon as one becomes available.");
            }
            Queue<GenericPlayer> students = plugin.getStudentQueue();
            students.offer(student);

            for (GenericPlayer p : plugin.getOnlinePlayers()) {
                if (!p.canMentor()) {
                    continue;
                }

                p.sendMessage(
                        student.getName() + YELLOW + " needs a mentor! Type /mentor to " + "offer your services!");
            }
            if (plugin.discordEnabled()) {
                DiscordDelegate delegate = plugin.getDiscordDelegate();
                delegate.getChatChannel().sendMessage(new EmbedBuilder(null)
                        .setTitle(student.getName() + " needs a mentor!", null)
                        .setDescription("Join Tregmine and offer your services!")
                        .setColor(Color.GREEN)
                        .setFooter(delegate.getEmbedBuilder().getFooter(), delegate.getClient().getSelfUser().getAvatarUrl())
                        .build()).complete();
            }

        }
    }

    public static void startMentoring(Tregmine tregmine, GenericPlayer student, GenericPlayer mentor) {
        student.setMentor(mentor);
        mentor.setStudent(student);

        try (IContext ctx = tregmine.createContext()) {
            IMentorLogDAO mentorLogDAO = ctx.getMentorLogDAO();
            int mentorLogId = mentorLogDAO.getMentorLogId(student, mentor);
            Tregmine.LOGGER.info("Mentor log id: " + mentorLogId);
            if (mentorLogId == 0) {
                mentorLogDAO.insertMentorLog(student, mentor);
            } else {
                mentorLogDAO.updateMentorLogResume(mentorLogId);
            }
        } catch (DAOException e) {
            throw new RuntimeException(e);
        }

        Tregmine.LOGGER.info("[MENTOR] " + mentor.getName() + " is " + "mentoring " + student.getName());

        // Instructions for students
        student.sendMessage(mentor.getName() + GREEN + " has been assigned as your mentor!");
        student.sendMessage(YELLOW + "He or she will show you "
                + "around, answer any questions, and help you find a place " + "to build.");
        student.sendMessage(YELLOW + "If your mentor turns out to be unhelpful, " + "type " + RED
                + "/mentor cancel" + YELLOW + " to stop and wait " + "for a new mentor to become available.");

        // Instructions for mentor
        mentor.sendMessage(GREEN + "You have been assigned to " + "mentor " + student.getName() + GREEN + ".");
        mentor.sendMessage(YELLOW + "Please do this: ");
        mentor.sendMessage(YELLOW + "1. Explain basic rules (" + RED
                + "Do not force your student to read the rules, or take a test " + YELLOW + ")");
        mentor.sendMessage(YELLOW + "2. Demonstrate basic commands");
        mentor.sendMessage(YELLOW + "3. Show him or her around");
        mentor.sendMessage(
                YELLOW + "4. Help him or her to find a lot " + "and start building. If you own a zone, you may sell "
                        + "a lot, but keep in mind that it might be a good idea "
                        + "to let other players make offers too. Your students will "
                        + "also be able to build anywhere as long as they are within a " + "50 block radius of you.");
        mentor.sendMessage(YELLOW + "Scamming new players will not be  " + "tolerated.");
        mentor.sendMessage(YELLOW + "Mentoring takes at least 15 minutes, and "
                + "after that time has passed you can upgrade the tourist to " + "settler rank by doing " + GREEN
                + "/mentor complete" + YELLOW + ".");
        mentor.sendMessage(YELLOW + "Please start by teleporting to " + student.getName() + YELLOW
                + ", or by summoning him or her!");
    }

    @Override
    public boolean handlePlayer(GenericPlayer player, String[] args) {
        String action = "queue";
        if (args.length > 0) {
            action = args[0];
        }

        if ("queue".equalsIgnoreCase(action)) {
            if (!player.canMentor()) {
                player.sendMessage(RED + "You have not been granted mentoring abilities.");
                return true;
            }

            if (player.getStudent() != null) {
                player.sendMessage(RED + "You can only mentor one " + "student at any given time.");
                return true;
            }

            Queue<GenericPlayer> students = tregmine.getStudentQueue();
            if (students.size() > 0) {
                GenericPlayer student = students.poll();
                startMentoring(tregmine, student, player);
                return true;
            }

            Queue<GenericPlayer> mentors = tregmine.getMentorQueue();
            mentors.offer(player);

            player.sendMessage(GREEN + "You are now part of the mentor queue. " + "You are number "
                    + mentors.size() + ". Type /mentor cancel " + "to opt out.");
        } else if ("cancel".equalsIgnoreCase(action)) {
            if (player.getRank() == Rank.TOURIST) {
                GenericPlayer mentor = player.getMentor();

                try (IContext ctx = tregmine.createContext()) {
                    IMentorLogDAO mentorLogDAO = ctx.getMentorLogDAO();
                    int mentorLogId = mentorLogDAO.getMentorLogId(player, mentor);
                    mentorLogDAO.updateMentorLogEvent(mentorLogId, IMentorLogDAO.MentoringEvent.CANCELLED);
                } catch (DAOException e) {
                    throw new RuntimeException(e);
                }

                player.setMentor(null);
                mentor.setStudent(null);

                mentor.sendMessage(player.getName() + "" + RED + " cancelled " + "mentoring with you.");
                player.sendMessage(GREEN + "Mentoring cancelled. Attempting to " + "find you a new mentor.");

                findMentor(tregmine, player);
            } else {
                Queue<GenericPlayer> mentors = tregmine.getMentorQueue();
                if (!mentors.contains(player)) {
                    player.sendMessage(RED + "You are not part of the mentor queue. "
                            + "If you have already been assigned a student, you cannot " + "abort the mentoring.");
                    return true;
                }
                mentors.remove(player);

                player.sendMessage(GREEN + "You are no longer part of the mentor queue.");
            }
        } else if ("complete".equalsIgnoreCase(action)) {
            if (!player.getRank().canMentor() && tregmine.getOnlineTeachers() >= 3) {
                player.sendMessage(RED + "You do not have permission to mentor.");
                return true;
            }
            if (tregmine.getOnlineTeachers() < 3 && player.getMentor() == null && player.getRank() == Rank.TOURIST) {
                try (IContext ctx = tregmine.createContext()) {
                    player.setRank(Rank.SETTLER);

                    IPlayerDAO playerDAO = ctx.getPlayerDAO();
                    playerDAO.updatePlayer(player);
                    playerDAO.updatePlayerInfo(player);

                    IMentorLogDAO mentorLogDAO = ctx.getMentorLogDAO();
                    int mentorLogId = mentorLogDAO.getMentorLogId(player, player);

                    mentorLogDAO.updateMentorLogEvent(mentorLogId, IMentorLogDAO.MentoringEvent.SKIPPED);
                    player.sendMessage(ChatColor.GREEN + "You have been elevated to settler status.");
                } catch (DAOException e) {
                    throw new RuntimeException(e);
                }
                return true;
            }
            GenericPlayer student = player.getStudent();
            if (student == null) {
                player.sendMessage(RED + "You are not mentoring anyone right now.");
                return true;
            }
            if (student != null) {
                try (IContext ctx = tregmine.createContext()) {
                    student.setRank(Rank.SETTLER);

                    IPlayerDAO playerDAO = ctx.getPlayerDAO();
                    playerDAO.updatePlayer(student);
                    playerDAO.updatePlayerInfo(student);

                    IMentorLogDAO mentorLogDAO = ctx.getMentorLogDAO();
                    int mentorLogId = mentorLogDAO.getMentorLogId(student, player);

                    mentorLogDAO.updateMentorLogEvent(mentorLogId, IMentorLogDAO.MentoringEvent.COMPLETED);
                    player.sendMessage(GREEN + "Mentoring of " + student.getName() + GREEN + " has now finished!");
                    player.giveExp(100);

                    student.sendMessage(GREEN + "Congratulations! You have now achieved "
                            + "settler status. We hope you'll enjoy your stay on Tregmine!");
                } catch (DAOException e) {
                    throw new RuntimeException(e);
                }
                return true;
            }
        } else {
            return false;
        }

        return true;
    }
}
