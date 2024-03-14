I Respect Your Options
This mod has one simple function, to allow a modpack creator to deliver a set of default options that will be copied over to the config folder upon the game's first launch.
How will it work in practice?

To make it work, you need to create a folder named "iRespectYourOptions" located in "\config\iRespectYourOptions" directory. In this folder, you should put all the configuration files and folders. When you launch the game, the configuration file from the "\config\iRespectYourOptions" directory will be copied to the "/config" directory. A file named "has_run_before.txt" will also be created and placed in the "/config" directory. As long as this file exists, the configuration file won't be copied again. If you want to revert to the default options, delete the "has_run_before.txt" file, and the game will copy the configuration file from the "\config\iRespectYourOptions" directory again and overwrite the config files in the "\config" directory.

If you want to export the options.txt file from the vanilla game, you can place it in the "\config\iRespectYourOptions\minecraft_options" directory. The game will copy this file and overwrite the existing option.txt file. Then in the "\config" directory, a "has_run_before.txt" file will also be created. As long as this file exists, the game won't copy the configuration file again.

Inspired by the Your Options Shall Be Respected (YOSBR) mod. If you need the mod for a newer version go and find that one.


-------------------------------------------
Source installation information for modders
-------------------------------------------
This code follows the Minecraft Forge installation methodology. It will apply
some small patches to the vanilla MCP source code, giving you and it access 
to some of the data and functions you need to build a successful mod.

Note also that the patches are built against "unrenamed" MCP source code (aka
srgnames) - this means that you will not be able to read them directly against
normal code.

Source pack installation information:

Standalone source installation
==============================

Step 1: Open your command-line and browse to the folder where you extracted the zip file.

Step 2: Once you have a command window up in the folder that the downloaded material was placed, type:

Windows: "gradlew setupDecompWorkspace"
Linux/Mac OS: "./gradlew setupDecompWorkspace"

Step 3: After all that finished, you're left with a choice.
For eclipse, run "gradlew eclipse" (./gradlew eclipse if you are on Mac/Linux)

If you preffer to use IntelliJ, steps are a little different.
1. Open IDEA, and import project.
2. Select your build.gradle file and have it import.
3. Once it's finished you must close IntelliJ and run the following command:

"gradlew genIntellijRuns" (./gradlew genIntellijRuns if you are on Mac/Linux)

Step 4: The final step is to open Eclipse and switch your workspace to /eclipse/ (if you use IDEA, it should automatically start on your project)

If at any point you are missing libraries in your IDE, or you've run into problems you can run "gradlew --refresh-dependencies" to refresh the local cache. "gradlew clean" to reset everything {this does not effect your code} and then start the processs again.

Should it still not work, 
Refer to #ForgeGradle on EsperNet for more information about the gradle environment.

Tip:
If you do not care about seeing Minecraft's source code you can replace "setupDecompWorkspace" with one of the following:
"setupDevWorkspace": Will patch, deobfusicated, and gather required assets to run minecraft, but will not generated human readable source code.
"setupCIWorkspace": Same as Dev but will not download any assets. This is useful in build servers as it is the fastest because it does the least work.

Tip:
When using Decomp workspace, the Minecraft source code is NOT added to your workspace in a editable way. Minecraft is treated like a normal Library. Sources are there for documentation and research purposes and usually can be accessed under the 'referenced libraries' section of your IDE.

Forge source installation
=========================
MinecraftForge ships with this code and installs it as part of the forge
installation process, no further action is required on your part.

LexManos' Install Video
=======================
https://www.youtube.com/watch?v=8VEdtQLuLO0&feature=youtu.be

For more details update more often refer to the Forge Forums:
http://www.minecraftforge.net/forum/index.php/topic,14048.0.html
