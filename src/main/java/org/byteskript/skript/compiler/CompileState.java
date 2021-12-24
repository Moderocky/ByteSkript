/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.compiler;

import mx.kenzie.foundation.compiler.State;

public enum CompileState implements State {
    ROOT,
    MEMBER_BODY,
    CODE_BODY,
    STATEMENT,
    ENTRY_VALUE,
    IMPORT_BODY,
    AREA_BODY,
    HEADER
}
