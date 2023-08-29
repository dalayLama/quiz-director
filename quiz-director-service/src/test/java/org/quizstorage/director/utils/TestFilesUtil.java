package org.quizstorage.director.utils;

import lombok.experimental.UtilityClass;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.nio.file.Files;

@UtilityClass
public class TestFilesUtil {

    public static String readFile(String path) {
        try {
            Resource resource = new ClassPathResource(path);
            return Files.readString(resource.getFile().toPath());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
