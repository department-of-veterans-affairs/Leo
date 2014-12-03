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

-- -----------------------------------------------------
-- Table chex.user
-- -----------------------------------------------------
CREATE TABLE user!tableSuffix! (
  "id" SMALLINT NOT NULL ,
  "version" DECIMAL(20) NOT NULL ,
  "account_expired" BIT(1) NOT NULL ,
  "account_locked" BIT(1) NOT NULL ,
  "enabled" BIT(1) NOT NULL ,
  "password" VARCHAR(255) NOT NULL ,
  "password_expired" BIT(1) NOT NULL ,
  "username" VARCHAR(255) NOT NULL ,
  PRIMARY KEY ("id"));
  



-- -----------------------------------------------------
-- Table chex.document
-- -----------------------------------------------------
CREATE  TABLE document!tableSuffix! (
  "guid" VARCHAR(36) NOT NULL ,
  "version" DATETIME NOT NULL ,
  "document_xref_guid" VARCHAR(36) NOT NULL ,
  "document_xref_table" VARCHAR(500) NOT NULL ,
  "entry_date_time" DATETIME NOT NULL ,
  PRIMARY KEY ("guid"));



-- -----------------------------------------------------
-- Table chex.document_xref_example
-- -----------------------------------------------------
CREATE  TABLE document_xref_example!tableSuffix! (
  "guid" VARCHAR(36) NOT NULL ,
  "version" DATETIME NOT NULL ,
  "created_by" VARCHAR(255) NOT NULL ,
  "document_guid" VARCHAR(36) NOT NULL ,
  "encounter_date_time" DATETIME NOT NULL ,
  "patient_sid" VARCHAR(255) NOT NULL ,
  "tiu_document_ien" VARCHAR(255) NOT NULL ,
  "tiu_document_sid" VARCHAR(255) NOT NULL ,
  PRIMARY KEY ("guid"));




-- -----------------------------------------------------
-- Table chex.annotation
-- -----------------------------------------------------
CREATE  TABLE annotation!tableSuffix! (
  "guid" VARCHAR(36) NOT NULL ,
  "document_guid" VARCHAR(36) NOT NULL ,
  "end" INT NOT NULL ,
  "start" INT NOT NULL ,
  "type" VARCHAR(255) NOT NULL ,
  "group" VARCHAR(255) ,
  "user_id" INT ,
  "version" DATETIME NOT NULL ,
  PRIMARY KEY ("guid"),
  CONSTRAINT FKA34FEB2F425CB89D
    FOREIGN KEY ("user_id")
    REFERENCES user ("id" ),
  CONSTRAINT FKA34FEB2F18B0FE25
    FOREIGN KEY ("document_guid" )
    REFERENCES document!tableSuffix! ("guid" ));


-- -----------------------------------------------------
-- Table chex.feature
-- -----------------------------------------------------
CREATE  TABLE feature!tableSuffix! (
  "guid" VARCHAR(36) NOT NULL ,
  "version" DATETIME NOT NULL ,
  "annotation_guid" VARCHAR(36) NOT NULL ,
  "feature_index" INT NOT NULL ,
  "name" VARCHAR(500) NOT NULL ,
  "type" VARCHAR(500) NOT NULL ,
  "value" VARCHAR(2000) NULL ,
  PRIMARY KEY ("guid") ,
  CONSTRAINT FKC5A27AF6F70B6EE5
    FOREIGN KEY ("annotation_guid" )
    REFERENCES annotation!tableSuffix! ("guid" ));

CREATE TABLE processing_error!tableSuffix! (
    "id" integer,
    "reference_location" VARCHAR(5000),
    "error_message" VARCHAR(5000),
    "stack_trace" VARCHAR(5000),
    "error_date" DATETIME,
    "version" DATETIME)
