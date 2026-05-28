package forever.pajang.minethespire.impl;

import forever.pajang.minethespire.content.item.DarkShurikenItem;

public final class DarkShurikenChargeState {
    private static final int BELL_RING_INTERVAL_TICKS = 10;
    private static final int BELL_RING_COUNT = 3;

    private boolean charging;
    private int chargeTicks;
    private int bellRings;
    private int bellCooldown;

    public void beginCharge() {
        this.charging = true;
        this.chargeTicks = 0;
    }

    public void abortCharge() {
        this.charging = false;
        this.chargeTicks = 0;
    }

    public boolean isCharging() {
        return this.charging;
    }

    public boolean tickCharge() {
        if (!this.charging) {
            return false;
        }

        this.chargeTicks++;
        if (this.chargeTicks < DarkShurikenItem.MIND_BLOOM_CHARGE_TICKS) {
            return false;
        }

        this.charging = false;
        this.chargeTicks = 0;
        return true;
    }

    public void startBellRings() {
        this.bellRings = 1;
        this.bellCooldown = BELL_RING_INTERVAL_TICKS;
    }

    public boolean tickBell() {
        if (this.bellRings <= 0) {
            return false;
        }

        this.bellCooldown--;
        if (this.bellCooldown > 0) {
            return false;
        }

        this.bellRings++;
        if (this.bellRings >= BELL_RING_COUNT) {
            this.bellRings = 0;
            this.bellCooldown = 0;
        }
        else {
            this.bellCooldown = BELL_RING_INTERVAL_TICKS;
        }
        return true;
    }
}
