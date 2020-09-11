package com.test.service;

import cn.smallbun.screw.core.Configuration;
import cn.smallbun.screw.core.engine.EngineConfig;
import cn.smallbun.screw.core.engine.EngineFileType;
import cn.smallbun.screw.core.engine.EngineTemplateType;
import cn.smallbun.screw.core.execute.DocumentationExecute;
import cn.smallbun.screw.core.process.ProcessConfig;
import com.test.config.ScrewConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author songdaxin
 */
@org.springframework.context.annotation.Configuration
@Slf4j
public class InitService implements InitializingBean {

    private static final String URL_REGEX = "jdbc:mysql://(?<address>(\\w+\\.){0,5}):(?<port>\\d+)/(?<dataBase>\\w+)" +
            "?\\.*";
    @Resource
    ApplicationContext applicationContext;

    @Resource
    private ScrewConfig screwConfig;

    @Value("${spring.datasource.url}")
    private String dataBaseUrl;

    @Override
    public void afterPropertiesSet() {
        contextLoads();
    }


    public void contextLoads() {
        log.info("********************************");
        log.info("*****生成文档的配置情况如下*********");
        log.info("");
        log.info(String.valueOf(screwConfig));
//        log.info("数据库地址：{}", parseDataBase(dataBaseUrl));
        log.info("开始生成数据库文档");
        DataSource dataSourceMysql = applicationContext.getBean(DataSource.class);

        // 生成文件配置
        EngineConfig engineConfig = EngineConfig.builder()
                .fileOutputDir(screwConfig.getOutputDir())
                // 打开目录
                .openOutputDir(true)
                // 文件类型
                .fileType(EngineFileType.valueOf(screwConfig.getFileType()))
                // 生成模板实现
                .produceType(EngineTemplateType.freemarker).build();

        // 生成文档配置（包含以下自定义版本号、描述等配置连接）
        Configuration config = Configuration.builder()
                .version(screwConfig.getVersion())
                .description(screwConfig.getDescription())
                .dataSource(dataSourceMysql)
                .engineConfig(engineConfig)
                .produceConfig(getProcessConfig())
                .build();

        // 执行生成
        new DocumentationExecute(config).execute();
        log.info("生成完成！！！！");
        log.info("=======================");

    }

    private String parseDataBase(String dataBaseUrl) {
        Pattern pattern = Pattern.compile(URL_REGEX);
        Matcher matcher = pattern.matcher(dataBaseUrl);
        if (matcher.find()) {
            String address = matcher.group("address");
            String dataBase = matcher.group("dataBase");
            String port = matcher.group("port");
            System.out.println("djhshdsjhd");
        }
        return null;

    }


    /**
     * 配置想要生成的表+ 配置想要忽略的表
     *
     * @return 生成表配置
     */
    public ProcessConfig getProcessConfig() {
        // 忽略表名
        List<String> ignoreTableName = screwConfig.getIgnoreTableName();
        // 忽略表前缀，如忽略a开头的数据库表
        List<String> ignorePrefix = screwConfig.getIgnorePrefix();
        // 忽略表后缀
        List<String> ignoreSuffix = screwConfig.getIgnoreSuffix();
        // 指定表
        List<String> customTableName = screwConfig.getCustomTableName();

        return ProcessConfig.builder()
                //根据名称指定表生成
                .designatedTableName(customTableName)
                //根据表前缀生成
                .designatedTablePrefix(ignorePrefix)
                //根据表后缀生成
                .designatedTableSuffix(ignoreSuffix)
                //忽略表名
                .ignoreTableName(ignoreTableName)
                //忽略表前缀
                .ignoreTablePrefix(ignorePrefix)
                //忽略表后缀
                .ignoreTableSuffix(ignoreSuffix).build();
    }
}
