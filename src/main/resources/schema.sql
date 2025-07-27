CREATE TABLE blocked_extensions (
    id INT PRIMARY KEY AUTO_INCREMENT,
    ext VARCHAR(32) NOT NULL,
    register_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP()
);

CREATE TABLE attache_files (
    id INT PRIMARY KEY AUTO_INCREMENT,
    original_filename VARCHAR(128) NOT NULL,
    stored_filename VARCHAR(128) NOT NULL,
    register_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP()
)