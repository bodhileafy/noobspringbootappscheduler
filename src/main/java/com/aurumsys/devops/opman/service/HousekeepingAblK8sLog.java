package com.aurumsys.devops.opman.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.Calendar;

@Component
@Slf4j
public class HousekeepingAblK8sLog {

    @Value("${abl.k8s.node.log.path}")
    private String ABL_K8S_NODE_LOG_PATH;

    @Value("${abl.k8s.node.housekeeping.purge.days}")
    private int ABL_K8S_NODE_HOUSEKEEPING_PURSE_DAYS;

    public HousekeepingAblK8sLog() { }

    @Scheduled(cron = "${abl.k8s.node.housekeeping.cron}")
    public void housekeeping() throws IOException {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, ABL_K8S_NODE_HOUSEKEEPING_PURSE_DAYS * -1);
        long purgeTime = cal.getTimeInMillis();
        Files.walk(Paths.get(ABL_K8S_NODE_LOG_PATH))
                .filter(Files::isRegularFile)
                .forEach(path -> {
                    try {
                        if (Files.getLastModifiedTime(path).toMillis() < purgeTime) {
                            log.info("Delete");
                        } else {
                            log.info("Not Delete");
                            Files.setLastModifiedTime(path, FileTime.fromMillis(purgeTime));
                        }
                        log.info(Long.toString(purgeTime));
                        log.info(Long.toString(Files.getLastModifiedTime(path).toMillis()));
                        log.info(java.time.LocalTime.now().toString());
                        //Files.delete(path);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }

}