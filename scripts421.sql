alter table student add constraint age_constraint check (age > 15);
alter table student add constraint name_unique_constraint unique (name), add constraint name_not_null_constraint check (name is not null);
alter table faculty add constraint unique_name_color_constraint unique (name, color);
alter table student alter column age set default 20;