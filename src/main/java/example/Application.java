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
    private static final GptService gptService = SingletonHelper.GPT_SERVICE_INSTANCE;
    private static final YmlConfig ymlConfig = SingletonHelper.YML_CONFIG;
    private static final LarkService larkService = SingletonHelper.LARK_SERVICE_INSTANCE;

    private static final int TASK_INITIAL_DELAY = 5; // 定时任务的初始延迟（秒）
    private static final int TASK_INTERVAL = 5; // 定时任务的执行间隔（秒）
    private static final CountDownLatch keepAliveLatch = new CountDownLatch(1);

    public static void main(String[] args) {
        String userToken = larkService.getUserToken();
        System.out.println(userToken);

        List<LinkDto> linkDtoList = larkService.getSheetRowData(userToken);

        String num = String.valueOf(UUID.randomUUID());
        int numFlag = 1;
        for (LinkDto linkDto : linkDtoList) {
            String text = larkService.getSummaryLinkString(linkDto.getLinkToken(), userToken);
            if(text == null) {
                System.out.println("文章过长，暂不支持");
                numFlag++;
                continue;
            }
            // 查找第一个句号的位置
            int splitIndex = text.indexOf('。') + 1;

            // 自动拆分字符串
            String summary = text.substring(0, splitIndex);
            String labels = text.substring(splitIndex);

            NewRowData newRow = new NewRowData();
            newRow.setNumber(num);
            newRow.setSummary(summary);
            newRow.setLabel(labels);
            newRow.setUpdateLog("暂无");
            newRow.setLink(linkDto.getHref());
            larkService.insertNewRow(userToken, numFlag++, newRow);
        }



//        scheduleTask();
//        keepServerRunning();
    }



    private static void scheduleTask() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            // 定时任务内容
            System.out.println("执行更新任务（用户token）" + System.currentTimeMillis());
            //获取用户token 更新所有

        }, TASK_INITIAL_DELAY, TASK_INTERVAL, TimeUnit.SECONDS);
    }

    private static void keepServerRunning() {
        try {
            keepAliveLatch.await(); // 阻塞等待，直到 latch 被释放
        } catch (InterruptedException e) {
            System.err.println("服务器运行时被中断：" + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

}
