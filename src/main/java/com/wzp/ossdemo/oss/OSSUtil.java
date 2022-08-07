package com.wzp.ossdemo.oss;

import com.aliyun.oss.*;
import com.aliyun.oss.internal.OSSHeaders;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.StorageClass;

import java.io.InputStream;
import java.net.URL;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author zp.wei
 * @date 2022/7/20 19:53
 */
public class OSSUtil {


    private final String endpoint = OssClientConfig.ENDPOINT;
    private final String bucketName = OssClientConfig.BUCKETNAME;
    private final String accessKeyId = OssClientConfig.ACCESSKEYID;
    private final String accessKeySecret = OssClientConfig.ACCESSKEYSECRET;
    private final String expirationTime = OssClientConfig.EXPIRATIONTIME;
    private final List<String> fileTypeList = Stream.of("doc", "docx", "xls", "xlsx", "pdf").collect(Collectors.toList());
    //上传文件目录
    private final String sampleFolder = "sample/";
    private String folder;


    //创建ossClient
    private OSS getOssClient() {
        ClientBuilderConfiguration configuration = new ClientBuilderConfiguration();
        //设置是否支持cname，cname用于将自定义域名绑定到bucket上
        configuration.setSupportCname(true);
        return new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret, configuration);
    }

    /**
     * 获取存放路径
     *
     * @param filename
     * @param fileType
     * @return 返回带上路径的文件名
     */
    private String getFolderName(String filename, String fileType) {
        String fileName;
        if (fileTypeList.contains(fileType)) {
            fileName = sampleFolder + filename;
        } else {
            LocalDate localDate = LocalDate.now();
            int year = localDate.getYear();
            int month = localDate.getMonthValue();
            int day = localDate.getDayOfMonth();
            folder = year + "-" + month + "-" + day;
            fileName = folder + "/" + filename;
        }
        return fileName;
    }

    /**
     * 上传到OSS服务器 如果同名文件会覆盖服务器上的
     *
     * @param inputStream 文件流
     * @param filename    文件名称 包括后缀名
     * @return 出错返回"" ,唯一MD5数字签名
     */
    public String uploadFile(InputStream inputStream, String filename) {
        String fileType = FileTypeUtils.getFileType(inputStream);
        String fileName = getFolderName(filename, fileType);
        // 上传文件
        OSS ossClient = getOssClient();
        try {
            //判断文件是否存在
            boolean exist = ossClient.doesObjectExist(bucketName, fileName);
            if (!exist) {
                ObjectMetadata objectMetadata = new ObjectMetadata();
                objectMetadata.setHeader(OSSHeaders.OSS_STORAGE_CLASS, StorageClass.Standard.toString());
                objectMetadata.setObjectAcl(CannedAccessControlList.Private);
                //上传文件
                ossClient.putObject(bucketName, fileName, inputStream, objectMetadata);
            }
        } catch (OSSException oe) {
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Error Message:" + ce.getMessage());
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
        return getUrl(fileName);
    }


    /**
     * 获得url链接
     *
     * @param filename
     * @return
     */
    public String getUrl(String filename) {
        // 设置URL过期时间
        Date expiration = new Date(System.currentTimeMillis() + Long.parseLong(expirationTime));
        // 生成URL
        OSS ossClient = getOssClient();
        URL url = ossClient.generatePresignedUrl(bucketName, filename, expiration);
        return url.toString();
    }


}
