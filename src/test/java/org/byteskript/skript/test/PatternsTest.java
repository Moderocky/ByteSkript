/*
 * Copyright (c) 2021 ByteSkript org (Moderocky)
 * View the full licence information and permissions:
 * https://github.com/Moderocky/ByteSkript/blob/master/LICENSE
 */

package org.byteskript.skript.test;

import org.byteskript.skript.compiler.Pattern;
import org.junit.Test;

public class PatternsTest {
    
    @Test
    public void simpleOptional() {
        final Pattern pattern = new Pattern(new String[]{"[a] new thing"}, null);
        final java.util.regex.Pattern result = pattern.getCompiledPatterns()[0];
        assert result.pattern().equals("^(?:a )?new thing$");
        assert result.matcher("a new thing").matches();
        assert result.matcher("new thing").matches();
    }
    
    @Test
    public void simpleChoice() {
        final Pattern pattern = new Pattern(new String[]{"the (new|old) thing"}, null);
        final java.util.regex.Pattern result = pattern.getCompiledPatterns()[0];
        assert result.pattern().equals("^the (?:new|old) thing$");
        assert result.matcher("the new thing").matches();
        assert result.matcher("the old thing").matches();
    }
    
    @Test
    public void optionalAndChoice() {
        final Pattern pattern = new Pattern(new String[]{"[the] (new|old) thing"}, null);
        final java.util.regex.Pattern result = pattern.getCompiledPatterns()[0];
        assert result.pattern().equals("^(?:the )?(?:new|old) thing$");
        assert result.matcher("new thing").matches();
        assert result.matcher("old thing").matches();
        assert result.matcher("the new thing").matches();
    }
    
    @Test
    public void optionalInChoice() {
        final Pattern pattern = new Pattern(new String[]{"([a ]new|old) thing"}, null);
        final java.util.regex.Pattern result = pattern.getCompiledPatterns()[0];
        assert result.pattern().equals("^(?:(?:a )?new|old) thing$");
        assert result.matcher("a new thing").matches();
        assert result.matcher("old thing").matches();
        assert result.matcher("new thing").matches();
    }
    
    @Test
    public void choiceInOptional() {
        final Pattern pattern = new Pattern(new String[]{"[(new|old)] thing"}, null);
        final java.util.regex.Pattern result = pattern.getCompiledPatterns()[0];
        assert result.pattern().equals("^(?:(?:new|old) )?thing$");
        assert result.matcher("thing").matches();
        assert result.matcher("new thing").matches();
        assert result.matcher("old thing").matches();
    }
    
    @Test
    public void strange() {
        final Pattern pattern = new Pattern(new String[]{"[the] [current] (process|thread)"}, null);
        final java.util.regex.Pattern result = pattern.getCompiledPatterns()[0];
        assert result.pattern().equals("^(?:the )?(?:current )?(?:process|thread)$");
        assert result.matcher("process").matches();
        assert result.matcher("thread").matches();
        assert result.matcher("the thread").matches();
        assert result.matcher("current thread").matches();
        assert result.matcher("the current thread").matches();
    }
    
    @Test
    public void strangeNested() {
        final Pattern pattern = new Pattern(new String[]{"%Integer% (ms|millis|milli[ ]second[s])"}, null);
        final java.util.regex.Pattern result = pattern.getCompiledPatterns()[0];
        assert result.pattern().equals("^(.+) (?:ms|millis|milli(?: )?second(?:s)?)$");
        assert result.matcher("4 millis").matches();
        assert result.matcher("4 ms").matches();
        assert !result.matcher("5 mseconds").matches();
        assert result.matcher("5 milli seconds").matches();
        assert result.matcher("5 milliseconds").matches();
    }
    
    
}
