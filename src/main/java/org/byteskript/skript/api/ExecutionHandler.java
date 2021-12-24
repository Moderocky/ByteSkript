/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.api;

import java.util.concurrent.Future;

public interface ExecutionHandler {
    
    Future<?> execute(Runnable runnable);
    
    Future<?> schedule(Runnable runnable, long millis);
    
}
