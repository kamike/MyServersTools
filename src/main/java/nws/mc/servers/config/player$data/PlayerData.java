package nws.mc.servers.config.player$data;

import com.google.gson.reflect.TypeToken;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import nws.dev.core.json._JsonConfig;
import nws.mc.servers.Servers;
import nws.mc.servers.data$type.PosData;
import nws.mc.servers.helper._F;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;

public class PlayerData extends _JsonConfig<PD> {
    public static final HashMap<String, PlayerData> I = loadPlayerData();
    public PlayerData(String filePath) {
        super(filePath, """
                """, new TypeToken<>() {});
    }

    @Override
    public PD getDatas() {
        if (datas == null) datas = new PD();
        return datas;
    }


    public static HashMap<String, PlayerData> loadPlayerData() {
        HashMap<String, PlayerData> abc = new HashMap<>();
        List<Path> jsonFiles = _F.getFiles(Servers.ConfigDir_PlayerData, ".json");
        for (Path path : jsonFiles) {
            String fileName = path.getFileName().toString();
            fileName = fileName.substring(0, fileName.lastIndexOf("."));
            abc.put(fileName, new PlayerData(path.toString()));
        }
        return abc;

    }


    public static PlayerData get(String uuid) {
        return I.getOrDefault(uuid, new PlayerData(Servers.ConfigDir_PlayerData + uuid + ".json"));
    }
    public void addBack(ServerPlayer serverPlayer) {
        addBack(PosData.create(serverPlayer));
    }
    public void addBack(PosData pd) {
        getDatas().addBack(pd);
        save();
    }
    public void setHome(PosData posData) {
        getDatas().home = posData;
        save();
    }
}
