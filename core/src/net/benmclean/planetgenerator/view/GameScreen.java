package net.benmclean.planetgenerator.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import net.benmclean.planetgenerator.controller.GameInputProcessor;
import net.benmclean.planetgenerator.model.GameWorld;

public class GameScreen implements Screen, Disposable {
    public GameScreen() {
        this(42);
    }

    public GameScreen(long SEED) {
        this.SEED = SEED;
        world = new GameWorld(SEED);
    }

    public long SEED;
    public static final int VIRTUAL_WIDTH = 320;
    public static final int VIRTUAL_HEIGHT = 200;
    public static final int TILE_WIDTH = 16;
    public static final int TILE_HEIGHT = 16;
    public Assets assets;
    private Color worldBackgroundColor;
    private Color screenBackgroundColor;
    private Viewport worldView;
    private Viewport screenView;
    private SpriteBatch batch;
    private TiledMap map;
    private OrthogonalTiledMapRenderer tiledMapRenderer;
    private FrameBuffer frameBuffer;
    private Texture screenTexture;
    private TextureRegion screenRegion;
    private Color[][] palettes;
    private Color[] greyPalette;
    private Color[] gameboyPalette;
    public GameWorld world;
    public GameInputProcessor input;

    public static TiledMapTileLayer.Cell makeCell(TiledMapTile tile) {
        TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
        cell.setTile(tile);
        return cell;
    }

    @Override
    public void show() {
        assets = new Assets();
        worldBackgroundColor = Color.BLACK;
        screenBackgroundColor = Color.BLACK;
        worldView = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        screenView = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        batch = new SpriteBatch();
        map = new TiledMap();
        frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, VIRTUAL_WIDTH, VIRTUAL_HEIGHT, true, true);
        screenRegion = new TextureRegion();

        greyPalette = new Color[4];
        greyPalette[0] = new Color(0 / 255f, 0 / 255f, 0 / 255f, 255 / 255f);
        greyPalette[1] = new Color(85 / 255f, 85 / 255f, 85 / 255f, 255 / 255f);
        greyPalette[2] = new Color(170 / 255f, 170 / 255f, 170 / 255f, 255 / 255f);
        greyPalette[3] = new Color(255 / 255f, 255 / 255f, 255 / 255f, 255 / 255f);

        gameboyPalette = new Color[4];
        gameboyPalette[0] = new Color(15 / 255f, 56 / 255f, 15 / 255f, 255 / 255f);
        gameboyPalette[1] = new Color(48 / 255f, 98 / 255f, 48 / 255f, 255 / 255f);
        gameboyPalette[2] = new Color(140 / 255f, 173 / 255f, 15 / 255f, 255 / 255f);
        gameboyPalette[3] = new Color(156 / 255f, 189 / 255f, 15 / 255f, 255 / 255f);

        palettes = new Color[2][];
        palettes[0] = greyPalette;
        palettes[1] = gameboyPalette;

        MapLayers layers = map.getLayers();
        TiledMapTileLayer[] layer = new TiledMapTileLayer[2];
        for (int x = 0; x < layer.length; x++)
            layer[x] = new TiledMapTileLayer(world.SIZE_X, world.SIZE_Y, TILE_WIDTH, TILE_HEIGHT);
        for (int x = 0; x < world.SIZE_X; x++) {
            for (int y = 0; y < world.SIZE_Y; y++) {
                StaticTiledMapTile tile = null;
                Boolean answer = world.isWall(x, y);
                if (answer != null && !answer) {
                    tile = new StaticTiledMapTile(assets.floor);
                    layer[1].setCell(x, y, makeCell(tile));
                } else if (answer != null) {
                    tile = new StaticTiledMapTile(assets.wall);
                    layer[0].setCell(x, y, makeCell(tile));
                }
            }
        }
        for (int x = 0; x < layer.length; x++)
            layers.add(layer[x]);
        tiledMapRenderer = new OrthogonalTiledMapRenderer(map);
        screenView.getCamera().position.set(VIRTUAL_WIDTH / 2, VIRTUAL_HEIGHT / 2, 0);
        screenView.update(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        batch.enableBlending();
        input = new GameInputProcessor(world, this);
        Gdx.input.setInputProcessor(input);
    }

    @Override
    public void render(float delta) {
        input.tick(delta);

        frameBuffer.begin();
        Gdx.gl.glClearColor(worldBackgroundColor.r, worldBackgroundColor.g, worldBackgroundColor.b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        worldView.apply();
        tiledMapRenderer.getBatch().setShader(assets.shader);

        for (int layer = 0; layer < map.getLayers().getCount(); layer++) {
            tiledMapRenderer.getBatch().begin();
            assets.applyPalette(palettes[layer]);
            for (int dx = -1; dx <= 1; dx++)
                for (int dy = -1; dy <= 1; dy++) {
                    worldView.getCamera().position.set(
                            // player position + center of tile + over the edge for wrapping
                            world.getPlayerX() * TILE_WIDTH + TILE_WIDTH / 2 + world.SIZE_X * TILE_WIDTH * dx,
                            world.getPlayerY() * TILE_HEIGHT + TILE_HEIGHT / 2 + world.SIZE_Y * TILE_HEIGHT * dy,
                            0
                    );
                    worldView.update(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
                    tiledMapRenderer.setView((OrthographicCamera) worldView.getCamera());
                    tiledMapRenderer.renderTileLayer((TiledMapTileLayer) map.getLayers().get(layer));
                }
            tiledMapRenderer.getBatch().end();
        }

        tiledMapRenderer.getBatch().setShader(null);

        batch.setProjectionMatrix(worldView.getCamera().combined);
        batch.begin();

        batch.end();
        frameBuffer.end();

        Gdx.gl.glClearColor(screenBackgroundColor.r, screenBackgroundColor.g, screenBackgroundColor.b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        screenView.apply();
        batch.setProjectionMatrix(screenView.getCamera().combined);
        batch.begin();
        screenTexture = frameBuffer.getColorBufferTexture();
        screenTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        screenRegion.setRegion(screenTexture);
        screenRegion.flip(false, true);
        batch.draw(screenRegion, 0, 0);
        batch.end();
    }

    public void drawRect(SpriteBatch batch, int x, int y, int width, int height) {
        batch.draw(assets.one, x + width - 1, y + 1, 1, height - 1);
        batch.draw(assets.one, x + 1, y, width - 1, 1);
        batch.draw(assets.one, x, y, 1, height - 1);
        batch.draw(assets.one, x, y + height - 1, width - 1, 1);
    }

    public void drawSquareOverTile(SpriteBatch batch, int x, int y) {
        batch.draw(assets.one, x * TILE_WIDTH, y * TILE_HEIGHT, TILE_WIDTH, TILE_HEIGHT);
    }

    @Override
    public void resize(int width, int height) {
        screenView.update(width, height);
    }

    @Override
    public void dispose() {
        batch.dispose();
        frameBuffer.dispose();
        assets.dispose();
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    public static void toggleFullscreen() {
        if (Gdx.graphics.isFullscreen())
            Gdx.graphics.setWindowedMode(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        else
            Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
    }
}
