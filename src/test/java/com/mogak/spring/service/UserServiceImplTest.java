package com.mogak.spring.service;

import com.mogak.spring.domain.user.Address;
import com.mogak.spring.domain.user.Job;
import com.mogak.spring.domain.user.User;
import com.mogak.spring.global.ErrorCode;
import com.mogak.spring.jwt.JwtTokenProvider;
import com.mogak.spring.repository.AddressRepository;
import com.mogak.spring.repository.JobRepository;
import com.mogak.spring.repository.UserRepository;
import com.mogak.spring.security.SecurityAuthority;
import com.mogak.spring.support.ErrorCodeAssertions;
import com.mogak.spring.support.TestFixtureFactory;
import com.mogak.spring.util.Regex;
import com.mogak.spring.web.dto.userdto.UserRequestDto;
import com.mogak.spring.web.dto.userdto.UserResponseDto;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private JobRepository jobRepository;
    @Mock
    private AddressRepository addressRepository;
    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    @DisplayName("닉네임은 현재 정규식 정책을 따른다")
    void nicknameRegexValidation() {
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(Regex.USER_NICKNAME_REGEX.matchRegex("hyun1234", "NICKNAME")).isFalse();
        softly.assertThat(Regex.USER_NICKNAME_REGEX.matchRegex("hyun!", "NICKNAME")).isFalse();
        softly.assertThat(Regex.USER_NICKNAME_REGEX.matchRegex("1234!", "NICKNAME")).isFalse();
        softly.assertThat(Regex.USER_NICKNAME_REGEX.matchRegex("hyun1234!", "NICKNAME")).isTrue();
        softly.assertAll();
    }

    @Test
    @DisplayName("사용 가능한 닉네임을 검증하면 true를 반환한다")
    void verifyNicknameReturnsTrue() {
        when(userRepository.findActiveByNickname("newbie")).thenReturn(Optional.empty());

        assertThat(userService.verifyNickname("newbie")).isTrue();
    }

    @Test
    @DisplayName("이미 사용 중인 닉네임을 검증하면 예외를 반환한다")
    void verifyNicknameThrowsWhenDuplicate() {
        when(userRepository.findActiveByNickname("taken")).thenReturn(Optional.of(TestFixtureFactory.user(1L, "taken@test.com", "taken", null, null)));

        Throwable throwable = org.assertj.core.api.Assertions.catchThrowable(() -> userService.verifyNickname("taken"));

        ErrorCodeAssertions.assertErrorCode(throwable, ErrorCode.ALREADY_EXIST_USER);
    }

    @Test
    @DisplayName("유효하지 않은 이메일로 사용자를 조회하면 예외를 반환한다")
    void getUserByEmailThrowsWhenEmailInvalid() {
        Throwable throwable = org.assertj.core.api.Assertions.catchThrowable(() -> userService.getUserByEmail("wrong-email"));

        ErrorCodeAssertions.assertErrorCode(throwable, ErrorCode.NOT_VALID_EMAIL);
    }

    @Test
    @DisplayName("회원 가입 요청이 유효하면 사용자를 등록한다")
    void createRegistersUser() {
        Job job = TestFixtureFactory.job("개발/데이터");
        Address address = TestFixtureFactory.address("서울특별시");
        User user = TestFixtureFactory.user(10L, "user@test.com", null, null, null);
        UserRequestDto.CreateUserDto request = new UserRequestDto.CreateUserDto("tester", "개발/데이터", "서울특별시");
        UserRequestDto.UploadImageDto uploadImageDto = new UserRequestDto.UploadImageDto("profile.png", "https://cdn/profile.png");

        when(userRepository.findActiveByNickname("tester")).thenReturn(Optional.empty());
        when(jobRepository.findJobByName("개발/데이터")).thenReturn(Optional.of(job));
        when(addressRepository.findAddressByName("서울특별시")).thenReturn(Optional.of(address));
        when(userRepository.findActiveById(10L)).thenReturn(Optional.of(user));
        when(jwtTokenProvider.createAccessToken(10L, "user@test.com", SecurityAuthority.USER.getAuthority())).thenReturn("access-token");
        when(jwtTokenProvider.createRefreshToken(10L)).thenReturn("refresh-token");

        UserResponseDto.CreateDto result = userService.create(10L, request, uploadImageDto);

        assertThat(result.userId()).isEqualTo(10L);
        assertThat(result.nickname()).isEqualTo("tester");
        assertThat(result.tokens().accessToken()).isEqualTo("access-token");
        assertThat(result.tokens().refreshToken()).isEqualTo("refresh-token");
        assertThat(user.getJob()).isEqualTo(job);
        assertThat(user.getAddress()).isEqualTo(address);
        assertThat(user.getProfileImgUrl()).isEqualTo("https://cdn/profile.png");
    }

    @Test
    @DisplayName("이미 등록된 사용자는 다시 회원 가입할 수 없다")
    void createThrowsWhenUserAlreadyRegistered() {
        Job job = TestFixtureFactory.job("개발/데이터");
        Address address = TestFixtureFactory.address("서울특별시");
        User user = TestFixtureFactory.user(10L, "user@test.com", "existing", null, null);
        UserRequestDto.CreateUserDto request = new UserRequestDto.CreateUserDto("tester", "개발/데이터", "서울특별시");
        UserRequestDto.UploadImageDto uploadImageDto = new UserRequestDto.UploadImageDto(null, null);

        when(userRepository.findActiveByNickname("tester")).thenReturn(Optional.empty());
        when(jobRepository.findJobByName("개발/데이터")).thenReturn(Optional.of(job));
        when(addressRepository.findAddressByName("서울특별시")).thenReturn(Optional.of(address));
        when(userRepository.findActiveById(10L)).thenReturn(Optional.of(user));

        Throwable throwable = org.assertj.core.api.Assertions.catchThrowable(() -> userService.create(10L, request, uploadImageDto));

        ErrorCodeAssertions.assertErrorCode(throwable, ErrorCode.ALREADY_EXIST_USER);
    }

    @Test
    @DisplayName("현재 사용자의 직업을 변경하면 변경된 직업이 저장된다")
    void updateJobUpdatesCurrentUser() {
        Job currentJob = TestFixtureFactory.job("기획/전략");
        Job updatedJob = TestFixtureFactory.job("개발/데이터");
        Address address = TestFixtureFactory.address("서울특별시");
        User user = TestFixtureFactory.user(1L, "user@test.com", "tester", currentJob, address);

        when(jobRepository.findJobByName("개발/데이터")).thenReturn(Optional.of(updatedJob));
        when(userRepository.findActiveById(1L)).thenReturn(Optional.of(user));

        userService.updateJob(1L, new UserRequestDto.UpdateJobDto("개발/데이터"));

        assertThat(user.getJob()).isEqualTo(updatedJob);
    }

    @Test
    @DisplayName("현재 사용자의 프로필 이미지를 변경하면 변경된 이미지가 저장된다")
    void updateImgUpdatesCurrentUserProfile() {
        Job job = TestFixtureFactory.job("개발/데이터");
        Address address = TestFixtureFactory.address("서울특별시");
        User user = TestFixtureFactory.user(1L, "user@test.com", "tester", job, address);

        when(userRepository.findActiveById(1L)).thenReturn(Optional.of(user));

        UserRequestDto.UpdateImageDto dto = new UserRequestDto.UpdateImageDto("updated.png", "https://cdn/updated.png");

        userService.updateImg(1L, dto);

        assertThat(user.getProfileImgName()).isEqualTo("updated.png");
        assertThat(user.getProfileImgUrl()).isEqualTo("https://cdn/updated.png");
    }

    @Test
    @DisplayName("현재 사용자의 프로필을 조회하면 닉네임과 직업과 이미지 URL을 반환한다")
    void getUserProfileReturnsProfile() {
        Job job = TestFixtureFactory.job("개발/데이터");
        Address address = TestFixtureFactory.address("서울특별시");
        User user = TestFixtureFactory.user(1L, "user@test.com", "tester", job, address);
        user.updateProfileImg("https://cdn/profile.png", "profile.png");

        when(userRepository.findActiveById(1L)).thenReturn(Optional.of(user));

        UserResponseDto.GetUserDto result = userService.getUserProfile(1L);

        assertThat(result.nickname()).isEqualTo("tester");
        assertThat(result.job()).isEqualTo("개발/데이터");
        assertThat(result.imgUrl()).isEqualTo("https://cdn/profile.png");
    }

    @Test
    @DisplayName("토큰을 생성하면 JwtTokenProvider가 발급한 액세스 토큰을 반환한다")
    void getTokenDelegatesToProvider() {
        User user = TestFixtureFactory.user(11L, "user@test.com", "tester", null, null);
        when(jwtTokenProvider.createAccessToken(11L, "user@test.com", SecurityAuthority.USER.getAuthority())).thenReturn("access-token");

        assertThat(userService.getToken(user)).isEqualTo("access-token");
    }
}
