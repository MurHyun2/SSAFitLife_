INSERT into post(post_title, post_content, mem_no)
VALUES("배고프다 진짜", "오늘 맛있는거 나오냐??", 7);

SELECT * FROM diet;

select * from user;

select * from food;

select * from act;

desc act;

select * from save_act;

ALTER TABLE refresh_tokens DROP FOREIGN KEY refresh_tokens_ibfk_1;

ALTER TABLE refresh_tokens
ADD CONSTRAINT refresh_tokens_ibfk_1
FOREIGN KEY (mem_no) REFERENCES user (mem_no)
ON DELETE CASCADE;

INSERT INTO act (mem_no, act_name, act_inten) VALUES (4, '수면', 0.9);
INSERT INTO act (mem_no, act_name, act_inten) VALUES (4, '좌식 업무', 1.3);
INSERT INTO act (mem_no, act_name, act_inten) VALUES (4, '가벼운 걷기', 2.5);
INSERT INTO act (mem_no, act_name, act_inten) VALUES (4, '식사 및 휴식', 1.5);
INSERT INTO act (mem_no, act_name, act_inten) VALUES (4, '가벼운 운동', 3.0);
INSERT INTO act (mem_no, act_name, act_inten) VALUES (4, 'TV 시청/독서', 1.0);
INSERT INTO act (mem_no, act_name, act_inten) VALUES (4, '중강도 운동', 5.0);
INSERT INTO act (mem_no, act_name, act_inten) VALUES (4, '일상 활동', 2.0);
INSERT INTO act (mem_no, act_name, act_inten) VALUES (4, '여가 활동', 1.8);
INSERT INTO act (mem_no, act_name, act_inten) VALUES (4, '재택 업무', 1.2);
INSERT INTO act (mem_no, act_name, act_inten) VALUES (4, '집안일', 2.5);
INSERT INTO act (mem_no, act_name, act_inten) VALUES (4, '스트레칭/요가', 2.5);
INSERT INTO act (mem_no, act_name, act_inten) VALUES (4, '휴식', 1.0);

INSERT INTO act (mem_no, act_name, act_inten, act_calorie, is_shared) VALUES
(1, '수면', 0.1, 50, 0),
(1, '좌식 업무', 1.2, 100, 0),
(1, '가벼운 걷기', 3.0, 200, 0),
(1, '식사 및 휴식', 1.5, 150, 0),
(1, '가벼운 운동', 4.0, 300, 0),
(1, 'TV 시청/독서', 1.2, 80, 0),
(1, '중강도 운동', 6.0, 500, 0),
(1, '일상 활동', 3.0, 400, 0),
(1, '여가 활동', 2.5, 180, 0),
(1, '재택 업무', 1.5, 120, 0),
(1, '집안일', 3.5, 250, 0),
(1, '스트레칭/요가', 2.0, 180, 0),
(1, '휴식', 1.2, 80, 0);

desc act;

SELECT act_no, mem_no, act_name, act_inten, act_calorie, is_shared, (SELECT COUNT(*) FROM save_act WHERE act_no = act.act_no) as actAddCount
        FROM act
        WHERE mem_no = 1
        ORDER BY act_no DESC;

SELECT act_no, mem_no, act_name, act_inten, act_calorie, is_shared,
            (
                SELECT COUNT(*)
                FROM save_act
                WHERE act_no = act.act_no
            ) AS actAddCount
        FROM act
        ORDER BY actAddCount DESC;
        
        SELECT *, (SELECT SUM(status)
                    FROM Food_Like_Dislike
                    WHERE food_no = Food.food_no) sumLikeDislike,
                    (SELECT COUNT(*)
                FROM diet
                WHERE food_no = food.food_no
            ) AS foodAddCount
        FROM Food
        ORDER BY foodAddCount DESC;

select count(*) from save_act where act_no = 32;

select * from calendar;

select * from save_act;

delete from save_act
WHERE mem_no = 12;

select * from user;

delete from user where mem_no = 13;

ALTER TABLE save_act
ADD COLUMN total_metabolism INT;

ALTER TABLE save_act
ADD COLUMN act_date DATETIME;

select * from save_act;

select * from user;

ALTER TABLE refresh_tokens DROP FOREIGN KEY refresh_tokens_ibfk_1;

ALTER TABLE refresh_tokens
ADD CONSTRAINT refresh_tokens_ibfk_1
FOREIGN KEY (mem_no) REFERENCES user (mem_no)
ON DELETE CASCADE;

SELECT CONSTRAINT_NAME
FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE
WHERE TABLE_NAME = 'save_act';

ALTER TABLE save_act DROP FOREIGN KEY save_act_ibfk_3;

desc food;

select * from calendar;

select * from save_act;

select * from act;

select * from act_temp;

desc act;

delete from diet
WHERE food_no NOT IN (select food_no from food);
;

desc act_temp;

ALTER TABLE diet
ADD CONSTRAINT FK_diet_food_no
FOREIGN KEY (food_no) 
REFERENCES food(food_no)
ON DELETE CASCADE;

ALTER TABLE food
DROP FOREIGN KEY food_ibfk_1;

INSERT INTO act (mem_no, act_name, act_inten, act_calorie, is_shared) VALUES
(1, '걷기', 3.0, 150.0, 1),
(1, '달리기', 8.0, 400.0, 1),
(1, '수영', 6.0, 300.0, 1),
(1, '자전거', 5.0, 250.0, 1),
(1, '등산', 7.0, 350.0, 1),
(1, '요가', 2.5, 120.0, 1),
(1, '필라테스', 3.5, 180.0, 1),
(1, '웨이트', 4.0, 200.0, 1),
(1, '축구', 7.5, 375.0, 1),
(1, '농구', 7.0, 350.0, 1);

INSERT INTO act_temp (mem_no, act_temp_name, is_shared) VALUES
(1, '아침 루틴', 1),
(1, '저녁 루틴', 1),
(1, '주말 운동', 1),
(1, '상체 운동', 0),
(1, '하체 운동', 0),
(2, '다이어트 운동', 1),
(2, '체력 강화', 1),
(3, '아침 스트레칭', 1),
(3, '점심 산책', 0),
(3, '저녁 요가', 1);

-- 변수 선언 및 초기화
SET @start_date = CURDATE() - INTERVAL 30 DAY;

-- 운동 기록 생성을 위한 프로시저
DELIMITER //
CREATE PROCEDURE generate_save_act_data()
BEGIN
    DECLARE i INT DEFAULT 0;
    DECLARE curr_date DATE;
    SET curr_date = CURDATE() - INTERVAL 30 DAY;
    
    WHILE i < 30 DO
        -- 매일 2-3개의 운동 기록 생성
        INSERT INTO save_act (act_time, mem_no, act_no, act_temp_no, act_date)
        SELECT 
            ROUND(0.5 + RAND() * 2, 1), -- 0.5~2.5시간
            1 + ROUND(RAND() * 2), -- mem_no 1~3
            1 + ROUND(RAND() * 9), -- act_no 1~10
            1 + ROUND(RAND() * 4), -- act_temp_no 1~5
            curr_date + INTERVAL i DAY;
            
        SET i = i + 1;
    END WHILE;
END //
DELIMITER ;

-- 프로시저 실행
CALL generate_save_act_data();

-- 프로시저 삭제
DROP PROCEDURE IF EXISTS generate_save_act_data;


INSERT INTO food (food_name, food_amt, food_carb, food_protein, food_fat, food_calorie, mem_no, is_shared) VALUES
('김치찌개', 500, 20.5, 15.3, 12.2, 350, 1, 1),
('불고기', 200, 8.3, 25.4, 14.6, 250, 2, 1),
('비빔밥', 450, 60.7, 20.2, 10.5, 500, 3, 0),
('된장찌개', 400, 15.2, 18.3, 8.1, 200, 4, 1),
('삼겹살', 150, 0, 20.5, 30.2, 350, 5, 0),
('닭가슴살 샐러드', 300, 10.2, 35.6, 5.5, 220, 6, 1),
('제육볶음', 250, 15.3, 18.7, 14.3, 280, 7, 1),
('오므라이스', 350, 55.3, 15.4, 12.2, 420, 8, 0),
('갈비탕', 400, 5.4, 30.5, 10.6, 280, 9, 1),
('라면', 300, 45.2, 10.3, 15.7, 400, 10, 0),
('초밥', 200, 40.5, 15.3, 5.6, 300, 1, 1),
('떡볶이', 350, 50.2, 7.3, 12.5, 350, 2, 0),
('파스타', 400, 60.7, 12.2, 18.6, 480, 3, 1),
('햄버거', 300, 30.2, 20.4, 25.3, 550, 4, 0),
('샌드위치', 250, 35.4, 18.5, 12.4, 330, 5, 1),
('피자', 350, 40.3, 18.6, 22.5, 450, 6, 0),
('순두부찌개', 450, 10.5, 20.6, 9.4, 180, 7, 1),
('치킨', 500, 20.5, 40.3, 30.2, 600, 8, 0),
('우동', 400, 50.4, 10.3, 8.7, 310, 9, 1),
('카레라이스', 400, 55.3, 18.5, 12.2, 380, 10, 0),
('샐러드', 150, 5.3, 8.4, 2.5, 80, 1, 1),
('볶음밥', 350, 60.2, 12.6, 15.3, 420, 2, 0),
('콩나물국', 300, 8.5, 10.3, 2.1, 90, 3, 1),
('감자튀김', 200, 30.5, 3.6, 20.5, 350, 4, 0),
('짜장면', 400, 65.3, 15.4, 18.2, 500, 5, 1),
('짬뽕', 450, 50.2, 20.5, 15.6, 480, 6, 1),
('탕수육', 300, 40.3, 18.7, 20.5, 420, 7, 0),
('김밥', 250, 35.5, 10.3, 8.6, 300, 8, 1),
('쌀국수', 400, 45.6, 15.3, 10.2, 350, 9, 1),
('된장국', 350, 12.4, 10.6, 5.3, 200, 10, 0),
('비엔나소시지', 150, 3.4, 8.5, 12.5, 180, 1, 1),
('두부김치', 300, 15.3, 20.5, 12.4, 250, 2, 1),
('갈비찜', 450, 12.5, 35.4, 18.7, 350, 3, 0),
('부대찌개', 400, 25.3, 30.5, 20.2, 500, 4, 1),
('냉면', 350, 45.5, 15.6, 8.5, 300, 5, 0),
('잡채', 300, 40.4, 10.6, 14.3, 350, 6, 1),
('양념치킨', 400, 30.5, 25.6, 20.3, 450, 7, 0),
('고등어구이', 200, 0, 25.5, 15.4, 280, 8, 1),
('회덮밥', 350, 40.3, 30.5, 8.4, 400, 9, 0),
('팥죽', 300, 50.2, 8.6, 4.3, 200, 10, 1),
('호떡', 200, 35.6, 5.4, 10.5, 280, 1, 1),
('찜닭', 450, 25.3, 35.4, 18.7, 480, 2, 0),
('굴국밥', 350, 10.4, 25.6, 8.5, 300, 3, 1),
('양파링', 150, 20.5, 2.6, 10.4, 180, 4, 0),
('호박죽', 300, 45.3, 8.5, 6.4, 220, 5, 1),
('매운탕', 400, 12.5, 30.4, 10.5, 300, 6, 0),
('크림파스타', 400, 60.7, 18.4, 20.5, 550, 7, 1),
('미역국', 350, 15.4, 20.5, 8.6, 200, 8, 0),
('토스트', 250, 30.5, 10.2, 15.3, 350, 9, 1),
('청국장', 350, 18.4, 20.3, 12.5, 250, 10, 1);


alter table diet;

select * from post;

desc post;

desc food;

select * from user;

INSERT INTO post (post_title, post_content, post_created_date, post_updated_date, post_views, mem_no) VALUES
('첫 번째 게시글', '이것은 첫 번째 게시글 내용입니다.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 10, 1),
('두 번째 게시글', '두 번째 게시글의 상세 내용을 여기에 작성합니다.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 20, 2),
('맛집 추천', '서울에 있는 맛집을 추천합니다.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 35, 3),
('여행 후기', '부산 여행을 다녀온 후기를 작성합니다.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 45, 4),
('IT 트렌드', '2024년 IT 트렌드 분석.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 12, 5),
('취미 공유', '나의 취미생활을 공유합니다.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 22, 6),
('영화 리뷰', '최근 개봉한 영화를 리뷰합니다.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 18, 7),
('독서 일기', '최근 읽은 책의 감상평입니다.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 9, 8),
('운동 기록', '헬스장에서의 운동 기록을 공유합니다.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 27, 9),
('자기계발 팁', '자기계발에 도움이 되는 팁을 공유합니다.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 11, 10),
('프로그래밍 이야기', '프로그래밍 관련 경험을 나눕니다.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 31, 1),
('음악 추천', '최근에 들은 좋은 음악을 추천합니다.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 19, 2),
('드라마 감상', '인기 드라마 감상평.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 14, 3),
('커리어 고민', '직장에서의 커리어 고민을 공유합니다.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 23, 4),
('패션 리뷰', '트렌디한 패션 스타일을 리뷰합니다.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 20, 5),
('경제 분석', '최근 경제 동향을 분석합니다.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 33, 6),
('맛집 탐방', '최근 방문한 맛집 탐방 후기입니다.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 41, 7),
('코딩 팁', '유용한 코딩 팁과 노하우.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 16, 8),
('스포츠 이야기', '내가 좋아하는 스포츠에 대한 이야기.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 25, 9),
('사진 공유', '내가 찍은 사진을 공유합니다.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 30, 10),
('글쓰기 연습', '글쓰기 연습을 위한 게시글.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 17, 1),
('여행 계획', '다음 여행 계획을 세우고 있습니다.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 22, 2),
('게임 리뷰', '최근 플레이한 게임에 대한 리뷰입니다.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 15, 3),
('일상 이야기', '평범한 하루의 일상을 나눕니다.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 28, 4),
('건강 팁', '건강을 위한 유익한 팁입니다.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 14, 5),
('요리 레시피', '간단한 요리 레시피 공유합니다.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 35, 6),
('자기소개', '안녕하세요, 자기소개합니다.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 10, 7),
('책 추천', '강력 추천하는 책을 소개합니다.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 21, 8),
('반려동물 이야기', '내 반려동물에 대한 이야기.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 32, 9),
('운동 루틴', '나만의 운동 루틴을 공유합니다.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 20, 10),
('여행지 추천', '가보고 싶은 여행지를 추천합니다.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 14, 1),
('음식 후기', '맛본 음식 후기를 남깁니다.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 25, 2),
('사진 촬영 팁', '사진 잘 찍는 법을 알려드립니다.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 18, 3),
('개발자 이야기', '개발자로서의 경험을 나눕니다.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 31, 4);


update user
set nickname = 'a'
where email = 'a@a';

select * from food
WHERE mem_no = 1;


desc user;

desc diet;

SELECT CONSTRAINT_NAME
FROM information_schema.KEY_COLUMN_USAGE
WHERE TABLE_NAME = 'diet' -- 테이블 이름에 맞게 수정하세요.
AND COLUMN_NAME = 'diet_temp_no';

SELECT SUM(status)
        FROM Food_Like_Dislike
        WHERE food_no = 1;
        
INSERT INTO Food (food_name, food_amt, food_carb, food_protein, food_fat, food_calorie, mem_no, is_shared) VALUES
('Chicken Breast', 200, 0, 46, 2, 220, 1, TRUE),
('Brown Rice', 150, 34, 3, 1, 170, 2, FALSE),
('Greek Yogurt', 100, 6, 10, 0, 60, 3, TRUE),
('Apple', 180, 25, 0, 0, 95, 4, FALSE),
('Salmon Fillet', 150, 0, 34, 10, 280, 5, TRUE),
('Banana', 120, 27, 1, 0, 105, 1, FALSE),
('Broccoli', 80, 6, 3, 0, 30, 2, TRUE),
('Almonds', 50, 6, 10, 22, 300, 3, FALSE),
('Oatmeal', 100, 12, 5, 2, 120, 4, TRUE),
('Eggs', 70, 1, 6, 5, 70, 5, FALSE);

INSERT INTO Food_Like_Dislike (food_no, mem_no, status) VALUES
(1, 1, 1),   -- Chicken Breast 좋아요
(2, 2, -1),  -- Brown Rice 싫어요
(3, 3, 1),   -- Greek Yogurt 좋아요
(4, 4, 0),   -- Apple 중립
(5, 5, 1),   -- Salmon Fillet 좋아요
(6, 1, 1),   -- Banana 좋아요
(7, 2, -1),  -- Broccoli 싫어요
(8, 3, 1),   -- Almonds 좋아요
(9, 4, 0),   -- Oatmeal 중립
(10, 5, -1); -- Eggs 싫어요

INSERT INTO Food_Like_Dislike (food_no, mem_no, status) VALUES
(1 , 2, 1),
(1 , 3, 1),
(1 , 4, 1),
(1 , 5, -1);

SELECT * FROM
Food_Like_Dislike;

INSERT INTO Diet_Temp (mem_no, diet_temp_name, is_shared) VALUES
(1, 'High Protein Plan', TRUE),
(2, 'Low Carb Plan', FALSE),
(3, 'Balanced Diet', TRUE),
(4, 'Vegan Diet', FALSE),
(5, 'Keto Plan', TRUE),
(1, 'Weight Gain Plan', TRUE),
(2, 'Weight Loss Plan', FALSE),
(3, 'Mediterranean Diet', TRUE),
(4, 'Paleo Plan', FALSE),
(5, 'Intermittent Fasting', TRUE);


INSERT INTO Diet (mem_no, food_no, diet_temp_no, diet_type, food_quantity) VALUES
-- High Protein Plan (diet_temp_no = 1)
(1, 1, 1, 0, 2),   -- 아침: Chicken Breast 200g
(1, 3, 1, 0, 1),   -- 아침: Greek Yogurt 150g
(1, 5, 1, 1, 1),   -- 점심: Salmon Fillet 150g
(1, 7, 1, 1, 2),   -- 점심: Broccoli 100g
(1, 8, 1, 2, 1),    -- 저녁: Almonds 50g
(1, 6, 1, 2, 1.5),   -- 저녁: Banana 120g

-- Low Carb Plan (diet_temp_no = 2)
(2, 2, 2, 0, 1.5),   -- 아침: Brown Rice 150g
(2, 9, 2, 0, 1),   -- 아침: Oatmeal 100g
(2, 3, 2, 1, 2),   -- 점심: Greek Yogurt 100g
(2, 8, 2, 1, 1),    -- 점심: Almonds 50g
(2, 4, 2, 2, 3),   -- 저녁: Apple 180g
(2, 7, 2, 2, 1),   -- 저녁: Broccoli 100g

-- Balanced Diet (diet_temp_no = 3)
(3, 4, 3, 0, 2),   -- 아침: Apple 180g
(3, 6, 3, 0, 2),   -- 아침: Banana 120g
(3, 5, 3, 1, 1),   -- 점심: Salmon Fillet 150g
(3, 2, 3, 1, 2.3),   -- 점심: Brown Rice 100g
(3, 9, 3, 2, 2),   -- 저녁: Oatmeal 100g
(3, 1, 3, 2, 1);   -- 저녁: Chicken Breast 200g

