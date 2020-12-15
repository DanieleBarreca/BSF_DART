-- -----------------------------------------------------
-- Data for table annotation_dictionary`
-- -----------------------------------------------------
START TRANSACTION;
USE `${dart.sql.db}`;
INSERT INTO `${dart.sql.db}`.`annotation_dictionary` (`db_id`, `annotation_type`, `annotation_label`, `annotation_description`, `rank`) VALUES (1, 'ACMG_AMP_GERMLINE', 'PATHOGENIC', NULL, 1);
INSERT INTO `${dart.sql.db}`.`annotation_dictionary` (`db_id`, `annotation_type`, `annotation_label`, `annotation_description`, `rank`) VALUES (2, 'ACMG_AMP_GERMLINE', 'LIKELY PATHOGENIC', NULL, 2);
INSERT INTO `${dart.sql.db}`.`annotation_dictionary` (`db_id`, `annotation_type`, `annotation_label`, `annotation_description`, `rank`) VALUES (3, 'ACMG_AMP_GERMLINE', 'UNKNOWN', NULL, 3);
INSERT INTO `${dart.sql.db}`.`annotation_dictionary` (`db_id`, `annotation_type`, `annotation_label`, `annotation_description`, `rank`) VALUES (4, 'ACMG_AMP_GERMLINE', 'LIKELY BENIGN', NULL, 4);
INSERT INTO `${dart.sql.db}`.`annotation_dictionary` (`db_id`, `annotation_type`, `annotation_label`, `annotation_description`, `rank`) VALUES (5, 'ACMG_AMP_GERMLINE', 'BENIGN', NULL, 5);
INSERT INTO `${dart.sql.db}`.`annotation_dictionary` (`db_id`, `annotation_type`, `annotation_label`, `annotation_description`, `rank`) VALUES (6, 'ACMG_AMP_SOMATIC', 'TIER 1', NULL, 1);
INSERT INTO `${dart.sql.db}`.`annotation_dictionary` (`db_id`, `annotation_type`, `annotation_label`, `annotation_description`, `rank`) VALUES (7, 'ACMG_AMP_SOMATIC', 'TIER 2', NULL, 2);
INSERT INTO `${dart.sql.db}`.`annotation_dictionary` (`db_id`, `annotation_type`, `annotation_label`, `annotation_description`, `rank`) VALUES (8, 'ACMG_AMP_SOMATIC', 'TIER 3', NULL, 3);
INSERT INTO `${dart.sql.db}`.`annotation_dictionary` (`db_id`, `annotation_type`, `annotation_label`, `annotation_description`, `rank`) VALUES (9, 'ACMG_AMP_SOMATIC', 'TIER 4', NULL, 4);

COMMIT;

-- -----------------------------------------------------
-- Data for table `inheritance_dictionary`
-- -----------------------------------------------------
START TRANSACTION;
USE `${dart.sql.db}`;
INSERT INTO `${dart.sql.db}`.`inheritance_dictionary` (`db_id`, `annotation_type`, `inheritance_label`) VALUES (1, 'ACMG_AMP_GERMLINE', 'DOMINANT');
INSERT INTO `${dart.sql.db}`.`inheritance_dictionary` (`db_id`, `annotation_type`, `inheritance_label`) VALUES (2, 'ACMG_AMP_GERMLINE', 'RECESSIVE');
INSERT INTO `${dart.sql.db}`.`inheritance_dictionary` (`db_id`, `annotation_type`, `inheritance_label`) VALUES (3, 'ACMG_AMP_GERMLINE', 'UNKNOWN');
INSERT INTO `${dart.sql.db}`.`inheritance_dictionary` (`db_id`, `annotation_type`, `inheritance_label`) VALUES (4, 'ACMG_AMP_SOMATIC', 'SOMATIC');
INSERT INTO `${dart.sql.db}`.`inheritance_dictionary` (`db_id`, `annotation_type`, `inheritance_label`) VALUES (5, 'ACMG_AMP_SOMATIC', 'PREDISPOSITION');

COMMIT;