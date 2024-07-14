package example.service;

import com.lark.oapi.core.request.EventReq;
import com.lark.oapi.core.utils.Jsons;
import com.lark.oapi.event.CustomEventHandler;
import com.lark.oapi.event.EventDispatcher;
import com.lark.oapi.service.im.ImService;
import com.lark.oapi.service.im.v1.model.P2MessageReceiveV1;
import com.lark.oapi.ws.Client;
import example.pojo.YmlConfig;
import util.SingletonHelper;

import java.nio.charset.StandardCharsets;

public class LarkEventService {
    private static final LarkService larkService = SingletonHelper.LARK_SERVICE_INSTANCE;

    private static final YmlConfig ymlConfig = SingletonHelper.YML_CONFIG;
    private static final EventDispatcher EVENT_HANDLER = EventDispatcher.newBuilder("", "")
            .onP2MessageReceiveV1(new ImService.P2MessageReceiveV1Handler() {
                @Override
                public void handle(P2MessageReceiveV1 event) {
                    System.out.printf("[ onP2MessageReceiveV1 access ], data: %s\n", Jsons.DEFAULT.toJson(event.getEvent()));
                    larkService.summaryStart();
                    //TODO ......
                }
            })
            .onCustomizedEvent("message", new CustomEventHandler() {
                @Override
                public void handle(EventReq event) throws Exception {
                    System.out.printf("[ onCustomizedEvent access ], type: message, data: %s\n", new String(event.getBody(), StandardCharsets.UTF_8));
                }
            })
            .build();


    public void start(){
        Client cli = new Client.Builder(ymlConfig.getLarkAppID(), ymlConfig.getLarkAppSecret())
                .eventHandler(EVENT_HANDLER)
                .build();
        cli.start();
        System.out.println("正在监听应用内表格变动事件...");
    }
}