using System.Collections.Generic;

namespace TicTacToeVibe.Core;

/// <summary>
/// Represents the Tic-Tac-Toe board state.
/// </summary>
public sealed class Board
{
    private readonly Player[,] _cells = new Player[3, 3];

    /// <summary>Gets the player whose turn it is.</summary>
    public Player CurrentPlayer { get; private set; } = Player.X;

    /// <summary>Gets a value indicating whether the game has finished.</summary>
    public bool IsGameOver { get; private set; }

    /// <summary>Gets the winner if the game has ended.</summary>
    public Player? Winner { get; private set; }

    /// <summary>Gets a value indicating whether the game ended in a draw.</summary>
    public bool IsDraw => IsGameOver && Winner is null;

    /// <summary>Accesses a board cell.</summary>
    public Player this[int row, int col] => _cells[row, col];

    /// <summary>
    /// Attempts to apply a move for the current player.
    /// </summary>
    /// <param name="move">Move to apply.</param>
    /// <returns><see langword="true"/> if the move was applied.</returns>
    public bool TryApply(Move move)
    {
        if (IsGameOver)
        {
            return false;
        }

        if (_cells[move.Row, move.Col] != Player.None)
        {
            return false;
        }

        _cells[move.Row, move.Col] = CurrentPlayer;

        if (CheckWin(CurrentPlayer))
        {
            IsGameOver = true;
            Winner = CurrentPlayer;
        }
        else if (!HasEmpty())
        {
            IsGameOver = true;
            Winner = null;
        }
        else
        {
            CurrentPlayer = Other(CurrentPlayer);
        }

        return true;
    }

    /// <summary>Enumerates legal moves.</summary>
    public IEnumerable<Move> GetLegalMoves()
    {
        if (IsGameOver)
        {
            yield break;
        }

        for (var r = 0; r < 3; r++)
        {
            for (var c = 0; c < 3; c++)
            {
                if (_cells[r, c] == Player.None)
                {
                    yield return new Move(r, c);
                }
            }
        }
    }

    /// <summary>Resets the board to the initial state.</summary>
    public void Reset()
    {
        for (var r = 0; r < 3; r++)
        {
            for (var c = 0; c < 3; c++)
            {
                _cells[r, c] = Player.None;
            }
        }

        CurrentPlayer = Player.X;
        IsGameOver = false;
        Winner = null;
    }

    /// <summary>Creates a deep copy of the board.</summary>
    public Board Clone()
    {
        var clone = new Board
        {
            CurrentPlayer = CurrentPlayer,
            IsGameOver = IsGameOver,
            Winner = Winner
        };
        for (var r = 0; r < 3; r++)
        {
            for (var c = 0; c < 3; c++)
            {
                clone._cells[r, c] = _cells[r, c];
            }
        }
        return clone;
    }

    private static Player Other(Player player) => player == Player.X ? Player.O : Player.X;

    private bool CheckWin(Player player)
    {
        for (var i = 0; i < 3; i++)
        {
            if (_cells[i, 0] == player && _cells[i, 1] == player && _cells[i, 2] == player)
            {
                return true;
            }
            if (_cells[0, i] == player && _cells[1, i] == player && _cells[2, i] == player)
            {
                return true;
            }
        }

        if (_cells[0, 0] == player && _cells[1, 1] == player && _cells[2, 2] == player)
        {
            return true;
        }
        if (_cells[0, 2] == player && _cells[1, 1] == player && _cells[2, 0] == player)
        {
            return true;
        }

        return false;
    }

    private bool HasEmpty()
    {
        for (var r = 0; r < 3; r++)
        {
            for (var c = 0; c < 3; c++)
            {
                if (_cells[r, c] == Player.None)
                {
                    return true;
                }
            }
        }
        return false;
    }
}
