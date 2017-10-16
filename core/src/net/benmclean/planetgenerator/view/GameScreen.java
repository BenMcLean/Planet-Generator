package net.benmclean.planetgenerator.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import net.benmclean.planetgenerator.controller.GameInputProcessor;
import net.benmclean.planetgenerator.model.Assets;
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
    public static final int VIRTUAL_WIDTH = 355;
    public static final int VIRTUAL_HEIGHT = 200;
    private Color worldBackgroundColor;
    private Color screenBackgroundColor;
    private Viewport worldView;
    private Viewport screenView;
    private SpriteBatch batch;
    private OrthogonalTiledMapRenderer tiledMapRenderer;
    private FrameBuffer frameBuffer;
    private Texture screenTexture;
    private TextureRegion screenRegion;
    public GameWorld world;
    public GameInputProcessor input;
//    private GUIConsole console;

    @Override
    public void show() {
        screenBackgroundColor = Color.BLACK;
        worldView = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        screenView = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        batch = new SpriteBatch();
        frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, VIRTUAL_WIDTH, VIRTUAL_HEIGHT, false, false);
        screenRegion = new TextureRegion();

//        playerPalette = new PaletteShader(new Palette4(
//                0, 0, 0, 255,
//                127, 127, 127, 255,
//                255, 255, 255, 255,
//                255, 255, 255, 0
//        ));

        tiledMapRenderer = new OrthogonalTiledMapRenderer(world.getPlanet().getMap());
        screenView.getCamera().position.set(VIRTUAL_WIDTH / 2, VIRTUAL_HEIGHT / 2, 0);
        screenView.update(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        batch.enableBlending();
        input = new GameInputProcessor(world, this);
        Gdx.input.setInputProcessor(input);

        //console = new GUIConsole(assets.commodore64);
    }

    @Override
    public void render(float delta) {
        input.tick(delta);

        frameBuffer.begin();
        Gdx.gl.glClearColor(
                world.getPlanet().backgroundColor.r,
                world.getPlanet().backgroundColor.g,
                world.getPlanet().backgroundColor.b,
                1f
        );
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        worldView.apply();
//        tiledMapRenderer.getBatch().setShader(assets.shader);

        for (MapLayer layer : world.getPlanet().getMap().getLayers()) {
            tiledMapRenderer.getBatch().begin();
//            palettes[layer].bind(tiledMapRenderer.getBatch().getShader());

            for (int dx = -1; dx <= 1; dx++)
                for (int dy = -1; dy <= 1; dy++) {
                    worldView.getCamera().position.set(
                            // player position + center of tile + over the edge for wrapping
                            world.getPlayerX() * Assets.TILE_WIDTH + Assets.TILE_WIDTH / 2 + world.getPlanet().SIZE_X * Assets.TILE_WIDTH * dx,
                            world.getPlayerY() * Assets.TILE_HEIGHT + Assets.TILE_HEIGHT / 2 + world.getPlanet().SIZE_Y * Assets.TILE_HEIGHT * dy,
                            0
                    );
                    worldView.update(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
                    tiledMapRenderer.setView((OrthographicCamera) worldView.getCamera());
                    tiledMapRenderer.renderTileLayer((TiledMapTileLayer) layer);
                }
            tiledMapRenderer.getBatch().end();
        }
//        tiledMapRenderer.getBatch().setShader(null);

        worldView.getCamera().position.set(
                // player position + center of tile
                world.getPlayerX() * Assets.TILE_WIDTH + Assets.TILE_WIDTH / 2,
                world.getPlayerY() * Assets.TILE_HEIGHT + Assets.TILE_HEIGHT / 2,
                0
        );
        worldView.update(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        batch.setProjectionMatrix(worldView.getCamera().combined);
//        batch.setShader(assets.shader);
        batch.begin();
//        playerPalette.bind(batch.getShader());

        batch.draw(
                world.getAssets().atlas.findRegion("characters/AstronautS0"),
                world.getPlayerX() * Assets.TILE_WIDTH,
                world.getPlayerY() * Assets.TILE_HEIGHT
        );

//        batch.setColor(Color.RED);
//        for (int x = 0; x < world.SIZE_X; x++) {
//            drawSquareOverTile(batch, x, x);
//            drawSquareOverTile(batch, world.SIZE_X - x, x);
//        }
//        batch.setColor(Color.WHITE);

        batch.end();
//        batch.setShader(null);
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

//        console.draw();
    }

    public void drawRect(SpriteBatch batch, int x, int y, int width, int height) {
        batch.draw(world.getAssets().one, x + width - 1, y + 1, 1, height - 1);
        batch.draw(world.getAssets().one, x + 1, y, width - 1, 1);
        batch.draw(world.getAssets().one, x, y, 1, height - 1);
        batch.draw(world.getAssets().one, x, y + height - 1, width - 1, 1);
    }

    public void drawSquareOverTile(SpriteBatch batch, int x, int y) {
        batch.draw(world.getAssets().one, x * Assets.TILE_WIDTH, y * Assets.TILE_HEIGHT, Assets.TILE_WIDTH, Assets.TILE_HEIGHT);
    }

    @Override
    public void resize(int width, int height) {
        screenView.update(width, height);
//        console.update(width, height);
    }

    @Override
    public void dispose() {
        batch.dispose();
        frameBuffer.dispose();
        world.dispose();
//        console.dispose();
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
