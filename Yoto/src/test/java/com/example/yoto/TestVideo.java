package com.example.yoto;

import com.example.yoto.model.user.User;
import com.example.yoto.model.video.Video;
import com.example.yoto.model.video.VideoRepository;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class TestVideo {

    @InjectMocks
    public Video video;

    @Mock
   public VideoRepository videoRepository;

    @Before
    public  void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetAllVideos()
    {
        List<Video> list = new ArrayList<>();
        Video first = new Video(1, "Test 0 video", new User(), LocalDateTime.now(),"gregreggfbrvideo.mp4",true);
        Video second = new Video(2, "Test 1 video", new User(), LocalDateTime.now(),"gregregg4t4fbrvideo.mp4",false);
        Video third = new Video(45, "Test 2 video", new User(), LocalDateTime.now(),"gregregg43fbrvideo.mp4",false);

        list.add(first);
        list.add(second);
        list.add(third);

        when(videoRepository.findAll()).thenReturn(list);

        //videoRepository
        List<Video> videos = videoRepository.findAll();

        assertEquals(3, videos.size());
        verify(videoRepository, times(1)).findAll();
    }

}
