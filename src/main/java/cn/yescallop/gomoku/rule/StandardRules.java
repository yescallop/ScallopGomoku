package cn.yescallop.gomoku.rule;

import cn.yescallop.gomoku.rule.standard.*;

/**
 * Constant definitions for the standard rules.
 *
 * @author Scallop Ye
 */
public final class StandardRules {

    public static final Rule FREESTYLE_GOMOKU = Rule.create("Free-style Gomoku", FreestyleGomoku::new);
    public static final Rule STANDARD_GOMOKU = Rule.create("Standard Gomoku", StandardGomoku::new);
    public static final Rule SWAP = Rule.create("Swap", Swap::new);
    public static final Rule SWAP2 = Rule.create("Swap2", Swap2::new);

    private StandardRules() {
        // no instance
    }
}
