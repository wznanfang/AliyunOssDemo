package com.wzp.ossdemo.oss;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zp.wei
 * @date 2022/7/29 10:31
 */
public class FileTypeUtils {

    public static int default_check_length = 3;
    final static HashMap<String, String> fileTypeMap = new HashMap<>();


    //初始化文件头类型
    static {
        fileTypeMap.put("ffd8ff", "jpg");
        fileTypeMap.put("89504e", "png");
        fileTypeMap.put("d0cf11", "doc");
        fileTypeMap.put("504b03", "docx");
        fileTypeMap.put("255044", "pdf");
    }


    /**
     * 获取文件类型
     *
     * @param inputStream
     * @return
     */
    public static String getFileType(InputStream inputStream) {
        byte[] bytes = new byte[default_check_length];
        try {
            //获取文件头前三位魔数的二进制
            inputStream.read(bytes, 0, bytes.length);
            //转为16进制
            String code = bytesToHexString(bytes);
            for (Map.Entry<String, String> item : fileTypeMap.entrySet()) {
                if (code.equals(item.getKey())) {
                    return item.getValue();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    /**
     * 字节数组转为16进制
     *
     * @param bytes
     * @return
     */
    public static String bytesToHexString(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            int value = bytes[i] & 0xFF;
            String hashValue = Integer.toHexString(value);
            if (hashValue.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hashValue);
        }
        return stringBuilder.toString();
    }

    /**
     * 通过文件后缀名判断文件类型
     *
     * @param filename
     * @return
     */
    public static String getFileType(String filename) {
        return filename.substring(filename.lastIndexOf(".") + 1);
    }


}
