package cn.yescallop.gomoku.rule;

import cn.yescallop.gomoku.rule.standard.*;

/**
 * Constant definitions for the standard rules.
 *
 * @author Scallop Ye
 */
public final class StandardRules {

    // Gomoku Rules
    public static final Rule FREESTYLE_GOMOKU = Rule.createGomoku("Free-style Gomoku", FreestyleGomoku::new);
    public static final Rule STANDARD_GOMOKU = Rule.createGomoku("Standard Gomoku", StandardGomoku::new);
    public static final Rule GOMOKU_PRO = Rule.createGomoku("Gomoku-Pro", GomokuPro::new);
    public static final Rule SWAP = Rule.createGomoku("Swap", Swap::new);
    public static final Rule SWAP2 = Rule.createGomoku("Swap2", Swap2::new);
    public static final Rule CHINESE_SWAP = Rule.createGomoku("Chinese Swap", ChineseSwap::new);

    // Renju Rules
    public static final Rule STANDARD_RENJU = Rule.createRenju("Standard Renju", StandardRenju::new);

    private StandardRules() {
        // no instance
    }
}
