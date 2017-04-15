package com.gobs.ui;

public class GUIStyle<Color, Font> {
    private GUIFormat<Color, Font> labelFormat;
    private GUIFormat<Color, Font> headerFormat;
    private GUIFormat<Color, Font> buttonFormat;
    private GUIFormat<Color, Font> buttonSelectedFormat;
    private GUIFormat<Color, Font> listItemFormat;
    private GUIFormat<Color, Font> listItemSelectedFormat;

    public GUIStyle() {
        this(null);
    }

    public GUIStyle(GUIStyle<Color, Font> parent) {
        if (parent != null) {
            labelFormat = new GUIFormat<>(parent.getLabelFormat());
            headerFormat = new GUIFormat<>(parent.getHeaderFormat());
            buttonFormat = new GUIFormat<>(parent.getButtonFormat());
            buttonSelectedFormat = new GUIFormat<>(parent.getButtonSelectedFormat());
            listItemFormat = new GUIFormat<>(parent.getListItemFormat());
            listItemSelectedFormat = new GUIFormat<>(parent.getListItemSelectedFormat());
        } else {
            labelFormat = new GUIFormat<>();
            headerFormat = new GUIFormat<>();
            buttonFormat = new GUIFormat<>();
            buttonSelectedFormat = new GUIFormat<>();
            listItemFormat = new GUIFormat<>();
            listItemSelectedFormat = new GUIFormat<>();
        }
    }

    public GUIFormat<Color, Font> getLabelFormat() {
        return labelFormat;
    }

    public GUIFormat<Color, Font> getHeaderFormat() {
        return headerFormat;
    }

    public GUIFormat<Color, Font> getButtonFormat() {
        return buttonFormat;
    }

    public GUIFormat<Color, Font> getButtonSelectedFormat() {
        return buttonSelectedFormat;
    }

    public GUIFormat<Color, Font> getListItemFormat() {
        return listItemFormat;
    }

    public GUIFormat<Color, Font> getListItemSelectedFormat() {
        return listItemSelectedFormat;
    }

    public void setFont(Font font) {
        labelFormat.setTextFont(font);
        headerFormat.setTextFont(font);
        buttonFormat.setTextFont(font);
        buttonSelectedFormat.setTextFont(font);
        listItemFormat.setTextFont(font);
        listItemSelectedFormat.setTextFont(font);
    }
}
