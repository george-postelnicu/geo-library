alter table author
    add constraint uk_author_name unique (name);
alter table keyword
    add constraint uk_keyword_name unique (name);
alter table language
    add constraint uk_language_name unique (name);
