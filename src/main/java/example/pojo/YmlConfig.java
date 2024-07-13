package example.pojo;

import example.annotation.YamlValue;
import lombok.Data;

@Data
public class YmlConfig {
    @YamlValue("gpt.sk")
    private String gptSK;
    @YamlValue("gpt.url")
    private String gptUrl;
    @YamlValue("gpt.model")
    private String gptModel;
    @YamlValue("gpt.init-prompt")
    private String initPrompt;
    @YamlValue("lark.app-id")
    private String larkAppID;
    @YamlValue("lark.app-secret")
    private String larkAppSecret;
    @YamlValue("lark.sheet-id")
    private String sheetID;
    @YamlValue("lark.new-sheet-id")
    private String newSheetID;



}
