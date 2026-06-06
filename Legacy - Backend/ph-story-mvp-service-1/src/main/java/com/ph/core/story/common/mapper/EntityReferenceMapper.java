package com.ph.core.story.common.mapper;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;
import com.ph.core.story.domain.model.Categories;
import com.ph.core.story.domain.model.MediaFiles;
import com.ph.core.story.domain.model.Profiles;
import com.ph.core.story.user.domain.model.User;
import com.ph.core.story.domain.model.Stories;

@Component
public class EntityReferenceMapper {

    @PersistenceContext
    private EntityManager entityManager;

    @Named("mapUser")
    public User mapUser(Long id) {
        return id == null ? null : entityManager.getReference(User.class, id);
    }

    @Named("mapProfile")
    public Profiles mapProfile(Long id) {
        return id == null ? null : entityManager.getReference(Profiles.class, id);
    }

    @Named("mapCategory")
    public Categories mapCategory(Long id) {
        return id == null ? null : entityManager.getReference(Categories.class, id);
    }

    @Named("mapStory")
    public Stories mapStory(Long id) {
        return id == null ? null : entityManager.getReference(Stories.class, id);
    }

    @Named("mapMediaFiles")
    public MediaFiles mapMediaFiles(Long id) {
        return id == null ? null : entityManager.getReference(MediaFiles.class, id);
    }

}
