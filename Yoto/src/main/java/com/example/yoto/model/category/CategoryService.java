package com.example.yoto.model.category;

import com.example.yoto.model.exceptions.BadRequestException;
import com.example.yoto.model.user.User;
import com.example.yoto.model.video.Video;
import com.example.yoto.model.video.VideoService;
import com.example.yoto.model.video.VideoSimpleResponseDTO;
import com.example.yoto.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class CategoryService {

    @Autowired
    private Util util;

    public CategoryComplexResponseDTO getById(int id) {
        if (id > 0) {
            return categoryToCategoryComplexDTO(util.categoryGetById(id));
        }
        throw new BadRequestException("Id is not positive");
    }

    public List<CategoryComplexResponseDTO> getAll() {
        List<CategoryComplexResponseDTO> dtos = new ArrayList<>();
        List<Category> categories = util.categoryRepository.findAll();
        for (Category cat : categories) {
            dtos.add(categoryToCategoryComplexDTO(cat));
        }
        return dtos;
    }

    public int followCategory(int cid, int uid) {
        Category category = util.categoryGetById(cid);
        User user = util.userGetById(uid);

        category.getFollowersOfCategory().add(user);
        util.categoryRepository.save(category);
        return category.getFollowersOfCategory().size();

    }


    public int unfollowCategory(int cid, int uid) {
        Category category = util.categoryGetById(cid);
        User user = util.userGetById(uid);

        category.getFollowersOfCategory().remove(user);
        util.categoryRepository.save(category);
        return category.getFollowersOfCategory().size();

    }

    public int addVideoInCategory(int vid, int cid, int uid) {
        Category category =util.categoryGetById(cid);
        util.userGetById(uid);
        Video video = util.videoGetById(vid);

        category.getVideosInCategory().add(video);
        util.categoryRepository.save(category);
        return category.getVideosInCategory().size();

    }

    public int removeVideoFromCategory(int vid, int cid, int uid) {
        Category category = util.categoryGetById(cid);
        util.userGetById(uid);
        Video video = util.videoGetById(vid);

        category.getVideosInCategory().remove(video);
        util.categoryRepository.save(category);
        return category.getVideosInCategory().size();

    }


    private CategoryComplexResponseDTO categoryToCategoryComplexDTO(Category category) {
        Set<VideoSimpleResponseDTO> videos = new HashSet<>();
        for (Video video : category.getVideosInCategory()) {
            videos.add(VideoService.videoToSimpleDTO(video));
        }

        CategoryComplexResponseDTO dto = new CategoryComplexResponseDTO();
        dto.setId(category.getId());
        dto.setTitle(category.getTitle());
        dto.setCategoryImageUrl(category.getCategoryImageUrl());
        dto.setDescription(category.getDescription());
        dto.setCreateDate(category.getCreateDate());
        dto.setFollowers(category.getFollowersOfCategory().size());
        dto.setVideos(videos);

        return dto;
    }
}
