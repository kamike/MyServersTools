package nws.mc.servers.config.player$group;

import com.google.gson.reflect.TypeToken;
import nws.dev.core.json._JsonConfig;
import nws.mc.servers.Servers;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlayerGroup {
    public static final HashMap<String,Group> groups = getGroups();

    public static HashMap<String,Group> getGroups() {
        HashMap<String,Group> abc = new HashMap<>();
        List<Path> jsonFiles = getFiles(Servers.ConfigDir_PlayerGroup,".json");
        for (Path path : jsonFiles) {
            String fileName = path.getFileName().toString();
            //System.out.println("fileName:"+fileName+"path:"+path.toString());
            abc.put(fileName,new Group(path.toString()));
        }
        return abc;
    }
    private static List<Path> getFiles(String directoryPath, String suffix) {
        List<Path> jsonFiles = new ArrayList<>();
        Path startPath = Paths.get(directoryPath);
        try {
            Files.walkFileTree(startPath, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    if (file.toString().endsWith(suffix)) {
                        jsonFiles.add(file);
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return jsonFiles;
    }

    public static class Group extends _JsonConfig<List<String>> {
        public Group(String filePath) {
            super(filePath, "",new TypeToken<>(){});
        }

        @Override
        public List<String> getDatas() {
            if (datas == null) datas = new ArrayList<>();
            return datas;
        }

        @Override
        public String toString() {
            return datas.toString();
        }
    }
}
