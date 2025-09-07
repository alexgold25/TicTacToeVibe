using FluentAssertions;
using TicTacToeVibe.Core;
using Xunit;

namespace TicTacToeVibe.Core.Tests;

public class GameTests
{
    [Fact]
    public void ApplyingMoveSwitchesPlayer()
    {
        var board = new Board();
        board.TryApply(new Move(0, 0)).Should().BeTrue();
        board.CurrentPlayer.Should().Be(Player.O);
    }

    [Fact]
    public void DetectsRowWin()
    {
        var b = new Board();
        b.TryApply(new Move(0, 0));
        b.TryApply(new Move(1, 0));
        b.TryApply(new Move(0, 1));
        b.TryApply(new Move(1, 1));
        b.TryApply(new Move(0, 2));
        b.IsGameOver.Should().BeTrue();
        b.Winner.Should().Be(Player.X);
    }

    [Fact]
    public void DetectsColumnWin()
    {
        var b = new Board();
        b.TryApply(new Move(0, 0));
        b.TryApply(new Move(0, 1));
        b.TryApply(new Move(1, 0));
        b.TryApply(new Move(1, 1));
        b.TryApply(new Move(2, 0));
        b.IsGameOver.Should().BeTrue();
        b.Winner.Should().Be(Player.X);
    }

    [Fact]
    public void DetectsDiagonalWin()
    {
        var b = new Board();
        b.TryApply(new Move(0, 0));
        b.TryApply(new Move(0, 1));
        b.TryApply(new Move(1, 1));
        b.TryApply(new Move(0, 2));
        b.TryApply(new Move(2, 2));
        b.IsGameOver.Should().BeTrue();
        b.Winner.Should().Be(Player.X);
    }

    [Fact]
    public void DetectsDraw()
    {
        var b = new Board();
        b.TryApply(new Move(0,0));
        b.TryApply(new Move(0,1));
        b.TryApply(new Move(0,2));
        b.TryApply(new Move(1,1));
        b.TryApply(new Move(1,0));
        b.TryApply(new Move(1,2));
        b.TryApply(new Move(2,1));
        b.TryApply(new Move(2,0));
        b.TryApply(new Move(2,2));
        b.IsDraw.Should().BeTrue();
    }

    [Fact]
    public void AiPrefersCenterOnEmptyBoard()
    {
        var board = new Board();
        var ai = new MinimaxAi();
        ai.ChooseMove(board).Should().Be(new Move(1,1));
    }

    [Fact]
    public void AiBlocksImmediateThreat()
    {
        var board = new Board();
        board.TryApply(new Move(0,0)); // X
        board.TryApply(new Move(1,1)); // O
        board.TryApply(new Move(0,1)); // X threatens row
        var ai = new MinimaxAi();
        ai.ChooseMove(board).Should().Be(new Move(0,2));
    }

    [Fact]
    public void AiWinsWhenPossible()
    {
        var board = new Board();
        board.TryApply(new Move(0,0)); // X
        board.TryApply(new Move(1,1)); // O
        board.TryApply(new Move(2,1)); // X
        board.TryApply(new Move(1,0)); // O
        board.TryApply(new Move(2,0)); // X
        var ai = new MinimaxAi();
        // O can win by playing (1,2)
        ai.ChooseMove(board).Should().Be(new Move(1,2));
    }

    [Fact]
    public void AiChoosesImmediateWinOverDelayedWin()
    {
        var board = new Board();
        board.TryApply(new Move(0,0)); // X
        board.TryApply(new Move(1,0)); // O
        board.TryApply(new Move(0,1)); // X
        board.TryApply(new Move(2,0)); // O
        var ai = new MinimaxAi();
        // X can win immediately at (0,2) but center also leads to a win later
        ai.ChooseMove(board).Should().Be(new Move(0,2));
    }
}
