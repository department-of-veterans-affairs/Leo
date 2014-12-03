---
-- #%L
-- Leo Core
-- %%
-- Copyright (C) 2010 - 2014 Department of Veterans Affairs
-- %%
-- Licensed under the Apache License, Version 2.0 (the "License");
-- you may not use this file except in compliance with the License.
-- You may obtain a copy of the License at
-- 
--      http://www.apache.org/licenses/LICENSE-2.0
-- 
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.
-- #L%
---
SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';


-- -----------------------------------------------------
-- Table `user`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `user!tableSuffix!` ;

CREATE  TABLE IF NOT EXISTS `user!tableSuffix!` (
  `id` SMALLINT(6) NOT NULL AUTO_INCREMENT ,
  `version` BIGINT(20) NOT NULL  ,
  `account_expired` BIT(1) NOT NULL ,
  `account_locked` BIT(1) NOT NULL ,
  `enabled` BIT(1) NOT NULL ,
  `password` VARCHAR(255) NOT NULL ,
  `password_expired` BIT(1) NOT NULL ,
  `username` VARCHAR(255) NOT NULL ,
  PRIMARY KEY (`id`) ,
  UNIQUE INDEX `username` (`username` ASC) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `document`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `document!tableSuffix!` ;

CREATE  TABLE IF NOT EXISTS `document!tableSuffix!` (
  `guid` VARCHAR(36) NOT NULL ,
  `version` DATETIME NOT NULL ,
  `document_xref_guid` VARCHAR(36) NOT NULL ,
  `document_xref_table` VARCHAR(500) NOT NULL ,
  `entry_date_time` DATETIME NOT NULL ,
  PRIMARY KEY (`guid`) ,
  UNIQUE INDEX `guid` (`guid` ASC) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `chex`.`annotation`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `annotation!tableSuffix!` ;

CREATE  TABLE IF NOT EXISTS `annotation!tableSuffix!` (
  `guid` VARCHAR(36) NOT NULL ,
  `version` DATETIME NOT NULL ,
  `document_guid` VARCHAR(36) NOT NULL ,
  `end` INT(11) NOT NULL ,
  `start` INT(11) NOT NULL ,
  `type` VARCHAR(255) NOT NULL ,
  `group` VARCHAR(255) ,
  `user_id` SMALLINT(6) ,
  PRIMARY KEY (`guid`) ,
  UNIQUE INDEX `guid` (`guid` ASC) ,
  INDEX `FKA34FEB2F18B0FE25` (`document_guid` ASC) ,
  INDEX `FKA34FEB2F425CB89D` (`user_id` ASC) ,
  CONSTRAINT `FKA34FEB2F425CB89D!tableSuffix!`
    FOREIGN KEY (`user_id` )
    REFERENCES `user!tableSuffix!` (`id` ),
  CONSTRAINT `FKA34FEB2F18B0FE25!tableSuffix!`
    FOREIGN KEY (`document_guid` )
    REFERENCES `document!tableSuffix!` (`guid` ))
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `document_xref_example`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `document_xref_example!tableSuffix!` ;

CREATE  TABLE IF NOT EXISTS `document_xref_example!tableSuffix!` (
  `guid` VARCHAR(36) NOT NULL ,
  `version` DATETIME NOT NULL ,
  `created_by` VARCHAR(255) NOT NULL ,
  `tiu_document_sid` VARCHAR(255) NOT NULL ,
  PRIMARY KEY (`guid`) ,
  UNIQUE INDEX `guid` (`guid` ASC))
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `feature`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `feature!tableSuffix!` ;

CREATE  TABLE IF NOT EXISTS `feature!tableSuffix!` (
  `guid` VARCHAR(36) NOT NULL ,
  `version` DATETIME NOT NULL ,
  `annotation_guid` VARCHAR(36) NOT NULL ,
  `feature_index` INT(11) NOT NULL ,
  `name` VARCHAR(500) NOT NULL ,
  `type` VARCHAR(500) NOT NULL ,
  `value` VARCHAR(2000) NULL ,
  PRIMARY KEY (`guid`) ,
  UNIQUE INDEX `guid` (`guid` ASC) ,
  INDEX `FKC5A27AF6F70B6EE5!tableSuffix!` (`annotation_guid` ASC) ,
  CONSTRAINT `FKC5A27AF6F70B6EE5!tableSuffix!`
    FOREIGN KEY (`annotation_guid` )
    REFERENCES `annotation!tableSuffix!` (`guid` ))
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;



SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
