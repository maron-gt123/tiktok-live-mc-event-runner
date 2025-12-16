package jp.example.tiktok;

public enum ActionType {
    TNT, COBWEB, SAND, LAVA, METEOR, LIGHTNING;

    public static ActionType fromString(String s) {
        return ActionType.valueOf(s.toUpperCase());
    }
}
