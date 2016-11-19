package net.benmclean.planetgenerator.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GameScreen implements Screen, Disposable {
    public GameScreen() {
        this(42);
    }

    public GameScreen(long SEED) {
        this.SEED = SEED;
        //world = new GameWorld(SEED);
    }

    public long SEED;
    public static final int VIRTUAL_WIDTH = 64;
    public static final int VIRTUAL_HEIGHT = 64;
    public static final int TILE_WIDTH = 8;
    public static final int TILE_HEIGHT = 8;
    public static final double visibilityThreshold = 0.2d;
    //public Assets assets = new Assets();
    private Color worldBackgroundColor = Color.BLACK;
    private Color screenBackgroundColor = Color.BLACK;
    private Viewport worldView = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
    private Viewport screenView = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
    private SpriteBatch batch = new SpriteBatch();
    //private TiledMap map = new TiledMap();
    //private TiledMapRenderer tiledMapRenderer;
    private FrameBuffer frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, VIRTUAL_HEIGHT, VIRTUAL_WIDTH, true, true);
    private Texture screenTexture;
    private TextureRegion screenRegion = new TextureRegion();
    //private OrthogonalTiledMapIterator visibleIterator;
//    public GameWorld world;
//    public GameInputProcessor input;

    @Override
    public void show() {
        screenView.getCamera().position.set(32, 32, 0);
        screenView.update(VIRTUAL_HEIGHT, VIRTUAL_WIDTH);
//        visibleIterator = new OrthogonalTiledMapIterator((OrthographicCamera) worldView.getCamera(), layer);
        batch.enableBlending();
//        input = new GameInputProcessor(world);
//        Gdx.input.setInputProcessor(input);
    }

    @Override
    public void render(float delta) {
        frameBuffer.begin();
        Gdx.gl.glClearColor(worldBackgroundColor.r, worldBackgroundColor.g, worldBackgroundColor.b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        worldView.apply();
//        worldView.getCamera().position.set(world.getPlayerX() * TILE_HEIGHT + 4, world.getPlayerY() * TILE_WIDTH + 4, 0);
        worldView.update(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
//        tiledMapRenderer.setView((OrthographicCamera) worldView.getCamera());
//        tiledMapRenderer.render();
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

//    public void drawRect(SpriteBatch batch, int x, int y, int width, int height) {
//        batch.draw(assets.one, x + width - 1, y + 1, 1, height - 1);
//        batch.draw(assets.one, x + 1, y, width - 1, 1);
//        batch.draw(assets.one, x, y, 1, height - 1);
//        batch.draw(assets.one, x, y + height - 1, width - 1, 1);
//    }
//
//    public void drawSquareOverTile(SpriteBatch batch, int x, int y) {
//        batch.draw(assets.one, x * TILE_WIDTH, y * TILE_HEIGHT, TILE_WIDTH, TILE_HEIGHT);
//    }

    @Override
    public void resize(int width, int height) {
        screenView.update(width, height);
    }

    @Override
    public void dispose() {
        batch.dispose();
        frameBuffer.dispose();
//        assets.dispose();
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
            Gdx.graphics.setWindowedMode(VIRTUAL_WIDTH * 12, VIRTUAL_HEIGHT * 12);
        else
            Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
    }
}
