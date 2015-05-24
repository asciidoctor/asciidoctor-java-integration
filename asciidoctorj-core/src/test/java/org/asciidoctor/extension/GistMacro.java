package org.asciidoctor.extension;

import java.util.Arrays;
import java.util.Map;

import org.asciidoctor.ast.BlockNode;
import org.asciidoctor.ast.Block;

public class GistMacro extends BlockMacroProcessor {

    public GistMacro(String macroName) {
        super(macroName);
    }

    public GistMacro(String macroName, Map<String, Object> config) {
        super(macroName, config);
    }
    
    @Override
    public Block process(BlockNode parent, String target, Map<String, Object> attributes) {
       
       String content = "<div class=\"content\">\n" + 
       		"<script src=\"https://gist.github.com/"+target+".js\"></script>\n" + 
       		"</div>"; 
       
       return createBlock(parent, "pass", Arrays.asList(content), attributes);
    }

}
