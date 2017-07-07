package info.tregmine.zones;

import info.tregmine.api.GenericPlayer;
import info.tregmine.quadtree.Point;
import info.tregmine.quadtree.Rectangle;

import java.util.*;

public class Zone {
    private String texture;
    private int id;
    private String world;
    private String name;
    private List<Rectangle> rects;
    private Set<Flags> flags;
    private boolean enterDefault = true;
    private boolean placeDefault = true;
    private boolean destroyDefault = true;
    private boolean pvp = false;
    private boolean hostiles = false;
    private boolean communism = false;
    private boolean publicProfile = false;
    private String textEnter;
    private String textExit;
    private String mainOwner;
    private Map<Integer, Permission> users;

    public Zone() {
        rects = new ArrayList<Rectangle>();
        users = new HashMap<Integer, Permission>();

        this.flags = EnumSet.noneOf(Flags.class);
    }

    public void addRect(Rectangle rect) {
        rects.add(rect);
    }

    public void addUser(GenericPlayer player, Permission perm) {
        users.put(player.getId(), perm);
    }

    public boolean contains(Point p) {
        for (Rectangle rect : rects) {
            if (rect.contains(p)) {
                return true;
            }
        }

        return false;
    }

    public void deleteUser(GenericPlayer player) {
        users.remove(player.getId());
    }

    public boolean getDestroyDefault() {
        return destroyDefault;
    }

    public void setDestroyDefault(boolean destroyDefault) {
        this.destroyDefault = destroyDefault;
    }

    public boolean getEnterDefault() {
        return enterDefault;
    }

    public void setEnterDefault(boolean enterDefault) {
        this.enterDefault = enterDefault;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMainOwner() {
        return this.mainOwner;
    }

    public void setMainOwner(String _owner) {
        this.mainOwner = _owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getPlaceDefault() {
        return placeDefault;
    }

    public void setPlaceDefault(boolean placeDefault) {
        this.placeDefault = placeDefault;
    }

    public List<Rectangle> getRects() {
        return rects;
    }

    public void setRects(List<Rectangle> rects) {
        this.rects = rects;
    }

    public String getTextEnter() {
        return textEnter;
    }

    public void setTextEnter(String textEnter) {
        this.textEnter = textEnter;
    }

    public String getTextExit() {
        return textExit;
    }

    public void setTextExit(String textExit) {
        this.textExit = textExit;
    }

    public String getTexture() {
        if (this.texture == null) {
            return "";
        }

        return this.texture;
    }

    public void setTexture(String texture) {
        this.texture = texture;
    }

    public Permission getUser(GenericPlayer player) {
        return users.get(player.getId());
    }

    public Collection<Integer> getUsers() {
        return users.keySet();
    }

    public void setUsers(Map<Integer, Permission> v) {
        this.users = v;
    }

    public String getWorld() {
        return world;
    }

    public void setWorld(String world) {
        this.world = world;
    }

    public boolean hasFlag(Flags flag) {
        return flags.contains(flag);
    }

    public boolean hasHostiles() {
        return hostiles;
    }

    public boolean hasPublicProfile() {
        return publicProfile;
    }

    public boolean isCommunist() {
        return communism;
    }

    public void setCommunist(boolean v) {
        this.communism = v;
    }

    public boolean isPvp() {
        return pvp;
    }

    public void setPvp(boolean pvp) {
        this.pvp = pvp;
    }

    public void removeFlag(Flags flag) {
        flags.remove(flag);
    }

    public void setFlag(Flags flag) {
        flags.add(flag);
    }

    public void setHostiles(boolean hostiles) {
        this.hostiles = hostiles;
    }

    public void setPublicProfile(boolean v) {
        this.publicProfile = v;
    }

    // Flags are stored as integers - order must _NOT_ be changed
    public enum Flags {
        BLOCK_WARNED, // default false == 1
        ADMIN_ONLY, // default false == 2
        REQUIRE_RESIDENCY // default false == 4
    }

    public enum lotStatus {
        dropzone, bank
    }

    public enum Permission {
        // can modify the zone in any way
        Owner("%s is now an owner of %s.", "You are now an owner of %s.", "%s is no longer an owner of %s.",
                "You are no longer an owner of %s.", "You are an owner in this zone."),
        // can build in the zone
        Maker("%s is now a maker in %s.", "You are now a maker in %s.", "%s is no longer a maker in %s.",
                "You are no longer a maker in %s.", "You are a maker in this zone."),
        // is allowed in the zone, if this isn't the default
        Allowed("%s is now allowed in %s.", "You are now allowed in %s.", "%s is no longer allowed in %s.",
                "You are no longer allowed in %s.", "You are allowed in this zone."),
        // banned from the zone
        Banned("%s is now banned from %s.", "You have been banned from %s.", "%s is no longer banned in %s.",
                "You are no longer banned in %s.", "You are banned from this zone.");

        private String addedConfirmation;
        private String addedNotification;
        private String delConfirmation;
        private String delNotification;
        private String permNotification;

        Permission(String addedConfirmation, String addedNotification, String delConfirmation,
                   String delNotification, String permNotification) {
            this.addedConfirmation = addedConfirmation;
            this.addedNotification = addedNotification;
            this.delConfirmation = delConfirmation;
            this.delNotification = delNotification;
            this.permNotification = permNotification;
        }

        public static Permission fromString(String type) {
            if ("owner".equalsIgnoreCase(type)) {
                return Permission.Owner;
            } else if ("maker".equalsIgnoreCase(type)) {
                return Permission.Maker;
            } else if ("allowed".equalsIgnoreCase(type)) {
                return Permission.Allowed;
            } else if ("banned".equalsIgnoreCase(type)) {
                return Permission.Banned;
            }

            return null;
        }

        public String getAddedConfirmation() {
            return addedConfirmation;
        }

        public String getAddedNotification() {
            return addedNotification;
        }

        public String getDeletedConfirmation() {
            return delConfirmation;
        }

        public String getDeletedNotification() {
            return delNotification;
        }

        public String getPermissionNotification() {
            return permNotification;
        }
    }
}
