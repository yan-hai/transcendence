package com.nobodyhub.transcendence.zhihu.common.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nobodyhub.transcendence.api.common.converter.ApiResponseConverter;
import com.nobodyhub.transcendence.api.common.executor.ApiAsyncExecutor;
import com.nobodyhub.transcendence.api.common.kafka.KafkaHeaderHandler;
import com.nobodyhub.transcendence.api.common.message.ApiChannel;
import com.nobodyhub.transcendence.api.common.message.ApiChannelBaseService;
import com.nobodyhub.transcendence.message.ApiRequestMessage;
import com.nobodyhub.transcendence.zhihu.common.domain.ZhihuApiPaging;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
public abstract class ZhihuApiChannelBaseService<T extends ApiChannel> extends ApiChannelBaseService<T> {
    protected final ObjectMapper objectMapper;

    protected ZhihuApiChannelBaseService(T channel,
                                         ApiResponseConverter converter,
                                         ApiAsyncExecutor apiAsyncExecutor,
                                         KafkaHeaderHandler headerHandler,
                                         ObjectMapper objectMapper) {
        super(channel, converter, apiAsyncExecutor, headerHandler);
        this.objectMapper = objectMapper;
    }

    /**
     * create new request message from the paging information
     *
     * @param paging
     * @param targetChannel
     * @return
     */
    protected Optional<ApiRequestMessage> getNextUrl(ZhihuApiPaging paging, String targetChannel) {
        if (paging != null
            && paging.getNext() != null
            && !paging.getIsEnd()) {
            return Optional.of(new ApiRequestMessage(targetChannel, paging.getNext()));
        }
        return Optional.empty();
    }
}
