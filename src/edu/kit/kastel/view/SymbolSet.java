package edu.kit.kastel.view;

/**
 * This class represents a set of symbols for farmland board.
 * @author ucktt
 */
public class SymbolSet {

    private static final String DEFAULT_ASCII_SYMBOL_SET = "++++++++-|+############=N####";
    private final String symbols;

    /**
     * constructs a new symbolSet instance.
     * @param symbols a string of symbols that appear in a set of symbols
     */
    public SymbolSet(String symbols) {
        this.symbols = symbols;
    }

    /**
     * Returns the symbol at the given index.
     * @param index the given index of the symbol to retrieve
     * @return the symbol at the given index
     */
    public char getSymbol(int index) {
        return symbols.charAt(index);
    }

    /**
     * returns the default ASCII symbol set.
     * @return a symbol set using default ASCII characters
     */
    public static SymbolSet defaultAscii() {
        return new SymbolSet(DEFAULT_ASCII_SYMBOL_SET);
    }
}
