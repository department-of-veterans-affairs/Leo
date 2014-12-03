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
IF OBJECT_ID('[!schema!].feature!tableSuffix!', 'U') IS NOT NULL
	DROP TABLE [!schema!].feature!tableSuffix! ;
	
IF OBJECT_ID('[!schema!].document_xref_example!tableSuffix!', 'U') IS NOT NULL
	DROP TABLE [!schema!].document_xref_example!tableSuffix!;

IF OBJECT_ID('[!schema!].annotation!tableSuffix!', 'U') IS NOT NULL
	DROP TABLE [!schema!].annotation!tableSuffix!;

IF OBJECT_ID('[!schema!].document!tableSuffix!', 'U') IS NOT NULL
	DROP TABLE [!schema!].document!tableSuffix!;

IF OBJECT_ID('[!schema!].user!tableSuffix!', 'U') IS NOT NULL DROP TABLE [!schema!].user!tableSuffix!;

	
CREATE TABLE [!schema!].user!tableSuffix! (
  id SMALLINT IDENTITY ,
  version BIGINT NULL ,
  account_expired BIT NULL ,
  account_locked BIT NULL ,
  enabled BIT NULL ,
  password VARCHAR(255) NULL ,
  password_expired BIT NULL ,
  username VARCHAR(255) NOT NULL ,
  PRIMARY KEY (id));
  
CREATE UNIQUE INDEX user_username!tableSuffix! on [!schema!].user!tableSuffix!(username);

CREATE TABLE [!schema!].document!tableSuffix! (
  guid VARCHAR(36) NOT NULL ,
  version DATETIME NOT NULL ,
  document_xref_guid VARCHAR(36) NOT NULL ,
  document_xref_table VARCHAR(500) NOT NULL ,
  entry_date_time DATETIME NOT NULL ,
  PRIMARY KEY (guid));

CREATE UNIQUE INDEX document_guid!tableSuffix! on [!schema!].document!tableSuffix! (guid);

CREATE TABLE [!schema!].annotation!tableSuffix! (
  guid VARCHAR(36) NOT NULL ,
  version DATETIME NOT NULL ,
  document_guid VARCHAR(36) NOT NULL REFERENCES [!schema!].document!tableSuffix! (guid) ,
  [end] INT NOT NULL ,
  start INT NOT NULL ,
  type VARCHAR(255) NOT NULL ,
  [group] VARCHAR(255) ,
  user_id SMALLINT REFERENCES  [!schema!].user!tableSuffix! (id),
  PRIMARY KEY (guid));
  
CREATE UNIQUE INDEX annotation_guid!tableSuffix! on [!schema!].annotation!tableSuffix! (guid);
CREATE INDEX FKA34FEB2F18B0FE25!tableSuffix! on [!schema!].annotation!tableSuffix! (document_guid);
CREATE INDEX FKA34FEB2F425CB89D!tableSuffix! on [!schema!].annotation!tableSuffix! (user_id);

CREATE TABLE [!schema!].document_xref_example!tableSuffix! (
  guid VARCHAR(36) NOT NULL ,
  version DATETIME NOT NULL ,
  patient_sid VARCHAR(255) NOT NULL ,
  tiu_document_sid VARCHAR(255) NOT NULL ,
  PRIMARY KEY (guid));

CREATE UNIQUE INDEX document_xref_example_guid!tableSuffix! on [!schema!].document_xref_example!tableSuffix!(guid);

CREATE TABLE [!schema!].feature!tableSuffix! (
  guid VARCHAR(36) NOT NULL ,
  version DATETIME NOT NULL ,
  annotation_guid VARCHAR(36) NOT NULL REFERENCES [!schema!].annotation!tableSuffix! (guid ),
  feature_index INT NOT NULL ,
  name VARCHAR(500) NOT NULL ,
  type VARCHAR(500) NULL ,
  value VARCHAR(2000) NULL ,
  PRIMARY KEY (guid) );
  
CREATE UNIQUE INDEX feauture_guid!tableSuffix! on [!schema!].feature!tableSuffix!(guid);
CREATE INDEX FKC5A27AF6F70B6EE5!tableSuffix! on [!schema!].feature!tableSuffix!(annotation_guid);