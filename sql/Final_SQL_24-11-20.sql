ALTER TABLE weight
MODIFY COLUMN body_fat_percentage FLOAT;
ALTER TABLE body_fat_percentage
MODIFY COLUMN body_fat_percentage FLOAT;
ALTER TABLE calendar
MODIFY COLUMN body_fat_percentage FLOAT;

DROP DATABASE final_prj;
CREATE DATABASE final_prj;
USE final_prj;

-- User 테이블
-- 회원 정보를 저장하는 테이블
CREATE TABLE User (
    mem_no BIGINT AUTO_INCREMENT PRIMARY KEY, -- 회원 번호 (고유값)
    email VARCHAR(100) UNIQUE NOT NULL, -- 이메일 (고유값, 반드시 입력)
    password VARCHAR(100) NOT NULL, -- 비밀번호 (반드시 입력)
    name VARCHAR(50) NOT NULL, -- 이름 (반드시 입력)
    height FLOAT, -- 키 (선택 입력)
    gender CHAR(1), -- 성별 (1자리 문자: 'M' 또는 'F')
    birthday DATE, -- 생년월일 (DATE 타입으로 변경)
    phone_number VARCHAR(20), -- 전화번호 (선택 입력)
    nickname VARCHAR(100), -- 닉네임 (선택 입력)
    role VARCHAR(20), -- 권한
    last_act_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- 마지막 활동 날짜 (자동 갱신)
    is_active BOOLEAN NOT NULL DEFAULT TRUE, -- 계정 활성화 여부 (기본값: TRUE)
    join_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP -- 가입 날짜 (현재 시간으로 기본값 설정)
);

-- Refresh_Tokens 테이블
-- 사용자의 리프레시 토큰 정보를 저장하는 테이블
CREATE TABLE Refresh_Tokens (
    token_no BIGINT AUTO_INCREMENT PRIMARY KEY,  -- 토큰 번호 (고유값)
    refresh_token TEXT NOT NULL,    -- 리프레시 토큰 (고유값)
    expiration TIMESTAMP,          -- 만료 시간 (기본값 없음, 생성 시 비즈니스 로직에 따라 설정)
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- 생성 시간 (현재 시간으로 기본값 설정)
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- 갱신 시간 (수정 시 자동으로 현재 시간으로 갱신)
    mem_no BIGINT,                -- 회원 번호
    FOREIGN KEY (mem_no) REFERENCES User(mem_no) -- User 테이블의 mem_no를 참조
);

-- SNS_Login 테이블
-- 사용자의 SNS 로그인 정보를 저장하는 테이블
CREATE TABLE SNS_Login (
    sns_no BIGINT AUTO_INCREMENT PRIMARY KEY, -- SNS 로그인 번호 (고유값)
    sns_type VARCHAR(50) NOT NULL, -- SNS 유형 (예: Google, Facebook)
    sns_id VARCHAR(100) NOT NULL, -- SNS 계정 ID
    sns_email VARCHAR(100), -- SNS 이메일 (선택 입력)
    mem_no BIGINT, -- 회원 번호
    UNIQUE(sns_id, sns_type), -- SNS ID와 유형의 조합이 고유해야 함
    FOREIGN KEY (mem_no) REFERENCES User(mem_no) -- User 테이블의 mem_no를 참조
);

-- Food 테이블
-- 음식 정보를 저장하는 테이블
CREATE TABLE Food (
    food_no BIGINT AUTO_INCREMENT PRIMARY KEY, -- 음식 번호 (고유값)
    food_name VARCHAR(100), -- 음식 이름
    food_amt FLOAT, -- 음식 양 (g)
    food_carb FLOAT, -- 탄수화물 함량 (g)
    food_protein FLOAT, -- 단백질 함량 (g)
    food_fat FLOAT, -- 지방 함량 (g)
    food_calorie FLOAT, -- 칼로리 (kcal)
    mem_no BIGINT, -- 작성자 회원 번호
    is_shared BOOLEAN NOT NULL DEFAULT FALSE, -- 공유 여부 (기본값: FALSE)
    FOREIGN KEY (mem_no) REFERENCES User(mem_no) -- User 테이블의 mem_no를 참조
);

-- Food_Like_Dislike 테이블
-- 사용자가 음식에 대해 좋아요/싫어요를 표시한 기록을 저장하는 테이블
CREATE TABLE Food_Like_Dislike (
    like_dislike_no BIGINT AUTO_INCREMENT PRIMARY KEY, -- 좋아요/싫어요 번호 (고유값)
    food_no BIGINT, -- 음식 번호
    mem_no BIGINT, -- 회원 번호
    status TINYINT DEFAULT 0, -- 상태 (좋아요: 1, 싫어요: -1, 0 등)
    create_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- 생성 시각
    FOREIGN KEY (food_no) REFERENCES Food(food_no), -- Food 테이블의 food_no를 참조
    FOREIGN KEY (mem_no) REFERENCES User(mem_no) -- User 테이블의 mem_no를 참조
);

-- Diet_Temp 테이블
-- 사용자가 만든 식단 템플릿 정보를 저장하는 테이블
CREATE TABLE Diet_Temp (
    diet_temp_no BIGINT AUTO_INCREMENT PRIMARY KEY, -- 식단 템플릿 번호 (고유값)
    mem_no BIGINT, -- 회원 번호
    diet_temp_name VARCHAR(50), -- 템플릿 이름
    is_shared BOOLEAN NOT NULL DEFAULT FALSE, -- 공유 여부 (기본값: TRUE)
    FOREIGN KEY (mem_no) REFERENCES User(mem_no) -- User 테이블의 mem_no를 참조
);

-- Diet 테이블
-- 사용자의 식단 기록을 저장하는 테이블
CREATE TABLE Diet (
    diet_no BIGINT AUTO_INCREMENT PRIMARY KEY, -- 식단 번호 (고유값)
    mem_no BIGINT, -- 회원 번호
    food_no BIGINT, -- 음식 번호
    -- diet_temp_no BIGINT, -- 식단 템플릿 번호
    diet_type INT, -- 식단 종류 (예: 아침, 점심, 저녁, 간식 -> 0, 1, 2, 3)
    food_quantity FLOAT, -- 음식의 양
    date TIMESTAMP,
    FOREIGN KEY (mem_no) REFERENCES User(mem_no), -- User 테이블의 mem_no를 참조
    FOREIGN KEY (food_no) REFERENCES Food(food_no), -- Food 테이블의 food_no를 참조
    FOREIGN KEY (diet_temp_no) REFERENCES Diet_Temp(diet_temp_no) -- Diet_Temp 테이블의 diet_temp_no를 참조
);

-- Act 테이블: 운동 정보를 저장하는 테이블
CREATE TABLE Act (
    act_no BIGINT AUTO_INCREMENT PRIMARY KEY, -- 운동 번호 (고유값)
    mem_no BIGINT, -- 회원 번호
    act_name VARCHAR(20), -- 운동 이름
    act_inten FLOAT, -- 운동 시간 (분 단위)
    act_calorie FLOAT, -- 소모 칼로리 (kcal)
    is_shared BOOLEAN NOT NULL DEFAULT FALSE, -- 공유 여부 (기본값: FALSE)
    FOREIGN KEY (mem_no) REFERENCES User(mem_no) -- User 테이블의 mem_no를 참조
);

-- Act_Like_Dislike 테이블: 사용자가 운동에 대해 좋아요/싫어요를 표시한 기록을 저장하는 테이블
CREATE TABLE Act_Like_Dislike (
    like_dislike_no INT AUTO_INCREMENT PRIMARY KEY, -- 좋아요/싫어요 번호 (고유값)
    act_no BIGINT, -- 운동 번호 (외래키, Act 테이블 참조)
    mem_no BIGINT, -- 회원 번호 (외래키, User 테이블 참조)
    status BIGINT DEFAULT 0, -- 상태 (좋아요: 1, 싫어요: -1 등)
    create_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- 생성 시각
    update_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- 수정 시각, 수정 시 자동 업데이트
    FOREIGN KEY (act_no) REFERENCES Act(act_no), -- Act 테이블의 act_no를 참조
    FOREIGN KEY (mem_no) REFERENCES User(mem_no) -- User 테이블의 mem_no를 참조
);

-- Act_Temp 테이블
-- 운동 템플릿 정보를 저장하는 테이블
CREATE TABLE Act_Temp (
    act_temp_no BIGINT AUTO_INCREMENT PRIMARY KEY, -- 운동 템플릿 번호 (고유값)
    mem_no BIGINT, -- 회원 번호
    act_temp_name VARCHAR(50), -- 운동 템플릿 이름
    is_shared BOOLEAN NOT NULL DEFAULT FALSE, -- 공유 여부 (기본값: FALSE)
    FOREIGN KEY (mem_no) REFERENCES User(mem_no) -- User 테이블의 mem_no를 참조
);

-- Save_Act 테이블: 사용자가 저장한 운동 기록을 관리하는 테이블
CREATE TABLE Save_Act (
    save_act_no BIGINT AUTO_INCREMENT PRIMARY KEY, -- 운동 저장 번호 (고유값)
    act_time FLOAT, -- 운동 시간 (분 단위)
    mem_no BIGINT, -- 회원 번호
    act_no BIGINT, -- 운동 번호
    act_temp_no BIGINT, -- 운동 템플릿 번호
    FOREIGN KEY (mem_no) REFERENCES User(mem_no), -- User 테이블의 mem_no를 참조
    FOREIGN KEY (act_no) REFERENCES Act(act_no), -- Act 테이블의 act_no를 참조
    FOREIGN KEY (act_temp_no) REFERENCES Act_Temp(act_temp_no) -- Act_Temp 테이블의 act_temp_no를 참조
);

-- Calendar 테이블
-- 사용자 캘린더 정보를 저장하는 테이블
CREATE TABLE Calendar (
    calendar_no BIGINT AUTO_INCREMENT PRIMARY KEY, -- 캘린더 번호 (고유값)
    date DATE,
    mem_no BIGINT, -- 회원 번호
    weight FLOAT, -- 몸무게
    skeletal_muscle_mass FLOAT, -- 골격근량
    body_fat_percentage FLOAT, -- 체지방률
    diet_no BIGINT, -- 식단 번호
    save_act_no BIGINT, -- 활동 저장 번호
    FOREIGN KEY (mem_no) REFERENCES User(mem_no), -- User 테이블의 mem_no를 참조
    FOREIGN KEY (diet_no) REFERENCES Diet(diet_no), -- Diet 테이블의 diet_no를 참조
    FOREIGN KEY (save_act_no) REFERENCES Save_Act(save_act_no) -- Save_Act 테이블의 save_act_no를 참조
);

-- Post 테이블
-- 게시물 정보를 저장하는 테이블
CREATE TABLE Post (
   post_no BIGINT AUTO_INCREMENT PRIMARY KEY, -- 게시물 번호 (고유값)
   post_title VARCHAR(255), -- 게시물 제목
   post_content TEXT, -- 게시물 내용
   post_created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- 생성 날짜
   post_updated_date TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- 수정 날짜 (수정 시 자동으로 갱신)
   post_views INT DEFAULT 0, -- 조회수 (기본값: 0)
   mem_no BIGINT, -- 회원 번호 (작성자)
   FOREIGN KEY (mem_no) REFERENCES User(mem_no) -- User 테이블의 mem_no를 참조 (작성자 정보)
);

-- File_Upload 테이블
-- 파일 업로드 정보를 저장하는 테이블
CREATE TABLE File_Upload (
   file_no BIGINT AUTO_INCREMENT PRIMARY KEY, -- 파일 번호 (고유값)
   file_name VARCHAR(255), -- 파일 이름
   file_type VARCHAR(50), -- 파일 유형 (예: 이미지, 문서)
   file_size BIGINT, -- 파일 크기 (바이트 단위)
   file_path VARCHAR(255), -- 파일 경로 (파일이 저장된 위치)
   upload_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- 업로드 날짜 (기본값: 현재 시간)
   updated_date TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- 수정 날짜 (수정 시 자동으로 갱신)
   mem_no BIGINT, -- 회원 번호 (업로드한 회원)
   post_no BIGINT, -- 게시물 번호 (파일이 연결된 게시물)
   FOREIGN KEY (mem_no) REFERENCES User(mem_no), -- User 테이블의 mem_no를 참조 (업로드한 회원)
   FOREIGN KEY (post_no) REFERENCES Post(post_no) -- Post 테이블의 post_no를 참조 (게시물 정보)
);

-- Comment 테이블
-- 댓글 정보를 저장하는 테이블
CREATE TABLE Comment (
   comment_no BIGINT AUTO_INCREMENT PRIMARY KEY, -- 댓글 번호 (고유값)
   comment_content VARCHAR(255), -- 댓글 내용
   comment_created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- 생성 날짜 (기본값: 현재 시간)
   comment_updated_date TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- 수정 날짜 (수정 시 자동으로 갱신)
   comment_parent_no BIGINT DEFAULT 0, -- 부모 댓글 번호 (최초 댓글의 경우 0, 대댓글의 경우 부모 댓글 번호)
   mem_no BIGINT, -- 회원 번호 (댓글 작성자)
   post_no BIGINT, -- 게시물 번호 (댓글이 작성된 게시물)
   FOREIGN KEY (mem_no) REFERENCES User(mem_no), -- User 테이블의 mem_no를 참조 (댓글 작성자)
   FOREIGN KEY (post_no) REFERENCES Post(post_no), -- Post 테이블의 post_no를 참조 (댓글이 달린 게시물)
   FOREIGN KEY (comment_parent_no) REFERENCES Comment(comment_no) ON DELETE SET NULL -- 부모 댓글 삭제 시 자식 댓글도 삭제됨
);

-- Like_Dislike 테이블
-- 다양한 엔터티에 대한 좋아요/싫어요를 저장하는 테이블
CREATE TABLE Like_Dislike (
   like_dislike_no BIGINT AUTO_INCREMENT PRIMARY KEY, -- 좋아요/싫어요 번호 (고유값)
   entity_type VARCHAR(10), -- 엔터티 유형 (예: "Post", "Comment" 등)
   entity_no BIGINT, -- 엔터티 번호 (게시물 번호 또는 댓글 번호 등)
   mem_no BIGINT, -- 회원 번호 (좋아요/싫어요를 한 회원)
   status BIGINT DEFAULT 0, -- 상태 (좋아요: 1, 싫어요: 0, 기타 상태 값 등)
   create_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- 생성 시각 (기본값: 현재 시간)
   update_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- 수정 시각 (수정 시 자동으로 갱신)
   FOREIGN KEY (mem_no) REFERENCES User(mem_no) -- User 테이블의 mem_no를 참조 (좋아요/싫어요를 한 회원)
);

-- Supplement_Category 테이블
-- 보충제 카테고리 정보를 저장하는 테이블
CREATE TABLE Supplement_Category (
   category_no BIGINT AUTO_INCREMENT PRIMARY KEY, -- 카테고리 번호 (고유값)
   category_name VARCHAR(50), -- 카테고리 이름 (예: "단백질", "비타민" 등)
   amount_intake VARCHAR(100), -- 섭취량 (예: "1회 20g")
   category_comment VARCHAR(100), -- 카테고리 코멘트 (예: "운동 후 섭취")
   category_tier BIGINT -- 카테고리 순위 (우선순위나 등급을 나타내는 값)
);

-- Supplement 테이블
-- 보충제 정보를 저장하는 테이블
CREATE TABLE Supplement (
   sup_no BIGINT AUTO_INCREMENT PRIMARY KEY, -- 보충제 번호 (고유값)
   sup_name VARCHAR(50), -- 보충제 이름
   sup_url VARCHAR(200), -- 보충제 URL (구매 링크 등)
   category_no BIGINT, -- 보충제 카테고리 번호
   FOREIGN KEY (category_no) REFERENCES Supplement_Category(category_no) -- Supplement_Category 테이블의 category_no를 참조
);