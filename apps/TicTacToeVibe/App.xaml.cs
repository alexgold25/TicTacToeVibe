using System;
using System.Windows;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Hosting;
using TicTacToeVibe.Core;

namespace TicTacToeVibe.Wpf;

/// <summary>
/// Interaction logic for the application.
/// </summary>
public partial class App : Application
{
    private IHost? _host;

    private IHostBuilder CreateHostBuilder() => Host.CreateDefaultBuilder()
        .ConfigureServices(services =>
        {
            services.AddSingleton<IGameService, GameService>();
            services.AddSingleton<IComputerPlayer, MinimaxAi>();
            services.AddTransient<MainWindowViewModel>();
            services.AddTransient<MainWindow>();
        });

    /// <inheritdoc />
    protected override void OnStartup(StartupEventArgs e)
    {
        base.OnStartup(e);
        _host = CreateHostBuilder().Build();
        _host.Start();
        var window = _host.Services.GetRequiredService<MainWindow>();
        MainWindow = window;
        window.Show();
    }

    /// <inheritdoc />
    protected override void OnExit(ExitEventArgs e)
    {
        _host?.Dispose();
        base.OnExit(e);
    }
}
