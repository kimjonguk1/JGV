package dev.jwkim.jgv.controlles.user;

import dev.jwkim.jgv.entities.user.ReviewEntity;
import dev.jwkim.jgv.entities.user.UserEntity;
import dev.jwkim.jgv.results.CommonResult;
import dev.jwkim.jgv.results.user.LikedResult;
import dev.jwkim.jgv.results.user.ReviewResult;
import dev.jwkim.jgv.services.user.ReviewService;
import jakarta.servlet.http.HttpSession;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Set;

@Controller
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    //평점 작성
    @RequestMapping(value = "/write", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String postWrite(@RequestBody ReviewEntity review, HttpSession session) {
        UserEntity loggedInUser = (UserEntity) session.getAttribute("user");
        if(loggedInUser == null) {
            JSONObject obj = new JSONObject();
            obj.put("result", ReviewResult.NOT_LOGGED_IN);
            return obj.toString();
        }

        // 이미 평점을 작성했는지 확인
        boolean hasWritten = reviewService.hasUserWrittenReview(review.getMoNum(), loggedInUser.getUsNum());
        if (hasWritten) {
            JSONObject obj = new JSONObject();
            obj.put("result", ReviewResult.ALREADY_WRITTEN);
            return obj.toString();
        }

        review.setUsNum(loggedInUser.getUsNum());
        ReviewResult result = this.reviewService.postReview(review);
        JSONObject response = new JSONObject();
        response.put("result", result);
        return response.toString();
    }

    //좋아요 카운트 증가
    @RequestMapping(value = "/like/{reviewId}", method = RequestMethod.POST)
    @ResponseBody
    public LikedResult postLike(@PathVariable("reviewId") int reviewId, HttpSession session) {
        UserEntity loggedInUser = (UserEntity) session.getAttribute("user");
        if(loggedInUser == null) {
            return LikedResult.NOT_LOGGED_IN;
        }

        // 세션에서 좋아요 기록 확인
        @SuppressWarnings("unchecked")
        Set<Integer> likedReviews = (Set<Integer>) session.getAttribute("likedReviews");
        if (likedReviews == null) {
            likedReviews = new HashSet<>();
            session.setAttribute("likedReviews", likedReviews);
        }

        // 중복 좋아요 방지
        if (likedReviews.contains(reviewId)) {
            return LikedResult.ALREADY_LIKED; // 이미 좋아요를 누른 경우
        }

        boolean result = this.reviewService.incrementLike(reviewId);
        if(result) {
            likedReviews.add(reviewId);
            return LikedResult.SUCCESS;
        } else {
            return LikedResult.FAILURE;
        }
    }


    //수정
    @RequestMapping(value = "/{reviewId}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ReviewResult updateReview(@PathVariable("reviewId") int reviewId, @RequestBody ReviewEntity updatedReview, HttpSession session) {
        UserEntity loggedInUser = (UserEntity) session.getAttribute("user");
        if (loggedInUser == null) {
            return ReviewResult.NOT_LOGGED_IN;
        }
        //어드민이 아닐 경우
        if(!loggedInUser.isUsIsAdmin()) {
            ReviewEntity existingReview = this.reviewService.getReviewsById(reviewId);
            if (existingReview == null || existingReview.getUsNum() != loggedInUser.getUsNum()) {
                return ReviewResult.UNAUTHORIZED;
            }
        }
        updatedReview.setReNum(reviewId);
        boolean success = this.reviewService.updateReview(updatedReview);
        return success ? ReviewResult.SUCCESS : ReviewResult.FAILURE;
    }

    //삭제
    @RequestMapping(value = "/{reviewId}", method = RequestMethod.PATCH, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ReviewResult deleteReview(@PathVariable("reviewId") int reviewId, HttpSession session) {
        UserEntity loggedInUser = (UserEntity) session.getAttribute("user");
        if (loggedInUser == null) {
            return ReviewResult.NOT_LOGGED_IN;
        }
        if(!loggedInUser.isUsIsAdmin()) {
            ReviewEntity existingReview = this.reviewService.getReviewsById(reviewId);
            if (existingReview == null || existingReview.getUsNum() != loggedInUser.getUsNum()) {
                return ReviewResult.UNAUTHORIZED;
            }
        }
        boolean success = this.reviewService.deleteReview(reviewId);
        return success ? ReviewResult.SUCCESS : ReviewResult.FAILURE;
    }


}
