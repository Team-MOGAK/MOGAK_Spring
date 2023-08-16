package com.mogak.spring.service;

import com.mogak.spring.converter.MogakConverter;
import com.mogak.spring.domain.common.State;
import com.mogak.spring.domain.jogak.Jogak;
import com.mogak.spring.domain.jogak.JogakState;
import com.mogak.spring.domain.mogak.Mogak;
import com.mogak.spring.domain.mogak.MogakCategory;
import com.mogak.spring.domain.mogak.MogakPeriod;
import com.mogak.spring.domain.mogak.Period;
import com.mogak.spring.domain.post.Post;
import com.mogak.spring.domain.user.User;
import com.mogak.spring.exception.*;
import com.mogak.spring.repository.*;
import com.mogak.spring.web.dto.MogakRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@RequiredArgsConstructor
@Service
public class MogakServiceImpl implements MogakService {
    private final UserRepository userRepository;
    private final MogakRepository mogakRepository;
    private final MogakPeriodRepository mogakPeriodRepository;
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
    public Mogak create(MogakRequestDto.CreateDto request, HttpServletRequest req) {
        System.out.println("request.getUserId() = " + req.getParameter("userId"));
        Long userId = Long.valueOf(req.getParameter("userId"));
        User user = userRepository.findById(userId).orElseThrow(() -> new UserException(ErrorCode.NOT_EXIST_USER));
        String otherCategory = request.getOtherCategory();
        MogakCategory category = categoryRepository.findMogakCategoryByName(request.getCategory()).orElseThrow(() -> new MogakException(ErrorCode.NOT_EXIST_CATEGORY));
        if (category.getName().equals("기타") && otherCategory == null) {
            throw new MogakException(ErrorCode.NOT_EXIST_OTHER_CATEGORY);
        }
        State state = State.registerState(request.getStartAt(), request.getEndAt(), LocalDate.now());
        Mogak result = mogakRepository.save(MogakConverter.toMogak(request, category, otherCategory, user, state));
        saveMogakPeriod(request.getDays(), result);
        return result;
    }

    /**
     * 모각주기 저장 메소드
     * */
    private void saveMogakPeriod(List<String> days, Mogak mogak) {
        List<Period> periods = new ArrayList<>();
        for (String day: days) {
            periods.add(periodRepository.findOneByDays(day)
                    .orElseThrow(() -> new MogakException(ErrorCode.NOT_EXIST_DAY)));
        }
        for (Period period: periods) {
            MogakPeriod mogakPeriod = MogakPeriod.builder()
                    .period(period)
                    .mogak(mogak)
                    .build();
            mogakPeriodRepository.save(mogakPeriod);
        }
    }

    /**
     * 모각주기 업데이트 메소드
     * */
    private void updateMogakPeriod(List<String> days, Mogak mogak) {
        List<Period> periods = new ArrayList<>();
        for (String day : days) {
            periods.add(periodRepository.findOneByDays(day)
                    .orElseThrow(() -> new MogakException(ErrorCode.NOT_EXIST_DAY)));
        }
        List<MogakPeriod> mogakPeriods = mogakPeriodRepository.findAllByMogak_Id(mogak.getId());

        int periodSize = periods.size();
        int mpSize = mogakPeriods.size();
        if (mpSize == periodSize) {
            IntStream.range(0, periodSize)
                    .forEach(i -> mogakPeriods.get(i).updatePeriod(periods.get(i)));
        } else if (mpSize > periodSize) {
            IntStream.range(0, periodSize)
                    .forEach(i -> mogakPeriods.get(i).updatePeriod(periods.get(i)));
            IntStream.range(periodSize, mpSize)
                    .forEach(i -> mogakPeriodRepository.delete(mogakPeriods.get(i)));
        } else {
            IntStream.range(0, mpSize)
                    .forEach(i -> mogakPeriods.get(i).updatePeriod(periods.get(i)));
            IntStream.range(mpSize, periodSize)
                    .forEach(i -> {
                        MogakPeriod mogakPeriod = MogakPeriod.builder()
                                .period(periods.get(i))
                                .mogak(mogak)
                                .build();
                        mogakPeriodRepository.save(mogakPeriod);
                    });
        }
    }

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
        if (Optional.ofNullable(request.getCategory()).isPresent()) {
            MogakCategory category = categoryRepository.findMogakCategoryByName(request.getCategory())
                    .orElseThrow(() -> new MogakException(ErrorCode.NOT_EXIST_CATEGORY));
            if (category.getName().equals("기타") && request.getOtherCategory() == null) {
                throw new MogakException(ErrorCode.NOT_EXIST_OTHER_CATEGORY);
            }
            if (category.getName().equals("기타")) {
                mogak.updateOtherCategory(request.getOtherCategory());
            }
            mogak.updateCategory(category);
        }

        mogak.updateFromDto(request);
        if (Optional.ofNullable(days).isPresent()) {
            updateMogakPeriod(days, mogak);
        }
        return mogak;
    }

    /**
     * 모각 조회(페이징)
     * */
    @Override
    public List<Mogak> getMogakList(HttpServletRequest req, int cursor, int size) {
        Long userId = Long.valueOf(req.getParameter("userId"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ErrorCode.NOT_EXIST_USER));
        PageRequest pageRequest = PageRequest.of(cursor, size);
        return mogakRepository.findAllByUserIdOrderByCreatedAtDesc(user.getId(), pageRequest);
    }

    /**
     * 진행중이고 해당 날의 모각들 불러오기
     * */
    @Override
    public List<Mogak> getOngoingTodayMogakList(int today) {
        return mogakRepository.findAllOngoingToday(State.ONGOING.name(), today);
    }

    /**
     * 모각 결과 내리기
     * 4시에 동작
     * 자정 전에 시작했던 조각들 때문
     * */
    @Transactional
    @Override
    public void judgeMogakByDay(LocalDate day) {
        List<Mogak> mogaks = mogakRepository.findAllByEndAt(day);
        for (Mogak mogak: mogaks) {
            List<Jogak> jogaks = jogakRepository.findAllByMogak(mogak);
            double achievementRate = getAcheiveRate(jogaks);
            judgeMogak(mogak, achievementRate);
        }
    }

    private double getAcheiveRate(List<Jogak> jogaks) {
        int success = 0;
        for (Jogak jogak: jogaks) {
            if (jogak.getState().equals(JogakState.SUCCESS.name())) {
                success += 1;
            }
        }
        return (double) success / jogaks.size() * 100;
    }

    private void judgeMogak(Mogak mogak, double rate) {
        if (rate >= 80.0) {
            mogak.updateState(State.COMPLETE.name());
        } else {
            mogak.updateState(State.FAIL.name());
        }
    }

    /**
     * 모각 삭제 API
     * */
    @Transactional
    @Override
    public void deleteMogak(Long mogakId) {
        //모각 존재 확인
        Mogak mogak = mogakRepository.findById(mogakId)
                .orElseThrow(() -> new MogakException(ErrorCode.NOT_EXIST_MOGAK));
        // 모각 주기 삭제
        mogakPeriodRepository.deleteAllByMogakId(mogakId);
        // 조각 삭제 필요
        List<Jogak> jogaks = mogak.getJogaks();
        if (!jogaks.isEmpty()) {
            jogakRepository.deleteAll(mogak.getJogaks());
        }
        // 회고록 삭제 + 회고록 삭제에서 댓글 삭제도 같이 구현 필요
        List<Post> posts = postRepository.findAllByMogak(mogak);
        if (!posts.isEmpty()) {
            for (Post post : posts) {
                if (!postImgRepository.findAllByPost(post).isEmpty()) {
                    postImgRepository.deleteAllByPost(post);
                }
                if (!postCommentRepository.findAllByPost(post).isEmpty()) {
                    postCommentRepository.deleteAllByPost(post);
                }
            }
            postRepository.deleteAllByMogak(mogak);
        }
        mogakRepository.deleteById(mogakId);
    }

}
