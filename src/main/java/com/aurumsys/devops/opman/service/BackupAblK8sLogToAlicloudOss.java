package com.aurumsys.devops.opman.service;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Paths;

@Component
public class BackupAblK8sLogToAlicloudOss {

    @Value("${abl.k8s.node.log.path}")
    private String ABL_K8S_NODE_LOG_PATH;

    public BackupAblK8sLogToAlicloudOss() { }

    public void backup() throws IOException {
        InetAddress id = InetAddress.getLocalHost();
        System.out.println(id.getHostName());
        Files.walk(Paths.get(ABL_K8S_NODE_LOG_PATH))
                .filter(Files::isRegularFile)
                .forEach(path -> {
                    OSS ossClient = new OSSClientBuilder().build("https://oss-ap-southeast-3-internal.aliyuncs.com",
                            "LTAI4Fuo9hrW9AjFy84p6LYa",
                            "dyTa5r4W5noapik5CNWLDDzrPgKYfd");
                    PutObjectRequest putObjectRequest = new PutObjectRequest(
                            "log-vol01",
                            path.getParent().toString().substring(1).replace("\\", "/")+"/"+id.getHostName()+"/"+path.getFileName(),
                            new File(path.toString()));
                    ossClient.putObject(putObjectRequest);
                    ossClient.shutdown();
                    System.out.println(path.getParent().toString().substring(1).replace("\\", "/")+"/"+id.getHostName()+"/"+path.getFileName());
                });
    }

}