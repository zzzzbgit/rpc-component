package com.leyi.client.builder.processor;

/**
 * 构建处理器链
 */
public class ProcessorChainBuilder {

    /**
     * 头节点
     */
    private EnhancedProcessor head;

    /**
     * 私有构造函数
     *
     * @param head
     */
    private ProcessorChainBuilder(EnhancedProcessor head) {
        this.head = head;
    }

    /**
     * 构建处理器链
     *
     * @param head
     * @return
     */
    public static ProcessorChainBuilder newInstance(EnhancedProcessor head) {
        return new ProcessorChainBuilder(head);
    }

    /**
     * 构建处理器链
     *
     * @return
     */
    public EnhancedProcessor build() {
        return this.head;
    }

    /**
     * 添加处理器
     *
     * @param processor
     * @return
     */
    public ProcessorChainBuilder addProcessor(EnhancedProcessor processor) {
        if (!head.hasNext()) {
            head.setNextProcessor(processor);
        } else {
            addProcessorToTail(processor);
        }
        return this;
    }

    /**
     * 添加处理器到尾部
     *
     * @param processor
     */
    private void addProcessorToTail(EnhancedProcessor processor) {
        EnhancedProcessor current = head;
        while (current.hasNext()) {
            current = current.nextProcessor();
        }
        current.setNextProcessor(processor);
    }

}
