package com.boilerplate.boilerplate.domain.image.repository;

import com.boilerplate.boilerplate.domain.image.entity.Image;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {

    boolean existsByUrl(String url);

    Optional<Image> findByUrl(String url);
}
