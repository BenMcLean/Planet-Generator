import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.tools.imagepacker.TexturePacker2;
import com.badlogic.gdx.tools.imagepacker.TexturePacker2.Settings;

public class TexturePacker {
    private static void delete(final File delete) {
        if (delete.isDirectory()) {
            for (final File file : delete.listFiles()) {
                delete(file);
            }
        }
        delete.delete();
    }

    public static void main(final String[] args) throws Exception {
        final Settings settings = new Settings();
        settings.maxWidth = 1024;
        settings.maxHeight = 1024;
        settings.pot = true;
        settings.forceSquareOutput = true;
        settings.filterMin = TextureFilter.MipMapLinearLinear;
        settings.filterMag = TextureFilter.Linear;
        TexturePacker2.process(settings, "../assets-raw/terrain");
        settings.edgePadding = false;
        settings.paddingX = 0;
        settings.paddingY = 0;
        settings.duplicatePadding = false;
        packMap(settings, "../", "assets-raw/terrain", "../assets/", 32);
    }

    private static void packMap(final Settings settings, final String root, final String in,
                                final String out, final int resolution) throws Exception {
        final String inPath = root + "/" + in;
        sort(inPath, "" + resolution);
        TexturePacker2.process(settings, inPath + resolution, out + "/" + in + "/" + resolution);
    }

    private static void sort(final String root, final String resolution) throws IOException {
        final File outDir = new File(root + "/" + resolution);
        delete(outDir);
        outDir.mkdirs();

        for (final File file : new File(root).listFiles()) {
            if (file.getName().endsWith("-" + resolution + ".png")) {
                Files.copy(file.toPath(),
                        new File(outDir, file.getName().replace("-" + resolution, "")).toPath(),
                        StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }
}