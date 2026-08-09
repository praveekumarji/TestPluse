package com.testpulse.service;

import com.testpulse.model.Bookmark;
import java.util.List;

public interface BookmarkService {
    Bookmark addBookmark(Long userId, Long questionId);
    void removeBookmark(Long userId, Long questionId);
    List<Bookmark> getBookmarksByUserId(Long userId);
}
