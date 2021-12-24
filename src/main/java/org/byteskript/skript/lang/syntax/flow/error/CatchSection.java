package org.byteskript.skript.lang.syntax.flow.error;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.WriteInstruction;
import mx.kenzie.foundation.compiler.State;
import org.byteskript.skript.api.syntax.Section;
import org.byteskript.skript.compiler.*;
import org.byteskript.skript.compiler.structure.PreVariable;
import org.byteskript.skript.compiler.structure.SectionMeta;
import org.byteskript.skript.compiler.structure.TryCatchTree;
import org.byteskript.skript.error.ScriptCompileError;
import org.byteskript.skript.error.ScriptParseError;
import org.byteskript.skript.lang.element.StandardElements;
import org.byteskript.skript.lang.handler.StandardHandlers;
import org.byteskript.skript.lang.syntax.variable.VariableExpression;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;

public class CatchSection extends Section {
    
    public CatchSection() {
        super(SkriptLangSpec.LIBRARY, StandardElements.SECTION, "catch %Variable%");
    }
    
    @Override
    public void preCompile(Context context, Pattern.Match match) throws Throwable {
        final ElementTree holder = context.getLine().nested()[0];
        if (!(holder.current() instanceof VariableExpression))
            throw new ScriptParseError(context.lineNumber(), "The error must be a variable: 'catch {varname}'");
        holder.type = StandardHandlers.SET;
        holder.compile = false;
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) throws Throwable {
        if (!(context.getTree(context.getSection(1)) instanceof TryCatchTree tree))
            throw new ScriptCompileError(context.lineNumber(), "Catch used without preceding try-section.");
        final ElementTree holder = context.getLine().nested()[0];
        if (!(holder.current() instanceof VariableExpression))
            throw new ScriptParseError(context.lineNumber(), "The error must be a variable: 'catch {varname}'");
        final Label label = tree.getEnd().use();
        final Label next = tree.getStartCatch();
        final MethodBuilder method = context.getMethod();
        if (method == null) throw new ScriptCompileError(context.lineNumber(), "Try/catch used outside method.");
        context.getMethod().writeCode(((writer, visitor) -> {
            visitor.visitJumpInsn(Opcodes.GOTO, label);
            visitor.visitLabel(next);
        }));
        final int slot = context.slotOf(this.getHolderVariable(context, match));
        method.writeCode(WriteInstruction.storeObject(slot));
        context.setState(CompileState.CODE_BODY);
    }
    
    private PreVariable getHolderVariable(Context context, Pattern.Match match) {
        final String pattern = match.groups()[0].trim();
        assert pattern.startsWith("{") && pattern.endsWith("}");
        final String name = pattern.substring(1, pattern.length() - 1);
        if (name.charAt(0) == '@' || name.charAt(0) == '_' || name.charAt(0) == '!')
            throw new ScriptCompileError(context.lineNumber(), "Holder variable must be a normal variable: '{var}'");
        return context.getVariable(name);
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.startsWith("catch ")) return null;
        return super.match(thing, context);
    }
    
    @Override
    public boolean allowedIn(State state, Context context) {
        return super.allowedIn(state, context)
            && context.getSection() != null
            && context.getMethod() != null;
    }
    
    @Override
    public void onSectionExit(Context context, SectionMeta meta) {
        if (!(context.getTree(context.getSection()) instanceof TryCatchTree tree))
            throw new ScriptCompileError(context.lineNumber(), "Unable to balance try/catch flow tree.");
        context.setState(CompileState.CODE_BODY);
        tree.close(context);
    }
    
    @Override
    public void compileInline(Context context, Pattern.Match match) throws Throwable {
        throw new ScriptCompileError(context.lineNumber(), "'Catch' must be used as section-header.");
    }
    
}
