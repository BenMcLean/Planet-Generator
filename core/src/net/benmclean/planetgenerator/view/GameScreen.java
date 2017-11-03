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
import com.strongjoshua.console.GUIConsole;
import net.benmclean.planetgenerator.controller.Executor;
import net.benmclean.planetgenerator.controller.GameInputProcessor;
import net.benmclean.planetgenerator.model.Assets;
import net.benmclean.planetgenerator.model.Universe;

public class GameScreen implements Screen, Disposable {
    public GameScreen() {
        this(42);
    }

    public GameScreen(long SEED) {
        this.SEED = SEED;
        universe = new Universe(SEED);
    }

    public long SEED;
    public static final int VIRTUAL_WIDTH = 356;
    public static final int VIRTUAL_HEIGHT = 200;
    private Color screenBackgroundColor;
    private Viewport worldView;
    private Viewport screenView;
    private SpriteBatch batch;
    private OrthogonalTiledMapRenderer tiledMapRenderer;
    private FrameBuffer frameBuffer;
    private Texture screenTexture;
    private TextureRegion screenRegion;
    public Universe universe;
    public GameInputProcessor input;
    protected GUIConsole console;
    public net.benmclean.planetgenerator.controller.Executor executor;

    @Override
    public void show() {
        screenBackgroundColor = Color.BLACK;
        worldView = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        screenView = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        batch = new SpriteBatch();
        frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, VIRTUAL_WIDTH, VIRTUAL_HEIGHT, false, false);
        screenRegion = new TextureRegion();
        tiledMapRenderer = new OrthogonalTiledMapRenderer(universe.getPlanet().getMap());
        screenView.getCamera().position.set(VIRTUAL_WIDTH / 2, VIRTUAL_HEIGHT / 2, 0);
        screenView.update(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        batch.enableBlending();
        input = new GameInputProcessor(universe, this);
        Gdx.input.setInputProcessor(input);

        console = new GUIConsole(universe.getAssets().skin);
        console.setLoggingToSystem(true);
        console.setSizePercent(100f,100f);

        executor = new Executor().setUniverse(universe);
        console.setCommandExecutor(executor);
    }

    @Override
    public void render(float delta) {
        input.tick(delta);

        frameBuffer.begin();
        Gdx.gl.glClearColor(
                universe.getPlanet().backgroundColor.r,
                universe.getPlanet().backgroundColor.g,
                universe.getPlanet().backgroundColor.b,
                1f
        );
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        worldView.apply();
        for (MapLayer layer : universe.getPlanet().getMap().getLayers()) {
            tiledMapRenderer.getBatch().begin();
            for (int dx = -1; dx <= 1; dx++)
                for (int dy = -1; dy <= 1; dy++) {
                    worldView.getCamera().position.set(
                            // player position + center of tile + over the edge for wrapping
                            universe.getPlayerX() * Assets.TILE_WIDTH + Assets.TILE_WIDTH / 2 + universe.getPlanet().SIZE_X * Assets.TILE_WIDTH * dx,
                            universe.getPlayerY() * Assets.TILE_HEIGHT + Assets.TILE_HEIGHT / 2 + universe.getPlanet().SIZE_Y * Assets.TILE_HEIGHT * dy,
                            0
                    );
                    worldView.update(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
                    tiledMapRenderer.setView((OrthographicCamera) worldView.getCamera());
                    tiledMapRenderer.renderTileLayer((TiledMapTileLayer) layer);
                }
            tiledMapRenderer.getBatch().end();
        }

        worldView.getCamera().position.set(
                // player position + center of tile
                universe.getPlayerX() * Assets.TILE_WIDTH + Assets.TILE_WIDTH / 2,
                universe.getPlayerY() * Assets.TILE_HEIGHT + Assets.TILE_HEIGHT / 2,
                0
        );
        worldView.update(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        batch.setProjectionMatrix(worldView.getCamera().combined);
        batch.begin();
        batch.draw(
//                universe.getPlayer().findRegion("S0"),
                universe.getPlayer().ship,
                universe.getPlayerX() * Assets.TILE_WIDTH + 2,
                universe.getPlayerY() * Assets.TILE_HEIGHT + 2
        );
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

        console.draw();
    }

    public void drawRect(SpriteBatch batch, int x, int y, int width, int height) {
        batch.draw(universe.getAssets().one, x + width - 1, y + 1, 1, height - 1);
        batch.draw(universe.getAssets().one, x + 1, y, width - 1, 1);
        batch.draw(universe.getAssets().one, x, y, 1, height - 1);
        batch.draw(universe.getAssets().one, x, y + height - 1, width - 1, 1);
    }

    public void drawSquareOverTile(SpriteBatch batch, int x, int y) {
        batch.draw(universe.getAssets().one, x * Assets.TILE_WIDTH, y * Assets.TILE_HEIGHT, Assets.TILE_WIDTH, Assets.TILE_HEIGHT);
    }

    @Override
    public void resize(int width, int height) {
        screenView.update(width, height);
        console.refresh();
    }

    @Override
    public void dispose() {
        batch.dispose();
        frameBuffer.dispose();
        universe.dispose();
        console.dispose();
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
