package mx.kenzie.skript.lang.syntax.flow.loop;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.WriteInstruction;
import mx.kenzie.foundation.compiler.State;
import mx.kenzie.skript.api.syntax.Section;
import mx.kenzie.skript.compiler.*;
import mx.kenzie.skript.compiler.structure.LoopTree;
import mx.kenzie.skript.compiler.structure.PreVariable;
import mx.kenzie.skript.compiler.structure.SectionMeta;
import mx.kenzie.skript.error.ScriptCompileError;
import mx.kenzie.skript.error.ScriptParseError;
import mx.kenzie.skript.lang.element.StandardElements;
import mx.kenzie.skript.lang.handler.StandardHandlers;
import mx.kenzie.skript.lang.syntax.variable.VariableExpression;
import mx.kenzie.skript.runtime.internal.OperatorHandler;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;

import java.util.Iterator;

public class LoopInSection extends Section {
    
    public LoopInSection() {
        super(SkriptLangSpec.LIBRARY, StandardElements.SECTION, "loop %Object% in %Object%");
    }
    
    @Override
    public void preCompile(Context context, Pattern.Match match) throws Throwable {
        final ElementTree holder = context.getLine().nested()[0];
        if (!(holder.current() instanceof VariableExpression))
            throw new ScriptParseError(context.lineNumber(), "The extracted element must be a variable.");
        holder.type = StandardHandlers.SET;
        holder.compile = false;
        final LoopTree tree = new LoopTree(context.getSection(1));
        context.createTree(tree);
    }
    
    @Override
    public void preCompileInline(Context context, Pattern.Match match) throws Throwable {
        final ElementTree holder = context.getLine().nested()[0];
        if (!(holder.current() instanceof VariableExpression))
            throw new ScriptParseError(context.lineNumber(), "The extracted element must be a variable.");
        holder.type = StandardHandlers.SET;
        holder.compile = false;
        final LoopTree tree = new LoopTree(context.getSection());
        context.createTree(tree);
    }
    
    private PreVariable getHolderVariable(Context context, Pattern.Match match) {
        final String pattern = match.groups()[0].trim();
        assert pattern.startsWith("{") && pattern.endsWith("}");
        final String name = pattern.substring(1, pattern.length() - 1);
        return context.getVariable(name);
    }
    
    private void compileTogether(Context context, LoopTree tree, Pattern.Match match) throws Throwable {
        final MethodBuilder method = context.getMethod();
        assert method != null;
        final PreVariable variable = new PreVariable(null);
        variable.internal = true;
        context.forceUnspecVariable(variable);
        final int slot = context.slotOf(variable);
        final int holder = context.slotOf(this.getHolderVariable(context, match));
        this.writeCall(method, OperatorHandler.class.getMethod("acquireIterator", Object.class), context);
        method.writeCode(WriteInstruction.storeObject(slot));
        final Label top = new Label();
        final Label end = tree.getEnd().use();
        tree.setTop(top);
        tree.slot = slot;
        method.writeCode((writer, visitor) -> visitor.visitLabel(top));
        method.writeCode(WriteInstruction.loadObject(slot));
        method.writeCode(WriteInstruction.invokeInterface(Iterator.class.getMethod("hasNext")));
        method.writeCode((writer, visitor) -> visitor.visitJumpInsn(153, end));
        method.writeCode(WriteInstruction.loadObject(slot));
        method.writeCode(WriteInstruction.invokeInterface(Iterator.class.getMethod("next")));
        method.writeCode(WriteInstruction.storeObject(holder));
        context.setState(CompileState.CODE_BODY);
    }
    
    @Override
    public void compileInline(Context context, Pattern.Match match) throws Throwable {
        if (!(context.getTree(context.getSection()) instanceof LoopTree tree))
            throw new ScriptCompileError(context.lineNumber(), "Illegal mid-statement flow break.");
        this.compileTogether(context, tree, match);
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) throws Throwable {
        if (!(context.getTree(context.getSection(1)) instanceof LoopTree tree))
            throw new ScriptCompileError(context.lineNumber(), "Illegal mid-statement flow break.");
        this.compileTogether(context, tree, match);
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.startsWith("loop ")) return null;
        if (!thing.contains(" in ")) return null;
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
        if (!(context.getTree(context.getSection()) instanceof LoopTree tree))
            throw new ScriptCompileError(context.lineNumber(), "Unable to balance loop flow tree.");
        context.setState(CompileState.CODE_BODY);
        final MethodBuilder method = context.getMethod();
        final Label top = tree.getTop();
        method.writeCode((writer, visitor) -> visitor.visitJumpInsn(Opcodes.GOTO, top));
        method.writeCode(tree.getEnd().instruction());
    }
    
}
