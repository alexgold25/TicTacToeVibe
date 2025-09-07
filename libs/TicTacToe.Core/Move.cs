namespace TicTacToeVibe.Core;

/// <summary>
/// Represents a move on the board.
/// </summary>
/// <param name="Row">Row index.</param>
/// <param name="Col">Column index.</param>
public readonly record struct Move(int Row, int Col);
