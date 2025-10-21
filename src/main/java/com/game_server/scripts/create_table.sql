-- Tạo database
CREATE DATABASE IF NOT EXISTS gamesapxep;
USE gamesapxep;

-- Bảng User
CREATE TABLE User (
                      id INT(10) PRIMARY KEY AUTO_INCREMENT,
                      username VARCHAR(100) UNIQUE NOT NULL,
                      password VARCHAR(20) NOT NULL,
                      nickname VARCHAR(255),
                      total_matches INT(10) DEFAULT 0,
                      total_wins INT(10) DEFAULT 0,
                      total_score INT(10) DEFAULT 0
);

-- Bảng Match
CREATE TABLE `Match` (
                         id INT(10) PRIMARY KEY AUTO_INCREMENT,
                         start_time TIMESTAMP NULL,
                         end_time TIMESTAMP NULL,
                         result VARCHAR(255),
                         status VARCHAR(255),
                         Userid1 INT(10),
                         Userid2 INT(10),
                         FOREIGN KEY (Userid1) REFERENCES User(id),
                         FOREIGN KEY (Userid2) REFERENCES User(id)
);

-- Bảng Round
CREATE TABLE Round (
                       id INT(10) PRIMARY KEY AUTO_INCREMENT,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       Matchid INT(10),
                       FOREIGN KEY (Matchid) REFERENCES `Match`(id)
);

-- Bảng RoundDetail
CREATE TABLE RoundDetail (
                             id INT(10) PRIMARY KEY AUTO_INCREMENT,
                             score INT(10),
                             is_winner INT(10),
                             Roundid INT(10),
                             Userid INT(10),
                             FOREIGN KEY (Roundid) REFERENCES Round(id),
                             FOREIGN KEY (Userid) REFERENCES User(id)
);

-- Bảng UserAnswer
CREATE TABLE UserAnswer (
                            id INT(10) PRIMARY KEY AUTO_INCREMENT,
                            time_completed INT(10),
                            status VARCHAR(255),
                            RoundDetailid INT(10),
                            FOREIGN KEY (RoundDetailid) REFERENCES RoundDetail(id)
);