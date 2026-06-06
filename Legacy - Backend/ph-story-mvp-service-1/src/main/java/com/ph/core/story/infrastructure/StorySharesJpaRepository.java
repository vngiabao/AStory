package com.ph.core.story.infrastructure;

import com.ph.core.story.application.query.dto.StorySharedHistoryItemResponse;
import com.ph.core.story.application.query.dto.StorySharedItemResponse;
import com.ph.core.story.domain.model.StoryShares;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StorySharesJpaRepository
        extends JpaRepository<StoryShares, Long>, JpaSpecificationExecutor<StoryShares> {
    @Query("""
                SELECT new com.ph.core.story.application.query.dto.StorySharedItemResponse(
                    ss.id,
                    s.id,
                    ss.sharedUser.id,
                    s.title,
                    s.content,
                    s.createdDate,
                    u.id,
                    u.username,
                    p.fullname,
                    ss.createdDate
                )
                FROM StoryShares ss
                JOIN ss.story s
                JOIN s.user u
                JOIN s.profile p
                WHERE ss.sharedUser.id = :userId
                  AND ss.deleted = false
                  AND s.deleted = false
                  AND (cast(:keyword as text) IS NULL OR LOWER(s.title) LIKE LOWER(CONCAT('%', cast(:keyword as text), '%')))
                ORDER BY ss.createdDate DESC
            """)
    Page<StorySharedItemResponse> findReceivedStories(
            @Param("userId") Long userId,
            @Param("keyword") String keyword,
            Pageable pageable);

    @Query("""
                SELECT new com.ph.core.story.application.query.dto.StorySharedHistoryItemResponse(
                    ss.id,
                    s.id,
                    s.title,
                    s.content,
                    s.createdDate,
                    s.modifiedDate,
                    recipientUser.id,
                    recipientUser.username,
                    recipientProfile.fullname,
                    recipientUser.email,
                    COALESCE(contact.preferenceName, recipientProfile.fullname, recipientUser.username),
                    COALESCE(recipientProfile.fullname, recipientUser.email, recipientUser.username),
                    ss.createdDate
                )
                FROM StoryShares ss
                JOIN ss.story s
                JOIN s.user senderUser
                JOIN ss.sharedUser recipientUser
                LEFT JOIN Profiles recipientProfile
                    ON recipientProfile.user.id = recipientUser.id
                   AND recipientProfile.deleted = false
                LEFT JOIN Contacts contact
                    ON contact.user.id = senderUser.id
                   AND contact.profile.user.id = recipientUser.id
                   AND contact.deleted = false
                WHERE senderUser.id = :userId
                  AND ss.deleted = false
                  AND s.deleted = false
                  AND recipientUser.deleted = false
                  AND (
                        cast(:keyword as text) IS NULL
                        OR LOWER(COALESCE(s.title, '')) LIKE LOWER(CONCAT('%', cast(:keyword as text), '%'))
                        OR LOWER(COALESCE(contact.preferenceName, '')) LIKE LOWER(CONCAT('%', cast(:keyword as text), '%'))
                        OR LOWER(COALESCE(recipientProfile.fullname, '')) LIKE LOWER(CONCAT('%', cast(:keyword as text), '%'))
                        OR LOWER(COALESCE(recipientUser.username, '')) LIKE LOWER(CONCAT('%', cast(:keyword as text), '%'))
                        OR LOWER(COALESCE(recipientUser.email, '')) LIKE LOWER(CONCAT('%', cast(:keyword as text), '%'))
                  )
                ORDER BY ss.createdDate DESC
            """)
    Page<StorySharedHistoryItemResponse> findSharedHistory(
            @Param("userId") Long userId,
            @Param("keyword") String keyword,
            Pageable pageable);

}
