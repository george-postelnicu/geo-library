CREATE TABLE book_author
(
    book_id   BIGINT NOT NULL,
    author_id BIGINT NOT NULL,
    PRIMARY KEY (book_id, author_id),
    FOREIGN KEY (book_id) REFERENCES book (id),
    FOREIGN KEY (author_id) REFERENCES author (id)
);

CREATE TABLE book_keyword
(
    book_id    BIGINT NOT NULL,
    keyword_id BIGINT NOT NULL,
    PRIMARY KEY (book_id, keyword_id),
    FOREIGN KEY (book_id) REFERENCES book (id),
    FOREIGN KEY (keyword_id) REFERENCES keyword (id)
);

CREATE TABLE book_language
(
    book_id     BIGINT NOT NULL,
    language_id BIGINT NOT NULL,
    PRIMARY KEY (book_id, language_id),
    FOREIGN KEY (book_id) REFERENCES book (id),
    FOREIGN KEY (language_id) REFERENCES language (id)
);
