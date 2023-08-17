package org.joker.service;

import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class CollectCodeFileService {
    /**
     * 判断文件是否为指定后缀名
     * @param code
     * @param extensionName
     * @return
     */
    public boolean matchExtensionName(File code, String extensionName) {
        if (!code.exists()) {
            return false;
        }
        String fileName = code.getName();
        if (extensionName.equals(fileName.substring(fileName.lastIndexOf(".") + 1))) {
            return true;
        }else {
            return false;
        }
    }

    public String getExtensionName(File code) {
        if (!code.exists()) {
            return "";
        }
        String fileName = code.getName();
        return getExtensionName(code.getName());
    }

    public String getExtensionName(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }
}
