import axios from 'axios';

const axiosInstance = axios.create({
    baseURL: 'http://192.168.210.61:8080',
    // baseURL: 'http://192.168.219.107:8080',
    withCredentials: true,
    headers: {
        'Content-Type': 'application/json'
    }
});

// 요청 인터셉터
axiosInstance.interceptors.request.use((config) => {
    const token = localStorage.getItem('accessToken');
    if (token) {
        config.headers['Authorization'] = `${token}`;
    }
    return config;
}, (error) => {
    return Promise.reject(error);
});

// 응답 인터셉터 (401 에러 처리)
axiosInstance.interceptors.response.use((response) => {
    return response;
}, async (error) => {
    if (error.response && error.response.status === 401) {
        console.error('인증 오류 발생. 다시 로그인하세요.');
        // 로그아웃 처리 또는 리프레시 토큰 로직 추가 가능
        localStorage.removeItem('accessToken');
        window.location.href = '/'; // 로그인 페이지로 이동
    }
    return Promise.reject(error);
});

export default axiosInstance;
