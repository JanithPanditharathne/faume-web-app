package com.zone24x7.faume.webapp.util;

import com.zone24x7.faume.webapp.service.FilesStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Util class for static context initializing
 */
@Component
public class StaticContextInitializer {

    @Autowired
    private FilesStorageService filesStorageService;

    /**
     * Method to execute after the initialization of the static context
     *
     * @throws Exception May throw IO exception error occurs with file storage access
     */
    @PostConstruct
    public void init() throws Exception {
        filesStorageService.deleteAll();
        filesStorageService.init();
    }
}
