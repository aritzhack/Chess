package io.github.aritzhack.chess;

import com.google.common.collect.Sets;
import io.github.aritzhack.aritzh.awt.render.IRender;

import java.awt.Point;
import java.util.Set;

/**
 * @author Aritz Lopez
 */
public class Piece {

    private final PieceType type;
    private final boolean isBlack;
    private Set<Point> possibleMovements = Sets.newHashSet();

    public Piece(PieceType type, boolean isBlack) {
        this.type = type;
        this.isBlack = isBlack;
    }

    public PieceType getType() {
        return type;
    }

    public boolean isBlack() {
        return isBlack;
    }

    public void render(IRender render, int x, int y) {
        if (this.type != PieceType.NONE) render.draw(x, y, (this.isBlack ? "n" : "") + this.type.spriteName);
    }

    public Set<Point[]> getPossibleMovements(int x, int y) {
        return this.type.getPossibleMovements(x, y, this.isBlack);
    }

    public void setMovements(Set<Point> possibleMovements) {
        this.possibleMovements = possibleMovements;
    }

    public Set<Point> getMovements() {
        return this.possibleMovements;
    }

    public static enum PieceType {
        KING("rey"), QUEEN("reina"), ROOK("torre"), KNIGHT("caballo"), BISHOP("alfil"), PAWN("peon"), NONE(null);
        private String spriteName;

        PieceType(String spriteName) {

            this.spriteName = spriteName;
        }

        public Set<Point[]> getPossibleMovements(int x, int y, boolean isBlack) {
            switch (this) {
                case KING:
                    return Sets.newHashSet(new Point[]{p(x, y - 1)}, new Point[]{p(x - 1, y)}, new Point[]{p(x + 1, y)}, new Point[]{p(x, y + 1)});
                case QUEEN:
                    return Sets.union(ROOK.getPossibleMovements(x, y, isBlack), BISHOP.getPossibleMovements(x, y, isBlack));
                case ROOK:
                    return Sets.newHashSet(
                        new Point[]{p(x - 1, y), p(x - 2, y), p(x - 3, y), p(x - 4, y), p(x - 5, y), p(x - 6, y), p(x - 7, y)},
                        new Point[]{p(x + 1, y), p(x + 2, y), p(x + 3, y), p(x + 4, y), p(x + 5, y), p(x + 6, y), p(x + 7, y)},
                        new Point[]{p(x, y - 1), p(x, y - 2), p(x, y - 3), p(x, y - 4), p(x, y - 5), p(x, y - 6), p(x, y - 7)},
                        new Point[]{p(x, y + 1), p(x, y + 2), p(x, y + 3), p(x, y + 4), p(x, y + 5), p(x, y + 6), p(x, y + 7)});
                case KNIGHT:
                    return Sets.newHashSet(
                        new Point[]{p(x - 2, y - 1)}, new Point[]{p(x - 1, y - 2)}, new Point[]{p(x + 1, y - 2)}, new Point[]{p(x + 2, y - 1)},
                        new Point[]{p(x - 2, y + 1)}, new Point[]{p(x - 1, y + 2)}, new Point[]{p(x + 1, y + 2)}, new Point[]{p(x + 2, y + 1)}
                    );
                case BISHOP:
                    return Sets.newHashSet(
                        new Point[]{p(x - 1, y - 1), p(x - 2, y - 2), p(x - 3, y - 3), p(x - 4, y - 4), p(x - 5, y - 5), p(x - 6, y - 6), p(x - 7, y - 7)},
                        new Point[]{p(x + 1, y + 1), p(x + 2, y + 2), p(x + 3, y + 3), p(x + 4, y + 4), p(x + 5, y + 5), p(x + 6, y + 6), p(x + 7, y + 7)},
                        new Point[]{p(x + 1, y - 1), p(x + 2, y - 2), p(x + 3, y - 3), p(x + 4, y - 4), p(x + 5, y - 5), p(x + 6, y - 6), p(x + 7, y - 7)},
                        new Point[]{p(x - 1, y + 1), p(x - 2, y + 2), p(x - 3, y + 3), p(x - 4, y + 4), p(x - 5, y + 5), p(x - 6, y + 6), p(x - 7, y + 7)}
                    );
                case PAWN:
                    if (isBlack && y == 1) {
                        return Sets.newHashSet(new Point[]{p(x, y + 1), p(x, y + 2)}, new Point[]{});
                    } else if (!isBlack && y == 6) {
                        return Sets.newHashSet(new Point[]{p(x, y - 1), p(x, y - 2)}, new Point[]{});
                    } else {
                        return Sets.newHashSet(new Point[]{p(x, y + (isBlack ? 1 : -1))}, new Point[]{});
                    }
                case NONE:
                    return Sets.newHashSet();
            }
            return Sets.newHashSet();
        }

        private Point p(int x, int y) {
            return new Point(x, y);
        }
    }
}
