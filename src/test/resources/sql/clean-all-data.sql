SET foreign_key_checks = 0;

-- relations
delete from book_author;
delete from book_keyword;
delete from book_language;

-- author
delete from author;
delete from keyword;
delete from language;
delete from book;

SET foreign_key_checks = 1;
