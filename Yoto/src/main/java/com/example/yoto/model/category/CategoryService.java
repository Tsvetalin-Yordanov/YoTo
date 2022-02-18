package com.example.yoto.model.category;

import com.example.yoto.model.exceptions.BadRequestException;
import com.example.yoto.model.exceptions.NotFoundException;
import com.example.yoto.model.user.User;
import com.example.yoto.model.user.UserRepository;
import com.example.yoto.model.video.Video;
import com.example.yoto.model.video.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private VideoRepository videoRepository;

    public Category getById(int id) {
        if (id > 0) {
            return getCategoryById(id);
        }
        throw new BadRequestException("Id is not positive");
    }

    public List<Category> getAll() {
        return categoryRepository.findAll();
    }

    public int followCategory(int cid, int uid) {
        Category category = getCategoryById(cid);
        User user = getUserById(uid);

        category.getFollowersOfCategory().add(user);
        categoryRepository.save(category);
        return category.getFollowersOfCategory().size();

    }


    public int unfollowCategory(int cid, int uid) {
        Category category = getCategoryById(cid);
        User user = getUserById(uid);

        category.getFollowersOfCategory().remove(user);
        categoryRepository.save(category);
        return category.getFollowersOfCategory().size();

    }

    public int addVideoInCategory(int vid, int cid, int uid) {
        Category category = getCategoryById(cid);
        User user = getUserById(uid);
        Video video = getVideoById(vid);

        category.getVideosInCategory().add(video);
        categoryRepository.save(category);
        return category.getVideosInCategory().size();

    }

    public int removeVideoFromCategory(int vid, int cid, int uid) {
        Category category = getCategoryById(cid);
        User user = getUserById(uid);
        Video video = getVideoById(vid);

        category.getVideosInCategory().remove(video);
        categoryRepository.save(category);
        return category.getVideosInCategory().size();

    }

    private Category getCategoryById(int id) {
        return categoryRepository.findById(id).orElseThrow(() -> new NotFoundException("Category not found"));
    }

    private User getUserById(int id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
    }

    private Video getVideoById(int id) {
        return videoRepository.findById(id).orElseThrow(() -> new NotFoundException("Video not found"));
    }
}
