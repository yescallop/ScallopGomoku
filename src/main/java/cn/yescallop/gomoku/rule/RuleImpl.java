package cn.yescallop.gomoku.rule;

import java.util.function.Supplier;

/**
 * @author Scallop Ye
 */
class RuleImpl implements Rule {

    private final String name;
    private final boolean renju;
    private final Supplier<Judge> judgeSupplier;

    RuleImpl(String name, boolean renju, Supplier<Judge> judgeSupplier) {
        this.name = name;
        this.renju = renju;
        this.judgeSupplier = judgeSupplier;
    }

    @Override
    public Judge newJudge() {
        return judgeSupplier.get();
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public boolean isRenjuRule() {
        return renju;
    }
}
