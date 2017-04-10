package com.gobs.ui.gdx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.gobs.ui.GUILoader;
import java.io.Reader;

public class GdxGUILoader extends GUILoader<JsonValue, Color, BitmapFont> {
    public GdxGUILoader(GdxGUI gui) {
        super(gui);
    }

    @Override
    protected JsonValue readFile(Reader file) {
        JsonReader reader = new JsonReader();

        return reader.parse(file);
    }

    @Override
    protected boolean hasField(JsonValue value, String field) {
        return value.has(field);
    }

    @Override
    protected String readString(JsonValue value, String field) {
        return value.getString(field);
    }

    @Override
    protected int readInt(JsonValue value, String field) {
        return value.getInt(field);
    }

    @Override
    protected float readFloat(JsonValue value, String field) {
        return value.getFloat(field);
    }

    @Override
    protected JsonValue readObject(JsonValue value, String field) {
        return value.get(field);
    }

    @Override
    protected Iterable<JsonValue> readArray(JsonValue value) {
        return value;
    }

    @Override
    protected Iterable<JsonValue> readChildren(JsonValue value, String field) {
        return value.get(field);
    }

    @Override
    protected boolean isArray(JsonValue value) {
        return value.isArray();
    }
}
