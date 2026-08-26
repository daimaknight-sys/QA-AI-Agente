package analyzer;

import java.util.List;

public class PageInfo {
    public List<String> inputNames;
    public List<String> buttonTexts;
    public boolean hasForm;

    public PageInfo(List<String> inputNames, List<String> buttonTexts, boolean hasForm) {
        this.inputNames = inputNames;
        this.buttonTexts = buttonTexts;
        this.hasForm = hasForm;
    }
}