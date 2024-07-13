package util;

import example.annotation.ConfigValueProcessor;
import example.annotation.YmlConfigLoader;
import example.pojo.YmlConfig;
import example.service.GptService;
import example.service.LarkService;

//初始化单例
public class SingletonHelper {
    public static final YmlConfig YML_CONFIG = new YmlConfig();
    static {
        YmlConfigLoader configLoader = new YmlConfigLoader("base-config.yml");

        ConfigValueProcessor processor = new ConfigValueProcessor(configLoader);
        processor.process(YML_CONFIG);
    }
    public static final GptService GPT_SERVICE_INSTANCE = new GptService(
            YML_CONFIG.getGptSK(),
            YML_CONFIG.getGptUrl(),
            YML_CONFIG.getInitPrompt(),
            YML_CONFIG.getGptModel());

    public static final LarkService LARK_SERVICE_INSTANCE = new LarkService();

}