package example;

import example.service.GptService;
import example.service.LarkService;

public class Application {
    // 静态内部类负责初始化单例
    public static class SingletonHelper {
        public static final GptService GPT_SERVICE_INSTANCE = new GptService(
                "sk-",
                "https:///v1/chat/completions",
                "帮我总结一下我说的话",
                "gpt-3.5-turbo");
        public static final LarkService LARK_SERVICE_INSTANCE = new LarkService();
    }

    private static final GptService gptService = SingletonHelper.GPT_SERVICE_INSTANCE;

    public static void main(String[] args) {
        System.out.println(gptService.sendMsg("你好"));

    }
}
