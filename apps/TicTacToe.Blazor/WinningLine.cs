namespace TicTacToeVibe.Blazor;

/// <summary>
/// Possible winning line positions on the Tic-Tac-Toe board.
/// </summary>
public enum WinningLine
{
    None,
    Row0,
    Row1,
    Row2,
    Col0,
    Col1,
    Col2,
    DiagonalMain,
    DiagonalAnti
}
