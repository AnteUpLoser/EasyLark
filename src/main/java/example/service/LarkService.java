package example.service;

import example.Application;

public class LarkService {
    private static final GptService gptService = Application.SingletonHelper.GPT_SERVICE_INSTANCE;

    public void getNewPage(){
        System.out.println(gptService.sendMsg("naosda"));
    }
}
