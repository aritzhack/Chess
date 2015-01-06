package io.github.aritzhack.chess;

import io.github.aritzhack.aritzh.awt.gameEngine.test.AbstractGame;
import io.github.aritzhack.aritzh.awt.gameEngine.test.TestEngine;

/**
 * @author Aritz Lopez
 */
public class Game extends AbstractGame {

    private TestEngine testEngine;

    public Game() {
        testEngine = new TestEngine(this, 1280, 720);
    }

    @Override
    public String getGameName() {
        return "Chess";
    }

    @Override
    public void onRender() {
        super.onRender();
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
    }
}
