package example.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONException;
import com.alibaba.fastjson2.JSONObject;
import example.pojo.YmlConfig;
import example.pojo.lark.LinkDto;
import example.pojo.lark.NewRowData;
import example.pojo.lark.SheetRowData;
import okhttp3.*;
import util.SingletonHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LarkService {
    private static final YmlConfig ymlConfig = SingletonHelper.YML_CONFIG;
    private static final String GET_USER_TOKEN_URL = "https://open.feishu.cn/open-apis/auth/v3/app_access_token/internal";
    private static final String NEW_SHEET_URL = "https://open.feishu.cn/open-apis/sheets/v3/spreadsheets";
    private static final String GET_SHEET_ROW_DATA_URL = "https://open.feishu.cn/open-apis/sheets/v2/spreadsheets/"+ymlConfig.getSheetID()+"/values/5cc663!B1:B4";
    private static final String GET_SHEET_ID_URL = "https://open.feishu.cn/open-apis/sheets/v3/spreadsheets/"+ ymlConfig.getNewSheetID()+"/sheets/query";

    private static final String PUT_SHEET_DATA = "https://open.feishu.cn/open-apis/sheets/v2/spreadsheets/"+ ymlConfig.getNewSheetID() +"/values";



    private static final GptService gptService = SingletonHelper.GPT_SERVICE_INSTANCE;

    public String httpGet(String url, String userToken){
        OkHttpClient client = new OkHttpClient();

        //请求体格式
//        MediaType mediaType = MediaType.parse("application/json");

        //创建请求体


        // 创建 POST 请求
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + userToken)
                .get()
                .build();
//        System.out.println(request);
        try {
            // 发送请求并获取响应
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                return null;
            }

            //            System.out.println(responseBody);
            return Objects.requireNonNull(response.body()).string();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
//    public String httpPost()

    public String getUserToken(){
        OkHttpClient client = new OkHttpClient();

        String jsonBody = String.format("""
                {
                    "app_id": "%s",
                    "app_secret": "%s"
                }
                """, ymlConfig.getLarkAppID(), ymlConfig.getLarkAppSecret());

        // 请求体格式
        MediaType mediaType = MediaType.parse("application/json");

        // 创建请求体
        RequestBody requestBody = RequestBody.create(mediaType, jsonBody);

        // 创建 POST 请求
        Request request = new Request.Builder()
                .url(GET_USER_TOKEN_URL)
                .post(requestBody)
                .build();

        // 处理响应
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                // 将响应体转换为字符串
                String responseBody = Objects.requireNonNull(response.body()).string();
                System.out.println(responseBody);
                // 使用 FastJSON 解析响应体
                JSONObject jsonObject = JSON.parseObject(responseBody);
                // 获取 tenant_access_token
                return jsonObject.getString("tenant_access_token");
            } else {
                throw new IOException("Unexpected code " + response);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


// 获取所有link对象
    public List<LinkDto> getSheetRowData(String userToken){
        String jsonString = httpGet(GET_SHEET_ROW_DATA_URL, userToken);
        // 解析JSON数据到JSONObject
        JSONObject jsonObject = JSON.parseObject(jsonString);

        // 获取 "valueRange" 对象
        JSONObject valueRangeObject = jsonObject.getJSONObject("data").getJSONObject("valueRange");

        if (valueRangeObject == null) {
            System.out.println("valueRange is null");
            return null;
        }

        // 获取 "values" 数组
        JSONArray valuesArray = valueRangeObject.getJSONArray("values");

        if (valuesArray == null) {
            System.out.println("values is null");
            return null;
        }

        // 创建一个列表来存储所有的 链接token 对象
        List<LinkDto> linkDtoList = new ArrayList<>();

        // 遍历 values 数组，解析每一个 SheetRowData 对象
        for (int i = 1; i < valuesArray.size(); i++) {
            try{
                JSONArray innerArray = valuesArray.getJSONArray(i).getJSONArray(0);
                if (innerArray != null) {
                    List<SheetRowData> sheetRowDataList = innerArray.toJavaList(SheetRowData.class);
                    System.out.println(sheetRowDataList);
                    if(sheetRowDataList.get(0).getLink() != null) {
                        LinkDto dto = new LinkDto();
                        dto.setHref(sheetRowDataList.get(0).getLink());
                        dto.setLinkToken(sheetRowDataList.get(0).getToken());
                        linkDtoList.add(dto);
                    }
                }
            }catch (JSONException ignored){
            }
        }
        return linkDtoList;
    }

    //获取link链接中的纯文本并用GPT概括
    public String getSummaryLinkString(String linkToken, String userToken){
        String url = "https://open.feishu.cn/open-apis/docx/v1/documents/"+linkToken+"/raw_content";
        String linkArticleString = httpGet(url, userToken);
        String summary = gptService.sendMsg(linkArticleString);
        return summary;
    }


    //获取新表格的SheetID
    public String getSheetId(String userToken){
        String jsonData = httpGet(GET_SHEET_ID_URL, userToken);
        // 解析 JSON 字符串为 JSONObject
        JSONObject jsonObject = JSON.parseObject(jsonData);

        // 获取 data 对象
        JSONObject dataObject = jsonObject.getJSONObject("data");

        // 获取 sheets 数组
        JSONArray sheetsArray = dataObject.getJSONArray("sheets");

        // 获取第一个 sheet 对象
        if (sheetsArray != null && !sheetsArray.isEmpty()) {
            JSONObject sheetObject = sheetsArray.getJSONObject(0);

            // 提取 sheet_id
            return sheetObject.getString("sheet_id");
        } else {
            System.out.println("No sheets found");
            return null;
        }
    }

    //插入表格新行
    public void insertNewRow(String userToken, int num, NewRowData newRowData) {
        String range = getSheetId(userToken) + "!A"+num + ":E5";

        // 构建请求体数据
        String jsonBody = String.format("""
                {
                    "valueRange": {
                        "range": "%s",
                        "values": [
                            ["%s", "%s", "%s", "%s", "%s"]
                        ]
                    }
                }
                """, range, newRowData.getNumber(), newRowData.getUpdateLog(), newRowData.getSummary(), newRowData.getLink(), newRowData.getLabel());
        // 创建请求体
        RequestBody requestBody = RequestBody.create(jsonBody, MediaType.get("application/json"));

        // 构建请求
        Request request = new Request.Builder()
                .url(PUT_SHEET_DATA)
                .addHeader("Authorization", "Bearer " + userToken)
                .put(requestBody)
                .build();

        // 发送请求
        OkHttpClient client = new OkHttpClient();
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String responseBody = Objects.requireNonNull(response.body()).string();
                System.out.println("Response: " + responseBody);
            } else {
                System.err.println("Request failed with code: " + response.code());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



}
