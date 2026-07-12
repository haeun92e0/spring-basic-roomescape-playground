DROP TABLE IF EXISTS reservation CASCADE;
DROP TABLE IF EXISTS member CASCADE;
DROP TABLE IF EXISTS theme CASCADE;
DROP TABLE IF EXISTS time CASCADE;

CREATE TABLE time
(
    id         BIGINT      NOT NULL AUTO_INCREMENT,
    time_value VARCHAR(20) NOT NULL, //실제 시간 값
    deleted    BOOLEAN     NOT NULL DEFAULT FALSE, //논리 삭제로 구현
    PRIMARY KEY (id)
);

CREATE TABLE theme //방탈출 테마 정보 저장 테이블
(
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    name        VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL, //테마 설명
    deleted     BOOLEAN      NOT NULL DEFAULT FALSE, //논리 삭제
    PRIMARY KEY (id)
);

CREATE TABLE member //회원 정보 저장 테이블
(
    id       BIGINT              NOT NULL AUTO_INCREMENT,
    name     VARCHAR(255)        NOT NULL,
    email    VARCHAR(255) UNIQUE NOT NULL, //이메일은 중복 X
    password VARCHAR(255)        NOT NULL,
    role     VARCHAR(255)        NOT NULL, //회원 권한
    PRIMARY KEY (id)
);

CREATE TABLE reservation //예약 정보를 저장
(
    id       BIGINT       NOT NULL AUTO_INCREMENT,
    date     VARCHAR(255) NOT NULL,
    name     VARCHAR(255) NOT NULL,
    time_id  BIGINT, //reservation에서는 id로 저장, 참조하도록
    theme_id BIGINT,
    PRIMARY KEY (id),
    FOREIGN KEY (time_id) REFERENCES time (id),
    FOREIGN KEY (theme_id) REFERENCES theme (id)
);
//예약이 회원과 직접 연결되어있지는 않은 구조


INSERT INTO member (name, email, password, role)
VALUES ('어드민', 'admin@email.com', 'password', 'ADMIN'),
       ('브라운', 'brown@email.com', 'password', 'USER');

INSERT INTO theme (name, description)
VALUES ('테마1', '테마1입니다.'),
       ('테마2', '테마2입니다.'),
       ('테마3', '테마3입니다.');

INSERT INTO time (time_value)
VALUES ('10:00'),
       ('12:00'),
       ('14:00'),
       ('16:00'),
       ('18:00'),
       ('20:00');

INSERT INTO reservation (name, date, time_id, theme_id)
VALUES ('어드민', '2024-03-01', 1, 1),
       ('어드민', '2024-03-01', 2, 2),
       ('어드민', '2024-03-01', 3, 3);
