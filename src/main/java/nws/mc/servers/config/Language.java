package nws.mc.servers.config;

import com.google.gson.reflect.TypeToken;
import com.mojang.logging.LogUtils;
import net.minecraft.network.chat.Component;
import nws.dev.core.json._JsonConfig;
import nws.mc.servers.Servers;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Locale;

public class Language extends _JsonConfig<HashMap<String, String>> {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String filePath = getLanguage();
    public static final Language I = new Language();

    public static void reSet() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String[] langs = {"zh_cn"};
        for (String lang : langs) {
            try (InputStream inputStream = classLoader.getResourceAsStream("assets/servers/lang/" + lang + ".json")) {
                if (inputStream == null) {
                    throw new FileNotFoundException("Resource not found: assets/servers/lang/" + lang + ".json");
                }
                Path outputPath = Paths.get(Servers.ConfigDir_Language+"zh_cn.json");
                Files.copy(inputStream, outputPath, StandardCopyOption.REPLACE_EXISTING);
                //System.out.println("File written successfully.");
            } catch (IOException e) {
                LOGGER.error("Failed to write file", e);
            }
        }
    }


    public static String getLanguage() {
        reSet();
        String lang = Config.I.getDatas().lang;
        if (lang.equals("auto")) {
            Locale defaultLocale = Locale.getDefault();
            lang = defaultLocale.getLanguage()+"_"+defaultLocale.getCountry();
        }
        lang = lang.toLowerCase();
        File file = new File(Servers.ConfigDir_Language + lang + ".json");
        if (!file.exists()) {
            file = new File(Servers.ConfigDir_Language + "en_us.json");
        }
        //System.out.println("lang:"+ file.getPath());
        return file.getPath();
    }
    public Language() {
        super(filePath, """
                {
                    "trash.command.error.cleaning": "Cleaning up now. Please wait.",
                    "trash.command.error.has_player": "A player is checking the trash, please wait.",
                    "trash.menu.title": "Trash Bin",
                    "trash.menu.button.previous_page": "Previous Page",
                    "trash.menu.button.next_page": "Next Page",
                    "trash.menu.button.home_page": "Home Page",
                    "trash.menu.button.tips": "--",
                    "blacklist.deny": "You do not have permission to join the current server",
                    "clear.entity.limit": "Discovery of a large number of entities to begin liquidation",
                    "clear.entity.done": "clear $clear.count entity",
                    "clear.item.done": "clear $clear.count item,send '/servers trash' to open the trash bin",
                    "clear.normal.1":"clear in 1 second",
                    "clear.normal.2":"clear in 2 second",
                    "clear.normal.3":"clear in 3 second",
                    "clear.normal.4":"clear in 4 second",
                    "clear.normal.5":"clear in 5 second",
                    "clear.normal.6":"clear in 6 second",
                    "clear.normal.7":"clear in 7 second",
                    "clear.normal.8":"clear in 8 second",
                    "clear.normal.9":"clear in 9 second",
                    "clear.normal.10":"clear in 10 second",
                    "clear.normal.30":"clear in 30 second",
                    "clear.normal.60":"clear in 60 second"
                }
                """, new TypeToken<>(){});
    }

    public static String get(String key) {
        return getOrDefault(key, key);
    }
    public static String getOrDefault(String key, String def) {
        return I.getDatas().getOrDefault(key,def);
    }
    public static Component getComponent(String key) {
        return Component.literal(get(key));
    }
    public static Component getComponentOrDefault(String key, String def) {
        return Component.literal(getOrDefault(key,def));
    }
}
