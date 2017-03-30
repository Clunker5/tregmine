package info.tregmine.database;

import info.tregmine.api.GenericPlayer;

import java.util.List;

public interface IMailDAO {
    boolean deleteMail(String username, int mailId) throws DAOException;

    List<String[]> getAllMail(String username) throws DAOException;

    int getMailTotal(String username) throws DAOException;

    int getMailTotalEver(String username) throws DAOException;

    boolean sendMail(GenericPlayer player, String sendTo, String message) throws DAOException;
}
