package kr.sooragenius.toy.board.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class FileStore {
    private String originalName;
    private String storeName;
}
