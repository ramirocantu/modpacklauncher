package net.creeperhost.creeperlauncher.api;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import net.creeperhost.creeperlauncher.CreeperLauncher;
import net.creeperhost.creeperlauncher.api.data.*;
import net.creeperhost.creeperlauncher.api.handlers.*;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class WebSocketMessengerHandler
{
    private static HashMap<TypeToken<? extends BaseData>, IMessageHandler<? extends BaseData>> handlers = new HashMap<TypeToken<? extends BaseData>, IMessageHandler<? extends BaseData>>();
    private static HashMap<String, Class<? extends BaseData>> dataMap = new HashMap<>();
    static Gson gson = new Gson();

    static
    {
        registerDataMap("installedInstances", InstalledInstancesData.class);
        registerHandler(InstalledInstancesData.class, new InstalledInstancesHandler());
        registerDataMap("launchInstance", LaunchInstanceData.class);
        registerHandler(LaunchInstanceData.class, new LaunchInstanceHandler());
        registerDataMap("instanceInfo", InstanceInfoData.class);
        registerHandler(InstanceInfoData.class, new InstanceInfoHandler());
        registerDataMap("installInstance", InstallInstanceData.class);
        registerHandler(InstallInstanceData.class, new InstallInstanceHandler());
        registerDataMap("cancelInstallInstance", CancelInstallInstanceData.class);
        registerHandler(CancelInstallInstanceData.class, new CancelInstallInstanceHandler());
        registerDataMap("updateInstance", UpdateInstanceData.class);
        registerHandler(UpdateInstanceData.class, new UpdateInstanceHandler());
        registerDataMap("uninstallInstance", UninstallInstanceData.class);
        registerHandler(UninstallInstanceData.class, new UninstallInstanceHandler());
        registerDataMap("instanceConfigure", InstanceConfigureData.class);
        registerHandler(InstanceConfigureData.class, new InstanceConfigureHandler());
        registerDataMap("instanceBrowse", BrowseInstanceData.class);
        registerHandler(BrowseInstanceData.class, new BrowseInstanceHandler());
        registerDataMap("getSettings", SettingsInfoData.class);
        registerHandler(SettingsInfoData.class, new SettingsInfoHandler());
        registerDataMap("saveSettings", SettingsConfigureData.class);
        registerHandler(SettingsConfigureData.class, new SettingsConfigureHandler());
        registerDataMap("modalCallback", OpenModalData.ModalCallbackData.class);
        registerHandler(OpenModalData.ModalCallbackData.class, new ModalCallbackHandler());
    }

    public static void registerHandler(Class<? extends BaseData> clazz, IMessageHandler<? extends BaseData> handler)
    {
        TypeToken<? extends BaseData> typeToken = TypeToken.get(clazz);
        handlers.put(typeToken, handler);
    }

    public static void registerDataMap(String typeString, Class<? extends BaseData> clazz)
    {
        dataMap.put(typeString, clazz);
    }

    public static void handleMessage(String data)
    {
        JsonParser parser = new JsonParser();
        JsonElement parse = parser.parse(data);
        if (parse.isJsonObject())
        {
            JsonObject jsonObject = parse.getAsJsonObject();
            if (jsonObject.has("type"))
            {
                String type = jsonObject.get("type").getAsString();
                Class<? extends BaseData> dataType = dataMap.get(type);
                TypeToken typeToken = TypeToken.get(dataType);
                IMessageHandler<? extends BaseData> iMessageHandler = handlers.get(typeToken);
                if (iMessageHandler != null)
                {
                    BaseData parsedData = gson.fromJson(data, typeToken.getType());
                    if (parsedData.secret != null && parsedData.secret.equals(CreeperLauncher.websocketSecret)) {
                        CompletableFuture.runAsync(()->iMessageHandler.handle(parsedData));
                    }
                }
            }
        }
    }
}
