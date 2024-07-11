package example.service;

import com.alibaba.fastjson.JSON;
import example.pojo.gpt.GptReq;
import example.pojo.gpt.GptRes;
import okhttp3.*;

import java.io.IOException;
import java.util.Objects;

public record GptService(String gptSK,
                         String gptUrl,
                         String initPrompt,
                         String gptModel) {


    /**
     * 获取gpt响应文字
     *
     * @param reqString 发送请求语句
     * @return gpt响应语句
     */
    public String sendMsg(String reqString) {
        OkHttpClient client = new OkHttpClient();

        //请求体格式
        MediaType mediaType = MediaType.parse("application/json");

        //创建请求体
        GptReq gptReq = new GptReq(reqString, initPrompt, gptModel);
        String gptReqJSON = JSON.toJSONString(gptReq);
        RequestBody requestBody = RequestBody.create(gptReqJSON, mediaType);

        // 创建 POST 请求
        Request request = new Request.Builder()
                .url(gptUrl)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + gptSK)
                .post(requestBody)
                .build();
//        System.out.println(request);
        try {
            // 发送请求并获取响应
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) return null;

            String responseBody = Objects.requireNonNull(response.body()).string();
            GptRes gptResJson = JSON.parseObject(responseBody, GptRes.class);
            return gptResJson.getChoices().get(0).getMessage().getContent();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }
}
