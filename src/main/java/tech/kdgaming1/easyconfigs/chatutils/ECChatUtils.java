package tech.kdgaming1.easyconfigs.chatutils;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;

public class ECChatUtils {
    public static void printChatMessage(String message) {
        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(message));
    }
}
