package com.gobs.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.Disposable;

/**
 *
 */
public class FontFactory implements Disposable {
    private FreeTypeFontGenerator generator;

    public BitmapFont getFont(int size) {
        String file = "fonts/sazanami-mincho.ttf";

        generator = new FreeTypeFontGenerator(Gdx.files.internal(file));

        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = size;
        BitmapFont font = generator.generateFont(parameter);

        return font;
    }

    @Override
    public void dispose() {
        if (generator != null) {
            generator.dispose();
        }
    }
}
