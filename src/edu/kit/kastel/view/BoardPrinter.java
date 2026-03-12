package edu.kit.kastel.view;

import edu.kit.kastel.model.Game;
import edu.kit.kastel.model.Verbosity;
import edu.kit.kastel.model.board.Position;
import edu.kit.kastel.model.unit.FarmerKing;
import edu.kit.kastel.model.unit.RegularUnit;
import edu.kit.kastel.model.unit.Unit;

/**
 * * this utility class helps with printing the board output display using the provided static methods.
 *
 * @author ucktt
 */
public final class BoardPrinter {

    private BoardPrinter() {
    }

    /**
     * Formats the current game board as a string.
     *
     * @param game the game providing the board state and display settings
     * @return the formatted board representation
     */
    public static String boardDisplay(Game game) {
        StringBuilder stringBuilder = new StringBuilder();
        SymbolSet symbols = game.getFarmlandBoard().getSymbolSet();
        Verbosity verbosity = game.getVerbosity();

        if (verbosity == Verbosity.ALL) {
            stringBuilder.append(topBorderLine(symbols, game));
            stringBuilder.append(System.lineSeparator());
            for (int row = 6; row >= 0; row--) {
                stringBuilder.append(rowContent(row, game, symbols));
                stringBuilder.append(System.lineSeparator());

                if (row > 0) {
                    stringBuilder.append(middleBorderLine(symbols, game, row, row - 1));
                } else {
                    stringBuilder.append(bottomBorderLine(symbols, game));
                }
                stringBuilder.append(System.lineSeparator());
            }
        } else {
            for (int row = 6; row >= 0; row--) {
                stringBuilder.append(rowContent(row, game, symbols)).append(System.lineSeparator());
            }
        }
        stringBuilder.append(columnContent());
        return stringBuilder.toString();
    }

    private static boolean isSelected(Game game, Position position) {
        Position savedPosition = game.getSavedPosition();
        return savedPosition != null && savedPosition.equals(position);
    }

    private static String topBorderLine(SymbolSet symbol, Game game) {
        StringBuilder stringBuilder = new StringBuilder();
        char topLeftCorner = symbol.getSymbol(0);
        char topLeftCornerSelected = symbol.getSymbol(11);
        char topRightCorner = symbol.getSymbol(1);
        char topRightCornerSelected = symbol.getSymbol(12);
        char topMiddle = symbol.getSymbol(4);
        char topMiddleSelectedLeft = symbol.getSymbol(15);
        char topMiddleSelectedRight = symbol.getSymbol(16);
        char horizontal = symbol.getSymbol(8);
        char horizontalSelected = symbol.getSymbol(23);

        Position firstField = new Position(0, 6);
        stringBuilder.append("  ");

        if (isSelected(game, firstField)) {
            stringBuilder.append(topLeftCornerSelected);
        } else {
            stringBuilder.append(topLeftCorner);
        }

        for (int column = 0; column < 6; column++) {
            Position leftField = new Position(column, 6);
            Position rightField = new Position(column + 1, 6);

            if (isSelected(game, leftField)) {
                stringBuilder.append(String.valueOf(horizontalSelected).repeat(3)).append(topMiddleSelectedLeft);
            } else if (isSelected(game, rightField)) {
                stringBuilder.append(String.valueOf(horizontal).repeat(3));
                stringBuilder.append(topMiddleSelectedRight);
            } else {
                stringBuilder.append(String.valueOf(horizontal).repeat(3)).append(topMiddle);
            }
        }
        Position lastField = new Position(6, 6);
        if (isSelected(game, lastField)) {
            stringBuilder.append(String.valueOf(horizontalSelected).repeat(3)).append(topRightCornerSelected);
        } else {
            stringBuilder.append(String.valueOf(horizontal).repeat(3)).append(topRightCorner);
        }

        return stringBuilder.toString();
    }

    private static String bottomBorderLine(SymbolSet symbol, Game game) {
        StringBuilder stringBuilder = new StringBuilder();
        char bottomLeftCorner = symbol.getSymbol(2);
        char bottomLeftCornerSelected = symbol.getSymbol(13);
        char bottomRightCorner = symbol.getSymbol(3);
        char bottomRightCornerSelected = symbol.getSymbol(14);
        char bottomMiddle = symbol.getSymbol(6);
        char bottomMiddleSelectedLeft = symbol.getSymbol(19);
        char bottomMiddleSelectedRight = symbol.getSymbol(20);
        char horizontal = symbol.getSymbol(8);
        char horizontalSelected = symbol.getSymbol(23);

        Position firstField = new Position(0, 0);
        stringBuilder.append("  ");

        if (isSelected(game, firstField)) {
            stringBuilder.append(bottomLeftCornerSelected);
        } else {
            stringBuilder.append(bottomLeftCorner);
        }

        for (int column = 0; column < 6; column++) {
            Position leftField = new Position(column, 0);
            Position rightField = new Position(column + 1, 0);

            if (isSelected(game, leftField)) {
                stringBuilder.append(String.valueOf(horizontalSelected).repeat(3)).append(bottomMiddleSelectedLeft);
            } else if (isSelected(game, rightField)) {
                stringBuilder.append(String.valueOf(horizontal).repeat(3));
                stringBuilder.append(bottomMiddleSelectedRight);
            } else {
                stringBuilder.append(String.valueOf(horizontal).repeat(3)).append(bottomMiddle);
            }
        }

        Position lastField = new Position(6, 0);
        if (isSelected(game, lastField)) {
            stringBuilder.append(String.valueOf(horizontalSelected).repeat(3)).append(bottomRightCornerSelected);
        } else {
            stringBuilder.append(String.valueOf(horizontal).repeat(3)).append(bottomRightCorner);
        }
        return stringBuilder.toString();
    }

    private static String middleBorderLine(SymbolSet symbol, Game game, int rowAbove, int rowBelow) {
        StringBuilder stringBuilder = new StringBuilder();

        char central = symbol.getSymbol(10); //k
        char centralSelectedTopLeft = symbol.getSymbol(28); //ü
        char centralSelectedTopRight = symbol.getSymbol(27); //ö
        char centralSelectedBottomLeft = symbol.getSymbol(26); //ä
        char centralSelectedBottomRight = symbol.getSymbol(25); //z
        char rightMiddle = symbol.getSymbol(5); //f
        char rightMiddleSelectedTop = symbol.getSymbol(18); //s
        char rightMiddleSelectedBottom = symbol.getSymbol(17); //r
        char leftMiddle = symbol.getSymbol(7); //h
        char leftMiddleSelectedTop = symbol.getSymbol(22); //w
        char leftMiddleSelectedBottom = symbol.getSymbol(21); //v
        char horizontal = symbol.getSymbol(8); //i
        char horizontalSelected = symbol.getSymbol(23); //x
        Position firstFieldAbove = new Position(0, rowAbove);
        Position firstFieldBottom = new Position(0, rowBelow);
        stringBuilder.append("  ");
        if (isSelected(game, firstFieldAbove)) {
            stringBuilder.append(leftMiddleSelectedBottom);
        } else if (isSelected(game, firstFieldBottom)) {
            stringBuilder.append(leftMiddleSelectedTop);
        } else {
            stringBuilder.append(leftMiddle);
        }
        for (int column = 0; column < 6; column++) {
            Position leftFieldTop = new Position(column, rowAbove);
            Position rightFieldTop = new Position(column + 1, rowAbove);
            Position leftFieldBottom = new Position(column, rowBelow);
            Position rightFieldBottom = new Position(column + 1, rowBelow);
            if (isSelected(game, leftFieldTop)) {
                stringBuilder.append(String.valueOf(horizontalSelected)
                        .repeat(3)).append(centralSelectedBottomRight);
            } else if (isSelected(game, rightFieldTop)) {
                stringBuilder.append(String.valueOf(horizontal).repeat(3))
                        .append(centralSelectedBottomLeft);
            } else if (isSelected(game, leftFieldBottom)) {
                stringBuilder.append(String.valueOf(horizontalSelected).repeat(3))
                        .append(centralSelectedTopRight);
            } else if (isSelected(game, rightFieldBottom)) {
                stringBuilder.append(String.valueOf(horizontal).repeat(3))
                        .append(centralSelectedTopLeft);
            } else {
                stringBuilder.append(String.valueOf(horizontal).repeat(3)).append(central);
            }
        }
        Position lastFieldTop = new Position(6, rowAbove);
        Position lastFieldBottom = new Position(6, rowBelow);
        if (isSelected(game, lastFieldTop)) {
            stringBuilder.append(String.valueOf(horizontalSelected).repeat(3))
                    .append(rightMiddleSelectedBottom);
        } else if (isSelected(game, lastFieldBottom)) {
            stringBuilder.append(String.valueOf(horizontalSelected).repeat(3))
                    .append(rightMiddleSelectedTop);
        } else {
            stringBuilder.append(String.valueOf(horizontal).repeat(3)).append(rightMiddle);
        }
        return stringBuilder.toString();
    }

    private static String rowContent(int rowIndex, Game game, SymbolSet symbol) {
        StringBuilder stringBuilder = new StringBuilder();
        int displayRowNumber = rowIndex + 1;
        char vertical = symbol.getSymbol(9);
        char verticalSelected = symbol.getSymbol(24);
        stringBuilder.append(displayRowNumber).append(" ");


        for (int column = 0; column < 7; column++) {
            Position position = new Position(column, rowIndex);
            Position previousPosition = new Position(column - 1, rowIndex);
            String unitSymbol = getFieldSymbol(position, game);

            boolean prevSelected = (column > 0) && isSelected(game, previousPosition);

            char leftBorder = (isSelected(game, position) || prevSelected) ? verticalSelected : vertical;

            if (unitSymbol.length() == 3) {
                stringBuilder.append(leftBorder).append(unitSymbol);
            } else if (unitSymbol.length() == 2) {
                stringBuilder.append(leftBorder).append(unitSymbol).append(" ");
            } else {
                stringBuilder.append(leftBorder).append(" ").append(unitSymbol).append(" ");
            }
        }

        Position position = new Position(6, rowIndex);
        char rightBorder = isSelected(game, position) ? verticalSelected : vertical;
        stringBuilder.append(rightBorder);
        return stringBuilder.toString();
    }

    private static String getFieldSymbol(Position position, Game game) {
        Unit unit = game.getFarmlandBoard().getField(position).getUnit();

        return getUnitSymbol(unit, game);
    }

    private static String getUnitSymbol(Unit unit, Game game) {
        if (unit == null) {
            return " ";
        }
        boolean star = unit.getTeam() == game.getCurrentTeam() && !unit.hasMoved();

        if (star) {
            switch (unit) {
                case FarmerKing ignored -> {
                    return "*" + (!unit.getTeam().isAiTeam() ? "X" : "Y");
                }
                case RegularUnit regularUnit when regularUnit.isBlocking() -> {
                    return "*" + (!unit.getTeam().isAiTeam() ? "x" : "y") + "b";
                }
                case RegularUnit regularUnit when !regularUnit.isBlocking() -> {
                    return "*" + (!unit.getTeam().isAiTeam() ? "x" : "y");
                }
                default -> {
                    return "   ";
                }
            }
        }

        if (unit instanceof FarmerKing) {
            return (!unit.getTeam().isAiTeam()) ? "X" : "Y";
        }

        RegularUnit regularUnit = (RegularUnit) unit;
        if (regularUnit.isBlocking()) {
            return " " + (!unit.getTeam().isAiTeam() ? "x" : "y") + 'b';
        }

        return (!unit.getTeam().isAiTeam()) ? "x" : "y";
    }

    private static String columnContent() {
        return "    A   B   C   D   E   F   G";
    }
}
