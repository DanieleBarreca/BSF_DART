SET @username='${dart.application.user}';
SET @email='${dart.application.user.email}';
SET @firstName='${dart.application.user.first_name}';
SET @lastName='${dart.application.user.last_name}';
SET @userGroup='${dart.application.user.group}';
SET @password='${dart.application.user.password}';

INSERT INTO `${dart.sql.db}`.`user`
(`login`, `email`, `first_name`, `last_name`, `password`, `server_role`, `public_user`)
VALUES (@username, @email, @firstName, @lastName, TO_BASE64(unhex(SHA2(@password, 256))), 'USER', 0);

SELECT @userid:=`userId` FROM `${dart.sql.db}`.`user` WHERE `login`=@username;
INSERT INTO `${dart.sql.db}`.`user_group`
(`group_name`)
VALUES (@userGroup);

SELECT @groupId:=`group_id` FROM `${dart.sql.db}`.`user_group` WHERE `group_name`=@userGroup;
INSERT INTO `${dart.sql.db}`.`user_roles`
(`user_id`, `group_id`,`canQuery`,`canSaveQueryPreset`,`canSaveQueryPanel`,`isAdmin`,`canUploadVCF`,`canValidateVariants`,`canAnnotatePathogenicity`,`canSaveReport`)
VALUES (@userid,@groupId,1,1,1,1,1,1,1,1);

