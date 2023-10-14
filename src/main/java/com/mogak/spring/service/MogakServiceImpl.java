package com.mogak.spring.service;

import com.mogak.spring.converter.JogakConverter;
import com.mogak.spring.converter.MogakConverter;
import com.mogak.spring.domain.common.State;
import com.mogak.spring.domain.mogak.Mogak;
import com.mogak.spring.domain.mogak.MogakCategory;
import com.mogak.spring.domain.jogak.JogakPeriod;
import com.mogak.spring.domain.jogak.Period;
import com.mogak.spring.domain.post.Post;
import com.mogak.spring.domain.user.User;
import com.mogak.spring.exception.MogakException;
import com.mogak.spring.exception.UserException;
import com.mogak.spring.global.ErrorCode;
import com.mogak.spring.repository.*;
import com.mogak.spring.web.dto.MogakRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MogakServiceImpl implements MogakService {
    private final UserRepository userRepository;
    private final MogakRepository mogakRepository;
//    private final MogakPeriodRepository mogakPeriodRepository;
    private final PeriodRepository periodRepository;
    private final MogakCategoryRepository categoryRepository;
    private final JogakRepository jogakRepository;
    private final PostRepository postRepository;
    private final PostImgRepository postImgRepository;
    private final PostCommentRepository postCommentRepository;

    /**
     * 모각 생성
     * */
    // throw 추상화, 공통으로 뽑아내거나 private으로 고유하게 구현
    @Transactional
    @Override
    public Mogak create(Long userId, MogakRequestDto.CreateDto request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserException(ErrorCode.NOT_EXIST_USER));
        MogakCategory category = categoryRepository.findMogakCategoryByName(request.getCategory()).orElseThrow(() -> new MogakException(ErrorCode.NOT_EXIST_CATEGORY));

        String otherCategory = request.getOtherCategory();
        if (category.getName().equals("기타") && otherCategory == null) {
            throw new MogakException(ErrorCode.NOT_EXIST_OTHER_CATEGORY);
        }
        State state = State.registerState(request.getStartAt(), request.getEndAt(), LocalDate.now());
        Mogak result = mogakRepository.save(MogakConverter.toMogak(request, category, otherCategory, user, state));

//        List<Period> periods = saveMogakPeriod(request.getDays(), result);
//        createTodayJogak(result, periods, Weeks.getTodayNum());
        return result;
    }

    private void createTodayJogak(Mogak result, List<Period> periods, int dayNum) {
        if (periods.stream().anyMatch(day -> day.getId() == dayNum) && result.getState().equals(State.ONGOING.name())) {
            jogakRepository.save(JogakConverter.toJogak(result));
        }
    }

//    /**
//     * 모각주기 저장 메소드
//     * */
//    private List<Period> saveMogakPeriod(List<String> days, Mogak mogak) {
//        List<Period> periods = new ArrayList<>();
//        for (String day : days) {
//            periods.add(periodRepository.findOneByDays(day)
//                    .orElseThrow(() -> new MogakException(ErrorCode.NOT_EXIST_DAY)));
//        }
//        for (Period period: periods) {
//            JogakPeriod jogakPeriod = JogakPeriod.builder()
//                    .period(period)
//                    .mogak(mogak)
//                    .build();
//            mogakPeriodRepository.save(jogakPeriod);
//        }
//        return periods;
//    }

    /**
     * 모각주기 업데이트 메소드
     * */
//    private void updateMogakPeriod(List<String> days, Mogak mogak) {
//        List<Period> periods = new ArrayList<>();
//        for (String day : days) {
//            periods.add(periodRepository.findOneByDays(day)
//                    .orElseThrow(() -> new MogakException(ErrorCode.NOT_EXIST_DAY)));
//        }
//        List<JogakPeriod> jogakPeriods = mogakPeriodRepository.findAllByMogak_Id(mogak.getId());
//        int periodSize = periods.size();
//        int mpSize = jogakPeriods.size();
//
//        IntStream.range(0, Math.min(mpSize, periodSize))
//                .forEach(i -> jogakPeriods.get(i).updatePeriod(periods.get(i)));
//        if (mpSize > periodSize) {
//            IntStream.range(periodSize, mpSize)
//                    .forEach(i -> mogakPeriodRepository.delete(jogakPeriods.get(i)));
//        } else {
////            IntStream.range(mpSize, periodSize)
////                    .forEach(i -> mogakPeriodRepository.save(JogakPeriod.of(periods.get(i), mogak)));
//        }
//    }

    /**
     * 모각 미리 달성 메소드
     * */
    @Transactional
    @Override
    public Mogak achieveMogak(Long mogakId) {
        Mogak mogak = mogakRepository.findById(mogakId)
                .orElseThrow(() -> new MogakException(ErrorCode.NOT_EXIST_MOGAK));
        if (!mogak.getState().equals("ONGOING")) {
            throw new MogakException(ErrorCode.WRONG_STATE_CHANGE);
        }
        mogak.updateState(State.COMPLETE.toString());
        return mogak;
    }

    /**
     * 모각 업데이트 메소드
     * */
    @Transactional
    @Override
    public Mogak updateMogak(MogakRequestDto.UpdateDto request) {
        List<String> days = request.getDays();
        Mogak mogak = mogakRepository.findById(request.getMogakId())
                .orElseThrow(() -> new MogakException(ErrorCode.NOT_EXIST_MOGAK));
        Optional<String> categoryOptional = Optional.ofNullable(request.getCategory());
        if (categoryOptional.isPresent()) {
            MogakCategory category = categoryRepository.findMogakCategoryByName(request.getCategory())
                    .orElseThrow(() -> new MogakException(ErrorCode.NOT_EXIST_CATEGORY));
            if (category.getName().equals("기타")) {
                if (request.getOtherCategory() == null) {
                    throw new MogakException(ErrorCode.NOT_EXIST_OTHER_CATEGORY);
                }
                mogak.updateOtherCategory(request.getOtherCategory());
            }
            mogak.updateCategory(category);
        }
        mogak.updateFromDto(request);
//        Optional.ofNullable(days).ifPresent(d -> updateMogakPeriod(d, mogak));
        return mogak;
    }

    /**
     * 모각 조회(페이징)
     * */
    @Override
    public List<Mogak> getMogakList(Long userId, int cursor, int size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ErrorCode.NOT_EXIST_USER));
        PageRequest pageRequest = PageRequest.of(cursor, size);
        return mogakRepository.findAllByUserIdOrderByCreatedAtDesc(user.getId(), pageRequest);
    }

    /**
     * 진행중이고 해당 날의 모각들 불러오기
     * */
//    @Override
//    public List<Mogak> getOngoingTodayMogakList(int today) {
//        return mogakRepository.findAllOngoingToday(State.ONGOING.name(), today);
//    }

//    /**
//     * 모각 결과 내리기
//     * 4시에 동작
//     * 자정 전에 시작했던 조각들 때문
//     * */
//    @Transactional
//    @Override
//    public void judgeMogakByDay(LocalDate day) {
//        List<Mogak> mogaks = mogakRepository.findAllByEndAt(day);
//        for (Mogak mogak: mogaks) {
////            List<Jogak> jogaks = jogakRepository.findAllByMogak(mogak);
////            double achievementRate = getAcheiveRate(jogaks);
////            judgeMogak(mogak, achievementRate);
//        }
//    }

//    private double getAcheiveRate(List<Jogak> jogaks) {
//        int success = 0;
//        for (Jogak jogak: jogaks) {
//            if (jogak.getState().equals(JogakState.SUCCESS.name())) {
//                success += 1;
//            }
//        }
//        return (double) success / jogaks.size() * 100;
//    }
//
//    private void judgeMogak(Mogak mogak, double rate) {
//        if (rate >= 80.0) {
//            mogak.updateState(State.COMPLETE.name());
//        } else {
//            mogak.updateState(State.FAIL.name());
//        }
//    }

    /**
     * 모각 삭제 API
     * */
    @Transactional
    @Override
    public void deleteMogak(Long mogakId) {
        Mogak mogak = mogakRepository.findById(mogakId)
                .orElseThrow(() -> new MogakException(ErrorCode.NOT_EXIST_MOGAK));
//        mogakPeriodRepository.deleteAllByMogakId(mogakId);
        jogakRepository.deleteAll(mogak.getJogaks());

        List<Post> posts = postRepository.findAllByMogak(mogak);
        if (!posts.isEmpty()) {
            posts.forEach(post -> {
                if (!postImgRepository.findAllByPost(post).isEmpty()) {
                    postImgRepository.deleteAllByPost(post);
                }
                if (!postCommentRepository.findAllByPost(post).isEmpty()) {
                    postCommentRepository.deleteAllByPost(post);
                }
            });
            postRepository.deleteAllByMogak(mogak);
        }
        mogakRepository.deleteById(mogakId);
    }

}
