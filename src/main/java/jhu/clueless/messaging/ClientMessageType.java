
package jhu.clueless.messaging;

public enum ClientMessageType {
    // Lobby
    C1_CREATE_GAME,
    C2_JOIN_GAME,
    C3_SELECT_CHARACTER,
    C4_START_GAME,

    // Game state and session
    C5_GET_GAME_STATE,
    C11_LEAVE_GAME,
    C12_REJOIN_GAME,
    C13_RETURN_TO_LOBBY,

    // Gameplay
    C6_MOVE,
    C7_MAKE_SUGGESTION,
    C8_DISPROVE_RESPONSE,
    C9_MAKE_ACCUSATION,
    C10_END_TURN
}
