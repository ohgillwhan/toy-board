package kr.sooragenius.toy.board.util;

import kr.sooragenius.toy.board.dto.FileStore;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MutlipartFileUtil {
    public static List<FileStore> transferTo(String path, List<MultipartFile> files) throws IOException {
        List<FileStore> result = new ArrayList<>();
        for(MultipartFile file : files) {
            if(!file.isEmpty()) {
                String storedName = UUID.randomUUID().toString();
                String originalName = file.getOriginalFilename();
                file.transferTo(new File(path + File.separator + storedName));

                result.add(new FileStore(originalName, storedName));
            }
        }
        return result;
    }
}
