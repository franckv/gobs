package com.gobs.ui;

public class GUIFormat<Color, Font> {
    private Color textColor;
    private Color textBgColor;
    private Font textFont;

    public GUIFormat() {

    }

    public GUIFormat(Color textColor, Color textBgColor, Font textFont) {
        this.textColor = textColor;
        this.textBgColor = textBgColor;
        this.textFont = textFont;
    }

    public GUIFormat(GUIFormat<Color, Font> parent) {
        this.textColor = parent.textColor;
        this.textBgColor = parent.textBgColor;
        this.textFont = parent.textFont;
    }

    public Color getTextColor() {
        return textColor;
    }

    public void setTextColor(Color textColor) {
        this.textColor = textColor;
    }

    public Color getTextBgColor() {
        return textBgColor;
    }

    public void setTextBgColor(Color textBgColor) {
        this.textBgColor = textBgColor;
    }

    public Font getTextFont() {
        return textFont;
    }

    public void setTextFont(Font textFont) {
        this.textFont = textFont;
    }
}
