/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.api;

/**
 * A handler mode for interacting with expressions.
 * The defaults (get/set/add/delete/...) are detailed in {@link org.byteskript.skript.lang.handler.StandardHandlers}.
 *
 * Special implementations could add behaviour like COMPARE_AND_SWAP or GET_ATOMIC.
 */
public interface HandlerType {
    
    String name();
    
    boolean expectInputs();
    
    boolean expectReturn();
    
}
