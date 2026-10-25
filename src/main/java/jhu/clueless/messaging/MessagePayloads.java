
package jhu.clueless.messaging;

import java.util.List;

public final class MessagePayloads {

    private MessagePayloads() {}

    // C1: Create game
    public record CreateGameRequest(
        String username
    ) {}

    // C2: Join game
    public record JoinGameRequest(
        String username
    ) {}

    // C3: Select character
    public record SelectCharacterRequest(
        String character
    ) {}

    // C6: Move
    public record MoveRequest(
        String to,
        boolean stay
    ) {}

    // C7: Make suggestion
    public record SuggestionRequest(
        String suspect,
        String weapon
    ) {}

    // C8: Disprove suggestion
    public record DisproveResponseRequest(
        String card
    ) {}

    // C9: Make accusation
    public record AccusationRequest(
        String suspect,
        String weapon,
        String room
    ) {}

    // U2: Your turn
    public record YourTurnPayload(
        int turn,
        String location,
        List<String> legalMoves,
        boolean canStay,
        boolean canSuggest,
        boolean canAccuse,
        boolean canEndTurn
    ) {}

    // B6: Player moved
    public record PlayerMovedPayload(
        String character,
        String from,
        String to,
        String via
    ) {}

    // U6: Error
    public record ErrorPayload(
        String code,
        String message,
        String requestType
    ) {}

    // B5: Turn changed
    public record TurnChangedPayload(
        String playerId,
        String character,
        int turn
    ) {}

    // B7: Suggestion made
    public record SuggestionMadePayload(
        String suggestionId,
        String suggester,
        String suspect,
        String weapon,
        String room
    ) {}
}
