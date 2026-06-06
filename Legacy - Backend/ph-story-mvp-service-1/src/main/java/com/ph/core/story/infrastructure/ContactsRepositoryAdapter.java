package com.ph.core.story.infrastructure;

import com.ph.core.story.common.base.BaseRepositoryAdapter;
import com.ph.core.story.domain.model.Contacts;
import com.ph.core.story.domain.repository.ContactsRepositoryPort;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class ContactsRepositoryAdapter extends BaseRepositoryAdapter<Contacts, Long>
        implements ContactsRepositoryPort {

    private final ContactsJpaRepository repository;

    public ContactsRepositoryAdapter(ContactsJpaRepository repository) {
        super(repository);
        this.repository = repository;
    }

    @Override
    public Optional<Contacts> findById(Long id) {
        return repository.findById(id).filter(contact -> !contact.isDeleted());
    }

    @Override
    public Page<Contacts> findAll(Pageable pageable) {
        return repository.findAll(
                (root, query, cb) -> cb.isFalse(root.get("deleted")),
                pageable);
    }

    @Override
    public Page<Contacts> searchByKeyword(String keyword, Long categoryId, Long userId, Pageable pageable) {
        return repository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. Luôn lọc deleted = false
            predicates.add(cb.isFalse(root.get("deleted")));

            // 2. Lọc theo UserID sở hữu contact
            if (userId != null) {
                predicates.add(cb.equal(root.get("user").get("id"), userId));
            }

            // 3.Lọc theo Category (Nếu có chọn)
            if (categoryId != null) {
                predicates.add(cb.equal(root.get("category").get("id"), categoryId));
            }

            // 4. Logic OR cho keyword
            if (keyword != null && !keyword.trim().isEmpty()) {
                String pattern = "%" + keyword.trim().toLowerCase() + "%";

                // Search trên trường preferenceName
                Predicate prefPredicate = cb.like(cb.lower(root.get("preferenceName")), pattern);

                // Search trên trường profile.fullname (cần join sang bảng profile)
                Join<Object, Object> profileJoin = root.join("profile");
                Predicate namePredicate = cb.like(cb.lower(profileJoin.get("fullname")), pattern);

                // Search trên phoneNumber (Bổ sung theo yêu cầu của bạn)
                Predicate phonePredicate = cb.like(cb.lower(profileJoin.get("phoneNumber")), pattern);

                // Kết hợp: (preferenceName LIKE ... OR profile.fullname LIKE ...)
                predicates.add(cb.or(prefPredicate, namePredicate, phonePredicate));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        }, pageable);

    }

    @Override
    public void clearCategoryByCategoryId(Long categoryId) {
        repository.clearCategoryByCategoryId(categoryId);
    }
}
