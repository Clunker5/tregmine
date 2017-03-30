package info.tregmine.api;

public class PlayerMute {
    private int duration;
    private long expiration;
    private boolean expires = true;
    private GenericPlayer muter;
    private GenericPlayer mutee;
    private boolean isEnforced = true;

    public PlayerMute(GenericPlayer p0, GenericPlayer p1) {
        this.muter = p0;
        this.mutee = p1;
        this.duration = -1;
        this.expires = false;
    }

    public PlayerMute(GenericPlayer p0, GenericPlayer p1, int p2) {
        this.duration = p2 * 1000;
        this.muter = p0;
        this.mutee = p1;
        if (p2 == -1) {
            this.expires = false;
        } else {
            this.expiration = System.currentTimeMillis() + this.duration;
        }
    }

    public int getDuration() {
        return this.duration;
    }

    public void setDuration(int p0) {
        this.duration = p0 * 1000;
    }

    public GenericPlayer getMutee() {
        return this.mutee;
    }

    public GenericPlayer getMuter() {
        return this.muter;
    }

    public boolean isCancelled() {
        return this.isEnforced;
    }

    public void setCancelled(boolean p0) {
        this.isEnforced = p0;
    }

    public boolean isExpired() {
        return this.expiration <= System.currentTimeMillis();
    }

    public boolean isIndefinite() {
        return this.duration == -1;
    }

    public void renewExpiration() {
        if (!this.expires)
            return;
        expiration = System.currentTimeMillis() + duration;
    }

    public long secondsLeft() {
        return this.expiration - System.currentTimeMillis() / 1000;
    }
}
