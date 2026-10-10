CREATE TABLE IF NOT EXISTS users (
                                     user_id BIGINT NOT NULL AUTO_INCREMENT,
                                     login_id VARCHAR(50) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    created_at DATETIME(6) NOT NULL,

    PRIMARY KEY (user_id),

    CONSTRAINT uq_users_login_id
    UNIQUE (login_id)
    ) ENGINE=InnoDB
    DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS haikus (
                                      haiku_id BIGINT NOT NULL AUTO_INCREMENT,
                                      user_id BIGINT NOT NULL,
                                      text VARCHAR(20) NOT NULL,
    season VARCHAR(10) NOT NULL,
    status VARCHAR(10) NOT NULL,
    self_evaluation VARCHAR(10) NOT NULL DEFAULT '無印',
    points INT NULL,
    memo TEXT,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,

    PRIMARY KEY (haiku_id),

    CONSTRAINT fk_haikus_user
    FOREIGN KEY (user_id) REFERENCES users (user_id),

    CONSTRAINT chk_haikus_season
    CHECK (season IN ('春', '夏', '秋', '冬')),

    CONSTRAINT chk_haikus_status
    CHECK (status IN ('推敲中', '完成')),

    CONSTRAINT chk_haikus_self_evaluation
    CHECK (self_evaluation IN ('◎', '○', '無印')),

    CONSTRAINT chk_haikus_points
    CHECK (points IS NULL OR points BETWEEN 1 AND 20),

    INDEX idx_haikus_user_created (user_id, created_at, haiku_id)
    ) ENGINE=InnoDB
    DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS remember_me_tokens (
                                                  token_id BIGINT NOT NULL AUTO_INCREMENT,
                                                  user_id BIGINT NOT NULL,
                                                  token_hash CHAR(64) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    expires_at DATETIME(6) NOT NULL,

    PRIMARY KEY (token_id),

    CONSTRAINT uq_remember_me_token_hash
    UNIQUE (token_hash),

    CONSTRAINT fk_remember_me_tokens_user
    FOREIGN KEY (user_id) REFERENCES users (user_id)
    ON DELETE CASCADE,

    INDEX idx_remember_me_tokens_user (user_id),
    INDEX idx_remember_me_tokens_expires (expires_at)
    ) ENGINE=InnoDB
    DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS meetings (
                                        meeting_id BIGINT NOT NULL AUTO_INCREMENT,
                                        user_id BIGINT NOT NULL,
                                        meeting_name VARCHAR(100) NOT NULL,
    meeting_date DATE NOT NULL,
    theme VARCHAR(100),
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,

    PRIMARY KEY (meeting_id),

    CONSTRAINT fk_meetings_user
    FOREIGN KEY (user_id) REFERENCES users (user_id),

    INDEX idx_meetings_user_date (
                                     user_id,
                                     meeting_date,
                                     meeting_id
                                 )
    ) ENGINE=InnoDB
    DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS meeting_haikus (
                                              meeting_haiku_id BIGINT NOT NULL AUTO_INCREMENT,
                                              meeting_id BIGINT NOT NULL,
                                              haiku_id BIGINT NOT NULL,
                                              points INT NULL,
                                              created_at DATETIME(6) NOT NULL,

    PRIMARY KEY (meeting_haiku_id),

    CONSTRAINT uq_meeting_haikus_meeting_haiku
    UNIQUE (meeting_id, haiku_id),

    CONSTRAINT fk_meeting_haikus_meeting
    FOREIGN KEY (meeting_id) REFERENCES meetings (meeting_id)
    ON DELETE CASCADE,

    CONSTRAINT fk_meeting_haikus_haiku
    FOREIGN KEY (haiku_id) REFERENCES haikus (haiku_id)
    ON DELETE CASCADE,

    CONSTRAINT chk_meeting_haikus_points
    CHECK (points IS NULL OR points BETWEEN 1 AND 20)
    ) ENGINE=InnoDB
    DEFAULT CHARSET=utf8mb4;