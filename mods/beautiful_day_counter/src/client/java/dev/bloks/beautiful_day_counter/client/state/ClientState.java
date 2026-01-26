package dev.bloks.beautiful_day_counter.client.state;

public final class ClientState {
    private static final ClientState INSTANCE = new ClientState();

    private boolean hudVisible = true;
    private long currentDay = 0L; // 0 indicates uninitialized
    private String dayLabel = "Day"; // configurable later
    private int tickSinceOverlayUpdate = 0;
    private String hudCorner = "bottom_right"; // top_left, top_right, bottom_left, bottom_right
    private boolean toastEnabled = true;
    private boolean toastActive = false;
    // Moon feature removed

    public static ClientState get() {
        return INSTANCE;
    }

    private ClientState() {}

    public boolean isHudVisible() {
        return hudVisible;
    }

    public void toggleHudVisible() {
        this.hudVisible = !this.hudVisible;
    }

    public long getCurrentDay() {
        return currentDay;
    }

    public void setCurrentDay(long currentDay) {
        this.currentDay = currentDay;
    }

    public String getDayLabel() {
        return dayLabel;
    }

    public void setDayLabel(String dayLabel) {
        this.dayLabel = dayLabel;
    }

    public int getTickSinceOverlayUpdate() {
        return tickSinceOverlayUpdate;
    }

    public void incrementOverlayTick() {
        this.tickSinceOverlayUpdate++;
    }

    public void resetOverlayTick() {
        this.tickSinceOverlayUpdate = 0;
    }

    public String getHudCorner() {
        return hudCorner;
    }

    public void setHudCorner(String hudCorner) {
        this.hudCorner = hudCorner;
    }

    public boolean isToastEnabled() {
        return toastEnabled;
    }

    public void setToastEnabled(boolean toastEnabled) {
        this.toastEnabled = toastEnabled;
    }

    public boolean isToastActive() {
        return toastActive;
    }

    public void setToastActive(boolean toastActive) {
        this.toastActive = toastActive;
    }

    

}
