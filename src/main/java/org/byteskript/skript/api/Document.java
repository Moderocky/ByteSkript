/*
 * Copyright (c) 2022 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.api;

/**
 * A miniature record for holding documentation for a syntax element.
 */
public record Document(String name, String type, String[] patterns, String description, String[] examples) {
}
