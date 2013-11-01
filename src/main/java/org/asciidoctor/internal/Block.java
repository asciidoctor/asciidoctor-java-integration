package org.asciidoctor.internal;

import java.util.List;
import java.util.Map;

public interface Block {

	String id();
	String title();
    String role();
    String style();
    List<Block> blocks();
    Map<String, Object> attributes();
    
    Object content();
    String render();
    
    String context();
    List<String> lines();
}
