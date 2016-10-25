package org.asciidoctor.ast;

import org.asciidoctor.internal.RubyHashUtil;
import org.asciidoctor.internal.RubyUtils;
import org.jruby.Ruby;
import org.jruby.RubyArray;
import org.jruby.RubyObject;

import java.util.List;
import java.util.Map;

public class AbstractBlockImpl extends AbstractNodeImpl implements AbstractBlock {

    private static final String BLOCK_CLASS = "Block";
    private static final String SECTION_CLASS = "Section";
    
    protected AbstractBlock delegate;


    public AbstractBlockImpl(AbstractBlock blockDelegate, Ruby runtime) {
        super(blockDelegate, runtime);
        this.delegate = blockDelegate;
    }

    @SuppressWarnings("deprecation")
    @Deprecated
    @Override
    public String title() {
        return getTitle();
    }

    @Override
    public String getTitle() {
        return delegate.getTitle();
    }

    public boolean isTitle() {
        return RubyUtils.invokeRubyMethod(delegate, "title?", new Object[0], Boolean.class);
    }

    @SuppressWarnings("deprecation")
    @Deprecated
    @Override
    public String style() {
        return getStyle();
    }

    @Override
    public String getStyle() {
        return delegate.getStyle();
    }

    public String getCaption() {
        return RubyUtils.invokeRubyMethod(delegate, "caption", new Object[0], String.class);
    }

    @SuppressWarnings("deprecation")
    @Deprecated
    @Override
    public List<AbstractBlock> blocks() {
        return getBlocks();
    }

    @Override
    public List<AbstractBlock> getBlocks() {
        List<AbstractBlock> rubyBlocks = delegate.getBlocks();

        for (int i = 0; i < rubyBlocks.size(); i++) {
            Object abstractBlock = rubyBlocks.get(i);
            if (!(abstractBlock instanceof RubyArray) && !(abstractBlock instanceof AbstractNode)) {
                RubyObject rubyObject = (RubyObject) abstractBlock;
                rubyBlocks.set(i, (AbstractBlock) NodeConverter.createASTNode(rubyObject));
            }
        }

        return rubyBlocks;
    }

    public boolean isBlocks() {
        return RubyUtils.invokeRubyMethod(delegate, "blocks?", new Object[0], Boolean.class);
    }

    @SuppressWarnings("deprecation")
    @Deprecated
    @Override
    public Object content() {
        return getContent();
    }

    @SuppressWarnings("deprecation")
    @Deprecated
    @Override
    public Object getContent() {
        return delegate.content();
    }

    @Override
    public String getNodeName() {
        return delegate.getNodeName();
    }

    @Override
    public String convert() {
        return delegate.convert();
    }

    @Override
    public int getLevel() {
        return delegate.getLevel();
    }

    @Override
    public AbstractBlock delegate() {
        return delegate;
    }

    public List<Section> getSections() {
        return RubyUtils.invokeRubyMethod(delegate, "sections", new Object[0], List.class);
    }

    public boolean isSections() {
        return RubyUtils.invokeRubyMethod(delegate, "sections?", new Object[0], Boolean.class);
    }

    @Override
    public List<AbstractBlock> findBy(Map<Object, Object> selector) {

        @SuppressWarnings("unchecked")
        List<AbstractBlock> findBy = delegate.findBy(RubyHashUtil.convertMapToRubyHashWithSymbolsIfNecessary(runtime,
                selector));

        for (int i = 0; i < findBy.size(); i++) {
            Object abstractBlock = findBy.get(i);
            if (!(abstractBlock instanceof RubyArray) && !(abstractBlock instanceof AbstractBlock)) {
                RubyObject rubyObject = (RubyObject)abstractBlock;
                findBy.set(i, (AbstractBlock) NodeConverter.createASTNode(rubyObject));
            }

        }
        return findBy;
    }

}
