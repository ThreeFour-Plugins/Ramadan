# Ramadan Plugin

This is a Minecraft plugin that enables Ramadan mode, preventing players from eating or sleeping during certain hours of the day.

## Installation

To install the plugin, download the latest version from the [releases page](https://github.com/ThreeFour-Plugins/Ramadan/releases) and place it in the `plugins` folder of your Minecraft server.

## Usage

The plugin adds the following commands:

- `/ramadan reload` - Reloads the configuration file for the plugin. Requires the `ramadan.reload` permission.
- `/donate <player> <amount>` - To donate other Player with amount of money.

The following permission is also available:

- `ramadan.reload` - Allows players to use the `/ramadan reload` command.

## Configuration

The plugin reads its configuration from a file called `config.yml`, which should be located in the `plugins/Ramadan` directory. The following options are available:

- `update` - To enable or disable update check.
