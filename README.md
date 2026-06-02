## 소개
스포츠에 특화된 지역 기반 SNS 애플리케이션으로,  
사용자는 사진·영상·텍스트 형태의 게시물을 공유하고,  
개인 및 팀 프로필, 팔로우 시스템, 실시간 채팅 기능을 통해 커뮤니티를 형성할 수 있습니다.

## 주요 기능
- **인증 및 계정 관리**
  - 회원가입, 로그인, 이메일 인증, 자동 로그인, 비밀번호 변경

- **게시물 기능**
  - 사진/영상/텍스트 기반 게시물 작성, 수정 및 삭제
  - 게시물 조회 및 상세보기
  - 좋아요 및 공유 기능

- **소셜 기능**
  - 사용자 검색
  - 팔로우 / 언팔로우 / 차단 / 신고

- **팀 기능**
  - 팀 프로필 생성 및 관리
  - 팀 기반 팔로우 및 초대 기능
  - 팀 채팅 지원

- **채팅 기능**
  - 개인 채팅 및 팀 채팅

- **위치 기반 기능**
  - 주소 검색 및 현재 위치 기반 주소 조회
  - 게시물 노출 위치 설정 및 관리

- **프로필 관리**
  - 개인/팀 프로필 전환 및 편집
  - 계정 및 팀 삭제

- **딥링크**
  - 링크를 통한 앱 내 특정 게시물 상세 페이지 진입

## 스크린샷
<img width="24%" alt="android-post" src="https://github.com/user-attachments/assets/a641bbc0-4604-44c9-b39d-328ce1f116ec" />
<img width="24%" alt="android-userprofile" src="https://github.com/user-attachments/assets/4bd74552-ac6d-4251-aa7b-352c3ee5c772" />
<img width="24%" alt="android-teamprofile" src="https://github.com/user-attachments/assets/21fc943d-5d62-427b-9128-3c29ba9b75fb" />
<img width="24%" alt="android-teamchat" src="https://github.com/user-attachments/assets/f225cb69-b3e9-44cd-916f-0a036042e05d" />

## 기술 스택
- **Language & UI**: Kotlin, Jetpack Compose
- **Architecture**: MVVM
- **Navigation**: Navigation Compose
- **Dependency Injection**: Hilt
- **Async & State Management**: Kotlin Coroutines, Flow, StateFlow
- **Networking & Serialization**: Ktor Client, Kotlinx Serialization
- **Storage & Security**: DataStore, Android Security Crypto
- **Media**: Coil, Media3 ExoPlayer
- **Location**: Google Play Services Location
- **Chat**: Stream Chat SDK

## 구조
- MVVM 기반 구조
- ViewModel을 통한 화면 상태 및 비즈니스 로직 관리
- API 모듈을 기능별로 분리하여 네트워크 요청 관리
