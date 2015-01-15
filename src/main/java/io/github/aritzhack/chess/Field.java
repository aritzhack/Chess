package io.github.aritzhack.chess;

import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import io.github.aritzhack.aritzh.awt.render.IRender;
import io.github.aritzhack.aritzh.awt.render.Sprite;

import java.awt.Point;
import java.util.Queue;
import java.util.Set;

import static io.github.aritzhack.chess.Piece.PieceType.*;

/**
 * @author Aritz Lopez
 */
public class Field {

    private boolean blacksTurn = false;

    public static final int SPRITE_SIZE = 64;
    private static final int GRAVE_SIZE = 2*SPRITE_SIZE;
    private final int width = 8, height = 8;
    private final Sprite light_gray = new Sprite(SPRITE_SIZE, SPRITE_SIZE, 0xFFAAAAAA);
    private final Sprite dark_gray = new Sprite(SPRITE_SIZE, SPRITE_SIZE, 0xFF444444);
    private final Sprite light_yellow = new Sprite(SPRITE_SIZE, SPRITE_SIZE, 0xFF999900);
    private final Sprite dark_yellow = new Sprite(SPRITE_SIZE, SPRITE_SIZE, 0xFFDDDD00);

    private final Sprite top_grave = new Sprite(SPRITE_SIZE * width, SPRITE_SIZE, 0xFF888888);
    private final Sprite bottom_grave = new Sprite(SPRITE_SIZE * width, SPRITE_SIZE, 0xFF888888);

    private final Sprite light_red = new Sprite(SPRITE_SIZE, SPRITE_SIZE, 0xFF990000);
    private final Sprite dark_red = new Sprite(SPRITE_SIZE, SPRITE_SIZE, 0xFFDD0000);
    private final Piece[][] pieces = new Piece[width][height];
    private Point sel = null;

    private final Queue<Piece> grave = Queues.newArrayDeque();

    private boolean shouldTitleChange = true;

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
        pieces[3][0] = new Piece(KING, true);
        pieces[3][7] = new Piece(QUEEN, false);
        pieces[4][0] = new Piece(QUEEN, true);
        pieces[4][7] = new Piece(KING, false);
        pieces[5][0] = new Piece(BISHOP, true);
        pieces[5][7] = new Piece(BISHOP, false);
        pieces[6][0] = new Piece(KNIGHT, true);
        pieces[6][7] = new Piece(KNIGHT, false);
        pieces[7][0] = new Piece(ROOK, true);
        pieces[7][7] = new Piece(ROOK, false);
    }

    public void render(IRender render) {

        render.draw(0, 0, top_grave);
        render.draw(0, GRAVE_SIZE + SPRITE_SIZE * height, bottom_grave);

        int b = 0, w = 0;
        for (Piece p : grave) {
            if (p.isBlack()) {
                p.render(render, b * SPRITE_SIZE, height * SPRITE_SIZE + GRAVE_SIZE);
                b++;
            } else {
                p.render(render, w * SPRITE_SIZE, 0);
                w++;
            }
        }

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                render.draw(x * SPRITE_SIZE, GRAVE_SIZE + y * SPRITE_SIZE, (((x + y) % 2) == 0 ? light_gray : dark_gray));
            }
        }

        if (this.sel != null) {
            render.draw(sel.x * SPRITE_SIZE, GRAVE_SIZE + sel.y * SPRITE_SIZE, (((sel.x + sel.y) % 2) == 0 ? dark_yellow : light_yellow));
            for (Point p : this.pieces[this.sel.x][this.sel.y].getMovements()) {
                if (this.pieces[p.x][p.y].getType() == NONE)
                    render.draw(p.x * SPRITE_SIZE, GRAVE_SIZE + p.y * SPRITE_SIZE, (((p.x + p.y) % 2) == 0 ? dark_yellow : light_yellow));
                else
                    render.draw(p.x * SPRITE_SIZE, GRAVE_SIZE + p.y * SPRITE_SIZE, (((p.x + p.y) % 2) == 0 ? dark_red : light_red));
            }
        }

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                pieces[x][y].render(render, x * SPRITE_SIZE, y * SPRITE_SIZE + GRAVE_SIZE);
            }
        }
    }

    public void update() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Set<Point> points = Sets.newHashSet();
                Piece piece = this.pieces[x][y];
                A:
                for (Point[] ps : piece.getPossibleMovements(x, y)) {
                    for (Point p : ps) {
                        if (!inBounds(p.x, p.y)) continue A;

                        if (piece.getType() == PAWN && Math.abs(p.y - y) == 1 && isPiece(p.x, p.y)) {
                            continue A;
                        }
                        if (!isPiece(p.x, p.y) || isBlack(p.x, p.y) != isBlack(x, y)) points.add(p);
                        if (isPiece(p.x, p.y)) continue A;
                    }
                }
                if (piece.getType() == PAWN) {
                    int d = piece.isBlack() ? +1 : -1;
                    if (inBounds(x - 1, y + d) && isPiece(x - 1, y + d) && isBlack(x - 1, y + d) != piece.isBlack()) {
                        points.add(new Point(x - 1, y + d));
                    }
                    if (inBounds(x + 1, y + d) && isPiece(x + 1, y + d) && isBlack(x + 1, y + d) != piece.isBlack()) {
                        points.add(new Point(x + 1, y + d));
                    }
                }
                piece.setMovements(points);
            }
        }

    }

    private boolean isPiece(int x, int y) {
        return inBounds(x, y) && this.pieces[x][y].getType() != NONE;
    }

    private boolean isBlack(int x, int y) {
        return isPiece(x, y) && this.pieces[x][y].isBlack();
    }

    private boolean inBounds(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    public void clicked(Point c) {
        Point clicked = new Point(c.x / 64, c.y / 64 - 1);
        if (!inBounds(clicked.x, clicked.y)) return;
        if (this.sel == null) {
            if (blacksTurn == isBlack(clicked.x, clicked.y)) this.sel = clicked;
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
                if (!found && blacksTurn == isBlack(clicked.x, clicked.y)) this.sel = clicked;
            }
        }
    }

    private void move(Point from, Point to) {
        Piece t = this.pieces[from.x][from.y];

        if (isPiece(to.x, to.y)) grave.add(this.pieces[to.x][to.y]);

        this.pieces[from.x][from.y] = new Piece(NONE, false);
        this.pieces[to.x][to.y] = t;
        this.blacksTurn = !this.blacksTurn;
        shouldTitleChange = true;
    }

    public boolean isBlacksTurn() {
        return blacksTurn;
    }

    public boolean shouldTitleChange() {
        if(shouldTitleChange) {
            shouldTitleChange = false;
            return true;
        }
        return false;
    }
}
