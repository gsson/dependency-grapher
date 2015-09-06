package se.fnord.graph.model;

public abstract class AbstractEdgeEntity<OUT_V extends PropertySetter, IN_V extends PropertySetter> implements EdgeEntity<OUT_V, IN_V> {
    private final IN_V in;
    private final OUT_V out;

    protected AbstractEdgeEntity(OUT_V out, IN_V in) {
        this.out = out;
        this.in = in;
    }

    @Override
    public OUT_V getOutVertex() {
        return out;
    }

    @Override
    public IN_V getInVertex() {
        return in;
    }
}
