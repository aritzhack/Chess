package io.github.aritzhack.chess;

import com.google.common.collect.Queues;
import com.google.common.collect.Sets;

import java.awt.Point;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;

import static io.github.aritzhack.chess.Piece.PieceType.*;

/**
 * @author Aritz Lopez
 */
public class FieldLogic {

    public static final int width = 8, height = 8;
    private final Piece[][] pieces = new Piece[width][height];
    private boolean blacksTurn = false;
    private boolean whiteKingChecked, blackKingChecked;
    private final Queue<Piece> grave = Queues.newArrayDeque();
    private boolean shouldTitleChange = true;

    public void setPiece(int x, int y, Piece.PieceType piece, boolean isBlack) {
        this.setPiece(x, y, new Piece(piece, isBlack));
    }

    public void setPiece(int x, int y, Piece piece) {
        if (inBounds(x, y)) this.pieces[x][y] = piece;
    }

    public void setBlacksTurn(boolean blacksTurn) {
        this.blacksTurn = blacksTurn;
    }

    public void calculateMovements() {
        calculateMovements(false);
    }

    private void calculateMovements(boolean checked) {
        boolean noneChecked = true;
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
                for (Point p : points) {
                    if (this.pieces[p.x][p.y].is(KING, !this.blacksTurn)) {
                        noneChecked = false;
                    }
                }
                piece.setMovements(points);
            }
        }

        whiteKingChecked = !noneChecked && !blacksTurn;
        blackKingChecked = !noneChecked && blacksTurn;

        if (this.blacksTurn ? blackKingChecked : whiteKingChecked) {
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    Point from = new Point(x, y);
                    Iterator<Point> iter = this.pieces[x][y].getMovements().iterator();
                    while (iter.hasNext()) {
                        Point to = iter.next();
                        if (this.isMovementChecked(from, to)) {
                            iter.remove();
                        }
                    }
                }
            }
        }
    }

    public void move(Point from, Point to) {
        Piece t = this.pieces[from.x][from.y];

        if (isPiece(to.x, to.y)) grave.add(this.pieces[to.x][to.y]);

        this.pieces[from.x][from.y] = new Piece(NONE, false);
        this.pieces[to.x][to.y] = t;
        this.blacksTurn = !this.blacksTurn;
        shouldTitleChange = true;
        System.out.println("Moved!");
    }

    public boolean isBlackKingChecked() {
        return blackKingChecked;
    }

    public boolean isWhiteKingChecked() {
        return whiteKingChecked;
    }

    public boolean isPiece(int x, int y) {
        return inBounds(x, y) && this.pieces[x][y].getType() != NONE;
    }

    public boolean isBlack(int x, int y) {
        return isPiece(x, y) && this.pieces[x][y].isBlack();
    }

    public boolean inBounds(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    private FieldLogic getMovementField(Point from, Point to) {
        FieldLogic fl = new FieldLogic();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                fl.setPiece(x, y, this.pieces[x][y].getType(), this.pieces[x][y].isBlack());
            }
        }
        fl.move(from, to);

        fl.calculateMovements(false);
        return fl;
    }

    private boolean isMovementChecked(Point from, Point to) {
        FieldLogic fl = getMovementField(from, to);
        return this.blacksTurn ? fl.isBlackKingChecked() : fl.isWhiteKingChecked();
    }

    public Queue<Piece> getGrave() {
        return grave;
    }

    public Piece getPiece(int x, int y) {
        return inBounds(x,y) ? this.pieces[x][y] : null;
    }
}
