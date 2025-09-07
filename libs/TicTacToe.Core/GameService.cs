namespace TicTacToeVibe.Core;

/// <summary>
/// Default implementation of <see cref="IGameService"/>.
/// </summary>
public sealed class GameService : IGameService
{
    /// <inheritdoc />
    public Board Board { get; } = new();

    /// <inheritdoc />
    public bool Play(Move move) => Board.TryApply(move);

    /// <inheritdoc />
    public void Reset() => Board.Reset();
}
