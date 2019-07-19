package cn.yescallop.gomoku.rule;

import java.util.function.Supplier;

/**
 * A Rule creates a Judge of the rule.
 *
 * @author Scallop Ye
 */
public interface Rule {

    /**
     * Creates a rule with the specified name and judge supplier.
     *
     * @param name          the name.
     * @param judgeSupplier the judge supplier.
     * @return the rule.
     */
    static Rule create(String name, Supplier<Judge> judgeSupplier) {
        return new RuleImpl(name, judgeSupplier);
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
}
