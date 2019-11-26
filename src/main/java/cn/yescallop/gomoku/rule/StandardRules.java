package cn.yescallop.gomoku.rule;

import cn.yescallop.gomoku.rule.standard.*;

/**
 * Constant definitions for the standard rules.
 *
 * @author Scallop Ye
 */
public final class StandardRules {

    // Gomoku Rules
    public static final Rule FREESTYLE_GOMOKU = new RuleImpl("Free-style Gomoku", Rule.Type.FREESTYLE_GOMOKU, null);
    public static final Rule STANDARD_GOMOKU = Rule.createGomoku("Standard Gomoku", null);
    public static final Rule GOMOKU_PRO = Rule.createGomoku("Gomoku-Pro", GomokuPro::new);
    public static final Rule SWAP = Rule.createGomoku("Swap", Swap::new);
    public static final Rule SWAP2 = Rule.createGomoku("Swap2", Swap2::new);
    public static final Rule CHINESE_SWAP = Rule.createGomoku("Chinese Swap", ChineseSwap::new);

    // Renju Rules
    public static final Rule STANDARD_RENJU = Rule.createRenju("Standard Renju", null);
    public static final Rule RIF = Rule.createRenju("RIF", RIF::new);
    public static final Rule SAKATA = Rule.createRenju("Sakata", Sakata::new);
    public static final Rule YAMAGUCHI = Rule.createRenju("Yamaguchi", Yamaguchi::new);
    public static final Rule TARANNIKOV = Rule.createRenju("Tarannikov", Tarannikov::new);
    public static final Rule TARAGUCHI = Rule.createRenju("Taraguchi", () -> new Taraguchi(5));
    public static final Rule SOOSYRV_8 = Rule.createRenju("Soosyrv-8", () -> new Soosyrv(8));

    private StandardRules() {
        // no instance
    }
}
