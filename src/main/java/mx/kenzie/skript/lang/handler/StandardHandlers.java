package mx.kenzie.skript.lang.handler;

import mx.kenzie.skript.api.HandlerType;

public enum StandardHandlers implements HandlerType {
    
    RUN(true, false),
    SET(true, false),
    ADD(true, false),
    REMOVE(true, false),
    GET(false, true),
    DELETE(false, false),
    FIND(true, true);
    final boolean expectInputs;
    final boolean expectReturn;
    
    StandardHandlers(boolean expectInputs, boolean expectReturn) {
        this.expectInputs = expectInputs;
        this.expectReturn = expectReturn;
    }
    
    @Override
    public boolean expectInputs() {
        return expectInputs;
    }
    
    @Override
    public boolean expectReturn() {
        return expectReturn;
    }
}
