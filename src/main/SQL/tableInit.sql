drop table if exists buyable_coin;
drop table if exists coin;
drop table if exists exchange;
drop table if exists member;
drop table if exists wallet;
drop table if exists wallet_coin;

create table buyable_coin (
    id long primary key,
    base_coin_id varchar(255) not null,


);