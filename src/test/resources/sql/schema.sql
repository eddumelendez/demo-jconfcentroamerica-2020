USE demodb;

CREATE TABLE communities
(
    id    BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    alias VARCHAR(255),
    name  VARCHAR(255)
);
