package nws.mc.servers.config.ban$item;

import com.google.gson.reflect.TypeToken;
import nws.dev.core.json._JsonConfig;
import nws.mc.servers.Servers;

import java.util.ArrayList;
import java.util.List;

public class BanItemConfig extends _JsonConfig<BanItemData> {
    private static final String file = Servers.ConfigDir_BlackList + "BanItem.json";
    public static final BanItemConfig I = new BanItemConfig();
    public BanItemConfig() {
        super(file, """
                {
                    "enable": false,
                    "msg": "ban.item.deny",
                    "bannedItems": [
                        "minecraft:bedrock"
                    ]
                }
                """, new TypeToken<>() {});
    }

    public List<String> getItems() {
        //System.out.println(getDatas().bannedItems);
        if (getDatas().bannedItems == null) getDatas().bannedItems = new ArrayList<>();
        return getDatas().bannedItems;
    }
    public boolean isBanned(String item) {
        //System.out.println(getItems());
        return getItems().contains(item);
    }
}
