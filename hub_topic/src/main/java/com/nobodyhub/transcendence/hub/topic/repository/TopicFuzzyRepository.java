package com.nobodyhub.transcendence.hub.topic.repository;

import com.nobodyhub.transcendence.hub.domain.Topic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface TopicFuzzyRepository extends PagingAndSortingRepository<Topic, String> {
    /**
     * find topic by name contains given keyword
     *
     * @param name
     * @return
     */
    Page<Topic> findByNameLike(String name, Pageable pageable);
}
