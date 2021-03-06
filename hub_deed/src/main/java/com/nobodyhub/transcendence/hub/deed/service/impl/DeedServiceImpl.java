package com.nobodyhub.transcendence.hub.deed.service.impl;

import com.nobodyhub.transcendence.common.merge.MergeUtils;
import com.nobodyhub.transcendence.hub.deed.client.PeopleHubClient;
import com.nobodyhub.transcendence.hub.deed.client.TopicHubClient;
import com.nobodyhub.transcendence.hub.deed.client.ZhihuAnswerApiClient;
import com.nobodyhub.transcendence.hub.deed.client.ZhihuArticleApiClient;
import com.nobodyhub.transcendence.hub.deed.repository.DeedRepository;
import com.nobodyhub.transcendence.hub.deed.service.DeedService;
import com.nobodyhub.transcendence.hub.domain.Deed;
import com.nobodyhub.transcendence.hub.domain.excerpt.DeedDataExcerpt;
import com.nobodyhub.transcendence.zhihu.domain.ZhihuAnswer;
import com.nobodyhub.transcendence.zhihu.domain.ZhihuArticle;
import com.nobodyhub.transcendence.zhihu.domain.ZhihuComment;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Optional;

import static com.nobodyhub.transcendence.hub.domain.Deed.DeedType.*;

@Service
public class DeedServiceImpl implements DeedService {
    private final DeedRepository deedRepository;

    private final TopicHubClient topicHubClient;
    private final PeopleHubClient peopleHubClient;
    private final ZhihuAnswerApiClient zhihuAnswerApiClient;
    private final ZhihuArticleApiClient zhihuArticleApiClient;

    public DeedServiceImpl(DeedRepository deedRepository,
                           TopicHubClient topicHubClient,
                           PeopleHubClient peopleHubClient,
                           ZhihuAnswerApiClient zhihuAnswerApiClient,
                           ZhihuArticleApiClient zhihuArticleApiClient) {
        this.deedRepository = deedRepository;
        this.topicHubClient = topicHubClient;
        this.peopleHubClient = peopleHubClient;
        this.zhihuAnswerApiClient = zhihuAnswerApiClient;
        this.zhihuArticleApiClient = zhihuArticleApiClient;
    }

    @Override
    public Deed save(ZhihuAnswer answer) {
        Optional<Deed> deed = this.deedRepository.findFirstByExcerptsContains(DeedDataExcerpt.of(ZHIHU_ANSWER, answer.getId()));
        if (deed.isPresent()) {
            Deed existDeed = deed.get();
            ZhihuAnswer exist = existDeed.getZhihuAnswer();
            exist = MergeUtils.merge(answer, exist);
            existDeed.setZhihuAnswer(exist);
            return deedRepository.save(existDeed);
        }
        return createNewDeedByZhihuAnswer(answer);
    }

    @Override
    public Deed find(ZhihuAnswer answer) {
        Optional<Deed> deed = this.deedRepository.findFirstByExcerptsContains(DeedDataExcerpt.of(ZHIHU_ANSWER, answer.getId()));
        return deed.orElseGet(() -> createNewDeedByZhihuAnswer(answer));
    }

    @Override
    public Optional<Deed> findByZhihuAnswerId(String answerId) {
        Optional<Deed> deed = this.deedRepository.findFirstByExcerptsContains(DeedDataExcerpt.of(ZHIHU_ANSWER, answerId));
        if (!deed.isPresent()) {
            zhihuAnswerApiClient.getAnswerById(answerId);
        }
        return deed;
    }

    private Deed createNewDeedByZhihuAnswer(ZhihuAnswer answer) {
        Deed deed = new Deed();
        deed.addExcerpt(DeedDataExcerpt.of(ZHIHU_ANSWER, answer.getId()));
        deed.setContent(answer.getContent());
        deed.setExcerpt(answer.getExcerpt());
        deed.setTopic(topicHubClient.getByZhihuQuestion(answer.getQuestion()));
        deed.setPeople(peopleHubClient.getByZhihuMember(answer.getAuthor()));
        deed.setCreatedAt(answer.getCreatedTime());
        deed.setZhihuAnswer(answer);
        return deed;
    }

    @Override
    public Deed save(ZhihuComment comment) {
        Optional<Deed> deed = this.deedRepository.findFirstByExcerptsContains(DeedDataExcerpt.of(ZHIHU_COMMENT, comment.getId()));
        if (deed.isPresent()) {
            Deed existDeed = deed.get();
            ZhihuComment exist = existDeed.getZhihuComment();
            exist = MergeUtils.merge(comment, exist);
            existDeed.setZhihuComment(exist);
            return deedRepository.save(existDeed);

        }
        return createNewDeedByZhihuComment(comment);
    }

    @Override
    public Deed find(ZhihuComment comment) {
        Optional<Deed> deed = this.deedRepository.findFirstByExcerptsContains(DeedDataExcerpt.of(ZHIHU_COMMENT, comment.getId()));
        return deed.orElseGet(() -> createNewDeedByZhihuComment(comment));
    }

    @Override
    public Optional<Deed> findByZhihuCommentId(String commentId) {
        Optional<Deed> deed = this.deedRepository.findFirstByExcerptsContains(DeedDataExcerpt.of(ZHIHU_COMMENT, commentId));
        if (!deed.isPresent()) {
            zhihuAnswerApiClient.getAnswerComments(commentId);
        }
        return deed;
    }

    private Deed createNewDeedByZhihuComment(ZhihuComment comment) {
        Deed deed = new Deed();
        deed.setContent(comment.getContent());
        deed.addExcerpt(DeedDataExcerpt.of(ZHIHU_COMMENT, comment.getId()));
        deed.setExcerpt(comment.getContent());
        // topic is null, it should belong to answer, which is specified by the parentId
        deed.setPeople(peopleHubClient.getByZhihuMember(comment.getAuthor().getMember()));
        deed.setCreatedAt(comment.getCreatedTime());
        deed.setZhihuComment(comment);
        deed.setParentId(comment.getParentId());
        return deed;
    }

    @Override
    public List<Deed> findByTopicName(String name, Pageable pageable) {
        return deedRepository.findByTopic_NameOrderByCreatedAtDesc(name, pageable);
    }

    @Override
    public List<Deed> findByPeopleName(String name, Pageable pageable) {
        return deedRepository.findByPeople_NameOrderByCreatedAtDesc(name, pageable);
    }

    @Override
    public List<Deed> findAllChildren(String parentId, Pageable pageable) {
        return deedRepository.findByParentIdOrderByCreatedAt(parentId, pageable);
    }

    @Override
    public Deed save(ZhihuArticle article) {
        Optional<Deed> deed = this.deedRepository.findFirstByExcerptsContains(DeedDataExcerpt.of(ZHIHU_ARTICLE, article.getId()));
        if (deed.isPresent()) {
            Deed existDeed = deed.get();
            ZhihuArticle exist = existDeed.getZhihuArticle();
            exist = MergeUtils.merge(article, exist);
            existDeed.setZhihuArticle(exist);
            return deedRepository.save(existDeed);
        }
        return createNewDeedByZhihuArticle(article);
    }

    @Override
    public Deed find(ZhihuArticle article) {
        Optional<Deed> deed = this.deedRepository.findFirstByExcerptsContains(DeedDataExcerpt.of(ZHIHU_ARTICLE, article.getId()));
        return deed.orElseGet(() -> createNewDeedByZhihuArticle(article));
    }

    @Override
    public Optional<Deed> findByZhihuArticleId(String articleId) {
        Optional<Deed> deed = this.deedRepository.findFirstByExcerptsContains(DeedDataExcerpt.of(ZHIHU_ARTICLE, articleId));
        if (!deed.isPresent()) {
            zhihuArticleApiClient.getArticleById(articleId);
        }
        return deed;
    }

    private Deed createNewDeedByZhihuArticle(ZhihuArticle article) {
        Deed deed = new Deed();
        deed.setContent(article.getExcerpt());
        deed.addExcerpt(DeedDataExcerpt.of(ZHIHU_ARTICLE, article.getId()));
        deed.setExcerpt(article.getExcerpt());
        deed.setTopic(topicHubClient.getByZhihuColumn(article.getColumn()));
        deed.setPeople(peopleHubClient.getByZhihuMember(article.getAuthor()));
        deed.setCreatedAt(article.getCreated());
        deed.setZhihuArticle(article);
        return deed;
    }
}
