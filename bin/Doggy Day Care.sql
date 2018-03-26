create table customer(
	c_id int not null,
	c_name varchar(30) not null,
	email_address varchar(50),
	phone_no varchar(12),
	address varchar(50),
	primary key (c_id));

create table loyalty_member(
	c_id int,
	number_of_points int,
	primary key (c_id),
	foreign key (c_id) references customer
	ON DELETE CASCADE);

create table service(
	s_id int,
	rate int,
	type varchar(30),
	duration int,
	price real,
	primary key (s_id));

create table res(
	confirmation_no int,
	from_date timestamp,
	to_date timestamp,
	s_id int,
	c_id int,
	primary key (confirmation_no),
	foreign key (c_id) references customer ON DELETE CASCADE,
	foreign key (s_id) references service ON DELETE CASCADE);

create table dog(
	d_id int not null,
	d_name varchar(20) not null,
	breed varchar(20),
	age int,
	sex varchar(1),
	c_id int,
	primary key (d_id),
	foreign key (c_id) references customer
	ON DELETE SET NULL);

create table employee_manages(
	e_id int not null,
	e_name varchar(25) not null,
	m_id int,
	primary key (e_id),
	foreign key (m_id) references employee_manages ON DELETE SET NULL);

create table full_time(
	e_id int not null,
	salary real,
	m_id int,
	primary key (e_id),
	foreign key (e_id) references employee_manages
	ON DELETE CASCADE);

create table part_time(
	e_id int not null,
	hourly_rate real,
	primary key (e_id),
	foreign key (e_id) references employee_manages
	ON DELETE CASCADE);

create table products(
	p_id int,
	p_name varchar(30),
	p_type varchar(30),
	p_brand varchar(30),
	quantity int,
	price real,
	primary key (p_id));

create table has_a_schedule(
	sc_id int not null,
	sc_from timestamp not null,
	sc_to timestamp not null,
	e_id int not null,
	d_id int not null,
	primary key (sc_id),
	foreign key (e_id) references employee_manages ON DELETE CASCADE,
	foreign key (d_id) references dog ON DELETE CASCADE);

create table stocks(
	e_id int,
	p_id int,
	stock_id int,
	quantity_ordered int,
	primary key (stock_id),
	foreign key (e_id) references employee_manages ON DELETE SET NULL,
	foreign key (p_id) references products ON DELETE SET NULL);

create table buys(
	receipt_no int,
	p_id int,
	c_id int,
	quantity_bought int,
	primary key (p_id,c_id,receipt_no),
	foreign key (p_id) references products ON DELETE CASCADE,
	foreign key (c_id) references customer ON DELETE CASCADE);


insert into customer 
values('1','Ekko','ekko@ddc.com','604-111-1111','1111 Ekko Street');

insert into customer 
values('2','Jinx Charm','jinx@ddc.com','604-222-2222','2222 Jinx Street');

insert into customer 
values('3','Diana Fowl','diana@ddc.com','604-333-3333','3333 Diana Street');

insert into customer 
values('4','Syndra Min','syndra@ddc.com','604-444-4444','4444 Syndra Street');

insert into customer 
values('5','Caitlyn Jenner','caitlyn@ddc.com','604-555-5555','5555 Caitlyn Street');

insert into loyalty_member 
values('1','10');

insert into loyalty_member 
values('2','20');

insert into loyalty_member 
values('3','30');

insert into loyalty_member 
values('4','40');

insert into loyalty_member 
values('5','50');

insert into service 
values('1','10','Dog Walking','3','30');

insert into service 
values('2','20','Grooming','3','60');

insert into service 
values('3','30','Boarding','24','100');

insert into service 
values('4','40','Dog Training','24','250');

insert into service 
values('5','50','All','24','300');

insert into res
values('1',to_timestamp('16-OCT-11 13:00','YY-MM-DD HH24:MI'),to_timestamp('16-OCT-12 17:00','YY-MM-DD HH24:MI'),'1','1');

insert into res
values('2',to_timestamp('16-OCT-12 12:00','YY-MM-DD HH24:MI'),to_timestamp('16-OCT-13 17:00','YY-MM-DD HH24:MI'),'2','2');

insert into res
values('3',to_timestamp('16-OCT-13 11:00','YY-MM-DD HH24:MI'),to_timestamp('16-OCT-17 17:00','YY-MM-DD HH24:MI'),'3','3');

insert into res
values('4',to_timestamp('16-OCT-14 11:00','YY-MM-DD HH24:MI'),to_timestamp('16-OCT-18 17:00','YY-MM-DD HH24:MI'),'4','4');

insert into res
values('5',to_timestamp('16-OCT-15 15:00','YY-MM-DD HH24:MI'),to_timestamp('16-OCT-19 17:00','YY-MM-DD HH24:MI'),'5','5');

insert into dog
values('1','Gnar','Husky','1','m','1');

insert into dog
values('2','Fizz','Shiba Inu','2','f','2');

insert into dog
values('3','Rengar','Golden Retriever','3','f','3');

insert into dog
values('4','Teemo','Pomeranian','4','m','4');

insert into dog
values('5','Volibear','Chihuahua','5','f','5');

insert into employee_manages
values('1','Mordekaiser Morgenstern',NULL);

insert into employee_manages
values('2','Aatrox Trek','1');

insert into employee_manages
values('3','KogMaw Maumau','1');

insert into employee_manages
values('4','Ziggs Zags','1');

insert into employee_manages
values('5','Talon Fowl','1');

insert into full_time
values('1','50000','1');

insert into full_time
values('2','45000',NULL);

insert into part_time
values('3','17');

insert into part_time
values('4','15');

insert into part_time
values('5','15');

insert into has_a_schedule
values('1',to_timestamp('16-OCT-11 13:00','YY-MM-DD HH24:MI'),to_timestamp('16-OCT-11 16:00','YY-MM-DD HH24:MI'),'1','1');

insert into has_a_schedule
values('2',to_timestamp('16-OCT-12 12:00','YY-MM-DD HH24:MI'),to_timestamp('16-OCT-12 15:00','YY-MM-DD HH24:MI'),'2','2');

insert into has_a_schedule
values('3',to_timestamp('16-OCT-13 11:00','YY-MM-DD HH24:MI'),to_timestamp('16-OCT-14 11:00','YY-MM-DD HH24:MI'),'3','3');

insert into has_a_schedule
values('4',to_timestamp('16-OCT-14 11:00','YY-MM-DD HH24:MI'),to_timestamp('16-OCT-15 11:00','YY-MM-DD HH24:MI'),'4','4');

insert into has_a_schedule
values('5',to_timestamp('16-OCT-15 15:00','YY-MM-DD HH24:MI'),to_timestamp('16-OCT-16 15:00','YY-MM-DD HH24:MI'),'5','5');

insert into products
values('1','toy','Kong','Classic Goodie Bone','12','18');

insert into products 
values('2','toy','SkyHoundz','Z-Disc','20','4.35');

insert into products
values('3','food','BarkShop','Mutt Mallows','20','8');

insert into products
values('4','food','Blue Buffalo','Blue Basics-Turkey','19','55');

insert into products
values('5','dog-care','Cesar Millan','No-Skid Food Bowl','15','15');

insert into stocks
values('3','1','1','10');

insert into stocks
values('4','2','2','20');

insert into stocks
values('4','3','3','30');

insert into stocks
values('5','4','4','40');

insert into stocks
values('5','5','5','50');

insert into buys
values('1','1','1','3');

insert into buys
values('2','2','2','3');

insert into buys
values('3','3','3','3');

insert into buys
values('4','4','4','3');

insert into buys
values('5','5','5','3');

insert into buys
values('6','2','1','3');

insert into buys
values('7','3','1','3');

insert into buys
values('8','4','1','3');

insert into buys values('9','5','1','3');