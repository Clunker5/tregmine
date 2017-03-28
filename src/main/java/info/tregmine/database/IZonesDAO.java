package info.tregmine.database;

import info.tregmine.quadtree.Rectangle;
import info.tregmine.zones.Lot;
import info.tregmine.zones.Zone;

import java.util.List;

public interface IZonesDAO {
    void addLot(Lot lot) throws DAOException;

    void addLotUser(int lotId, int userId) throws DAOException;

    void addRectangle(int zoneId, Rectangle rect) throws DAOException;

    void addUser(int zoneId, int userId, Zone.Permission perm) throws DAOException;

    int createZone(Zone zone) throws DAOException;

    void deleteLot(int lotId) throws DAOException;

    void deleteLotUser(int lotId, int userId) throws DAOException;

    void deleteLotUsers(int lotId) throws DAOException;

    void deleteUser(int zoneId, int userId) throws DAOException;

    void deleteZone(int id) throws DAOException;

    List<Integer> getLotOwners(int lotId) throws DAOException;

    List<Lot> getLots(String world) throws DAOException;

    List<Zone> getZones(String world) throws DAOException;

    void updateLotFlags(Lot lot) throws DAOException;

    void updateZone(Zone zone) throws DAOException;
}
