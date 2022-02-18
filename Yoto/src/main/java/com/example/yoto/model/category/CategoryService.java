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
import java.util.Optional;

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
            Optional<Category> optionalCategory = categoryRepository.findById(id);
            if (optionalCategory.isPresent()) {
                return optionalCategory.get();
            }
            throw new NotFoundException("Category not found");
        }
        throw new BadRequestException("Id is not positive");
    }

    public List<Category> getAll() {
        return categoryRepository.findAll();
    }

    public int followCategory(int cid, int uid) {
        Optional<Category> optionalCategory = categoryRepository.findById(cid);
        Optional<User> optionalUser = userRepository.findById(uid);
        if (optionalCategory.isPresent()) {
            if (optionalUser.isPresent()) {
                optionalCategory.get().getFollowersOfCategory().add(optionalUser.get());
                categoryRepository.save(optionalCategory.get());
                return optionalCategory.get().getFollowersOfCategory().size();
            }
            throw new NotFoundException("User not found");
        }
        throw new NotFoundException("Category not found");
    }


    public int unfollowCategory(int cid, int uid) {
        Optional<Category> optionalCategory = categoryRepository.findById(cid);
        Optional<User> optionalUser = userRepository.findById(uid);
        if (optionalCategory.isPresent()) {
            if (optionalUser.isPresent()) {
                optionalCategory.get().getFollowersOfCategory().remove(optionalUser.get());
                categoryRepository.save(optionalCategory.get());
                return optionalCategory.get().getFollowersOfCategory().size();
            }
            throw new NotFoundException("User not found");
        }
        throw new NotFoundException("Category not found");
    }

    public int addVideoInCategory(int vid, int cid, int uid) {
        Optional<Category> optionalCategory = categoryRepository.findById(cid);
        Optional<User> optionalUser = userRepository.findById(uid);
        Optional<Video> optionalVideo = videoRepository.findById(vid);
        if (optionalCategory.isPresent()) {
            if (optionalUser.isPresent()) {
                if (optionalVideo.isPresent()) {
                    optionalCategory.get().getVideosInCategory().add(optionalVideo.get());
                    categoryRepository.save(optionalCategory.get());
                    return optionalCategory.get().getVideosInCategory().size();
                }
                throw new NotFoundException("Video not found");
            }
            throw new NotFoundException("User not found");
        }
        throw new NotFoundException("Category not found");
    }

    public int removeVideoFromCategory(int vid, int cid, int uid) {
        Optional<Category> optionalCategory = categoryRepository.findById(cid);
        Optional<User> optionalUser = userRepository.findById(uid);
        Optional<Video> optionalVideo = videoRepository.findById(vid);
        if (optionalCategory.isPresent()) {
            if (optionalUser.isPresent()) {
                if (optionalVideo.isPresent()) {
                    optionalCategory.get().getVideosInCategory().remove(optionalVideo.get());
                    categoryRepository.save(optionalCategory.get());
                    return optionalCategory.get().getVideosInCategory().size();
                }
                throw new NotFoundException("Video not found");
            }
            throw new NotFoundException("User not found");
        }
        throw new NotFoundException("Category not found");
    }
}
