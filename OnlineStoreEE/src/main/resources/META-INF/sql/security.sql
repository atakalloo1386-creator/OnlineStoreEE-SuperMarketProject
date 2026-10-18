-- Runs after Hibernate creates the tables (persistence.xml: jakarta.persistence.sql-load-script-source).
-- Development data only: with create-drop the database is empty at every start.

-- The login reads these two views (core/security/SecurityConfig).
-- Deleted or disabled users cannot log in.
create or replace view realm_users as
    select username, password
    from users
    where deleted = false and enabled = true;

-- The groups of a user = the names of his roles + the names of the permissions of those roles.
-- @RolesAllowed can name either: @RolesAllowed("admin") or @RolesAllowed("attachment:file").
create or replace view realm_user_groups as
    select u.username, r.name as group_name
    from users u
    join user_roles ur on ur.user_id = u.id
    join roles r on r.id = ur.role_id
    where u.deleted = false and u.enabled = true and r.deleted = false
    union
    select u.username, p.name as group_name
    from users u
    join user_roles ur on ur.user_id = u.id
    join roles r on r.id = ur.role_id
    join role_permissions rp on rp.role_id = r.id
    join permissions p on p.id = rp.permission_id
    where u.deleted = false and u.enabled = true and r.deleted = false and p.deleted = false;

-- Roles
insert into roles (name, deleted, created_at, version) values ('admin', false, current_timestamp, 0);
insert into roles (name, deleted, created_at, version) values ('user', false, current_timestamp, 0);

-- Permissions, given to roles
insert into permissions (name, deleted, created_at, version) values ('attachment:file', false, current_timestamp, 0);

insert into role_permissions (role_id, permission_id)
    select r.id, p.id from roles r, permissions p where r.name = 'admin' and p.name = 'attachment:file';
insert into role_permissions (role_id, permission_id)
    select r.id, p.id from roles r, permissions p where r.name = 'user' and p.name = 'attachment:file';

-- Test users, passwords stored as BCrypt: admin / admin123 and user / user123.
insert into users (username, password, enabled, deleted, created_at, version)
    values ('admin', '$2a$10$xNgsVOMsZtBS1OpOJ1Lv.e1gEI21rTkBzbptrJwVTNRg.9KSAyX2W', true, false, current_timestamp, 0);
insert into users (username, password, enabled, deleted, created_at, version)
    values ('user', '$2a$10$LlWl2Na4nlUNNG7v9n3VRulT.vRx3YA9luU9SfcPZFCBPCoM7e0Ni', true, false, current_timestamp, 0);

insert into user_roles (user_id, role_id)
    select u.id, r.id from users u, roles r where u.username = 'admin' and r.name = 'admin';
insert into user_roles (user_id, role_id)
    select u.id, r.id from users u, roles r where u.username = 'user' and r.name = 'user';
