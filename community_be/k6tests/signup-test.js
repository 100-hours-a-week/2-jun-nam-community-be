import http from 'k6/http';
import { check } from 'k6';
import { uuidv4 } from 'https://jslib.k6.io/k6-utils/1.4.0/index.js';

export let options = {
    vus: 5000,               // 동시 사용자 수
    duration: '30s',       // 테스트 지속 시간
};

export default function () {
    // 고유한 이메일 생성을 위한 UUID
    const uniqueId = uuidv4();

    const payload = JSON.stringify({
        email: `user_${uniqueId}@example.com`,
        password: "1!Qqqqqq",
        nickname: `user${uniqueId}`,
        profileImage: ``,
        imageUrl: ``
    });

    const headers = {
        'Content-Type': 'application/json',
    };

    const res = http.post('http://localhost:8080/users', payload, { headers });

    check(res, {
        '회원가입 성공': (r) => r.status === 200,
        '이메일 중복 또는 기타 오류': (r) => r.status === 400,
    });
}
