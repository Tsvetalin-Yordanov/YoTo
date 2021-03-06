package com.example.yoto.model.video;

import com.example.yoto.model.user.UserSimpleResponseDTO;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
@AllArgsConstructor
@NoArgsConstructor
public class VideoDAO {

    @Autowired
    JdbcTemplate jdbcTemplate;


    public List<VideoSimpleResponseDTO> getOrderVideosByWatchedCount(String orderBy, int pageNumber, int rowNumbers) throws SQLException {
        StringBuilder queryFindAllVideosOrderByWatchers = new StringBuilder();
        queryFindAllVideosOrderByWatchers.append("SELECT video.id,video.title,video.upload_date,video.video_url,COUNT(uwv.user_id) AS views,")
                .append("video.user_id,user.first_name,user.last_name,user.about_me,user.profile_image_url ")
                .append("FROM videos AS video ")
                .append("JOIN users_watched_videos AS uwv ON uwv.video_id = video.id ")
                .append("JOIN users AS user ON video.user_id = user.id ")
                .append("GROUP BY uwv.video_id ")
                .append("UNION ")
                .append("SELECT ")
                .append("video.id,video.title,video.upload_date,video.video_url,0 as views,  ")
                .append("video.user_id,user.first_name,user.last_name,user.about_me,user.profile_image_url ")
                .append("FROM videos AS video ")
                .append("LEFT JOIN users_watched_videos AS uwv ON uwv.video_id = video.id ")
                .append("JOIN users AS user ON video.user_id = user.id ")
                .append("LEFT JOIN users_follow_users AS ufu ON user.id = ufu.publisher_id ")
                .append("WHERE uwv.user_id is null ")
                .append("ORDER BY views ")
                .append(orderBy.toUpperCase())
                .append(" LIMIT ")
                .append(rowNumbers)
                .append(" OFFSET ")
                .append((pageNumber * rowNumbers));

        StringBuilder queryUserByIdAndNumberVideos = new StringBuilder();
        queryUserByIdAndNumberVideos
                .append("SELECT u.id, COUNT(v.id) AS uploads ")
                .append("FROM users as U ")
                .append("JOIN videos AS v ON v.user_id = u.id ")
                .append("GROUP BY u.id ");

        StringBuilder queryUserIdAndNumberObservers = new StringBuilder();
        queryUserIdAndNumberObservers
                .append("SELECT publisher_id, COUNT(observer_id) AS observers ")
                .append("FROM users_follow_users ")
                .append("GROUP BY publisher_id;");


        List<VideoSimpleResponseDTO> videoSimpleResponseDTOS = jdbcTemplate.query(queryFindAllVideosOrderByWatchers.toString(), resultSet -> {
            List<VideoSimpleResponseDTO> simpleVideos = new ArrayList<>();

            while (resultSet.next()) {
                simpleVideos.add(builderSimpleVideo(resultSet));
            }
            return simpleVideos;
        });

        Map<Integer, Integer> uploadsVideos = new HashMap<>();
        jdbcTemplate.query(queryUserByIdAndNumberVideos.toString(), resultSet -> {
            while (resultSet.next()) {
                uploadsVideos.put(resultSet.getInt("id"), resultSet.getInt("uploads"));
            }
        });

        Map<Integer, Integer> followersUsers = new HashMap<>();
        jdbcTemplate.query(queryUserIdAndNumberObservers.toString(), resultSet -> {
            while (resultSet.next()) {
                followersUsers.put(resultSet.getInt("publisher_id"), resultSet.getInt("observers"));
            }
        });

        for (VideoSimpleResponseDTO videoSimpleResponseDTO : videoSimpleResponseDTOS) {
            int userId = videoSimpleResponseDTO.getUser().getId();
            videoSimpleResponseDTO.getUser().setVideos(uploadsVideos.get(userId) == null ? 0 : uploadsVideos.get(userId));
            videoSimpleResponseDTO.getUser().setFollowers(followersUsers.get(userId) == null ? 0 : followersUsers.get(userId));
        }

        return videoSimpleResponseDTOS;
    }

    private VideoSimpleResponseDTO builderSimpleVideo(ResultSet resultSet) throws SQLException {
        return VideoSimpleResponseDTO.builder()
                .id(resultSet.getInt("id"))
                .title(resultSet.getString("title"))
                .user(builderUserSimpleDto(resultSet))
                .uploadDate(resultSet.getTimestamp("upload_date").toLocalDateTime())
                .videoUrl(resultSet.getString("video_url"))
                .views(resultSet.getInt("views")).build();

    }

    private UserSimpleResponseDTO builderUserSimpleDto(ResultSet resultSet) throws SQLException {
        return UserSimpleResponseDTO.builder()
                .id(resultSet.getInt("user_id"))
                .firstName(resultSet.getString("first_name"))
                .lastName(resultSet.getString("last_name"))
                .aboutMe(resultSet.getString("about_me"))
                .profileImageUrl(resultSet.getString("profile_image_url")).build();
    }
}
