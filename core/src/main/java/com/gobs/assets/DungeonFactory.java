package com.gobs.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.gobs.map.Level;
import com.gobs.map.LevelCell.LevelCellType;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

public class DungeonFactory {
    private DungeonFactory() {
    }

    public static Level loadMap(int worldWidth, int worldHeight, String res) throws IOException {
        Level level = new Level(worldWidth, worldHeight);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                Gdx.files.internal(res).read(), Charset.forName("UTF-8")))) {
            String line;

            Array<String> lines = new Array<>();

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("#")) {
                    continue;
                }
                lines.add(line);
            }
            lines.reverse();

            for (int j = 0; j < lines.size; j++) {
                int i = 0;
                for (char c : lines.get(j).toCharArray()) {
                    if (c == 'w') {
                        level.setCell(i, j, LevelCellType.WALL);
                    } else if (c == '.') {
                        level.setCell(i, j, LevelCellType.FLOOR);
                    } else if (c == '@') {
                        level.setCell(i, j, LevelCellType.STAIRS);
                    }
                    i += 1;
                }
            }
        }

        return level;
    }
}
