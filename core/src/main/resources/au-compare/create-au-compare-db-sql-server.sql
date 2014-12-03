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
IF OBJECT_ID('[!schema!].auCompare!tableSuffix!', 'U') IS NOT NULL
	DROP TABLE [!schema!].auCompare!tableSuffix! ;

CREATE TABLE [!schema!].auCompare!tableSuffix! (
  id INT IDENTITY(1,1) ,
  docId VARCHAR(255) NOT NULL,
  mapping VARCHAR(255) NOT NULL,
  start BIGINT NULL ,
  [end] BIGINT NULL ,
  type VARCHAR(255) NULL,
  coveredText VARCHAR(MAX) NULL ,
  statClass VARCHAR(64) NULL ,
  PRIMARY KEY (id));