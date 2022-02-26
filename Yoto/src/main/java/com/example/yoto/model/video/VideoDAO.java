package com.example.yoto.model.video;

import com.example.yoto.model.user.UserSimpleResponseDTO;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
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

        String queryFindAllVideosOrderByWatchers = "SELECT " +
                "video.id,video.title,video.upload_date,video.video_url,COUNT(uwv.user_id) AS views," +
                "video.user_id,user.first_name,user.last_name,user.about_me,user.profile_image_url " +
                "FROM videos AS video " +
                "JOIN users_watched_videos AS uwv ON uwv.video_id = video.id " +
                "JOIN users AS user ON video.user_id = user.id " +
                "GROUP BY uwv.video_id " +
                "UNION " +
                "SELECT " +
                "video.id,video.title,video.upload_date,video.video_url,0 as views,  " +
                "video.user_id,user.first_name,user.last_name,user.about_me,user.profile_image_url " +
                "FROM videos AS video " +
                "LEFT JOIN users_watched_videos AS uwv ON uwv.video_id = video.id " +
                "JOIN users AS user ON video.user_id = user.id " +
                "LEFT JOIN users_follow_users AS ufu ON user.id = ufu.publisher_id " +
                "WHERE uwv.user_id is null " +
                "ORDER BY views " + orderBy.toUpperCase() + " LIMIT " + rowNumbers + " OFFSET " + ((pageNumber-1)*rowNumbers);


        String getUserByIdAndNumberVideos = "SELECT " +
                "u.id, " +
                "COUNT(v.id) AS uploads " +
                "FROM users as U " +
                "JOIN videos AS v ON v.user_id = u.id " +
                "GROUP BY u.id ";

        String getUserIdAndNumberObservers = "SELECT publisher_id, COUNT(observer_id) AS observers FROM users_follow_users" +
                " GROUP BY publisher_id;";


        List<VideoSimpleResponseDTO> videoSimpleResponseDTOS = jdbcTemplate.query(queryFindAllVideosOrderByWatchers, resultSet -> {
            List<VideoSimpleResponseDTO> simpleVideos = new ArrayList<>();

            while (resultSet.next()) {
                simpleVideos.add(builderSimpleVideo(resultSet));
            }
            return simpleVideos;
        });

        Map<Integer, Integer> uploadsVideos = new HashMap<>();
        jdbcTemplate.query(getUserByIdAndNumberVideos, resultSet -> {
            while (resultSet.next()) {
                uploadsVideos.put(resultSet.getInt("id"), resultSet.getInt("uploads"));
            }
        });

        Map<Integer, Integer> followersUsers = new HashMap<>();
        jdbcTemplate.query(getUserIdAndNumberObservers, resultSet -> {
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
