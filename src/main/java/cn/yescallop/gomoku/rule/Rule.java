package cn.yescallop.gomoku.rule;

import java.util.function.Supplier;

/**
 * A Rule creates a Judge of the rule.
 *
 * @author Scallop Ye
 */
public interface Rule {

    /**
     * Creates a Standard Gomoku rule with the specified name and judge supplier.
     *
     * @param name the name.
     * @param openingSupplier the opening supplier, null for no opening.
     * @return the rule.
     */
    static Rule createGomoku(String name, Supplier<Opening> openingSupplier) {
        return new RuleImpl(name, Type.STANDARD_GOMOKU, openingSupplier);
    }

    /**
     * Creates a Standard Renju rule with the specified name and judge supplier.
     *
     * @param name the name.
     * @param openingSupplier the opening supplier, null for no opening.
     * @return the rule.
     */
    static Rule createRenju(String name, Supplier<Opening> openingSupplier) {
        return new RuleImpl(name, Type.STANDARD_RENJU, openingSupplier);
    }

    /**
     * Constructs a new opening instance of this rule.
     *
     * @return the opening instance.
     */
    Opening newOpening();

    /**
     * Gets the name of the rule.
     *
     * @return the name.
     */
    String name();

    /**
     * Gets the type of the rule.
     *
     * @return the type.
     */
    Type type();

    enum Type {
        FREESTYLE_GOMOKU,
        STANDARD_GOMOKU,
        STANDARD_RENJU
    }
}
