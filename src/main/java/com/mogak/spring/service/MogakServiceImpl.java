package com.mogak.spring.service;

import com.mogak.spring.domain.jogak.DailyJogak;
import com.mogak.spring.domain.jogak.Jogak;
import com.mogak.spring.domain.modarat.Modarat;
import com.mogak.spring.domain.mogak.Mogak;
import com.mogak.spring.domain.mogak.MogakCategory;
import com.mogak.spring.domain.user.User;
import com.mogak.spring.exception.AuthException;
import com.mogak.spring.exception.BaseException;
import com.mogak.spring.exception.MogakException;
import com.mogak.spring.exception.UserException;
import com.mogak.spring.global.ErrorCode;
import com.mogak.spring.repository.*;
import com.mogak.spring.web.dto.jogakdto.JogakResponseDto;
import com.mogak.spring.web.dto.mogakdto.MogakRequestDto;
import com.mogak.spring.web.dto.mogakdto.MogakResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MogakServiceImpl implements MogakService {
    private final UserRepository userRepository;
    private final ModaratRepository modaratRepository;
    private final MogakRepository mogakRepository;
    private final MogakCategoryRepository categoryRepository;
    private final JogakRepository jogakRepository;
    private final JogakService jogakService;
    private final DailyJogakRepository dailyJogakRepository;
    private final PostRepository postRepository;
    private final PostImgRepository postImgRepository;
    private final PostCommentRepository postCommentRepository;

    /**
     * 모각 생성
     * */
    @Transactional
    @Override
    public MogakResponseDto.GetMogakDto create(Long userId, MogakRequestDto.CreateDto request) {
        User user = userRepository.findActiveById(userId)
                .orElseThrow(() -> new UserException(ErrorCode.NOT_EXIST_USER));
        Modarat modarat = getOwnedModarat(request.modaratId(), userId);
        if (!validateMogakNum(modarat)) {
            throw new BaseException(ErrorCode.EXCEED_MAX_MOGAK);
        }
        MogakCategory category = categoryRepository.findMogakCategoryByName(request.bigCategory())
                .orElseThrow(() -> new MogakException(ErrorCode.NOT_EXIST_CATEGORY));
        Mogak result = mogakRepository.save(Mogak.of(
                user,
                modarat,
                category,
                request.smallCategory(),
                request.title(),
                request.color()
        ));
        return toGetMogakDto(result);
    }

    // 모다라트의 모각 개수 검증
    private boolean validateMogakNum(Modarat modarat) {
        // 현재 유효한 기간 및 종료 날짜가 없는 조각 개수 체크
        return mogakRepository.findAllByModaratId(modarat.getId()).size() < 8;
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
     * 모각 업데이트 메소드
     * */
    @Transactional
    @Override
    public MogakResponseDto.GetMogakDto updateMogak(Long userId, MogakRequestDto.UpdateDto request) {
        Mogak mogak = getOwnedMogak(request.mogakId(), userId);
        Optional<String> categoryOptional = Optional.ofNullable(request.bigCategory());
        categoryOptional.ifPresent(categoryValue -> {
            MogakCategory category = categoryRepository.findMogakCategoryByName(categoryValue)
                    .orElseThrow(() -> new MogakException(ErrorCode.NOT_EXIST_CATEGORY));
            mogak.updateBigCategory(category);
            // 조각 카테고리 수정
            List<Jogak> jogakList = jogakRepository.findAllByMogak(mogak);
            jogakList.forEach(jogak -> jogak.updateCategory(category));
        });
        mogak.update(request.title(), request.smallCategory(), request.color());
        return toGetMogakDto(mogak);
    }

    /**
     * 모각 리스트 조회
     * */
    @Override
    public MogakResponseDto.GetMogakListDto getMogakDtoList(Long userId, Long modaratId) {
        userRepository.findActiveById(userId)
                .orElseThrow(() -> new UserException(ErrorCode.NOT_EXIST_USER));
        Modarat modarat = getOwnedModarat(modaratId, userId);
        List<MogakResponseDto.GetMogakDto> mogaks = mogakRepository.findAllByModaratId(modarat.getId()).stream()
                .map(this::toGetMogakDto)
                .collect(Collectors.toList());
        return new MogakResponseDto.GetMogakListDto(mogaks, mogaks.size());
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
    public void deleteMogak(Long userId, Long mogakId) {
        Mogak mogak = getOwnedMogak(mogakId, userId);
        deleteMogakCascadeAfterParentAuthorization(mogak.getId());
    }

    @Transactional
    @Override
    public void deleteMogakCascadeAfterParentAuthorization(Long mogakId) {
        Mogak mogak = mogakRepository.findActiveById(mogakId)
                .orElseThrow(() -> new MogakException(ErrorCode.NOT_EXIST_MOGAK));
        List<Jogak> jogaks = jogakRepository.findAllByMogak(mogak);
        jogaks.forEach(jogak -> jogakService.deleteJogakCascadeAfterParentAuthorization(jogak.getId()));
        jogakRepository.flush();

//        List<Post> posts = postRepository.findAllByMogak(mogak);
//        if (!posts.isEmpty()) {
//            posts.forEach(post -> {
//                if (!postImgRepository.findAllByPost(post).isEmpty()) {
//                    postImgRepository.deleteAllByPost(post);
//                }
//                if (!postCommentRepository.findAllByPost(post).isEmpty()) {
//                    postCommentRepository.deleteAllByPost(post);
//                }
//            });
//            postRepository.deleteAllByMogak(mogak);
//        }
        mogak.delete();
    }

    @Override
    public List<JogakResponseDto.GetJogakDto> getJogaks(Long userId, Long mogakId, LocalDate day) {
        User user = userRepository.findActiveById(userId)
                .orElseThrow(() -> new UserException(ErrorCode.NOT_EXIST_USER));
        Mogak mogak = getOwnedMogak(mogakId, userId);
        List<DailyJogak> dailyJogak = dailyJogakRepository.findDailyJogaks(user, day);
        return jogakRepository.findAllByMogakWithFetchGraph(mogak).stream()
                .filter(jogak -> jogak.getEndAt() == null || jogak.getEndAt().isAfter(day.minusDays(1)))
                .map(jogak -> JogakResponseDto.getJogakFrom(jogak, findCorrespondingDailyJogak(jogak, dailyJogak)))
                .collect(Collectors.toList());
    }

    private static Boolean findCorrespondingDailyJogak(Jogak jogak, List<DailyJogak> dailyJogaks) {
        return dailyJogaks.stream()
                .anyMatch(dailyJogak -> dailyJogak.getJogak().equals(jogak));
    }

    private MogakResponseDto.GetMogakDto toGetMogakDto(Mogak mogak) {
        return new MogakResponseDto.GetMogakDto(
                mogak.getId(),
                mogak.getTitle(),
                mogak.getBigCategory(),
                mogak.getSmallCategory(),
                mogak.getColor()
        );
    }

    private Modarat getOwnedModarat(Long modaratId, Long userId) {
        Modarat modarat = modaratRepository.findActiveById(modaratId)
                .orElseThrow(() -> new BaseException(ErrorCode.NOT_EXIST_MODARAT));
        if (!modarat.getUser().getId().equals(userId)) {
            throw new AuthException(ErrorCode.INVALID_PERMISSION);
        }
        return modarat;
    }

    private Mogak getOwnedMogak(Long mogakId, Long userId) {
        Mogak mogak = mogakRepository.findActiveById(mogakId)
                .orElseThrow(() -> new MogakException(ErrorCode.NOT_EXIST_MOGAK));
        if (!mogak.getUser().getId().equals(userId)) {
            throw new AuthException(ErrorCode.INVALID_PERMISSION);
        }
        return mogak;
    }
}
