package example.pojo.lark;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

@Data
public class SheetRowData {
    @JSONField(name = "link")
    private String link;
    @JSONField(name = "mentionNotify")
    private String mentionNotify;
    @JSONField(name = "mentionType")
    private String mentionType;
    @JSONField(name = "text")
    private String text;
    @JSONField(name = "token")
    private String token;
    @JSONField(name = "type")
    private String type;
}
