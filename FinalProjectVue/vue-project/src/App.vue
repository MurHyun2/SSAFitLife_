<template>
  <div>
    <RouterView/>
  </div>
  <div id="app">
      <button v-if="!isLoggedIn" @click="loginSidePanel" class="login"><a class="login-content">로그인</a><a class="plus">+</a></button>
      <button v-if="!isLoggedIn" @click="signupSidePanel" class="signup"><a class="signup-content">회원가입</a><a class="plus">+</a></button>
      <button v-if="isLoggedIn" @click="mypageSidePanel" class="mypage"><a class="mypage-content">마이페이지</a><a class="plus">+</a></button>
      <transition name="slide">
        <!-- 로그인 패널 -->
        <div v-if="isLoginVisible" class="login-side-panel">
          <button class="login-close-button" @click="loginSidePanel">닫기</button>
          <h2>로그인 화면</h2>
          <form @submit.prevent="login">
            <div class="input-group">
              <input v-model="username" type="text" placeholder="이메일" required><br>
              <span v-if="username && !username.includes('@')" class="error-message">올바른 이메일을 입력하세요.</span>
            </div>
            <div class="input-group">
              <input v-model="password" type="password" placeholder="비밀번호" required><br>
            </div>
            <div>
              <span v-if="checkLoginFlag" class="error-message">아이디 또는 비밀번호가 잘못 되었습니다. 아이디와 비밀번호를 정확히 입력해 주세요.</span>
            </div>
            <br>
            <button type="submit">로그인</button>
            <span class="forgot-password"><a href="#">비밀번호를 잊으셨나요?</a></span>
          </form>
        </div>
      </transition>
      <transition name="slide">
        <!-- 회원가입 패널 -->
        <div v-if="isSignupVisible" class="signup-side-panel">
          <button class="signup-close-button" @click="signupSidePanel">닫기</button>
          <h2>회원가입 화면</h2>
          <form @submit.prevent="handleSignup">
            <div class="input-group">
              <h3 class="join_title">이메일</h3>
              <input v-model="user.email" type="email" placeholder="이메일" @blur="checkEmail" required><br>
              <span v-if="emailError" class="error-message">{{ emailError }}</span>
            </div>
            <div class="input-group">
              <h3 class="join_title">비밀번호</h3>
              <input v-model="user.password" type="password" placeholder="비밀번호" required @input="checkPasswordMatch"><br>
              <input v-model="confirmPassword" type="password" placeholder="비밀번호 확인" required @input="checkPasswordMatch"><br>
              <span v-if="passwordError" class="error-message">{{ passwordError }}</span>
            </div>
            <div class="input-group">
              <h3 class="join_title">이름</h3>
              <input v-model="user.name" type="text" placeholder="이름" required><br>
            </div>
            <div class="input-group">
              <h3 class="join_title">닉네임</h3>
              <input v-model="user.nickname" type="text" placeholder="닉네임" @blur="checkNickname" required><br>
              <span v-if="nicknameError" class="error-message">{{ nicknameError }}</span>
            </div>
            <div class="input-group">
              <h3 class="join_title">키(cm)</h3>
              <input
                  id="height"
                  type="number"
                  v-model="user.height"
                  @input="validateHeight"
                  @keydown="preventNonNumericInput"
                  step="0.1"
                  placeholder="키"
              />
              <p v-if="heightError" class="error-message">{{ heightError }}</p>
            </div>
            <div class="input-group">
              <h3 class="join_title">성별</h3>
              <div class="gender-buttons">
                <label class="gender-option">
                  <input
                      type="radio"
                      v-model="user.gender"
                      value="0"
                      name="gender"
                  >
                  <span class="gender-label">남성</span>
                </label>
                <label class="gender-option">
                  <input
                      type="radio"
                      v-model="user.gender"
                      value="1"
                      name="gender"
                  >
                  <span class="gender-label">여성</span>
                </label>
              </div>
            </div>
            <div class="input-group">
              <h3 class="join_title">생년월일</h3>
              <div class="birthday-container">
                <div class="birthday-input">
                  <input
                      type="number"
                      v-model="signup.yyyy"
                      class="birthday-select"
                      placeholder="년도"
                      min="1924"
                      :max="new Date().getFullYear()"
                      @input="validateYear"
                  />
                </div>
                <div class="birthday-input">
                  <input
                      type="number"
                      v-model="signup.mm"
                      class="birthday-select"
                      placeholder="월"
                      min="1"
                      max="12"
                      @input="validateMonth"
                  />
                </div>
                <div class="birthday-input">
                  <input
                      type="number"
                      v-model="signup.dd"
                      class="birthday-select"
                      placeholder="일"
                      min="1"
                      :max="getLastDay"
                      @input="validateDay"
                  />
                </div>
              </div>
              <span v-if="checkFlag && (!signup.yyyy || !signup.mm || !signup.dd)" class="birthday-error">
                생년월일을 모두 입력해주세요
              </span>
            </div>
            <div class="input-group">
              <h3 class="join_title">전화번호</h3>
              <input v-model="user.phoneNumber" type="text" placeholder="전화번호" @input="formatPhoneNumber" required><br>
            </div>
            <br>
            <button type="submit" :disabled="isEmailExists || isNicknameExists || isPasswordDiff">회원가입</button>
          </form>
        </div>
      </transition>
      <transition name="slide">
        <!-- 마이페이지 패널 -->
        <div v-if="isMypageVisible" class="mypage-side-panel">
          <button class="mypage-close-button" @click="mypageSidePanel">닫기</button>
          <h2>마이페이지 화면</h2>
          <button @click="logout">로그아웃</button>
          <p>여기에 원하는 내용을 추가하세요.</p> 
        </div>
      </transition>
    </div>
</template>

<script setup>
// Vue Composition API 사용
import {ref, onMounted, watch, onUnmounted, computed} from 'vue';
import {useRouter} from 'vue-router';
import {useAuth} from '@/composables/useAuth';
import axiosInstance from "@/plugins/axios.js";

const { isLoggedIn, handleLogin, handleLogout, checkToken } = useAuth();
// const isLoggedOut = ref(!localStorage.getItem('accessToken'));

// 토큰 만료 여부와 로그인 상태 확인
onMounted(() => {
  checkToken();
});

// 로그인, 회원가입, 마이페이지 상태 변수
const isLoginVisible = ref(false);
const isSignupVisible = ref(false);
const isMypageVisible = ref(false);

const router = useRouter();
// 패널 토글 함수
const loginSidePanel = () => {
  isLoginVisible.value = !isLoginVisible.value;
  username.value = '';
  password.value = '';
};

const signupSidePanel = () => {
  isSignupVisible.value = !isSignupVisible.value;
  user.value = {
    email: '',
    password: '',
    name: '',
    height: '',
    gender: '',
    birthday: '',
    phoneNumber: '',
    nickname: '',
  };
  emailError.value = '';
  nicknameError.value = '';
  confirmPassword.value ='';
  isEmailExists.value = false;
  isNicknameExists.value = false;
  isPasswordDiff.value = false;
};

const mypageSidePanel = () => {
  isMypageVisible.value = !isMypageVisible.value;
};

//로그인 처리
const username = ref('');
const password = ref('');
const checkLoginFlag = ref(false);

const login = async () => {
  const loginFail = await handleLogin(username.value, password.value);
  isLoginVisible.value = loginFail;
  checkLoginFlag.value = loginFail;
};

// 로그아웃 처리
const logout = () => {
  handleLogout();
  isMypageVisible.value = false;
};

//회원가입 처리
const user = ref({
  email: '',
  password: '',
  name: '',
  height: '',
  gender: '',
  birthday: '',
  phoneNumber: '',
  nickname: '',
});

const confirmPassword = ref(''); // 비밀번호 확인 입력 필드 추가
const emailError = ref('');
const nicknameError = ref('');
const passwordError = ref('');
const isEmailExists = ref(false);
const isNicknameExists = ref(false);
const isPasswordDiff = ref(false);

// 이메일 중복 체크
const checkEmail = async () => {
  try {
    const response = await axiosInstance.get(`/user/check-email`, {
      params: {email: user.value.email},
    });

    if (response.data) {
      emailError.value = '이미 존재하는 이메일입니다.';
      isEmailExists.value = true;
    } else {
      emailError.value = '';
      isEmailExists.value = false;
    }
  } catch (err) {
    console.error(err);
    emailError.value = '이메일 확인에 실패했습니다.';
  }
};

// 닉네임 중복 체크
const checkNickname = async () => {
  try {
    const response = await axiosInstance.get(`/user/check-nickname`, {
      params: {nickname: user.value.nickname},
    });

    if (response.data) {
      nicknameError.value = '이미 존재하는 닉네임입니다.';
      isNicknameExists.value = true;
    } else {
      nicknameError.value = '';
      isNicknameExists.value = false;
    }
  } catch (err) {
    console.error(err);
    nicknameError.value = '닉네임 확인에 실패했습니다.';
  }
};

// 비밀번호 확인 검사
const validatePassword = () => {
  const password = user.value.password;

  // 비밀번호 조건 검사
  const hasUpperCase = /[A-Z]/.test(password);
  const hasNumber = /[0-9]/.test(password);
  const hasSpecialChar = /[!@#$%^&*(),.?":{}|<>]/.test(password);
  const isLongEnough = password.length >= 8;

  if (!isLongEnough || !hasUpperCase || !hasNumber || !hasSpecialChar) {
    passwordError.value = '대문자, 숫자, 특수문자를 조합하여 최소 8자리 이상의 길이로 입력하세요';
    isPasswordDiff.value = true;
    return false;
  }

  return true;
};

// 기존의 checkPasswordMatch 함수를 수정
const checkPasswordMatch = () => {
  if (!validatePassword()) {
    return;
  }

  if (user.value.password !== confirmPassword.value) {
    passwordError.value = '비밀번호가 일치하지 않습니다.';
    isPasswordDiff.value = true;
  } else {
    passwordError.value = '';
    isPasswordDiff.value = false;
  }
};

// 에러 메시지 상태
const heightError = ref('');

// 키 유효성 검사 함수
const validateHeight = () => {
  const height = parseFloat(user.value.height);

  if (isNaN(height) || height < 100.0 || height > 250.0) {
    heightError.value = '키는 100~250 사이여야 합니다.';
    if (height > 250.0) {
      user.value.height = 250.0; // 최대값으로 설정
    }
  } else {
    heightError.value = ''; // 에러 메시지 초기화
  }
};

// 키 유효성 검사 함수
const preventNonNumericInput = (event) => {
  const keyValue = event.key; // 현재 입력된 키
  const currentValue = event.target.value;

  // 숫자 및 허용 키 정의
  const allowedKeys = [
    'Backspace',  // 지우기
    'Tab',        // 탭
    'ArrowLeft',  // 왼쪽 화살표
    'ArrowRight', // 오른쪽 화살표
    'Delete'      // 삭제
  ];

  // 소수점 처리
  if (keyValue === '.') {
    if (currentValue.includes('.') || currentValue === '') {
      event.preventDefault(); // 이미 소수점이 있거나 첫 글자인 경우 방지
    }
    return;
  }

  // 소수점 이후 자릿수 제한
  if (currentValue.includes('.') && !allowedKeys.includes(keyValue)) {
    const decimalPlaces = currentValue.split('.')[1];
    if (decimalPlaces && decimalPlaces.length >= 1 && /^[0-9]$/.test(keyValue)) {
      event.preventDefault(); // 소수점 첫째 자리까지만 숫자 입력 허용
      return;
    }
  }

  // 숫자 또는 허용된 키인지 확인
  if (!/^[0-9]$/.test(keyValue) && !allowedKeys.includes(keyValue)) {
    event.preventDefault(); // 숫자와 허용된 키 외에는 방지
  }
};



// 전화번호 포맷팅 함수
const formatPhoneNumber = (event) => {
  // 입력값 가져오기
  let value = event.target.value;

  // 숫자만 남기기
  value = value.replace(/[^0-9]/g, '');

  // 000-0000-0000 형식으로 변환
  if (value.length <= 3) {
    event.target.value = value; // 000
  } else if (value.length <= 7) {
    event.target.value = `${value.slice(0, 3)}-${value.slice(3)}`; // 000-0000
  } else {
    event.target.value = `${value.slice(0, 3)}-${value.slice(3, 7)}-${value.slice(7, 11)}`; // 000-0000-0000
  }

  // 사용자 입력 데이터 반영
  user.value.phoneNumber = event.target.value;
  };

// 생일 관련 상태 관리
const signup = ref({
  yyyy: '',
  mm: '',
  dd: ''
});

const yyyyList = ref([]);
const mmlist = ref([]);
const ddlist = ref([]); // ddlist도 ref로 선언해야 함
const checkFlag = ref(false);



const validateYear = () => {
  const currentYear = new Date().getFullYear();
  const year = parseInt(signup.value.yyyy);

  if (isNaN(year) || year > currentYear) {
    signup.value.yyyy = Math.min(year, currentYear);
  }
}

const validateMonth = () => {
  const month = parseInt(signup.value.mm);

  if (isNaN(month) || month < 1 || month > 12) {
    signup.value.mm = Math.min(Math.max(1, month), 12);
  }
}

const validateDay = () => {
  const lastDay = new Date(signup.value.yyyy, signup.value.mm, 0).getDate();
  const day = parseInt(signup.value.dd);

  if (isNaN(day) || day < 1 || day > lastDay) {
    signup.value.dd = Math.min(Math.max(1, day), lastDay);
  }
}

// 마지막 날짜 계산을 위한 computed 속성
const getLastDay = computed(() => {
  if (!signup.value.yyyy || !signup.value.mm) return 31;
  return new Date(signup.value.yyyy, signup.value.mm, 0).getDate();
});

// 년도와 월이 변경될 때 일 수 재계산
watch(
    [
      () => signup.value.yyyy,
      () => signup.value.mm
    ],
    ([newYear, newMonth]) => {
      if (newMonth && newYear) {
        const lastDay = new Date(newYear, newMonth, 0).getDate();
        ddlist.value = Array.from({length: lastDay}, (_, i) => i + 1);

        // 현재 선택된 일자가 새로운 월의 마지막 날짜보다 큰 경우 조정
        if (signup.value.dd > lastDay) {
          signup.value.dd = lastDay;
        }
      } else {
        ddlist.value = [];
      }
    },
    { immediate: true }
);

// 생년월일 포맷팅
const formatBirthday = () => {
  if (!signup.value.yyyy || !signup.value.mm || !signup.value.dd) {
    return;
  }

  const month = signup.value.mm.toString().padStart(2, '0');
  const day = signup.value.dd.toString().padStart(2, '0');

  return `${signup.value.yyyy}-${month}-${day}`;
};

const handleSignup = async () => {
  try {
    user.value.birthday = formatBirthday();

    const response = await axiosInstance.post('/user/join', user.value);

    if (response.status === 200) {
      alert('회원가입 성공');
      // 회원가입 후 로그인 화면으로 전환
      isSignupVisible.value = false; // 회원가입 패널 숨기기
      isLoginVisible.value = true; // 로그인 패널 보이기
    }
  } catch (err) {
    console.error(err);
    alert('회원가입 실패');
  }
};
</script>

<style scoped>
/* 오른쪽 패널 스타일 */
.login-side-panel {
  position: fixed;
  top: 0;
  right: 0;
  width: 400px;
  height: 100%;
  background-color: white;
  box-shadow: -2px 0 5px rgba(0, 0, 0, 0.5);
  padding: 20px;
  transition: transform 0.3s ease;
  z-index: 10;
}
.signup-side-panel {
  position: fixed;
  top: 0;
  right: 0;
  width: 400px;
  height: 100%;
  background-color: white;
  box-shadow: -2px 0 5px rgba(0, 0, 0, 0.5);
  padding: 20px;
  transition: transform 0.3s ease;
  z-index: 10;
}
.mypage-side-panel {
  position: fixed;
  top: 0;
  right: 0;
  width: 600px;
  height: 100%;
  background-color: white;
  box-shadow: -2px 0 5px rgba(0, 0, 0, 0.5);
  padding: 20px;
  transition: transform 0.3s ease;
}

.login,.signup,.mypage{
    font-size: 17px;
    font-weight: bold;
    color: white;
    background-color: #97d4e9;
    position: absolute;
    top:45px;
    height: 55px;
    width: 150px;
    border-radius: 35px;
    display: flex; /* 내용 정렬을 위해 Flexbox 사용 */
    justify-content: center; /* 가로 가운데 정렬 */
    align-items: center; /* 세로 가운데 정렬 */
    box-shadow: 1px 1px 5px rgba(0, 0, 0, 0.3);
    cursor: pointer;
    border: none;
  }
  .login{
    right: 240px;
  }
  .signup{
    right: 70px;
  }
  .mypage{
    right: 70px;
  }
  .login-content{
    position: absolute;
    left:35px
  }
  .signup-content{
    position: absolute;
    left:30px
  }
  .mypage-content{
    position: absolute;
    left:30px
  }
  .plus{
    position: absolute;
    right:15px
  }
  input[type="text"],
  input[type="email"],
  input[type="password"],
  input[type="number"],
  input[type="date"]{
    width: 95%;
    padding: 10px;
    font-size: 16px;
    margin: 5px 0;
    border: 1px solid #ddd;
    border-radius: 5px;
  }

  select{
    width: 100%;
    padding: 10px;
    font-size: 16px;
    margin: 5px 0;
    border: 1px solid #ddd;
    border-radius: 5px;
  }

  button {
    width: 100%;
    padding: 15px;
    font-size: 18px;
    background-color: #97d4e9;
    color: white;
    border: none;
    border-radius: 5px;
    cursor: pointer;
  }
  button:disabled {
    background-color: #cccccc;
  }
  .forgot-password {
    display: block;
    margin-top: 10px;
    text-align: center;
  }

  .forgot-password a {
    text-decoration: none;
    color: #97d4e9;
  }

  .error-message {
    color: red;
    font-size: 12px;
  }

  .login-close-button,
  .signup-close-button {
    font-size: 16px;
    background: none;
    border: none;
    color: #97d4e9;
    cursor: pointer;
    padding: 10px;
  }

  .login, .signup {
    font-size: 18px;
    color: white;
    position: absolute;
    top: 50px;
    border-radius: 30px;
    padding: 15px 25px;
    cursor: pointer;
    transition: background-color 0.3s;
  }

  .login:hover,
  .signup:hover {
    background-color: #3c9ecf;
  }

  .login-content, .signup-content {
    font-weight: bold;
    margin-right: 10px;
  }

  .plus {
    font-size: 20px;
  }

/* 생일 입력 관련 스타일 */

.gender-buttons {
  display: flex;
  gap: 20px;
  margin: 10px 0;
}

.gender-option {
  position: relative;
  display: flex;
  align-items: center;
  cursor: pointer;
}

.gender-option input[type="radio"] {
  position: absolute;
  opacity: 0;
}

.gender-label {
  padding: 10px 30px;
  border: 2px solid #97d4e9;
  border-radius: 25px;
  color: #97d4e9;
  transition: all 0.3s ease;
}

.gender-option input[type="radio"]:checked + .gender-label {
  background-color: #97d4e9;
  color: white;
}

.gender-option:hover .gender-label {
  background-color: rgba(151, 212, 233, 0.1);
}

.birthday-container {
  display: flex;
  gap: 10px;
  margin: 10px 0;
}

.birthday-input {
  flex: 1;
}

.birthday-select {
  width: 100%;
  padding: 12px;
  border: 2px solid #e8e8e8;
  border-radius: 8px;
  font-size: 15px;
  color: #333;
  background-color: white;
  transition: all 0.3s ease;
  cursor: pointer;
  appearance: none;
  background-image: url("data:image/svg+xml;charset=UTF-8,%3csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='currentColor' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3e%3cpolyline points='6 9 12 15 18 9'%3e%3c/polyline%3e%3c/svg%3e");
  background-repeat: no-repeat;
  background-position: right 10px center;
  background-size: 1em;
}

.birthday-select:focus {
  border-color: #97d4e9;
  outline: none;
  box-shadow: 0 0 0 3px rgba(151, 212, 233, 0.2);
}

.birthday-select:hover {
  border-color: #97d4e9;
}

.birthday-error {
  display: block;
  color: #ff4444;
  font-size: 13px;
  margin-top: 5px;
}

.join_title {
  margin-bottom: 10px;
  color: #333;
  font-size: 15px;
  font-weight: 600;
}

/* Transition Effects */
.slide-enter-active,
.slide-leave-active {
  transition: transform 0.3s ease;
}

.slide-enter {
  transform: translateX(100%);
}

.slide-enter-to {
  transform: translateX(0);
}

.slide-leave {
  transform: translateX(0);
}

.slide-leave-to {
  transform: translateX(100%);
}

</style>
