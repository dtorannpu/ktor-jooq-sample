create table todo
(
    id          integer      not null generated always as identity primary key,
    title       varchar(256) not null,
    description varchar(256) not null
)