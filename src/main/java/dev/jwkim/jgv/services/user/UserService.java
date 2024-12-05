package dev.jwkim.jgv.services.user;

import dev.jwkim.jgv.entities.user.UserEntity;
import dev.jwkim.jgv.results.Result;
import jakarta.mail.MessageRemovedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

//    @Transactional
//    public Result register(UserEntity user) throws MessageRemovedException {
//        if (user == null ||
//        user.getUsName() == null || user.getUsName().isEmpty() || user.getUsName().length() < 2 ||
//                user.getUsName().length() > 15 || user.getUsId() == null || user.getUsId().isEmpty() || user.getUsId().length() < 2 ||  user.getUsId().length() > 20 ||
//                user.getUsPw() == null || user.getUsPw().isEmpty() || user.getUsPw().length() < 8 || user.getUsPw().length() > 100 || user.getUsBirth() == null || user.getUsContact() == null || user.getUsContact().isEmpty() || user.getUsContact().length() < 10 || user.getUsContact().length() > 13 || user.getUsEmail() == null || user.getUsEmail().isEmpty() || user.getUsEmail().length() < 8 || user.getUsEmail().length() > 50 || user.getUsGender() == null);
//
//        ) {}
//    }
}

