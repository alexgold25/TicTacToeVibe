namespace TicTacToeVibe.Core;

/// <summary>
/// Represents a computer player able to select moves.
/// </summary>
public interface IComputerPlayer
{
    /// <summary>Chooses the next move for the specified board.</summary>
    /// <param name="board">Board to evaluate.</param>
    /// <returns>The chosen move or <c>null</c> if no move is available.</returns>
    Move? ChooseMove(Board board);
}
