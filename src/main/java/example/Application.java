package example;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONException;
import com.alibaba.fastjson2.JSONObject;
import example.annotation.ConfigValueProcessor;
import example.annotation.YmlConfigLoader;
import example.pojo.YmlConfig;
import example.pojo.lark.LinkData;
import example.pojo.lark.LinkDto;
import example.pojo.lark.NewRowData;
import example.pojo.lark.SheetRowData;
import example.service.GptService;
import example.service.LarkEventService;
import example.service.LarkService;
import util.SingletonHelper;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Application {
    private static final LarkService larkService = SingletonHelper.LARK_SERVICE_INSTANCE;
    private static final LarkEventService larkEventService = SingletonHelper.LARK_EVENT_SERVICE;

    public static void main(String[] args) {
        //第一次启动，总结插入整个表格
        larkService.summaryStart();

        //ws长连接监听应用内事件， 当表格变动重新总结该行数据， 并更新日志
        larkEventService.start();
    }

}
