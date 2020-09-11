package com.test.config;

import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author songdaxin
 */
@Component
@ConfigurationProperties(prefix = "screw")
@Data
@ToString
public class ScrewConfig {

    private String version;

    private String outputDir;

    private boolean openAfterComplete;

    private String fileType;

    private String description;

    private List<String> ignoreTableName;
    private List<String> ignorePrefix;
    private List<String> ignoreSuffix;
    private List<String> customTableName;


}
