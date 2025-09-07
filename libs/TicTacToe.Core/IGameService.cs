namespace TicTacToeVibe.Core;

/// <summary>
/// Service managing a game instance.
/// </summary>
public interface IGameService
{
    /// <summary>Gets the current board.</summary>
    Board Board { get; }

    /// <summary>Resets the game to the initial state.</summary>
    void Reset();

    /// <summary>Attempts to play a move.</summary>
    /// <param name="move">Move to play.</param>
    /// <returns><see langword="true"/> if the move was applied.</returns>
    bool Play(Move move);
}
