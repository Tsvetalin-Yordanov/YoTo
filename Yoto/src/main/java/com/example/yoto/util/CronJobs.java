package com.example.yoto.util;

import com.example.yoto.model.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Configuration
@EnableScheduling
public class CronJobs {

    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private Util util;

    @Scheduled(cron = "0 0 8 25 12 ?")
    public void scheduleChristmas() {
        List<User> users = util.userRepository.findAll();
        Set<String> emails = new HashSet<>();
        for (User user : users) {
            emails.add(user.getEmail());
        }

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom("kaltodor11@gmail.com");
        msg.setSubject("Christmas wish");
        msg.setText("The members of YoTo wish you Happy Christmas!");
        for (String email:emails) {
            msg.setTo(email);
            javaMailSender.send(msg);
        }
    }

    @Scheduled(cron = "0 0 0 1 1 ?")
    public void scheduleNewYear() {
        List<User> users = util.userRepository.findAll();
        Set<String> emails = new HashSet<>();
        for (User user : users) {
            emails.add(user.getEmail());
        }

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom("kaltodor11@gmail.com");
        msg.setSubject("New year wish");
        msg.setText("The members of YoTo wish you a Happy New Year!");
        for (String email:emails) {
            msg.setTo(email);
            javaMailSender.send(msg);
        }
    }

    @Scheduled(cron = "0 0 8 * * *")
    public void scheduleBirthDay() {
        List<User> users = util.userRepository.findAllUsersBirthDayToday();
        Set<String> emails = new HashSet<>();
        for (User user : users) {
            emails.add(user.getEmail());
        }

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom("kaltodor11@gmail.com");
        msg.setSubject("Birthday wish");
        msg.setText("The members of YoTo wish you Happy birthday!");
        for (String email:emails) {
            msg.setTo(email);
            javaMailSender.send(msg);
        }
    }
}
