package dev.bloks.beautiful_day_counter.client.state;

public final class ClientState {
    private static final ClientState INSTANCE = new ClientState();

    private boolean hudVisible = true;
    private long currentDay = 1L;
    private String dayLabel = "Day"; // configurable later

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
}

