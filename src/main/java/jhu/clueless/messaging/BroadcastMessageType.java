
package jhu.clueless.messaging;

public enum BroadcastMessageType {
    // Lobby
    B1_PLAYER_JOINED,
    B2_CHARACTER_SELECTED,
    B3_PLAYER_LEFT,
    B4_GAME_STARTED,

    // Turns and movement
    B5_TURN_CHANGED,
    B6_PLAYER_MOVED,

    // Suggestions
    B7_SUGGESTION_MADE,
    B8_DISPROVE_PENDING,
    B9_PLAYER_CANNOT_DISPROVE,
    B10_SUGGESTION_DISPROVED,
    B11_SUGGESTION_NOT_DISPROVED,

    // Accusations and game end
    B12_ACCUSATION_MADE,
    B13_PLAYER_ELIMINATED,
    B14_GAME_OVER,

    // Optional messages
    B15_PLAYER_CONNECTION,
    B16_LOBBY_REOPENED,
    B17_WEAPON_MOVED
}
