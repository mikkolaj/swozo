package com.swozo.api.web.user;

import com.swozo.persistence.user.UserFavouriteFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserFavouriteFileRepository extends JpaRepository<UserFavouriteFile, Long> {
}
