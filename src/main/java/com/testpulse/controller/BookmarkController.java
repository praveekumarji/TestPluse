package com.testpulse.controller;

import com.testpulse.model.Bookmark;
import com.testpulse.service.BookmarkService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookmarks")
public class BookmarkController {

    private final BookmarkService bookmarkService;

    public BookmarkController(BookmarkService bookmarkService) {
        this.bookmarkService = bookmarkService;
    }

    @GetMapping
    public ResponseEntity<List<Bookmark>> getBookmarks(@RequestParam Long userId) {
        List<Bookmark> bookmarks = bookmarkService.getBookmarksByUserId(userId);
        return ResponseEntity.ok(bookmarks);
    }

    @PostMapping("/{questionId}")
    public ResponseEntity<Bookmark> addBookmark(@RequestParam Long userId, @PathVariable Long questionId) {
        Bookmark bookmark = bookmarkService.addBookmark(userId, questionId);
        return ResponseEntity.ok(bookmark);
    }

    @DeleteMapping("/{questionId}")
    public ResponseEntity<Void> removeBookmark(@RequestParam Long userId, @PathVariable Long questionId) {
        bookmarkService.removeBookmark(userId, questionId);
        return ResponseEntity.noContent().build();
    }
}
