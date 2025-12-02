package me.serbob.asteroidenterprisebridge;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

public class AsteroidEnterpriseBridge extends JavaPlugin implements PluginMessageListener {

    @Override
    public void onEnable() {
        getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", this);
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
    }

    @Override
    public void onDisable() {
        getServer().getMessenger().unregisterIncomingPluginChannel(this);
        getServer().getMessenger().unregisterOutgoingPluginChannel(this);
    }

    @Override
    public void onPluginMessageReceived(
            String channel,
            Player player,
            byte[] message
    ) {
        if (!channel.equals("BungeeCord"))
            return;

        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subchannel = in.readUTF();

        if (!subchannel.equals("Forward"))
            return;

        String server = in.readUTF();
        String subsubchannel = in.readUTF();

        if (!subsubchannel.equals("AsteroidBridge"))
            return;

        short len = in.readShort();
        byte[] msgBytes = new byte[len];
        in.readFully(msgBytes);

        ByteArrayDataInput msgin = ByteStreams.newDataInput(msgBytes);
        String action = msgin.readUTF();

        switch (action) {
            case "ExecuteCommand": {
                String command = msgin.readUTF();
                Bukkit.getScheduler().runTask(this, () -> {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                });

                break;
            }

            case "TeleportPlayer": {
                String playerName = msgin.readUTF();
                String worldName = msgin.readUTF();
                double x = msgin.readDouble();
                double y = msgin.readDouble();
                double z = msgin.readDouble();
                float yaw = msgin.readFloat();
                float pitch = msgin.readFloat();

                Player targetPlayer = Bukkit.getPlayer(playerName);
                World targetWorld = Bukkit.getWorld(worldName);

                if (targetPlayer == null || targetWorld == null)
                    return;

                Location loc = new Location(targetWorld, x, y, z, yaw, pitch);
                Bukkit.getScheduler().runTask(this, () -> {
                    targetPlayer.teleport(loc);
                });

                break;
            }
        }
    }
}
