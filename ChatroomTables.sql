use chatroom;

drop table if exists cr_avatars;
drop table if exists cr_msgs;
drop table if exists cr_users;
create table cr_users(
	uid char(36) primary key,
	uname varchar(20) not null,
	upswd varchar(20) not null,
	upriv int not null default 0,
	unique(uname, upswd)
);
create table cr_msgs(
	mid int primary key auto_increment,
	uid char(36) not null, -- user infomation
	mtime timestamp not null default current_timestamp,
	mcontent varchar(100) not null,
	locked tinyint(1) not null default 0,
	mhasprev tinyint(1) not null default 0, -- message has prev
	foreign key (uid) references cr_users(uid)
);
create table cr_avatars (
	uid char(36) primary key references cr_users(uid),
	uavatar blob
);

grant select, insert on chatroom.cr_users to 'nowifi'@'localhost';
grant select, insert on chatroom.cr_msgs to 'nowifi'@'localhost';
grant select, insert, delete on chatroom.cr_avatars to 'nowifi'@'localhost';

delimiter $$

drop procedure if exists clear_msg;
create procedure clear_msg(in hours int)
begin
	delete from cr_msgs where (
		timestampdiff(HOUR, mtime, now()) > hours and
		locked = 0 and
		uid not in (select uid from cr_users where upriv != 0)
	);
end $$

delimiter ;
