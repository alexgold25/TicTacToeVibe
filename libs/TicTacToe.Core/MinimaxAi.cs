using System;
using System.Collections.Generic;
using System.Linq;

namespace TicTacToeVibe.Core;

/// <summary>
/// Simple minimax based AI player.
/// </summary>
public sealed class MinimaxAi : IComputerPlayer
{
    /// <inheritdoc />
    public Move? ChooseMove(Board board)
    {
        if (board.IsGameOver)
        {
            return null;
        }

        var bestScore = int.MinValue;
        Move? bestMove = null;
        foreach (var move in OrderedMoves(board))
        {
            var clone = board.Clone();
            clone.TryApply(move);
            var score = Minimax(clone, false, int.MinValue, int.MaxValue, board.CurrentPlayer);
            if (score > bestScore)
            {
                bestScore = score;
                bestMove = move;
            }
        }
        return bestMove;
    }

    private static IEnumerable<Move> OrderedMoves(Board board)
    {
        var center = new Move(1, 1);
        if (board[1, 1] == Player.None)
        {
            yield return center;
        }

        Move[] corners =
        [
            new(0, 0),
            new(0, 2),
            new(2, 0),
            new(2, 2)
        ];
        foreach (var c in corners)
        {
            if (board[c.Row, c.Col] == Player.None)
            {
                yield return c;
            }
        }

        Move[] edges =
        [
            new(0, 1),
            new(1, 0),
            new(1, 2),
            new(2, 1)
        ];
        foreach (var e in edges)
        {
            if (board[e.Row, e.Col] == Player.None)
            {
                yield return e;
            }
        }
    }

    private static int Minimax(Board board, bool maximizing, int alpha, int beta, Player ai)
    {
        if (board.IsGameOver)
        {
            if (board.Winner == ai)
            {
                return 1;
            }
            if (board.Winner is null)
            {
                return 0;
            }
            return -1;
        }

        if (maximizing)
        {
            var value = int.MinValue;
            foreach (var move in board.GetLegalMoves())
            {
                var clone = board.Clone();
                clone.TryApply(move);
                value = Math.Max(value, Minimax(clone, false, alpha, beta, ai));
                alpha = Math.Max(alpha, value);
                if (alpha >= beta)
                {
                    break;
                }
            }
            return value;
        }
        else
        {
            var value = int.MaxValue;
            foreach (var move in board.GetLegalMoves())
            {
                var clone = board.Clone();
                clone.TryApply(move);
                value = Math.Min(value, Minimax(clone, true, alpha, beta, ai));
                beta = Math.Min(beta, value);
                if (alpha >= beta)
                {
                    break;
                }
            }
            return value;
        }
    }
}
