using CommunityToolkit.Mvvm.ComponentModel;
using CommunityToolkit.Mvvm.Input;
using System.Collections.ObjectModel;
using System.Linq;
using TicTacToeVibe.Core;

namespace TicTacToeVibe.Wpf;

/// <summary>
/// View model for the main window.
/// </summary>
public partial class MainWindowViewModel : ObservableObject
{
    private readonly IGameService _game;
    private readonly IComputerPlayer _ai;

    /// <summary>Initializes a new instance of the <see cref="MainWindowViewModel"/> class.</summary>
    public MainWindowViewModel(IGameService game, IComputerPlayer ai)
    {
        _game = game;
        _ai = ai;
        UpdateState();
    }

    /// <summary>Cells displayed on the board.</summary>
    public ObservableCollection<string> Cells { get; } = new(Enumerable.Repeat(string.Empty, 9));

    [ObservableProperty]
    private string statusText = string.Empty;

    [ObservableProperty]
    private bool isGameOver;

    [ObservableProperty]
    private bool isHumanVsAi = true;

    [ObservableProperty]
    private WinningLine winningLine = WinningLine.None;

    [RelayCommand(CanExecute = nameof(CanClick))]
    private void CellClick(int index)
    {
        if (_game.Play(ToMove(index)))
        {
            UpdateState();
            if (IsHumanVsAi && !_game.Board.IsGameOver)
            {
                var move = _ai.ChooseMove(_game.Board.Clone());
                if (move.HasValue)
                {
                    _game.Play(move.Value);
                }
                UpdateState();
            }
        }
    }

    private bool CanClick(int index) => !IsGameOver && _game.Board[index / 3, index % 3] == Player.None;

    [RelayCommand]
    private void NewGame()
    {
        _game.Reset();
        UpdateState();
    }

    private void UpdateState()
    {
        for (var i = 0; i < 9; i++)
        {
            Cells[i] = Symbol(_game.Board[i / 3, i % 3]);
        }
        IsGameOver = _game.Board.IsGameOver;
        StatusText = _game.Board.IsGameOver
            ? _game.Board.Winner switch
            {
                Player.X => "X wins",
                Player.O => "O wins",
                _ => "Draw"
            }
            : $"Turn: {_game.Board.CurrentPlayer}";
        WinningLine = _game.Board.IsGameOver && _game.Board.Winner is Player winner
            ? FindWinningLine(_game.Board, winner)
            : WinningLine.None;
        CellClickCommand.NotifyCanExecuteChanged();
    }

    private static string Symbol(Player player) => player switch
    {
        Player.X => "X",
        Player.O => "O",
        _ => string.Empty
    };

    private static Move ToMove(int index) => new(index / 3, index % 3);

    private static WinningLine FindWinningLine(Board board, Player player)
    {
        for (var i = 0; i < 3; i++)
        {
            if (board[i, 0] == player && board[i, 1] == player && board[i, 2] == player)
            {
                return i switch
                {
                    0 => WinningLine.Row0,
                    1 => WinningLine.Row1,
                    _ => WinningLine.Row2,
                };
            }
            if (board[0, i] == player && board[1, i] == player && board[2, i] == player)
            {
                return i switch
                {
                    0 => WinningLine.Col0,
                    1 => WinningLine.Col1,
                    _ => WinningLine.Col2,
                };
            }
        }

        if (board[0, 0] == player && board[1, 1] == player && board[2, 2] == player)
        {
            return WinningLine.DiagonalMain;
        }
        if (board[0, 2] == player && board[1, 1] == player && board[2, 0] == player)
        {
            return WinningLine.DiagonalAnti;
        }

        return WinningLine.None;
    }
}
