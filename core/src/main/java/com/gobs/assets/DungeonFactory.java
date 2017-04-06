package com.gobs.assets;

import com.badlogic.gdx.Gdx;
import com.gobs.map.Level;
import com.gobs.map.LevelCell.LevelCellType;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DungeonFactory {
    private DungeonFactory() {
    }

    public static Level loadMap(int worldWidth, int worldHeight, String res) throws IOException {
        Level level = new Level(worldWidth, worldHeight);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                Gdx.files.internal(res).read(), Charset.forName("UTF-8")))) {
            String line;

            List<String> lines = new ArrayList<>();

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("#")) {
                    continue;
                }
                lines.add(line);
            }
            Collections.reverse(lines);

            for (int j = 0; j < lines.size(); j++) {
                int i = 0;
                for (char c : lines.get(j).toCharArray()) {
                    if (c == 'w') {
                        level.setCell(i, j, LevelCellType.WALL, true);
                    } else if (c == '.') {
                        level.setCell(i, j, LevelCellType.FLOOR, false);
                    } else if (c == '@') {
                        level.setCell(i, j, LevelCellType.STAIRS, false);
                    }
                    i += 1;
                }
            }
        }

        return level;
    }
}
