package io.github.aritzhack.chess;

import com.google.common.collect.Sets;
import io.github.aritzhack.aritzh.awt.render.IRender;
import io.github.aritzhack.aritzh.awt.render.Sprite;

import java.awt.Point;
import java.util.Set;

import static io.github.aritzhack.chess.Piece.PieceType.*;

/**
 * @author Aritz Lopez
 */
public class Field {

    private boolean blacksTurn = false;

    public static final int SPRITE_SIZE = 64;
    private static final int GRAVE_SIZE = 2 * SPRITE_SIZE;
    private final Sprite light_gray = new Sprite(SPRITE_SIZE, SPRITE_SIZE, 0xFFAAAAAA);
    private final Sprite dark_gray = new Sprite(SPRITE_SIZE, SPRITE_SIZE, 0xFF444444);
    private final Sprite light_yellow = new Sprite(SPRITE_SIZE, SPRITE_SIZE, 0xFF999900);
    private final Sprite dark_yellow = new Sprite(SPRITE_SIZE, SPRITE_SIZE, 0xFFDDDD00);

    private final Sprite top_grave = new Sprite(SPRITE_SIZE * FieldLogic.width, SPRITE_SIZE, 0xFF888888);
    private final Sprite bottom_grave = new Sprite(SPRITE_SIZE * FieldLogic.width, SPRITE_SIZE, 0xFF888888);

    private final Sprite light_red = new Sprite(SPRITE_SIZE, SPRITE_SIZE, 0xFF990000);
    private final Sprite dark_red = new Sprite(SPRITE_SIZE, SPRITE_SIZE, 0xFFDD0000);
    private Point sel = null;

    private FieldLogic fl = new FieldLogic();

    public void initField() {


        for (int i = 0; i < FieldLogic.width; i++) {
            this.fl.setPiece(i, 1, new Piece(PAWN, true));
            this.fl.setPiece(i, 6, new Piece(PAWN, false));
            this.fl.setPiece(i, 2, new Piece(NONE, false));
            this.fl.setPiece(i, 3, new Piece(NONE, false));
            this.fl.setPiece(i, 4, new Piece(NONE, false));
            this.fl.setPiece(i, 5, new Piece(NONE, false));
        }

        this.fl.setPiece(0, 0, new Piece(ROOK, true));
        this.fl.setPiece(0, 7, new Piece(ROOK, false));
        this.fl.setPiece(1, 0, new Piece(KNIGHT, true));
        this.fl.setPiece(1, 7, new Piece(KNIGHT, false));
        this.fl.setPiece(2, 0, new Piece(BISHOP, true));
        this.fl.setPiece(2, 7, new Piece(BISHOP, false));
        this.fl.setPiece(3, 0, new Piece(KING, true));
        this.fl.setPiece(3, 7, new Piece(QUEEN, false));
        this.fl.setPiece(4, 0, new Piece(QUEEN, true));
        this.fl.setPiece(4, 7, new Piece(KING, false));
        this.fl.setPiece(5, 0, new Piece(BISHOP, true));
        this.fl.setPiece(5, 7, new Piece(BISHOP, false));
        this.fl.setPiece(6, 0, new Piece(KNIGHT, true));
        this.fl.setPiece(6, 7, new Piece(KNIGHT, false));
        this.fl.setPiece(7, 0, new Piece(ROOK, true));
        this.fl.setPiece(7, 7, new Piece(ROOK, false));
    }

    public void render(IRender render) {

        render.draw(0, 0, top_grave);
        render.draw(0, GRAVE_SIZE + SPRITE_SIZE * FieldLogic.height, bottom_grave);

        int b = 0, w = 0;
        for (Piece p : fl.getGrave()) {
            if (p.isBlack()) {
                p.render(render, b * SPRITE_SIZE, FieldLogic.height * SPRITE_SIZE + GRAVE_SIZE);
                b++;
            } else {
                p.render(render, w * SPRITE_SIZE, 0);
                w++;
            }
        }

        for (int y = 0; y < FieldLogic.height; y++) {
            for (int x = 0; x < FieldLogic.width; x++) {
                render.draw(x * SPRITE_SIZE, GRAVE_SIZE + y * SPRITE_SIZE, (((x + y) % 2) == 0 ? light_gray : dark_gray));
            }
        }

        if (this.sel != null) {
            render.draw(sel.x * SPRITE_SIZE, GRAVE_SIZE + sel.y * SPRITE_SIZE, (((sel.x + sel.y) % 2) == 0 ? dark_yellow : light_yellow));
            for (Point p : fl.getPiece(this.sel.x, this.sel.y).getMovements()) {
                if (fl.getPiece(p.x, p.y).getType() == NONE)
                    render.draw(p.x * SPRITE_SIZE, GRAVE_SIZE + p.y * SPRITE_SIZE, (((p.x + p.y) % 2) == 0 ? dark_yellow : light_yellow));
                else
                    render.draw(p.x * SPRITE_SIZE, GRAVE_SIZE + p.y * SPRITE_SIZE, (((p.x + p.y) % 2) == 0 ? dark_red : light_red));
            }
        }

        for (int y = 0; y < FieldLogic.height; y++) {
            for (int x = 0; x < FieldLogic.width; x++) {
                fl.getPiece(x, y).render(render, x * SPRITE_SIZE, y * SPRITE_SIZE + GRAVE_SIZE);
            }
        }
    }

    public void update() {
        for (int y = 0; y < FieldLogic.height; y++) {
            for (int x = 0; x < FieldLogic.width; x++) {
                Set<Point> points = Sets.newHashSet();
                Piece piece = fl.getPiece(x, y);
                A:
                for (Point[] ps : piece.getPossibleMovements(x, y)) {
                    for (Point p : ps) {
                        if (!fl.inBounds(p.x, p.y)) continue A;

                        if (piece.getType() == PAWN && Math.abs(p.y - y) == 1 && fl.isPiece(p.x, p.y)) {
                            continue A;
                        }
                        if (!fl.isPiece(p.x, p.y) || fl.isBlack(p.x, p.y) != fl.isBlack(x, y)) points.add(p);
                        if (fl.isPiece(p.x, p.y)) continue A;
                    }
                }
                if (piece.getType() == PAWN) {
                    int d = piece.isBlack() ? +1 : -1;
                    if (fl.inBounds(x - 1, y + d) && fl.isPiece(x - 1, y + d) && fl.isBlack(x - 1, y + d) != piece.isBlack()) {
                        points.add(new Point(x - 1, y + d));
                    }
                    if (fl.inBounds(x + 1, y + d) && fl.isPiece(x + 1, y + d) && fl.isBlack(x + 1, y + d) != piece.isBlack()) {
                        points.add(new Point(x + 1, y + d));
                    }
                }
                piece.setMovements(points);
            }
        }

    }

    public void clicked(Point c) {
        Point clicked = new Point(c.x / 64, c.y / 64 - 2);
        if (!fl.inBounds(clicked.x, clicked.y)) return;
        if (this.sel == null) {
            if (blacksTurn == fl.isBlack(clicked.x, clicked.y)) this.sel = clicked;
        } else {
            if (clicked.equals(this.sel)) this.sel = null;
            else {
                boolean found = false;
                for (Point p : fl.getPiece(this.sel.x, this.sel.y).getMovements()) {
                    if (p.equals(clicked)) {
                        found = true;
                        fl.move(this.sel, clicked);
                        this.sel = null;
                    }
                }
                if (!found && blacksTurn == fl.isBlack(clicked.x, clicked.y)) this.sel = clicked;
            }
        }
    }

    public boolean isBlacksTurn() {
        return blacksTurn;
    }

    public boolean shouldTitleChange() {
/*        if (shouldTitleChange) {
            shouldTitleChange = false;
            return true;
        }
        return false;*/
        return true;
    }
}
