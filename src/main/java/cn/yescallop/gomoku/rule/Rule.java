package cn.yescallop.gomoku.rule;

import java.util.function.Supplier;

/**
 * A Rule creates a Judge of the rule.
 *
 * @author Scallop Ye
 */
public interface Rule {

    /**
     * Creates a Gomoku rule with the specified name and judge supplier.
     *
     * @param name the name.
     * @param judgeSupplier the judge supplier.
     * @return the rule.
     */
    static Rule createGomoku(String name, Supplier<Judge> judgeSupplier) {
        return new RuleImpl(name, false, judgeSupplier);
    }

    /**
     * Creates a Renju rule with the specified name and judge supplier.
     *
     * @param name the name.
     * @param judgeSupplier the judge supplier.
     * @return the rule.
     */
    static Rule createRenju(String name, Supplier<Judge> judgeSupplier) {
        return new RuleImpl(name, true, judgeSupplier);
    }

    /**
     * Constructs a new judge of this rule.
     *
     * @return the judge.
     */
    Judge newJudge();

    /**
     * Gets the name of the rule.
     *
     * @return the name.
     */
    String name();

    /**
     * Gets whether the rule is a Renju rule.
     *
     * @return whether the rule is a Renju rule.
     */
    boolean isRenjuRule();
}
