package org.asciidoctor.extension.processorproxies;

import org.asciidoctor.ast.BlockNode;
import org.asciidoctor.ast.impl.NodeConverter;
import org.asciidoctor.extension.InlineMacroProcessor;
import org.asciidoctor.internal.RubyHashMapDecorator;
import org.asciidoctor.internal.RubyHashUtil;
import org.asciidoctor.internal.RubyUtils;
import org.jruby.Ruby;
import org.jruby.RubyClass;
import org.jruby.RubyHash;
import org.jruby.RubyRegexp;
import org.jruby.RubySymbol;
import org.jruby.anno.JRubyMethod;
import org.jruby.javasupport.JavaEmbedUtils;
import org.jruby.runtime.Block;
import org.jruby.runtime.Helpers;
import org.jruby.runtime.ObjectAllocator;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;
import org.jruby.util.RegexpOptions;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class InlineMacroProcessorProxy extends AbstractMacroProcessorProxy<InlineMacroProcessor> {

    private static final String SUPER_CLASS_NAME = "InlineMacroProcessor";

    public InlineMacroProcessorProxy(Ruby runtime, RubyClass metaClass, Class<? extends InlineMacroProcessor> inlineMacroProcessorClass) {
        super(runtime, metaClass, inlineMacroProcessorClass);
    }

    public InlineMacroProcessorProxy(Ruby runtime, RubyClass metaClass, InlineMacroProcessor inlineMacroProcessor) {
        super(runtime, metaClass, inlineMacroProcessor);
    }

    public static RubyClass register(final Ruby rubyRuntime, final String inlineMacroProcessorClassName) {

        try {
            Class<? extends InlineMacroProcessor>  inlineMacroProcessorClass = (Class<? extends InlineMacroProcessor>) Class.forName(inlineMacroProcessorClassName);
            return register(rubyRuntime, inlineMacroProcessorClass);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static RubyClass register(final Ruby rubyRuntime, final Class<? extends InlineMacroProcessor> inlineMacroProcessor) {
        RubyClass rubyClass = ProcessorProxyUtil.defineProcessorClass(rubyRuntime, SUPER_CLASS_NAME, new ObjectAllocator() {
            @Override
            public IRubyObject allocate(Ruby runtime, RubyClass klazz) {
                return new InlineMacroProcessorProxy(runtime, klazz, inlineMacroProcessor);
            }
        });
        ProcessorProxyUtil.defineAnnotatedMethods(rubyClass, InlineMacroProcessorProxy.class);
        return rubyClass;
    }

    public static RubyClass register(final Ruby rubyRuntime, final InlineMacroProcessor inlineMacroProcessor) {
        RubyClass rubyClass = ProcessorProxyUtil.defineProcessorClass(rubyRuntime, SUPER_CLASS_NAME, new ObjectAllocator() {
            @Override
            public IRubyObject allocate(Ruby runtime, RubyClass klazz) {
                return new InlineMacroProcessorProxy(runtime, klazz, inlineMacroProcessor);
            }
        });
        ProcessorProxyUtil.defineAnnotatedMethods(rubyClass, InlineMacroProcessorProxy.class);
        return rubyClass;
    }

    @JRubyMethod(name = "initialize", required = 1, optional = 1)
    public IRubyObject initialize(ThreadContext context, IRubyObject[] args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        if (getProcessor() != null) {
            // Instance was created in Java and has options set, so we pass these
            // instead of those passed by asciidoctor

            // If options contains a String with a Regexp create the RubyRegexp from it
            RubySymbol regexpSymbol = RubySymbol.newSymbol(getRuntime(), InlineMacroProcessor.REGEXP);
            Object regexp = getProcessor().getConfig().get(regexpSymbol);
            if (regexp != null && regexp instanceof String) {
                getProcessor().getConfig().put(regexpSymbol, convertRegexp(regexp));
            }

            Helpers.invokeSuper(
                    context,
                    this,
                    getMetaClass(),
                    METHOD_NAME_INITIALIZE,
                    new IRubyObject[]{
                            JavaEmbedUtils.javaToRuby(getRuntime(), getProcessor().getName()),
                            JavaEmbedUtils.javaToRuby(getRuntime(), getProcessor().getConfig())},
                    Block.NULL_BLOCK);
            // The Ruby initialize method may have changed the config, therefore copy it back
            // because the accessor is routed to the Java Processor.config
            getProcessor().setConfig(new RubyHashMapDecorator((RubyHash) getInstanceVariable(MEMBER_NAME_CONFIG)));
        } else {
            // First create only the instance passing in the block name
            setProcessor(
                    getProcessorClass()
                            .getConstructor(String.class)
                            .newInstance(
                                    RubyUtils.rubyToJava(getRuntime(), args[0], String.class)));

            // Then create the config hash that may contain config options defined in the Java constructor
            RubyHash config = RubyHashUtil.convertMapToRubyHashWithSymbolsIfNecessary(context.getRuntime(), getProcessor().getConfig());

            // Initialize the Ruby part and pass in the config options
            Helpers.invokeSuper(context, this, getMetaClass(), METHOD_NAME_INITIALIZE, new IRubyObject[] {args[0], config}, Block.NULL_BLOCK);

            // Reset the Java config options to the decorated Ruby hash, so that Java and Ruby work on the same config map
            getProcessor().setConfig(new RubyHashMapDecorator((RubyHash) getInstanceVariable(MEMBER_NAME_CONFIG)));
        }
        return null;
    }

    private RubyRegexp convertRegexp(Object regexp) {
        return RubyRegexp.newRegexp(getRuntime(), regexp.toString(), RegexpOptions.NULL_OPTIONS);
    }

    @JRubyMethod(name = "process", required = 3)
    public IRubyObject process(ThreadContext context, IRubyObject parent, IRubyObject target, IRubyObject attributes) {
        return JavaEmbedUtils.javaToRuby(
                getRuntime(),
                getProcessor().process(
                        (BlockNode) NodeConverter.createASTNode(parent),
                        RubyUtils.rubyToJava(getRuntime(), target, String.class),
                        RubyUtils.rubyToJava(getRuntime(), attributes, Map.class)));
    }

}
