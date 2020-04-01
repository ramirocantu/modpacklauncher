package net.creeperhost.creeperlauncher.api.handlers;

import net.creeperhost.creeperlauncher.Settings;
import net.creeperhost.creeperlauncher.api.data.InstanceInfoData;
import net.creeperhost.creeperlauncher.pack.LocalInstance;

import java.util.HashMap;
import java.util.UUID;

public class InstanceInfoHandler implements IMessageHandler<InstanceInfoData>
{
    @Override
    public void handle(InstanceInfoData data)
    {
        HashMap<String, String> instanceInfo = new HashMap<>();
        try
        {
            LocalInstance instance = new LocalInstance(UUID.fromString(data.uuid));
            instanceInfo.put("uuid", instance.getUuid().toString());
            instanceInfo.put("name", instance.getName());
            instanceInfo.put("memory", String.valueOf(instance.memory));
            instanceInfo.put("jvmargs", instance.jvmArgs);
            instanceInfo.put("width", String.valueOf(instance.width));
            instanceInfo.put("height", String.valueOf(instance.height));
            instanceInfo.put("embeddedjre", String.valueOf(instance.embeddedJre));

        } catch (Exception ignored)
        {
        }
        Settings.webSocketAPI.sendMessage(new InstanceInfoData.Reply(data, instanceInfo));
    }
}
