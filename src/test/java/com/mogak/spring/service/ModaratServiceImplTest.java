package com.mogak.spring.service;

import com.mogak.spring.auth.AppleOAuthUserProvider;
import com.mogak.spring.domain.modarat.Modarat;
import com.mogak.spring.domain.mogak.Mogak;
import com.mogak.spring.domain.mogak.MogakCategory;
import com.mogak.spring.domain.user.Address;
import com.mogak.spring.domain.user.Job;
import com.mogak.spring.domain.user.User;
import com.mogak.spring.global.ErrorCode;
import com.mogak.spring.repository.*;
import com.mogak.spring.support.ErrorCodeAssertions;
import com.mogak.spring.support.TestFixtureFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
class ModaratServiceImplTest {

    @Autowired private ModaratService modaratService;
    @Autowired private UserRepository userRepository;
    @Autowired private ModaratRepository modaratRepository;
    @Autowired private MogakRepository mogakRepository;
    @Autowired private JobRepository jobRepository;
    @Autowired private AddressRepository addressRepository;
    @Autowired private MogakCategoryRepository mogakCategoryRepository;

    @MockitoSpyBean private MogakService mogakService;
    @MockitoBean private AppleOAuthUserProvider appleOAuthUserProvider;
    @MockitoBean private StorageService storageService;

    @Test
    @DisplayName("다른 사용자의 모다라트를 조회하면 권한 오류를 반환한다")
    void getDetailThrowsWhenNotOwner() {
        User requester = saveUser("user@test.com", "user");
        User owner = saveUser("owner@test.com", "owner");
        Modarat modarat = modaratRepository.save(TestFixtureFactory.modarat(null, owner, "메인 모다라트", "#0000"));

        Throwable throwable = org.assertj.core.api.Assertions.catchThrowable(() -> modaratService.getDetailModarat(requester.getId(), modarat.getId()));

        ErrorCodeAssertions.assertErrorCode(throwable, ErrorCode.INVALID_PERMISSION);
    }

    @Test
    @DisplayName("요청자 id가 없으면 모다라트 조회 권한 오류를 반환한다")
    void getDetailThrowsWhenRequesterIsNull() {
        User owner = saveUser("owner@test.com", "owner");
        Modarat modarat = modaratRepository.save(TestFixtureFactory.modarat(null, owner, "메인 모다라트", "#0000"));

        Throwable throwable = org.assertj.core.api.Assertions.catchThrowable(() -> modaratService.getDetailModarat(null, modarat.getId()));

        ErrorCodeAssertions.assertErrorCode(throwable, ErrorCode.INVALID_PERMISSION);
    }

    @Test
    @DisplayName("다른 사용자의 모다라트를 삭제하면 권한 오류를 반환한다")
    void deleteThrowsWhenNotOwner() {
        User requester = saveUser("user@test.com", "user");
        User owner = saveUser("owner@test.com", "owner");
        Modarat modarat = modaratRepository.save(TestFixtureFactory.modarat(null, owner, "메인 모다라트", "#0000"));

        Throwable throwable = org.assertj.core.api.Assertions.catchThrowable(() -> modaratService.delete(requester.getId(), modarat.getId()));

        ErrorCodeAssertions.assertErrorCode(throwable, ErrorCode.INVALID_PERMISSION);
        verify(mogakService, never()).deleteMogakCascadeAfterParentAuthorization(anyLong());
    }

    @Test
    @DisplayName("모다라트를 삭제하면 내부 모각 삭제를 위임한 뒤 모다라트를 삭제한다")
    void deleteModaratCascadesAfterAuthorization() {
        User owner = saveUser("owner@test.com", "owner");
        Modarat modarat = modaratRepository.save(TestFixtureFactory.modarat(null, owner, "메인 모다라트", "#0000"));
        MogakCategory category = saveCategory("자격증");
        Mogak first = mogakRepository.save(TestFixtureFactory.mogak(null, owner, modarat, category, "첫 모각", "#1111"));
        Mogak second = mogakRepository.save(TestFixtureFactory.mogak(null, owner, modarat, category, "둘째 모각", "#2222"));
        TestFixtureFactory.attachJogaks(first, List.of());
        TestFixtureFactory.attachJogaks(second, List.of());

        modaratService.delete(owner.getId(), modarat.getId());

        verify(mogakService).deleteMogakCascadeAfterParentAuthorization(first.getId());
        verify(mogakService).deleteMogakCascadeAfterParentAuthorization(second.getId());
        org.assertj.core.api.Assertions.assertThat(modaratRepository.findById(modarat.getId())).isEmpty();
    }

    private User saveUser(String email, String nickname) {
        Job job = jobRepository.findJobByName("개발/데이터").orElseGet(() -> jobRepository.save(TestFixtureFactory.job("개발/데이터")));
        Address address = addressRepository.findAddressByName("서울특별시").orElseGet(() -> addressRepository.save(TestFixtureFactory.address("서울특별시")));
        return userRepository.save(TestFixtureFactory.user(null, email, nickname, job, address));
    }

    private MogakCategory saveCategory(String name) {
        return mogakCategoryRepository.findMogakCategoryByName(name)
                .orElseGet(() -> mogakCategoryRepository.save(TestFixtureFactory.category(null, name)));
    }
}
