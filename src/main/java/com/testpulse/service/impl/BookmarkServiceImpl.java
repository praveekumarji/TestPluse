package com.testpulse.service.impl;

import com.testpulse.model.Bookmark;
import com.testpulse.repository.BookmarkRepository;
import com.testpulse.service.BookmarkService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookmarkServiceImpl implements BookmarkService {

    private final BookmarkRepository bookmarkRepository;

    public BookmarkServiceImpl(BookmarkRepository bookmarkRepository) {
        this.bookmarkRepository = bookmarkRepository;
    }

    @Override
    @CacheEvict(value = "bookmarks", key = "'user:' + #userId")
    public Bookmark addBookmark(Long userId, Long questionId) {
        Bookmark bookmark = null;
        return bookmarkRepository.save(bookmark);
    }

    @Override
    @CacheEvict(value = "bookmarks", key = "'user:' + #userId")
    public void removeBookmark(Long userId, Long questionId) {
        // Implement logic to remove bookmark
    }

    @Override
    @Cacheable(value = "bookmarks", key = "'user:' + #userId")
    public List<Bookmark> getBookmarksByUserId(Long userId) {
        // Implement logic to fetch bookmarks by user ID
        return bookmarkRepository.findAll();
    }
}
