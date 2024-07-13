package example.pojo.lark;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

@Data
public class LinkData {
    //文章摘要
    private String bio;
    //文章标签
    private String label;
}
