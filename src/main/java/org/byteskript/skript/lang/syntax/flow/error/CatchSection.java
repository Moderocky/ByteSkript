/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.lang.syntax.flow.error;

import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.compiler.State;
import org.byteskript.skript.api.note.Documentation;
import org.byteskript.skript.api.syntax.Section;
import org.byteskript.skript.compiler.*;
import org.byteskript.skript.compiler.structure.ProgrammaticSplitTree;
import org.byteskript.skript.compiler.structure.SectionMeta;
import org.byteskript.skript.compiler.structure.TryCatchTree;
import org.byteskript.skript.error.ScriptCompileError;
import org.byteskript.skript.lang.element.StandardElements;
import org.byteskript.skript.lang.handler.StandardHandlers;

@Documentation(
    name = "Catch",
    description = """
        Run if an error occurs in the preceding try-block.""",
    examples = {
        """
            try:
                assert 1 is 2
            catch {error}:
                print "The error was: " + {error}
                    """
    }
)
public class CatchSection extends Section {
    
    public CatchSection() {
        super(SkriptLangSpec.LIBRARY, StandardElements.SECTION, "catch %Variable%");
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.startsWith("catch ")) return null;
        return super.match(thing, context);
    }
    
    @Override
    public Type getReturnType() {
        return CommonTypes.VOID;
    }
    
    @Override
    public void preCompile(Context context, Pattern.Match match) throws Throwable {
        final ElementTree holder = context.getCompileCurrent().nested()[0];
        holder.type = StandardHandlers.SET;
        holder.compile = false;
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) throws Throwable {
        if (!(context.getTree(context.getSection(1)) instanceof TryCatchTree tree))
            throw new ScriptCompileError(context.lineNumber(), "Catch used without preceding try-section.");
        this.compileTogether(context, match, tree);
    }
    
    @Override
    public boolean allowedIn(State state, Context context) {
        return super.allowedIn(state, context)
            && context.getSection() != null
            && context.getMethod() != null;
    }
    
    public void compileTogether(Context context, Pattern.Match match, TryCatchTree tree) throws Throwable {
        final ElementTree holder = context.getCompileCurrent().nested()[0];
        tree.branch(context);
        {
            holder.type = StandardHandlers.SET;
            holder.compile = true;
            holder.preCompile(context);
            holder.compile(context);
        }
        context.setState(CompileState.CODE_BODY);
    }
    
    @Override
    public void onSectionExit(Context context, SectionMeta meta) {
        final ProgrammaticSplitTree current;
        if (context.getTree(context.getSection()) instanceof TryCatchTree found) current = found;
        else current = context.getCurrentTree();
        if (current instanceof TryCatchTree tree) {
            context.setState(CompileState.CODE_BODY);
            tree.close(context);
        }
    }
    
    @Override
    public void compileInline(Context context, Pattern.Match match) throws Throwable {
        if (!(context.getTree(context.getSection(0)) instanceof TryCatchTree tree))
            throw new ScriptCompileError(context.lineNumber(), "Inline catch used without preceding try-section.");
        this.compileTogether(context, match, tree);
        tree.close(context);
    }
    
    @Override
    public void preCompileInline(Context context, Pattern.Match match) throws Throwable {
        this.preCompile(context, match);
    }
    
}
