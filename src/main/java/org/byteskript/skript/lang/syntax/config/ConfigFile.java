/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.lang.syntax.config;

import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;
import org.byteskript.skript.api.Deletable;
import org.byteskript.skript.api.Referent;
import org.byteskript.skript.api.note.Documentation;
import org.byteskript.skript.api.syntax.Section;
import org.byteskript.skript.compiler.CommonTypes;
import org.byteskript.skript.compiler.Context;
import org.byteskript.skript.compiler.Pattern;
import org.byteskript.skript.compiler.SkriptLangSpec;
import org.byteskript.skript.compiler.structure.BasicTree;
import org.byteskript.skript.compiler.structure.PreVariable;
import org.byteskript.skript.compiler.structure.SectionMeta;
import org.byteskript.skript.lang.element.StandardElements;
import org.byteskript.skript.lang.handler.StandardHandlers;
import org.byteskript.skript.runtime.config.ConfigMap;

import java.lang.reflect.Method;

@Documentation(
    name = "Config File",
    description = """
        Obtains data from a `.csk` config file.
        
        If used as a section header, this will automatically save the config
        to its file after the section exits.
        """,
    examples = {
        """
            set {config} to resources/myconf.csk
            add "hello: " + {var} to {config}
            set "hello" from {config} to "there"
            save config {config}
            """,
        """
            set {config} to folder/config.csk:
                add "key: value" to {config}
                """
    }
)
public class ConfigFile extends Section implements Referent, Deletable {
    
    public ConfigFile() {
        super(SkriptLangSpec.LIBRARY, StandardElements.EXPRESSION, "path/to/file.csk");
        this.handlers.put(StandardHandlers.DELETE, this.findMethod(ConfigMap.class, "delete"));
    }
    
    @Override
    public Pattern.Match match(String thing, Context context) {
        if (!thing.endsWith(".csk")) return null;
        if (thing.contains(" ")) {
            context.getError().addHint(this, "Config file paths should not contain spaces.");
            return null;
        }
        return new Pattern.Match(Pattern.fakeMatcher(thing), thing);
    }
    
    @Override
    public Type getReturnType() {
        return CommonTypes.CONFIG;
    }
    
    @Override
    public void compile(Context context, Pattern.Match match) throws Throwable {
        final String path = match.<String>meta().trim();
        final MethodBuilder method = context.getMethod();
        method.writeCode(WriteInstruction.push(path));
        final Method target = ConfigMap.class.getMethod("create", String.class);
        method.writeCode(WriteInstruction.invoke(target));
        if (!context.isSectionHeader()) return;
        final PreVariable variable = new PreVariable(null);
        variable.internal = true;
        context.forceUnspecVariable(variable);
        final int slot = context.slotOf(variable);
        method.writeCode(WriteInstruction.storeObject(slot));
        method.writeCode(WriteInstruction.loadObject(slot)); // make available to whatever eats this
        final ConfigTree tree = new ConfigTree(context.getSection(1));
        context.createTree(tree);
        tree.variable = variable;
    }
    
    @Override
    public void onSectionExit(Context context, SectionMeta meta) {
        final ConfigTree tree = context.findTree(ConfigTree.class);
        final MethodBuilder method = context.getMethod();
        if (tree == null) return;
        final int slot = context.slotOf(tree.variable);
        method.writeCode(WriteInstruction.loadObject(slot));
        final Method target = this.findMethod(ConfigMap.class, "save");
        method.writeCode(WriteInstruction.invoke(target));
    }
    
    @Override
    public void compileInline(Context context, Pattern.Match match) throws Throwable {
        this.compile(context, match);
    }
    
    @Override
    public boolean allowAsInputFor(Type type) {
        return CommonTypes.OBJECT.equals(type) || CommonTypes.REFERENT.equals(type) || CommonTypes.CONFIG.equals(type) || super.allowAsInputFor(type);
    }
    
    @Override
    public Type getHolderType() {
        return CommonTypes.CONFIG;
    }
    
    static class ConfigTree extends BasicTree {
        
        protected PreVariable variable;
        
        public ConfigTree(SectionMeta owner) {
            super(owner);
        }
    }
    
}
