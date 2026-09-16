package analyzer;

import java.util.List;

public class PageInfo {
    public List<String> inputNames;
    public List<InputInfo> inputs;
    public List<String> buttonTexts;
    public List<ButtonInfo> buttons;
    public boolean hasForm;

    public PageInfo(List<String> inputNames, List<InputInfo> inputs, List<String> buttonTexts, List<ButtonInfo> buttons, boolean hasForm) {
        this.inputNames = inputNames;
        this.inputs = inputs;
        this.buttonTexts = buttonTexts;
        this.buttons = buttons;
        this.hasForm = hasForm;
    }
}