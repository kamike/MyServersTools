package nws.mc.servers.helper;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public class _F {

    public static List<Path> getFiles(String directoryPath, String suffix) {
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
}
