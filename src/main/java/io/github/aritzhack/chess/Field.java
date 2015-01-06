package io.github.aritzhack.chess;

import com.google.common.collect.Sets;
import io.github.aritzhack.aritzh.awt.render.IRender;
import io.github.aritzhack.aritzh.awt.render.Sprite;

import java.awt.*;
import java.util.Set;

import static io.github.aritzhack.chess.Piece.PieceType.*;

/**
 * Created by Aritz on 06/01/2015.
 */
public class Field {

    public static final int SPRITE_SIZE = 64;
    private final int width = 8, height = 8;
    private final Sprite light_gray = new Sprite(64, 64, 0xFFAAAAAA);
    private final Sprite dark_gray = new Sprite(64, 64, 0xFF444444);
    private final Sprite light_yellow = new Sprite(64, 64, 0xFF999900);
    private final Sprite dark_yellow = new Sprite(64, 64, 0xFFDDDD00);

    private final Sprite light_red = new Sprite(64, 64, 0xFF990000);
    private final Sprite dark_red = new Sprite(64, 64, 0xFFDD0000);
    private final Piece[][] pieces = new Piece[width][height];
    private Point sel = null;

    public Field() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                pieces[x][y] = new Piece(NONE, false);
            }
        }
    }

    public void initField() {
        for (int i = 0; i < width; i++) {
            pieces[i][1] = new Piece(PAWN, true);
            pieces[i][6] = new Piece(PAWN, false);
        }

        pieces[0][0] = new Piece(ROOK, true);
        pieces[0][7] = new Piece(ROOK, false);
        pieces[1][0] = new Piece(KNIGHT, true);
        pieces[1][7] = new Piece(KNIGHT, false);
        pieces[2][0] = new Piece(BISHOP, true);
        pieces[2][7] = new Piece(BISHOP, false);
        pieces[3][0] = new Piece(QUEEN, true);
        pieces[3][7] = new Piece(KING, false);
        pieces[4][0] = new Piece(KING, true);
        pieces[4][7] = new Piece(QUEEN, false);
        pieces[5][0] = new Piece(BISHOP, true);
        pieces[5][7] = new Piece(BISHOP, false);
        pieces[6][0] = new Piece(KNIGHT, true);
        pieces[6][7] = new Piece(KNIGHT, false);
        pieces[7][0] = new Piece(ROOK, true);
        pieces[7][7] = new Piece(ROOK, false);
    }

    public void render(IRender render) {

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                render.draw(x * SPRITE_SIZE, y * SPRITE_SIZE, (((x + y) % 2) == 0 ? light_gray : dark_gray));
            }
        }

        if (this.sel != null) {
            render.draw(sel.x * SPRITE_SIZE, sel.y * SPRITE_SIZE, (((sel.x + sel.y) % 2) == 0 ? dark_yellow : light_yellow));
            for (Point p : this.pieces[this.sel.x][this.sel.y].getMovements()) {
                if(this.pieces[p.x][p.y].getType() == NONE) render.draw(p.x * SPRITE_SIZE, p.y * SPRITE_SIZE, (((p.x + p.y) % 2) == 0 ? dark_yellow : light_yellow));
                else render.draw(p.x * SPRITE_SIZE, p.y * SPRITE_SIZE, (((p.x + p.y) % 2) == 0 ? dark_red : light_red));
            }
        }

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                pieces[x][y].render(render, x, y);
            }
        }
    }

    public void update() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Set<Point> points = Sets.newHashSet();
                A:
                for (Point[] ps : this.pieces[x][y].getPossibleMovements(x, y)) {
                    for (Point p : ps) {
                        if (p.x < width && p.y < height && p.x >= 0 && p.y >= 0) {
                            if (this.pieces[p.x][p.y].getType() == NONE || this.pieces[p.x][p.y].isBlack() != this.pieces[x][y].isBlack())
                                points.add(p);
                            if (this.pieces[p.x][p.y].getType() != NONE) continue A;
                        } else continue A;
                    }
                }
                this.pieces[x][y].setMovements(points);
            }
        }
    }

    public void clicked(Point c) {
        Point clicked = new Point(c.x / 64, c.y / 64);
        if (this.sel == null) {
            this.sel = clicked;
            if (this.sel.x >= width || this.sel.y >= height) this.sel = null;
        } else {
            if (clicked.equals(this.sel)) this.sel = null;
            else {
                boolean found = false;
                for (Point p : this.pieces[this.sel.x][this.sel.y].getMovements()) {
                    if (p.equals(clicked)) {
                        found = true;
                        this.move(this.sel, clicked);
                        this.sel = null;
                    }
                }
                if(!found) this.sel = clicked;
            }
        }
    }

    private void move(Point from, Point to) {
        Piece t = this.pieces[from.x][from.y];
        this.pieces[from.x][from.y] = new Piece(NONE, false);
        this.pieces[to.x][to.y] = t;
    }

}
