package com.gzx.club.oss.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: gzx
 * @Description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileInfo {

    private String fileName;

    private Boolean directoryFlag;

    private String etag;

}
