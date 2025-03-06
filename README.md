# 🌟 Spring Boilerplate

📌 **프로젝트 개요**  
자주 쓰이는 백엔드 기능을 모듈화하여 미리 구현해둔 프로젝트입니다.  
공부 및 개발을 병행하며 지속적으로 기능을 추가할 예정입니다.

🔗 **Frontend:** [Next.js Boilerplate](https://github.com/yongu2000/next-boilerplate)

---

## 🚀 기능 목록

### 📌 공통

- [ ] 📷 **사진 업로드 최적화**
- [x] 📜 **로깅 시스템 구축**

### 📦 배포

- [ ] 🌍 **커스텀 도메인 적용**
- [ ] 🛠 **테스트 자동화**
- [ ] 🔒 **HTTPS 설정**
- [ ] ⚖ **로드 밸런싱 구성**

---

## 👤 유저 기능

### 🔑 인증 및 인가

- [x] 🏷 **JWT 기반 회원가입 및 로그인**
    - [ ] 📧 회원가입 시 이메일 인증
    - [ ] 🔍 아이디 찾기
    - [ ] 🔑 비밀번호 찾기 (인증 이메일 발송 후 변경)

- [ ] 🔗 **OAuth 2.0 로그인**
    - [ ] 🟢 Google
    - [ ] 🟩 Naver
    - [ ] 🟡 Kakao

- [x] 🚪 **로그아웃 기능**

- [ ] 🔄 **다중 토큰 인증**
    - [x] 🔑 Refresh Token 발급
        - [x] 🔄 Access Token 재발급
    - [x] 🔁 Refresh Token Rotate
    - [x] 🗄 Refresh Token DB 저장
    - [ ] 🗑 유효하지 않은 Refresh Token 삭제

- [ ] 🔗 **같은 아이디 다중 연결**
    - [ ] 📋 연결된 기기 목록 조회
    - [ ] ❌ 특정 기기 연결 해제

### 🛠 유저 정보 관리

- [ ] ✉ **이메일(아이디) 변경**
- [ ] 🔑 **비밀번호 변경**
- [ ] 🖼 **프로필 사진 설정**

---

## 📝 게시글 기능

### 📄 게시글 CRUD

- [x] ✍ **게시글 작성**
    - [ ] 📝 글쓰기 에디터 (볼드, 밑줄, 글자 색 등)
    - [ ] 🖼 글에 사진 첨부

- [x] 🔍 **게시글 조회**
    - [x] 📜 모든 글 목록 조회
    - [x] 🔎 게시글 상세 보기
    - [x] 💬 댓글 불러오기
    - [ ] 👤 특정 유저의 게시글 조회
    - [ ] ❤️ 좋아요 한 게시글 조회
    - [ ] 👀 조회수 처리
        - [ ] 🛡 동일 JWT Token에 대해 조회수 중복 처리

- [x] 📝 **게시글 수정**
- [x] 🗑 **게시글 삭제**
    - [ ] 🛠 삭제된 글 처리 방식 결정
        - [ ] ❌ 완전 삭제
        - [ ] 👤 유저 정보만 제거 후 보존

---

### 💬 댓글 기능

- [x] ✍ **댓글 작성**
- [x] 🔄 **답글(대댓글) 작성**
- [x] 📝 **댓글 수정**
- [x] 🗑 **댓글 삭제**
    - [ ] 🛠 삭제된 댓글 처리 방식 결정
        - [ ] ❌ 완전 삭제
        - [ ] 🚫 내용만 삭제 표시

---

### 🔍 게시글 및 댓글 추가 기능

- [ ] 🔎 **검색 엔진 최적화 (SEO)**
- [ ] 📖 **페이지네이션 적용**
    - [ ] 📄 **게시글 페이지네이션** (페이지 형식)
    - [ ] 🔄 **게시글 무한 스크롤**
    - [ ] 💬 **댓글 페이지네이션** (페이지 형식)
    - [ ] 🔄 **댓글 무한 스크롤**

---

## ⏳ 예정 기능

✅ **확장 기능 및 개발 예정 사항**

- 💬 **채팅 시스템**
- 💳 **결제 기능**
- ⚙ **대규모 요청 대비 설계 및 해결 방안**
- 🔥 **부하 테스트 (Load Testing)**
- 📲 **휴대폰 번호 / 카카오톡 알림 기능**
- 🎥 **(영상) 통화 기능**
- 🗺 **지도 서비스 연동**
- 🏛 **분산 데이터베이스 설계**
    - 🆔 **ID 값 생성 전략**
- 🤖 **AI 추천 시스템 (임베딩 벡터 활용)**

---

이 문서는 지속적으로 업데이트될 예정입니다. 🚀  
더 좋은 개선점이 있다면 언제든지 피드백 부탁드립니다! 😊