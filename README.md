# EasyConfigs Mod for Minecraft

EasyConfigs is a Minecraft mod designed for modpack creators and regular users alike. It simplifies the configuration process by providing a GUI for managing configurations and supports exporting and importing of configuration files. This allows modpack creators to ship default settings without fear of them being reset with every update. Regular users can also benefit from this mod by easily switching between different sets of configurations or sharing their configurations with friends.

## Features

- Save and load configurations with ease.
- Import and export configuration files.
- GUI for managing configurations.
- Ideal for mod pack creators to ship default settings.
- Allows regular users to switch between different configurations or share their configurations.

## Installation

1. Download the latest release of EasyConfigs from the [releases page](https://modrinth.com/mod/easyconfigs/versions).
2. Place the downloaded `.jar` file into your Minecraft's `mods` folder.
3. Run Minecraft with the Forge mod loader.

## Usage

After installing the mod, you can access the EasyConfigs GUI from the Minecraft pause menu. From there, you can manage your configurations.

## Some other notes to consider

In the config folder will there be a file that you can but files and folder that you don't want to copy or export. Some mods makes files that don't have configs and are no need to copy them.

## Commands

1. **Save Configurations**: `/EasyConfigs saveConfigs [1-9] [addMCOptions|don'tAddMCOptions]` - Saves the current configurations to a specified slot (1-9). You can also specify whether to include Minecraft options in the save.

2. **Load Configurations**: `/EasyConfigs loadConfigs [1-9] [loadMCOptions|don'tLoadMCOptions]` - Loads configurations from a specified slot (1-9). You can also specify whether to load Minecraft options.

3. **Load Default Configurations**: `/EasyConfigs loadDefaultConfigs [loadMCOptions|don'tLoadMCOptions]` - Loads the default configurations. You can specify whether to include Minecraft options.

4. **Confirm Overwrite**: `/EasyConfigs confirmOverwrite [1-9] [addMCOptions|don'tAddMCOptions]` - Confirms the overwrite of a specified slot (1-9) when saving configurations. You can also specify whether to include Minecraft options in the save.

5. **Export Configurations**: `/EasyConfigs exportConfigs [sourceFolder] [zipFileName] [addMCOptions|don'tAddMCOptions]` - Exports configurations from a specified source folder to a zip file. You can also specify whether to include Minecraft options in the export.

6. **Import Configurations**: `/EasyConfigs importConfigs [1-9] [setAsCurrentConfigs|onlyImportToSaveSlot] [zipFileName] [loadMCOptions|don'tLoadMCOptions]` - Imports configurations from a specified zip file to a slot (1-9). You can specify whether to set the imported configurations as current or only import to the save slot. You can also specify whether to load Minecraft options.

Please replace the placeholders with the actual values when using these commands.

## For Mod Pack Developers

EasyConfigs is a powerful tool for mod pack developers. It allows you to create a consistent configuration setup across all instances of your mod pack, ensuring that every player has the same gameplay experience. Here's how you can use EasyConfigs:

1. **Setup Your Configurations**: Start by setting up all the configurations for your mods and the vanilla game. Make sure everything works as you want it to.

2. **Open EasyConfigs GUI**: Once your configurations are set, open the EasyConfigs GUI. You can do this from the Minecraft pause menu.

3. **Create Default Configurations**: In the EasyConfigs GUI, click on the "Modpack" button. Then, click on the "Create Default Configs" button. This action will copy all the configurations from the game (including your mods and the vanilla game settings) to the default configs folder located at `easyconfigs/easyconfigsave0`.

4. **Review the Default Configurations**: Go through the `easyconfigs/easyconfigsave0` folder and ensure all necessary configuration files are present. Some mods create multiple files, and not all of them might be necessary. Delete any files that are not needed.

5. **Export Your Configurations**: Now that your default configurations are set, you're ready to export. When including files in your mod pack, do not select the `configs` folder or the `options.txt` or `server.dat` files. Instead, include the `easyconfigs` folder.

### Bonus Step for Mod Pack Developers

If you want to provide your users with the ability to choose between different configuration setups, you can do so by using the save slots feature of EasyConfigs:

1. **Save Different Configurations**: Set up different configurations for your mods and the vanilla game. For each setup, save the configurations to a different slot using Save Configs option in the menu.

2. **Inform Your Users**: In your mod pack description, inform your users about the different configuration setups available. Tell them they can load the different setups by using the EasyConfigs GUI.

3. **Export Your Configurations**: Now export just as before.

By following these steps, you can ensure that your mod pack users have a consistent configuration setup, and they don't have to worry about losing their settings when updating the mod pack.

Written by: Kd_Gaming1 and ChatGPT
