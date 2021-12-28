/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.lang.syntax.entry.syntax;

import mx.kenzie.foundation.AnnotationBuilder;
import org.byteskript.skript.api.HandlerType;
import org.byteskript.skript.api.syntax.SectionEntry;
import org.byteskript.skript.compiler.AreaFlag;
import org.byteskript.skript.compiler.Context;
import org.byteskript.skript.compiler.Pattern;
import org.byteskript.skript.compiler.SkriptLangSpec;
import org.byteskript.skript.compiler.structure.ProgrammaticSplitTree;
import org.byteskript.skript.compiler.structure.SectionMeta;
import org.byteskript.skript.compiler.structure.SyntaxTree;
import org.byteskript.skript.error.ScriptCompileError;
import org.byteskript.skript.lang.element.StandardElements;
import org.byteskript.skript.lang.handler.StandardHandlers;

import java.util.regex.Matcher;

public class SyntaxEntry extends SectionEntry {
    
    public SyntaxEntry() {
        super(SkriptLangSpec.LIBRARY, StandardElements.SECTION, "syntax");
    }
    
    private final Matcher matcher = Pattern.fakeMatcher("syntax");
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.equals("syntax")) return null;
        if (!context.hasFlag(AreaFlag.IN_FUNCTION)) {
            context.getError().addHint(this, "Syntax can only be declared inside a function.");
            return null;
        }
        return new Pattern.Match(matcher);
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) throws Throwable {
        context.addFlag(AreaFlag.IN_SYNTAX);
        final SyntaxTree tree = new SyntaxTree(context.getSection(1));
        context.createTree(tree);
    }
    
    @Override
    public void onSectionExit(Context context, SectionMeta meta) {
        context.removeFlag(AreaFlag.IN_SYNTAX);
        final ProgrammaticSplitTree current = context.getCurrentTree();
        if (!(current instanceof SyntaxTree tree))
            throw new ScriptCompileError(context.lineNumber(), "Another tree interrupted compilation.");
        final HandlerType mode = tree.mode;
        for (final SyntaxTree.Handler handler : tree.getHandlers()) {
            final AnnotationBuilder<?> builder = context.getMethod().addAnnotation(handler.type().annotation)
                .setVisible(true);
            if (handler.type() != SyntaxTree.Type.PROPERTY) {
                builder.addValue("value", new String[]{handler.pattern()});
            } else {
                builder.addValue("value", handler.pattern());
                builder.addValue("type", mode == null ? StandardHandlers.GET : mode);
            }
        }
    }
}
