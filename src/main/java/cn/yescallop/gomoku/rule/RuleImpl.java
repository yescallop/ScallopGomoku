package cn.yescallop.gomoku.rule;

import java.util.function.Supplier;

/**
 * @author Scallop Ye
 */
class RuleImpl implements Rule {

    private final String name;
    private final Type type;
    private final Supplier<Opening> openingSupplier;

    RuleImpl(String name, Type type, Supplier<Opening> openingSupplier) {
        this.name = name;
        this.type = type;
        this.openingSupplier = openingSupplier;
    }

    @Override
    public Opening newOpening() {
        return openingSupplier == null ? null : openingSupplier.get();
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Type type() {
        return type;
    }
}
