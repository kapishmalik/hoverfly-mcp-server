package io.spectolabs.hoverflymcp;

public class HoverflyResponse {
    private final boolean success;
    private final String message;

    public HoverflyResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public static HoverflyResponse ok(String message) {
        return new HoverflyResponse(true, message);
    }

    public static HoverflyResponse error(String message) {
        return new HoverflyResponse(false, message);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
