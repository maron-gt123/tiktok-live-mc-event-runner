package jp.example.tiktok;

public record LikeRate(
        int amount,            // 何LIKEでトリガーするか
        ActionType actionType, // 実行するアクションの種類
        int actionAmount,      // 実行するアクションの量（例: TNT数など）
        int cooldownSeconds    // クールダウン時間（秒）
) {}
