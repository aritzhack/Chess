package io.github.aritzhack.chess;

import io.github.aritzhack.aritzh.awt.gameEngine.input.InputHandler;
import io.github.aritzhack.aritzh.awt.gameEngine.test.AbstractGame;
import io.github.aritzhack.aritzh.awt.gameEngine.test.TestEngine;
import io.github.aritzhack.aritzh.awt.render.IRender;
import io.github.aritzhack.aritzh.awt.render.Sprite;
import io.github.aritzhack.aritzh.awt.render.SpriteSheetLoader;
import io.github.aritzhack.aritzh.logging.OSLogger;
import io.github.aritzhack.aritzh.logging.core.ILogger;

import java.util.Map;

/**
 * @author Aritz Lopez
 */
public class Game extends AbstractGame {

    private static final Map<String, Sprite> sprites = SpriteSheetLoader.load("sheet.sht");
    public static ILogger LOG = new OSLogger.Builder(System.out, "main").build();
    private TestEngine testEngine;
    private final Field field;

    public Game() {
        this.field = new Field();
        this.field.initField();
        this.testEngine = new TestEngine(this, 512, 640, LOG, sprites);
    }

    @Override
    public String getGameName() {
        return "Chess";
    }

    @Override
    public void onRender() {
        super.onRender();
        IRender render = testEngine.getRender();
        this.field.render(render);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        this.field.update();

        InputHandler ih = this.testEngine.getEngine().getInputHandler();
        while (!ih.getMouseEvents().isEmpty()) {
            InputHandler.MouseInputEvent e = ih.getMouseEvents().poll();
            if(e.getAction() == InputHandler.MouseAction.RELEASED && e.getButton() == InputHandler.MouseButton.LEFT) {
                this.field.clicked(e.getPosition());
            }
        }
        if(this.field.shouldTitleChange())
            this.testEngine.getEngine().setTitle(this.getGameName() + " - " + (this.field.isBlacksTurn()?"Black":"White") + "s' turn");
    }
}
