using CommunityToolkit.Mvvm.ComponentModel;
using CommunityToolkit.Mvvm.Input;
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
        Cells = new string[9];
        UpdateState();
    }

    [ObservableProperty]
    private string[] cells;

    [ObservableProperty]
    private string statusText = string.Empty;

    [ObservableProperty]
    private bool isGameOver;

    [ObservableProperty]
    private bool isHumanVsAi = true;

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
        CellClickCommand.NotifyCanExecuteChanged();
    }

    private static string Symbol(Player player) => player switch
    {
        Player.X => "X",
        Player.O => "O",
        _ => string.Empty
    };

    private static Move ToMove(int index) => new(index / 3, index % 3);
}
