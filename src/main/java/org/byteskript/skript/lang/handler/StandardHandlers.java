/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.lang.handler;

import org.byteskript.skript.api.HandlerType;

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
