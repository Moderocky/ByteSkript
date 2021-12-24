package org.byteskript.skript.api.automatic;

import org.byteskript.skript.api.FunctionalEntrySection;
import org.byteskript.skript.api.Library;
import org.byteskript.skript.api.syntax.Section;
import org.byteskript.skript.compiler.CompileState;
import org.byteskript.skript.compiler.Context;
import org.byteskript.skript.compiler.Pattern;
import org.byteskript.skript.compiler.structure.SectionMeta;
import org.byteskript.skript.error.ScriptCompileError;
import org.byteskript.skript.lang.element.StandardElements;

import java.lang.reflect.RecordComponent;
import java.util.ArrayList;
import java.util.List;

public final class GeneratedEntrySection extends Section {
    
    private final Class<Record> target;
    
    public GeneratedEntrySection(Library provider, final Class<Record> target, String... patterns) {
        super(provider, StandardElements.SECTION, patterns);
        this.target = target;
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) throws Throwable {
        context.setState(CompileState.AREA_BODY);
    }
    
    @Override
    public void onSectionExit(Context context, SectionMeta meta) {
        try {
            if (!FunctionalEntrySection.class.isAssignableFrom(target)) return;
            final Object[] arguments =
                meta.getData().toArray(new Object[0]);
            final List<Class<?>> parameters = new ArrayList<>();
            for (RecordComponent component : target.getRecordComponents()) {
                parameters.add(component.getType());
            }
            final Record record = target
                .getConstructor(parameters.toArray(new Class[0]))
                .newInstance(arguments);
            if (record instanceof FunctionalEntrySection func) func.compile(context);
        } catch (Throwable throwable) {
            throw new ScriptCompileError(context.lineNumber(), "Unable to load entry section.", throwable);
        } finally {
            context.setState(CompileState.MEMBER_BODY);
        }
    }
    
    public Class<Record> getTarget() {
        return target;
    }
}
