package org.asciidoctor.extension;

import org.asciidoctor.api.ast.ContentNode;
import org.asciidoctor.api.ast.StructuralNode;
import org.asciidoctor.api.extension.InlineMacroProcessor;

import java.util.HashMap;
import java.util.Map;

public class ManpageMacro extends InlineMacroProcessor {

    public ManpageMacro(String macroName) {
        super(macroName);
    }

    public ManpageMacro(String macroName, Map<String, Object> config) {
        super(macroName, config);
    }

    @Override
    public String process(ContentNode parent, String target,
            Map<String, Object> attributes) {
        Map<String, Object> options = new HashMap<String, Object>();
        options.put("type", ":link");
        options.put("target", target + ".html");
        return createPhraseNode(parent, "anchor", target, attributes, options).convert();
    }

}
