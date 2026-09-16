package analyzer;

public class InputInfo {
    public String identifierType;
    public String identifierValue;
    public boolean visible;
    public String revealedByButtonText; // null si es visible por defecto, o el texto del botón que lo revela

    public InputInfo(String identifierType, String identifierValue, boolean visible) {
        this.identifierType = identifierType;
        this.identifierValue = identifierValue;
        this.visible = visible;
    }
}