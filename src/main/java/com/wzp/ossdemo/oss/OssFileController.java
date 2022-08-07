package com.wzp.ossdemo.oss;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * @author zp.wei
 * @date 2022/7/20 20:07
 */
@RestController
@RequestMapping("/oss")
public class OssFileController {

    OSSUtil ossUtil = new OSSUtil();

    @PostMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file) throws Exception {
        // 原始文件名称，如a.png
        String filename = file.getOriginalFilename();
        String fileMd5 = getFileMD5(file);
        filename = fileMd5 + filename.substring(filename.lastIndexOf("."));
        String url = ossUtil.uploadFile(file.getInputStream(), filename);
        return url;
    }


    private String getFileMD5(MultipartFile file) throws Exception {
        byte[] fileBytes = file.getBytes();
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        byte[] digest = md5.digest(fileBytes);
        return new BigInteger(1, digest).toString(16);
    }


    /**
     * 获取文件链接
     *
     * @param filename
     * @return
     */
    @PostMapping("getUrl")
    public String getUrl(@RequestParam("filename") String filename) {
        return ossUtil.getUrl(filename);

    }


}
