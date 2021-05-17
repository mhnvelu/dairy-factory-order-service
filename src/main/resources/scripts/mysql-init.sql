DROP DATABASE if exists dairyfactoryorderservice;
drop user if exists `dairy_factory_order_service`@`%`;
create database if not exists dairyfactoryorderservice character set utf8mb4 collate
  utf8mb4_unicode_ci;
create user if not exists `dairy_factory_order_service`@`%` IDENTIFIED with mysql_native_password
  by 'password';
grant select, insert, update, delete, create, drop, references, index, alter, execute, CREATE,
  create view, show view, create routine, alter routine, event, trigger on
  `dairyfactoryorderservice`.* to
  `dairy_factory_order_service`@`%`;
flush privileges;