package cn.yescallop.gomoku.rule;

import cn.yescallop.gomoku.rule.standard.NoLimit;

/**
 * @author Scallop Ye
 */
public final class StandardRules {

    public static final Rule NO_LIMIT = Rule.create("No Limit", NoLimit::new);

    private StandardRules() {
        // no instance
    }
}
