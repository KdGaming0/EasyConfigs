package tech.kdgaming1.easyconfigs.chatutils;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static tech.kdgaming1.easyconfigs.EasyConfigs.MOD_ID;

public class ECChatUtils {
    private static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    public static void printChatMessage(String message) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.theWorld != null && mc.thePlayer != null) {
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(message));
        } else {
            LOGGER.info("This was suppose to be a chat massage but you are not in a world so here it is: " + message);
        }
    }
}
