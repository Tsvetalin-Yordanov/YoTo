package com.example.yoto.util;

import com.example.yoto.model.category.Category;
import com.example.yoto.model.category.CategoryRepository;
import com.example.yoto.model.comment.Comment;
import com.example.yoto.model.comment.CommentRepository;
import com.example.yoto.model.exceptions.NotFoundException;
import com.example.yoto.model.playList.PlayListRepository;
import com.example.yoto.model.playList.Playlist;
import com.example.yoto.model.relationship.commentsHaveComments.CommentHasCommentRepository;
import com.example.yoto.model.relationship.userReactToComments.UserReactToCommentRepository;
import com.example.yoto.model.relationship.userReactToVideo.UserReactToVideoRepository;
import com.example.yoto.model.user.User;
import com.example.yoto.model.user.UserRepository;
import com.example.yoto.model.video.Video;
import com.example.yoto.model.video.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class Util {
    public static final int USER_NAME_MAX_LENGTH = 200;
    public static final int USER_PASSWORD_MAX_LENGTH = 50;
    public static final int USER_EMAIL_MAX_LENGTH = 50;
    public static final String USER_PHONE_NUMBER_MAX_LENGTH =  "\\d{10}|(?:\\d{3}-){2}\\d{4}|\\(\\d{3}\\)\\d{3}-?\\d{4}";
    public static final int USER_ABOUT_ME_MAX_LENGTH = 2000;
    public static final int TITLE_MAX_LENGTH = 150;
    public static final int COMMENT_TXT_MAX_LENGTH = 2500;
    public static final String UPLOAD_FILES_DIRECTORY = "uploads";
    public static final String LOGGED = "logged";
    public static final String LOGGED_FROM = "logged_from";
    public static final String USER_ID = "user_id";
    public static final String ACCESS_TOKEN = "sl.BC1hN_74WzAM71DDrZAqV4tXP8BYOSGoB-3X5ZFTqWJD_BdajA76mo85F2qQ8xuwvg2gjUMrxqC6JfJs5R4RLpAYhiM2EEnDSJ-R-qJa2EbKiHGDae_jpYDvqjWlHR_8GhtlQAo";


    @Autowired
    public UserRepository userRepository;
    @Autowired
    public PlayListRepository playlistRepository;
    @Autowired
    public CategoryRepository categoryRepository;
    @Autowired
    public VideoRepository videoRepository;
    @Autowired
    public CommentRepository commentRepository;
    @Autowired
    public UserReactToVideoRepository userReactToVideoRepository;
    @Autowired
    public UserReactToCommentRepository userReactToCommentRepository;
    @Autowired
    public CommentHasCommentRepository commentHasCommentRepository;

    public User userGetById(int id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
    }

    public Playlist playlistGetById(int id) {
        return playlistRepository.findById(id).orElseThrow(() -> new NotFoundException("Playlist not found"));
    }

    public Category categoryGetById(int id) {
        return categoryRepository.findById(id).orElseThrow(() -> new NotFoundException("Category not found"));
    }

    public Video videoGetById(int id) {
        return videoRepository.findById(id).orElseThrow(() -> new NotFoundException("Video not found"));
    }

    public Comment commentGetById(int id) {
        return commentRepository.findById(id).orElseThrow(() -> new NotFoundException("Comment not found"));
    }

    public Integer getUserIdFromRequest(HttpServletRequest request) {
        return (int) request.getSession().getAttribute(USER_ID);
    }

}
