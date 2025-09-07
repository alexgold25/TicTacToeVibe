# TicTacToeVibe

Мультипроектное решение для игры крестики-нолики на .NET 8.

## Требования
- .NET SDK 8.0

## Команды
```bash
dotnet build TicTacToeVibe.sln
dotnet test tests/TicTacToe.Core.Tests
dotnet run --project apps/TicTacToeVibe
dotnet run --project apps/TicTacToe.Blazor
```

## Архитектура
- **libs/TicTacToe.Core** – чистая игровая логика.
- **apps/TicTacToeVibe** – WPF-клиент (MVVM, DI).
- **apps/TicTacToe.Blazor** – Blazor Web App.
- **tests/TicTacToe.Core.Tests** – модульные тесты ядра.

UI-слои не содержат бизнес-логики и используют общую библиотеку ядра.
