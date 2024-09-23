package com.shudun.client.builder.processor;

import com.shudun.base.util.Tools;

/**
 * 增强处理器
 */
public abstract class EnhancedProcessor {

    /**
     * 下一个处理器
     */
    private EnhancedProcessor nextProcessor;

    /**
     * 是否有下一个处理器
     *
     * @return
     */
    public boolean hasNext() {
        return !Tools.isEmpty(this.nextProcessor);
    }

    /**
     * 设置下一个处理器
     *
     * @param nextProcessor
     */
    public void setNextProcessor(EnhancedProcessor nextProcessor) {
        this.nextProcessor = nextProcessor;
    }

    /**
     * 获取下一个处理器
     *
     * @return
     */
    public EnhancedProcessor nextProcessor() {
        return this.nextProcessor;
    }

    /**
     * 执行beginProcess
     *
     * @return
     */
    public ProcessRet executeBeginProcess() {
        EnhancedProcessor currentProcessor = this;
        ProcessRet ret = null;
        do {
            ret = currentProcessor.beginProcess();
            if (ret.isInterrupt()) {
                break;
            }
            currentProcessor = currentProcessor.nextProcessor();
        } while (currentProcessor.hasNext());
        return ret;
    }

    /**
     * beginProcess
     *
     * @return
     */
    public abstract ProcessRet beginProcess();

    /**
     * 执行beforeSendProcess
     *
     * @return
     */
    public ProcessRet executeBeforeSendProcess() {
        EnhancedProcessor currentProcessor = this;
        ProcessRet ret = null;
        do {
            ret = currentProcessor.beforeSendProcess();
            if (ret.isInterrupt()) {
                break;
            }
            currentProcessor = currentProcessor.nextProcessor();
        } while (currentProcessor.hasNext());
        return ret;
    }

    /**
     * beforeSendProcess
     *
     * @return
     */
    public abstract ProcessRet beforeSendProcess();

    /**
     * 执行afterSendProcess
     *
     * @return
     */
    public ProcessRet executeAfterSendProcess() {
        EnhancedProcessor currentProcessor = this;
        ProcessRet ret = null;
        do {
            ret = currentProcessor.afterSendProcess();
            if (ret.isInterrupt()) {
                break;
            }
            currentProcessor = currentProcessor.nextProcessor();
        } while (currentProcessor.hasNext());
        return ret;
    }

    /**
     * afterSendProcess
     *
     * @return
     */
    public abstract ProcessRet afterSendProcess();

}
